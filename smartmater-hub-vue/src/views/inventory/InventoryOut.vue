<template>
  <div class="inventory-out-container">
    <h2 class="page-title">库存出库管理</h2>
    
    <el-card shadow="hover" class="inventory-card">
      <template #header>
        <div class="card-header">
          <span>出库记录</span>
          <el-button type="primary" @click="handleAddOutbound" icon="Plus">
            新增出库
          </el-button>
        </div>
      </template>
      
      <!-- 出库搜索 -->
      <div class="inventory-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索出库单号、物资名称或领用人"
          clearable
          prefix-icon="Search"
          @keyup.enter="fetchOutboundRecords"
          style="width: 350px; margin-right: 10px;"
        />
        <el-select
          v-model="searchOutboundType"
          placeholder="选择出库类型"
          clearable
          style="width: 150px; margin-right: 10px;"
        >
          <el-option label="全部类型" value="" />
          <el-option label="领用出库" value="issue" />
          <el-option label="销售出库" value="sale" />
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
        <el-button type="primary" @click="fetchOutboundRecords" icon="RefreshRight">
          刷新
        </el-button>
      </div>
      
      <!-- 出库列表 -->
      <div class="inventory-table-container">
        <el-table
          v-loading="loading"
          :data="outboundRecords"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="outboundNo" label="出库单号" width="180" />
          <el-table-column prop="outboundType" label="出库类型" width="120">
            <template #default="scope">
              <el-tag :type="getOutboundTypeColor(scope.row.outboundType)">
                {{ getOutboundTypeName(scope.row.outboundType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="operator" label="操作人" width="120" />
          <el-table-column prop="recipient" label="领用人" width="120" />
          <el-table-column prop="department" label="领用部门" width="120" />
          <el-table-column prop="totalQuantity" label="出库数量" width="120" align="center" />
          <el-table-column prop="totalAmount" label="出库金额" width="150" align="right" :formatter="formatAmount" />
          <el-table-column prop="outboundDate" label="出库日期" width="180" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.status === '1' ? 'success' : 'info'">
                {{ scope.row.status === '1' ? '已完成' : '待审核' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleViewOutbound(scope.row)" icon="View">
                查看
              </el-button>
              <el-button 
                type="success" 
                size="small" 
                @click="handleAuditOutbound(scope.row)" 
                icon="Check"
                :disabled="scope.row.status === '1'"
              >
                审核
              </el-button>
              <el-button type="danger" size="small" @click="handleDeleteOutbound(scope.row)" icon="Delete">
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
    
    <!-- 新增出库对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="outboundFormRef"
        :model="outboundForm"
        :rules="outboundRules"
        label-position="right"
        label-width="100px"
      >
        <el-form-item label="出库单号" prop="outboundNo">
          <el-input
            v-model="outboundForm.outboundNo"
            placeholder="系统自动生成"
            readonly
          />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出库类型" prop="outboundType">
              <el-select
                v-model="outboundForm.outboundType"
                placeholder="请选择出库类型"
                clearable
                @change="handleOutboundTypeChange"
              >
                <el-option label="领用出库" value="issue" />
                <el-option label="销售出库" value="sale" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="领用人" prop="recipient">
              <el-input
                v-model="outboundForm.recipient"
                placeholder="请输入领用人姓名"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="领用部门" prop="department">
              <el-select
                v-model="outboundForm.department"
                placeholder="请选择领用部门"
                clearable
              >
                <el-option label="采购部" value="采购部" />
                <el-option label="仓库管理部" value="仓库管理部" />
                <el-option label="生产部" value="生产部" />
                <el-option label="财务部" value="财务部" />
                <el-option label="行政部" value="行政部" />
                <el-option label="技术部" value="技术部" />
              </el-select>
            </el-form-item>
            
            <el-form-item v-if="outboundForm.outboundType === 'sale'" label="客户信息" prop="customerInfo">
              <el-input
                v-model="outboundForm.customerInfo"
                placeholder="请输入客户信息"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="出库日期" prop="outboundDate">
              <el-date-picker
                v-model="outboundForm.outboundDate"
                type="datetime"
                placeholder="请选择出库日期"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="操作人" prop="operator">
              <el-input
                v-model="outboundForm.operator"
                placeholder="请输入操作人姓名"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="用途" prop="purpose">
              <el-input
                v-model="outboundForm.purpose"
                placeholder="请输入出库用途"
                type="textarea"
                rows="3"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="备注" prop="remark">
              <el-input
                v-model="outboundForm.remark"
                placeholder="请输入备注信息"
                type="textarea"
                rows="3"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 出库明细 -->
        <el-divider>出库明细</el-divider>
        
        <el-table
          v-loading="detailLoading"
          :data="outboundForm.details"
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
                  :disabled="material.stockQuantity <= 0"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="materialName" label="物资名称" width="150" />
          <el-table-column prop="specification" label="型号规格" width="150" />
          <el-table-column prop="unit" label="单位" width="80" />
          <el-table-column prop="stockQuantity" label="当前库存" width="120" align="right">
            <template #default="scope">
              <span :class="scope.row.stockQuantity <= 0 ? 'stock-out' : 'stock-normal'">
                {{ scope.row.stockQuantity }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="出库数量" width="120">
            <template #default="scope">
              <el-input-number
                v-model="scope.row.quantity"
                :min="1"
                :max="scope.row.stockQuantity"
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
          <el-button type="primary" :loading="dialogLoading" @click="handleSaveOutbound">
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

const outboundRecords = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const searchOutboundType = ref('')
const dateRange = ref([])
const materials = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增出库')
const dialogLoading = ref(false)
const detailLoading = ref(false)
const outboundFormRef = ref(null)

// 出库表单数据
const outboundForm = reactive({
  id: '',
  outboundNo: '',
  outboundType: 'issue',
  operator: '',
  recipient: '',
  department: '',
  customerInfo: '',
  purpose: '',
  remark: '',
  outboundDate: new Date().toISOString().slice(0, 19).replace('T', ' '),
  status: '0',
  details: [
    {
      materialId: '',
      materialName: '',
      specification: '',
      unit: '',
      stockQuantity: 0,
      quantity: 1,
      price: 0,
      amount: 0
    }
  ]
})

// 出库表单验证规则
const outboundRules = {
  outboundType: [
    { required: true, message: '请选择出库类型', trigger: 'change' }
  ],
  operator: [
    { required: true, message: '请输入操作人姓名', trigger: 'blur' }
  ],
  recipient: [
    { required: true, message: '请输入领用人姓名', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请选择领用部门', trigger: 'change' }
  ],
  purpose: [
    { required: true, message: '请输入出库用途', trigger: 'blur' }
  ],
  outboundDate: [
    { required: true, message: '请选择出库日期', trigger: 'change' }
  ]
}

// 计算总数量和总金额
const totalQuantity = computed(() => {
  return outboundForm.details.reduce((sum, item) => sum + item.quantity, 0)
})

const totalAmount = computed(() => {
  return outboundForm.details.reduce((sum, item) => sum + (item.quantity * item.price), 0)
})

// 获取出库列表
const fetchOutboundRecords = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟出库记录数据
    const mockRecords = [
      {
        id: 1,
        outboundNo: 'CK20240101001',
        outboundType: 'issue',
        operator: 'admin',
        recipient: '张三',
        department: '行政部',
        totalQuantity: 10,
        totalAmount: 255,
        purpose: '日常办公使用',
        remark: '',
        outboundDate: '2024-01-01 14:00:00',
        status: '1'
      },
      {
        id: 2,
        outboundNo: 'CK20240102002',
        outboundType: 'issue',
        operator: 'admin',
        recipient: '李四',
        department: '技术部',
        totalQuantity: 5,
        totalAmount: 445,
        purpose: '项目开发需要',
        remark: '',
        outboundDate: '2024-01-02 09:30:00',
        status: '1'
      },
      {
        id: 3,
        outboundNo: 'CK20240103003',
        outboundType: 'sale',
        operator: 'admin',
        recipient: '外部客户',
        department: '销售部',
        customerInfo: '客户A',
        totalQuantity: 20,
        totalAmount: 510,
        purpose: '销售出库',
        remark: '',
        outboundDate: '2024-01-03 16:00:00',
        status: '1'
      },
      {
        id: 4,
        outboundNo: 'CK20240104004',
        outboundType: 'issue',
        operator: 'admin',
        recipient: '王五',
        department: '生产部',
        totalQuantity: 30,
        totalAmount: 75,
        purpose: '生产使用',
        remark: '',
        outboundDate: '2024-01-04 11:00:00',
        status: '0'
      }
    ]
    
    // 模拟搜索功能
    let filteredRecords = mockRecords
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filteredRecords = mockRecords.filter(record => 
        record.outboundNo.toLowerCase().includes(keyword) ||
        record.recipient.toLowerCase().includes(keyword) ||
        record.department.toLowerCase().includes(keyword)
      )
    }
    
    if (searchOutboundType.value) {
      filteredRecords = filteredRecords.filter(record => 
        record.outboundType === searchOutboundType.value
      )
    }
    
    // 模拟日期范围过滤
    if (dateRange.value && dateRange.value.length === 2) {
      filteredRecords = filteredRecords.filter(record => {
        const recordDate = new Date(record.outboundDate)
        const startDate = new Date(dateRange.value[0])
        const endDate = new Date(dateRange.value[1])
        return recordDate >= startDate && recordDate <= endDate
      })
    }
    
    outboundRecords.value = filteredRecords
    total.value = filteredRecords.length
    
    // 模拟物资数据
    materials.value = [
      { id: 1, name: 'A4打印纸', specification: '80g 500张/包', unit: '包', stockQuantity: 150, price: 25.5 },
      { id: 2, name: '黑色中性笔', specification: '0.5mm', unit: '支', stockQuantity: 300, price: 2.5 },
      { id: 3, name: '鼠标', specification: '有线 USB', unit: '个', stockQuantity: 20, price: 89.0 },
      { id: 4, name: '键盘', specification: '机械键盘 青轴', unit: '个', stockQuantity: 8, price: 299.0 },
      { id: 5, name: '文件夹', specification: 'A4 塑料材质', unit: '个', stockQuantity: 450, price: 5.0 },
      { id: 6, name: '订书机', specification: '标准型', unit: '个', stockQuantity: 0, price: 18.5 },
      { id: 7, name: '硒鼓', specification: 'HP LaserJet Pro M404n', unit: '个', stockQuantity: 5, price: 499.0 }
    ]
  } catch (error) {
    console.error('获取出库记录失败:', error)
    ElMessage.error('获取出库记录失败')
  } finally {
    loading.value = false
  }
}

// 获取出库类型名称
const getOutboundTypeName = (type) => {
  const typeMap = {
    issue: '领用出库',
    sale: '销售出库'
  }
  return typeMap[type] || type
}

// 获取出库类型颜色
const getOutboundTypeColor = (type) => {
  const colorMap = {
    issue: 'primary',
    sale: 'success'
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

// 处理新增出库
const handleAddOutbound = () => {
  dialogTitle.value = '新增出库'
  // 重置表单
  if (outboundFormRef.value) {
    outboundFormRef.value.resetFields()
  }
  // 生成出库单号
  const now = new Date()
  const outboundNo = `CK${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}${String(Math.floor(Math.random() * 1000)).padStart(3, '0')}`
  // 重置表单数据
  Object.assign(outboundForm, {
    id: '',
    outboundNo: outboundNo,
    outboundType: 'issue',
    operator: '',
    recipient: '',
    department: '',
    customerInfo: '',
    purpose: '',
    remark: '',
    outboundDate: now.toISOString().slice(0, 19).replace('T', ' '),
    status: '0',
    details: [
      {
        materialId: '',
        materialName: '',
        specification: '',
        unit: '',
        stockQuantity: 0,
        quantity: 1,
        price: 0,
        amount: 0
      }
    ]
  })
  dialogVisible.value = true
}

// 处理查看出库
const handleViewOutbound = (row) => {
  console.log('查看出库:', row)
  ElMessage.info('查看出库详情功能开发中...')
}

// 处理审核出库
const handleAuditOutbound = (row) => {
  ElMessageBox.confirm(
    `确定要审核出库记录 "${row.outboundNo}" 吗？`,
    '审核出库',
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
    console.log('审核出库:', row)
    ElMessage.success('出库审核成功')
    // 刷新出库记录
    fetchOutboundRecords()
  })
  .catch(() => {
    // 取消审核
  })
}

// 处理删除出库
const handleDeleteOutbound = (row) => {
  ElMessageBox.confirm(
    `确定要删除出库记录 "${row.outboundNo}" 吗？删除后无法恢复。`,
    '删除出库记录',
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
    console.log('删除出库:', row)
    ElMessage.success('出库记录删除成功')
    // 刷新出库记录
    fetchOutboundRecords()
  })
  .catch(() => {
    // 取消删除
  })
}

// 处理出库类型变化
const handleOutboundTypeChange = (value) => {
  console.log('出库类型变化:', value)
  // 可以根据出库类型动态调整表单字段
}

// 处理物资选择变化
const handleMaterialChange = (row) => {
  const material = materials.value.find(item => item.id === row.materialId)
  if (material) {
    row.materialName = material.name
    row.specification = material.specification
    row.unit = material.unit
    row.stockQuantity = material.stockQuantity
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
  outboundForm.details.push({
    materialId: '',
    materialName: '',
    specification: '',
    unit: '',
    stockQuantity: 0,
    quantity: 1,
    price: 0,
    amount: 0
  })
}

// 处理删除明细
const handleRemoveDetail = (index) => {
  if (outboundForm.details.length > 1) {
    outboundForm.details.splice(index, 1)
  } else {
    ElMessage.warning('至少保留一条明细记录')
  }
}

// 处理对话框关闭
const handleDialogClose = () => {
  // 重置表单
  if (outboundFormRef.value) {
    outboundFormRef.value.resetFields()
  }
}

// 处理保存出库
const handleSaveOutbound = async () => {
  if (!outboundFormRef.value) return
  
  try {
    const valid = await outboundFormRef.value.validate()
    if (valid) {
      dialogLoading.value = true
      
      // 检查明细是否完整
      const hasEmptyMaterial = outboundForm.details.some(item => !item.materialId)
      if (hasEmptyMaterial) {
        ElMessage.warning('请选择完整的物资信息')
        return
      }
      
      // 检查库存是否充足
      const hasInsufficientStock = outboundForm.details.some(item => item.quantity > item.stockQuantity)
      if (hasInsufficientStock) {
        ElMessage.warning('部分物资库存不足，请调整出库数量')
        return
      }
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      if (outboundForm.id) {
        // 编辑出库记录
        console.log('编辑出库记录:', outboundForm)
        ElMessage.success('出库记录编辑成功')
      } else {
        // 新增出库记录
        console.log('新增出库记录:', outboundForm)
        ElMessage.success('出库记录新增成功')
      }
      
      // 关闭对话框
      dialogVisible.value = false
      
      // 刷新出库记录
      fetchOutboundRecords()
    }
  } catch (error) {
    console.error('保存出库记录失败:', error)
    ElMessage.error('保存出库记录失败')
  } finally {
    dialogLoading.value = false
  }
}

// 分页相关
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  fetchOutboundRecords()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  fetchOutboundRecords()
}

// 页面挂载时获取出库记录
onMounted(() => {
  fetchOutboundRecords()
})

// 选择的记录
const selectedRecords = ref([])
</script>

<style scoped>
.inventory-out-container {
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
  .inventory-out-container {
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

/* 库存状态样式 */
.stock-normal {
  color: #67c23a;
}

.stock-out {
  color: #f56c6c;
  font-weight: bold;
}
</style>