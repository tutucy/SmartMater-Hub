<template>
  <div class="inventory-in-container">
    <h2 class="page-title">库存入库管理</h2>
    
    <el-card shadow="hover" class="inventory-card">
      <template #header>
        <div class="card-header">
          <span>入库记录</span>
          <el-button type="primary" @click="handleAddInbound" icon="Plus">
            新增入库
          </el-button>
        </div>
      </template>
      
      <!-- 入库搜索 -->
      <div class="inventory-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索入库单号、物资名称或供应商"
          clearable
          prefix-icon="Search"
          @keyup.enter="fetchInboundRecords"
          style="width: 350px; margin-right: 10px;"
        />
        <el-select
          v-model="searchInboundType"
          placeholder="选择入库类型"
          clearable
          style="width: 150px; margin-right: 10px;"
        >
          <el-option label="全部类型" value="" />
          <el-option label="采购入库" value="purchase" />
          <el-option label="生产入库" value="production" />
          <el-option label="调拨入库" value="transfer" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 300px; margin-right: 10px;"
        />
        <el-button type="primary" @click="fetchInboundRecords" icon="RefreshRight">
          刷新
        </el-button>
      </div>
      
      <!-- 入库列表 -->
      <div class="inventory-table-container">
        <el-table
          v-loading="loading"
          :data="inboundRecords"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="inboundNo" label="入库单号" width="180" />
          <el-table-column prop="inboundType" label="入库类型" width="120">
            <template #default="scope">
              <el-tag :type="getInboundTypeColor(scope.row.inboundType)">
                {{ getInboundTypeName(scope.row.inboundType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="supplierName" label="供应商" width="150" />
          <el-table-column prop="totalQuantity" label="入库数量" width="120" align="center" />
          <el-table-column prop="totalAmount" label="入库金额" width="150" align="right" :formatter="formatAmount" />
          <el-table-column prop="operator" label="操作人" width="120" />
          <el-table-column prop="inboundDate" label="入库日期" width="180" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.status === '1' ? 'success' : 'info'">
                {{ scope.row.status === '1' ? '已完成' : '待审核' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleViewInbound(scope.row)" icon="View">
                查看
              </el-button>
              <el-button 
                type="success" 
                size="small" 
                @click="handleAuditInbound(scope.row)" 
                icon="Check"
                :disabled="scope.row.status === '1'"
              >
                审核
              </el-button>
              <el-button type="danger" size="small" @click="handleDeleteInbound(scope.row)" icon="Delete">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 新增入库对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="inboundFormRef"
        :model="inboundForm"
        :rules="inboundRules"
        label-position="right"
        label-width="100px"
      >
        <el-form-item label="入库单号" prop="inboundNo">
          <el-input
            v-model="inboundForm.inboundNo"
            placeholder="系统自动生成"
            readonly
          />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入库类型" prop="inboundType">
              <el-select
                v-model="inboundForm.inboundType"
                placeholder="请选择入库类型"
                clearable
                @change="handleInboundTypeChange"
              >
                <el-option label="采购入库" value="purchase" />
                <el-option label="生产入库" value="production" />
                <el-option label="调拨入库" value="transfer" />
              </el-select>
            </el-form-item>
            
            <el-form-item 
              v-if="inboundForm.inboundType === 'purchase'" 
              label="采购订单号" 
              prop="orderNo"
            >
              <el-select
                v-model="inboundForm.orderNo"
                placeholder="请选择采购订单"
                clearable
              >
                <el-option
                  v-for="order in purchaseOrders"
                  :key="order.id"
                  :label="order.orderNo"
                  :value="order.orderNo"
                />
              </el-select>
            </el-form-item>
            
            <el-form-item label="供应商" prop="supplierId">
              <el-select
                v-model="inboundForm.supplierId"
                placeholder="请选择供应商"
                clearable
              >
                <el-option
                  v-for="supplier in suppliers"
                  :key="supplier.id"
                  :label="supplier.name"
                  :value="supplier.id"
                />
              </el-select>
            </el-form-item>
            
            <el-form-item label="入库日期" prop="inboundDate">
              <el-date-picker
                v-model="inboundForm.inboundDate"
                type="datetime"
                placeholder="请选择入库日期"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%;"
              />
            </el-form-item>
            
            <el-form-item label="操作人" prop="operator">
              <el-input
                v-model="inboundForm.operator"
                placeholder="系统自动填充"
                readonly
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="备注" prop="remark">
              <el-input
                v-model="inboundForm.remark"
                placeholder="请输入备注信息"
                type="textarea"
                rows="3"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 入库明细 -->
        <el-divider>入库明细</el-divider>
        
        <el-table
          v-loading="detailLoading"
          :data="inboundForm.details"
          style="width: 100%"
          border
        >
          <el-table-column type="selection" width="55" />
          <el-table-column label="物资信息" min-width="200">
            <template #default="scope">
              <el-select
                v-model="scope.row.materialId"
                placeholder="请选择物资"
                @change="handleMaterialChange(scope.row)"
              >
                <el-option
                  v-for="material in materials"
                  :key="material.id"
                  :label="`${material.name} (${material.specification})`"
                  :value="material.id"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="materialName" label="物资名称" width="150" />
          <el-table-column prop="specification" label="型号规格" width="150" />
          <el-table-column prop="unit" label="单位" width="80" />
          <el-table-column prop="quantity" label="入库数量" width="120">
            <template #default="scope">
              <el-input-number
                v-model="scope.row.quantity"
                :min="1"
                @change="handleQuantityChange"
              />
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="120">
            <template #default="scope">
              <el-input-number
                v-model="scope.row.price"
                :min="0"
                :precision="2"
                @change="handlePriceChange"
              />
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="120" align="right">
            <template #default="scope">
              ¥{{ (scope.row.quantity * scope.row.price).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="scope">
              <el-button 
                type="danger" 
                size="small" 
                @click="handleRemoveDetail(scope.$index)" 
                icon="Delete"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="detail-actions">
          <el-button type="success" @click="handleAddDetail" icon="Plus">
            添加明细
          </el-button>
          <div class="total-info">
            <span>总数量: {{ totalQuantity }} | 总金额: ¥{{ totalAmount.toFixed(2) }}</span>
          </div>
        </div>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="dialogLoading" @click="handleSaveInbound">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import request from '../../services/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const inboundRecords = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const searchInboundType = ref('')
const dateRange = ref([])
const suppliers = ref([])
const materials = ref([])
const purchaseOrders = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增入库')
const dialogLoading = ref(false)
const detailLoading = ref(false)
const inboundFormRef = ref(null)

// 入库表单数据
const inboundForm = reactive({
  id: '',
  inboundNo: '',
  inboundType: 'purchase',
  orderNo: '',
  supplierId: '',
  supplierName: '',
  inboundDate: new Date().toISOString().slice(0, 19).replace('T', ' '),
  operator: '当前用户',
  remark: '',
  status: '0',
  details: [
    {
      materialId: '',
      materialName: '',
      specification: '',
      unit: '',
      quantity: 1,
      price: 0,
      amount: 0
    }
  ]
})

// 入库表单验证规则
const inboundRules = {
  inboundType: [
    { required: true, message: '请选择入库类型', trigger: 'change' }
  ],
  supplierId: [
    { required: true, message: '请选择供应商', trigger: 'change' }
  ],
  inboundDate: [
    { required: true, message: '请选择入库日期', trigger: 'change' }
  ]
}

// 计算总数量和总金额
const totalQuantity = computed(() => {
  return inboundForm.details.reduce((sum, item) => sum + item.quantity, 0)
})

const totalAmount = computed(() => {
  return inboundForm.details.reduce((sum, item) => sum + (item.quantity * item.price), 0)
})

// 获取入库列表
const fetchInboundRecords = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟入库记录数据
    const mockRecords = [
      {
        id: 1,
        inboundNo: 'RK20240101001',
        inboundType: 'purchase',
        supplierName: '得力办公',
        totalQuantity: 150,
        totalAmount: 3825,
        operator: 'admin',
        inboundDate: '2024-01-01 10:00:00',
        status: '1'
      },
      {
        id: 2,
        inboundNo: 'RK20240102002',
        inboundType: 'production',
        supplierName: '内部生产',
        totalQuantity: 100,
        totalAmount: 5000,
        operator: 'admin',
        inboundDate: '2024-01-02 14:30:00',
        status: '1'
      },
      {
        id: 3,
        inboundNo: 'RK20240103003',
        inboundType: 'purchase',
        supplierName: '晨光文具',
        totalQuantity: 300,
        totalAmount: 750,
        operator: 'admin',
        inboundDate: '2024-01-03 09:15:00',
        status: '1'
      },
      {
        id: 4,
        inboundNo: 'RK20240104004',
        inboundType: 'transfer',
        supplierName: '仓库A',
        totalQuantity: 50,
        totalAmount: 4450,
        operator: 'admin',
        inboundDate: '2024-01-04 16:45:00',
        status: '0'
      }
    ]
    
    // 模拟搜索功能
    let filteredRecords = mockRecords
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filteredRecords = mockRecords.filter(record => 
        record.inboundNo.toLowerCase().includes(keyword) ||
        record.supplierName.toLowerCase().includes(keyword)
      )
    }
    
    if (searchInboundType.value) {
      filteredRecords = filteredRecords.filter(record => 
        record.inboundType === searchInboundType.value
      )
    }
    
    // 模拟日期范围过滤
    if (dateRange.value && dateRange.value.length === 2) {
      filteredRecords = filteredRecords.filter(record => {
        const recordDate = new Date(record.inboundDate)
        const startDate = new Date(dateRange.value[0])
        const endDate = new Date(dateRange.value[1])
        return recordDate >= startDate && recordDate <= endDate
      })
    }
    
    inboundRecords.value = filteredRecords
    total.value = filteredRecords.length
    
    // 模拟供应商数据
    suppliers.value = [
      { id: 1, name: '得力办公' },
      { id: 2, name: '晨光文具' },
      { id: 3, name: '科技数码' },
      { id: 4, name: '内部生产' },
      { id: 5, name: '仓库A' }
    ]
    
    // 模拟物资数据
    materials.value = [
      { id: 1, name: 'A4打印纸', specification: '80g 500张/包', unit: '包', price: 25.5 },
      { id: 2, name: '黑色中性笔', specification: '0.5mm', unit: '支', price: 2.5 },
      { id: 3, name: '鼠标', specification: '有线 USB', unit: '个', price: 89.0 },
      { id: 4, name: '键盘', specification: '机械键盘 青轴', unit: '个', price: 299.0 }
    ]
    
    // 模拟采购订单数据
    purchaseOrders.value = [
      { id: 1, orderNo: 'PO20240101001' },
      { id: 2, orderNo: 'PO20240102002' },
      { id: 3, orderNo: 'PO20240103003' }
    ]
  } catch (error) {
    console.error('获取入库记录失败:', error)
    ElMessage.error('获取入库记录失败')
  } finally {
    loading.value = false
  }
}

// 获取入库类型名称
const getInboundTypeName = (type) => {
  const typeMap = {
    purchase: '采购入库',
    production: '生产入库',
    transfer: '调拨入库'
  }
  return typeMap[type] || type
}

// 获取入库类型颜色
const getInboundTypeColor = (type) => {
  const colorMap = {
    purchase: 'primary',
    production: 'success',
    transfer: 'warning'
  }
  return colorMap[type] || 'info'
}

// 格式化金额
const formatAmount = (row, column, cellValue) => {
  return `¥${cellValue.toFixed(2)}`
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedRecords.value = selection
}

// 处理新增入库
const handleAddInbound = () => {
  dialogTitle.value = '新增入库'
  // 重置表单
  if (inboundFormRef.value) {
    inboundFormRef.value.resetFields()
  }
  // 生成入库单号
  const now = new Date()
  const inboundNo = `RK${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}${String(Math.floor(Math.random() * 1000)).padStart(3, '0')}`
  // 重置表单数据
  Object.assign(inboundForm, {
    id: '',
    inboundNo: inboundNo,
    inboundType: 'purchase',
    orderNo: '',
    supplierId: '',
    supplierName: '',
    inboundDate: now.toISOString().slice(0, 19).replace('T', ' '),
    operator: '当前用户',
    remark: '',
    status: '0',
    details: [
      {
        materialId: '',
        materialName: '',
        specification: '',
        unit: '',
        quantity: 1,
        price: 0,
        amount: 0
      }
    ]
  })
  dialogVisible.value = true
}

// 处理查看入库
const handleViewInbound = (row) => {
  console.log('查看入库:', row)
  ElMessage.info('查看入库详情功能开发中...')
}

// 处理审核入库
const handleAuditInbound = (row) => {
  ElMessageBox.confirm(
    `确定要审核入库记录 "${row.inboundNo}" 吗？`,
    '审核入库',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    }
  )
  .then(() => {
    // 模拟API调用
    return new Promise(resolve => setTimeout(resolve, 800))
  })
  .then(() => {
    console.log('审核入库:', row)
    ElMessage.success('入库审核成功')
    // 刷新入库记录
    fetchInboundRecords()
  })
  .catch(() => {
    // 取消审核
  })
}

// 处理删除入库
const handleDeleteInbound = (row) => {
  ElMessageBox.confirm(
    `确定要删除入库记录 "${row.inboundNo}" 吗？删除后无法恢复。`,
    '删除入库记录',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
  .then(() => {
    // 模拟API调用
    return new Promise(resolve => setTimeout(resolve, 800))
  })
  .then(() => {
    console.log('删除入库:', row)
    ElMessage.success('入库记录删除成功')
    // 刷新入库记录
    fetchInboundRecords()
  })
  .catch(() => {
    // 取消删除
  })
}

// 处理入库类型变化
const handleInboundTypeChange = (value) => {
  console.log('入库类型变化:', value)
  // 可以根据入库类型动态调整表单字段
}

// 处理物资选择变化
const handleMaterialChange = (row) => {
  const material = materials.value.find(item => item.id === row.materialId)
  if (material) {
    row.materialName = material.name
    row.specification = material.specification
    row.unit = material.unit
    row.price = material.price
  }
}

// 处理数量变化
const handleQuantityChange = () => {
  // 数量变化时自动计算金额
}

// 处理价格变化
const handlePriceChange = () => {
  // 价格变化时自动计算金额
}

// 处理添加明细
const handleAddDetail = () => {
  inboundForm.details.push({
    materialId: '',
    materialName: '',
    specification: '',
    unit: '',
    quantity: 1,
    price: 0,
    amount: 0
  })
}

// 处理删除明细
const handleRemoveDetail = (index) => {
  if (inboundForm.details.length > 1) {
    inboundForm.details.splice(index, 1)
  } else {
    ElMessage.warning('至少保留一条明细记录')
  }
}

// 处理对话框关闭
const handleDialogClose = () => {
  // 重置表单
  if (inboundFormRef.value) {
    inboundFormRef.value.resetFields()
  }
}

// 处理保存入库
const handleSaveInbound = async () => {
  if (!inboundFormRef.value) return
  
  try {
    const valid = await inboundFormRef.value.validate()
    if (valid) {
      dialogLoading.value = true
      
      // 检查明细是否完整
      const hasEmptyMaterial = inboundForm.details.some(item => !item.materialId)
      if (hasEmptyMaterial) {
        ElMessage.warning('请选择完整的物资信息')
        return
      }
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      if (inboundForm.id) {
        // 编辑入库记录
        console.log('编辑入库记录:', inboundForm)
        ElMessage.success('入库记录编辑成功')
      } else {
        // 新增入库记录
        console.log('新增入库记录:', inboundForm)
        ElMessage.success('入库记录新增成功')
      }
      
      // 关闭对话框
      dialogVisible.value = false
      
      // 刷新入库记录
      fetchInboundRecords()
    }
  } catch (error) {
    console.error('保存入库记录失败:', error)
    ElMessage.error('保存入库记录失败')
  } finally {
    dialogLoading.value = false
  }
}

// 分页相关
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  fetchInboundRecords()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  fetchInboundRecords()
}

// 页面挂载时获取入库记录
onMounted(() => {
  fetchInboundRecords()
})

// 选择的记录
const selectedRecords = ref([])
</script>

<style scoped>
.inventory-in-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 100px);
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.inventory-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.inventory-search {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.inventory-table-container {
  margin-bottom: 20px;
  overflow-x: auto;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .inventory-in-container {
    padding: 10px;
  }
  
  .page-title {
    font-size: 20px;
    margin-bottom: 15px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .inventory-search {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .inventory-search .el-input,
  .inventory-search .el-select,
  .inventory-search .el-date-picker {
    width: 100% !important;
    margin-right: 0 !important;
  }
  
  .pagination {
    justify-content: center;
  }
  
  /* 表格响应式处理 */
  .inventory-table-container {
    width: 100%;
    overflow-x: auto;
  }
  
  /* 对话框响应式 */
  .el-dialog {
    max-width: 95vw !important;
    width: auto !important;
    margin: 10px !important;
  }
  
  /* 表单响应式 */
  .el-form-item {
    margin-bottom: 12px;
  }
  
  /* 明细操作响应式 */
  .detail-actions {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
}

@media screen and (max-width: 480px) {
  .card-header .el-button {
    width: 100%;
  }
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.detail-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.total-info {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}
</style>