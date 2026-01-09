<template>
  <div class="inventory-check-container">
    <h2 class="page-title">库存盘点管理</h2>
    
    <el-card shadow="hover" class="inventory-card">
      <template #header>
        <div class="card-header">
          <span>盘点记录</span>
          <el-button type="primary" @click="handleAddCheckPlan" icon="Plus">
            新增盘点计划
          </el-button>
        </div>
      </template>
      
      <!-- 盘点搜索 -->
      <div class="inventory-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索盘点单号、盘点名称或盘点人"
          clearable
          prefix-icon="Search"
          @keyup.enter="fetchCheckRecords"
          style="width: 350px; margin-right: 10px;"
        />
        <el-select
          v-model="searchStatus"
          placeholder="盘点状态"
          clearable
          style="width: 150px; margin-right: 10px;"
        >
          <el-option label="全部状态" value="" />
          <el-option label="待执行" value="pending" />
          <el-option label="执行中" value="in_progress" />
          <el-option label="已完成" value="completed" />
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
        <el-button type="primary" @click="fetchCheckRecords" icon="RefreshRight">
          刷新
        </el-button>
      </div>
      
      <!-- 盘点列表 -->
      <div class="inventory-table-container">
        <el-table
          v-loading="loading"
          :data="checkRecords"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="checkNo" label="盘点单号" width="180" />
          <el-table-column prop="checkName" label="盘点名称" width="180" />
          <el-table-column prop="checkDate" label="盘点日期" width="180" />
          <el-table-column prop="checkPerson" label="盘点人" width="120" />
          <el-table-column prop="checkScope" label="盘点范围" width="150">
            <template #default="scope">
              <el-tag :type="scope.row.checkScope === 'all' ? 'primary' : 'success'">
                {{ scope.row.checkScope === 'all' ? '全部物资' : '指定分类' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="totalMaterials" label="物资总数" width="120" align="center" />
          <el-table-column prop="differentCount" label="差异数" width="120" align="center">
            <template #default="scope">
              <span v-if="scope.row.differentCount > 0" class="different-count">
                {{ scope.row.differentCount }}
              </span>
              <span v-else>
                {{ scope.row.differentCount }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-tag 
                :type="getStatusColor(scope.row.status)"
              >
                {{ getStatusName(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="250" fixed="right">
            <template #default="scope">
              <el-button 
                type="primary" 
                size="small" 
                @click="handleViewCheck(scope.row)" 
                icon="View"
              >
                查看
              </el-button>
              <el-button 
                v-if="scope.row.status === 'pending'" 
                type="success" 
                size="small" 
                @click="handleStartCheck(scope.row)" 
                icon="Check"
              >
                开始盘点
              </el-button>
              <el-button 
                v-if="scope.row.status === 'in_progress'" 
                type="warning" 
                size="small" 
                @click="handleCompleteCheck(scope.row)" 
                icon="CircleClose"
              >
                完成盘点
              </el-button>
              <el-button 
                v-if="scope.row.status === 'completed'" 
                type="info" 
                size="small" 
                @click="handleProcessResult(scope.row)" 
                icon="DocumentCopy"
                :disabled="scope.row.differentCount === 0"
              >
                处理结果
              </el-button>
              <el-button type="danger" size="small" @click="handleDeleteCheck(scope.row)" icon="Delete">
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
    
    <!-- 新增盘点计划对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="checkFormRef"
        :model="checkForm"
        :rules="checkRules"
        label-position="right"
        label-width="100px"
      >
        <el-form-item label="盘点单号" prop="checkNo">
          <el-input
            v-model="checkForm.checkNo"
            placeholder="系统自动生成"
            readonly
          />
        </el-form-item>
        
        <el-form-item label="盘点名称" prop="checkName">
          <el-input
            v-model="checkForm.checkName"
            placeholder="请输入盘点名称"
            clearable
          />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="盘点日期" prop="checkDate">
              <el-date-picker
                v-model="checkForm.checkDate"
                type="datetime"
                placeholder="请选择盘点日期"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%;"
              />
            </el-form-item>
            
            <el-form-item label="盘点人" prop="checkPerson">
              <el-input
                v-model="checkForm.checkPerson"
                placeholder="请输入盘点人姓名"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="盘点范围" prop="checkScope">
              <el-radio-group v-model="checkForm.checkScope" @change="handleCheckScopeChange">
                <el-radio label="all">全部物资</el-radio>
                <el-radio label="category">指定分类</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item 
              v-if="checkForm.checkScope === 'category'" 
              label="指定分类" 
              prop="categories"
            >
              <el-select
                v-model="checkForm.categories"
                placeholder="请选择盘点分类"
                clearable
                multiple
              >
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                />
              </el-select>
            </el-form-item>
            
            <el-form-item label="备注" prop="remark">
              <el-input
                v-model="checkForm.remark"
                placeholder="请输入备注信息"
                type="textarea"
                rows="3"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="dialogLoading" @click="handleSaveCheck">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 盘点操作对话框 -->
    <el-dialog
      v-model="checkDialogVisible"
      :title="`执行盘点 - ${currentCheck.checkNo}`"
      width="900px"
      @close="handleCheckDialogClose"
    >
      <div v-if="loadingCheckDetails" class="check-loading">
        <el-skeleton :rows="10" animated />
      </div>
      <div v-else>
        <el-table
          :data="checkDetails"
          style="width: 100%"
          border
          stripe
        >
          <el-table-column prop="materialName" label="物资名称" min-width="200" />
          <el-table-column prop="specification" label="型号规格" width="150" />
          <el-table-column prop="unit" label="单位" width="80" />
          <el-table-column prop="systemQuantity" label="系统库存" width="120" align="right" />
          <el-table-column prop="actualQuantity" label="实际数量" width="120">
            <template #default="scope">
              <el-input-number
                v-model="scope.row.actualQuantity"
                :min="0"
                @change="handleActualQuantityChange(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="difference" label="差异数量" width="120" align="right">
            <template #default="scope">
              <span :class="scope.row.difference !== 0 ? 'difference-value' : ''">
                {{ scope.row.difference }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="120" align="right">
            <template #default="scope">
              ¥{{ scope.row.price.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="differenceAmount" label="差异金额" width="120" align="right">
            <template #default="scope">
              <span :class="scope.row.differenceAmount !== 0 ? 'difference-value' : ''">
                ¥{{ scope.row.differenceAmount.toFixed(2) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="150">
            <template #default="scope">
              <el-input
                v-model="scope.row.remark"
                placeholder="请输入备注"
                clearable
              />
            </template>
          </el-table-column>
        </el-table>
        
        <div class="check-summary">
          <div class="summary-item">
            <span>物资总数：</span>
            <span class="summary-value">{{ checkDetails.length }}</span>
          </div>
          <div class="summary-item">
            <span>差异总数：</span>
            <span class="summary-value difference">{{ differenceCount }}</span>
          </div>
          <div class="summary-item">
            <span>差异总金额：</span>
            <span class="summary-value difference">
              ¥{{ differenceTotalAmount.toFixed(2) }}
            </span>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="checkDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="checkLoading" @click="handleSaveCheckDetails">
            保存盘点结果
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

const checkRecords = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const searchStatus = ref('')
const dateRange = ref([])
const categories = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增盘点计划')
const dialogLoading = ref(false)
const checkFormRef = ref(null)

// 盘点操作对话框
const checkDialogVisible = ref(false)
const loadingCheckDetails = ref(false)
const checkLoading = ref(false)
const checkDetails = ref([])
const currentCheck = ref({})

// 盘点表单数据
const checkForm = reactive({
  id: '',
  checkNo: '',
  checkName: '',
  checkDate: new Date().toISOString().slice(0, 19).replace('T', ' '),
  checkPerson: '',
  checkScope: 'all',
  categories: [],
  remark: '',
  status: 'pending',
  totalMaterials: 0,
  differentCount: 0
})

// 盘点表单验证规则
const checkRules = {
  checkName: [
    { required: true, message: '请输入盘点名称', trigger: 'blur' }
  ],
  checkDate: [
    { required: true, message: '请选择盘点日期', trigger: 'change' }
  ],
  checkPerson: [
    { required: true, message: '请输入盘点人姓名', trigger: 'blur' }
  ],
  categories: [
    { required: true, message: '请选择盘点分类', trigger: 'change' }
  ]
}

// 计算差异数量和差异总金额
const differenceCount = computed(() => {
  return checkDetails.value.filter(item => item.difference !== 0).length
})

const differenceTotalAmount = computed(() => {
  return checkDetails.value.reduce((sum, item) => sum + item.differenceAmount, 0)
})

// 获取盘点列表
const fetchCheckRecords = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟盘点记录数据
    const mockRecords = [
      {
        id: 1,
        checkNo: 'PD20240101001',
        checkName: '2024年1月月度盘点',
        checkDate: '2024-01-31 14:00:00',
        checkPerson: '张三',
        checkScope: 'all',
        totalMaterials: 100,
        differentCount: 5,
        status: 'completed',
        createTime: '2024-01-30 10:00:00'
      },
      {
        id: 2,
        checkNo: 'PD20240201002',
        checkName: '办公用品分类盘点',
        checkDate: '2024-02-15 09:30:00',
        checkPerson: '李四',
        checkScope: 'category',
        totalMaterials: 50,
        differentCount: 0,
        status: 'completed',
        createTime: '2024-02-14 16:00:00'
      },
      {
        id: 3,
        checkNo: 'PD20240301003',
        checkName: '3月季度盘点',
        checkDate: '2024-03-31 14:00:00',
        checkPerson: '王五',
        checkScope: 'all',
        totalMaterials: 120,
        differentCount: 0,
        status: 'pending',
        createTime: '2024-03-28 10:00:00'
      },
      {
        id: 4,
        checkNo: 'PD20240401004',
        checkName: '设备类物资盘点',
        checkDate: '2024-04-10 10:00:00',
        checkPerson: '赵六',
        checkScope: 'category',
        totalMaterials: 30,
        differentCount: 2,
        status: 'in_progress',
        createTime: '2024-04-09 14:00:00'
      }
    ]
    
    // 模拟搜索功能
    let filteredRecords = mockRecords
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filteredRecords = mockRecords.filter(record => 
        record.checkNo.toLowerCase().includes(keyword) ||
        record.checkName.toLowerCase().includes(keyword) ||
        record.checkPerson.toLowerCase().includes(keyword)
      )
    }
    
    if (searchStatus.value) {
      filteredRecords = filteredRecords.filter(record => 
        record.status === searchStatus.value
      )
    }
    
    // 模拟日期范围过滤
    if (dateRange.value && dateRange.value.length === 2) {
      filteredRecords = filteredRecords.filter(record => {
        const recordDate = new Date(record.checkDate)
        const startDate = new Date(dateRange.value[0])
        const endDate = new Date(dateRange.value[1])
        return recordDate >= startDate && recordDate <= endDate
      })
    }
    
    checkRecords.value = filteredRecords
    total.value = filteredRecords.length
    
    // 模拟分类数据
    categories.value = [
      { id: 1, name: '办公用品' },
      { id: 2, name: '文具' },
      { id: 3, name: '耗材' },
      { id: 4, name: '设备' },
      { id: 5, name: '电子设备' },
      { id: 6, name: '家具' },
      { id: 7, name: '生产物资' }
    ]
  } catch (error) {
    console.error('获取盘点记录失败:', error)
    ElMessage.error('获取盘点记录失败')
  } finally {
    loading.value = false
  }
}

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    pending: '待执行',
    in_progress: '执行中',
    completed: '已完成'
  }
  return statusMap[status] || status
}

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    pending: 'info',
    in_progress: 'warning',
    completed: 'success'
  }
  return colorMap[status] || 'info'
}

// 格式化金额
const formatAmount = (row, column, cellValue) => {
  return `¥${cellValue.toFixed(2)}`
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedRecords.value = selection
}

// 处理新增盘点计划
const handleAddCheckPlan = () => {
  dialogTitle.value = '新增盘点计划'
  // 重置表单
  if (checkFormRef.value) {
    checkFormRef.value.resetFields()
  }
  // 生成盘点单号
  const now = new Date()
  const checkNo = `PD${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}${String(Math.floor(Math.random() * 1000)).padStart(3, '0')}`
  // 重置表单数据
  Object.assign(checkForm, {
    id: '',
    checkNo: checkNo,
    checkName: '',
    checkDate: now.toISOString().slice(0, 19).replace('T', ' '),
    checkPerson: '',
    checkScope: 'all',
    categories: [],
    remark: '',
    status: 'pending',
    totalMaterials: 0,
    differentCount: 0
  })
  dialogVisible.value = true
}

// 处理盘点范围变化
const handleCheckScopeChange = (value) => {
  console.log('盘点范围变化:', value)
  if (value === 'all') {
    checkForm.categories = []
  }
}

// 处理查看盘点
const handleViewCheck = (row) => {
  console.log('查看盘点:', row)
  ElMessage.info('查看盘点详情功能开发中...')
}

// 处理开始盘点
const handleStartCheck = (row) => {
  console.log('开始盘点:', row)
  // 显示盘点操作对话框
  currentCheck.value = row
  loadCheckDetails(row)
  checkDialogVisible.value = true
}

// 加载盘点详情
const loadCheckDetails = (row) => {
  loadingCheckDetails.value = true
  
  // 模拟API调用
  setTimeout(() => {
    // 模拟盘点明细数据
    const mockDetails = [
      {
        materialId: 1,
        materialName: 'A4打印纸',
        specification: '80g 500张/包',
        unit: '包',
        systemQuantity: 150,
        actualQuantity: 150,
        difference: 0,
        price: 25.5,
        differenceAmount: 0,
        remark: ''
      },
      {
        materialId: 2,
        materialName: '黑色中性笔',
        specification: '0.5mm',
        unit: '支',
        systemQuantity: 300,
        actualQuantity: 295,
        difference: -5,
        price: 2.5,
        differenceAmount: -12.5,
        remark: ''
      },
      {
        materialId: 3,
        materialName: '鼠标',
        specification: '有线 USB',
        unit: '个',
        systemQuantity: 20,
        actualQuantity: 22,
        difference: 2,
        price: 89.0,
        differenceAmount: 178.0,
        remark: ''
      },
      {
        materialId: 4,
        materialName: '键盘',
        specification: '机械键盘 青轴',
        unit: '个',
        systemQuantity: 8,
        actualQuantity: 8,
        difference: 0,
        price: 299.0,
        differenceAmount: 0,
        remark: ''
      },
      {
        materialId: 5,
        materialName: '文件夹',
        specification: 'A4 塑料材质',
        unit: '个',
        systemQuantity: 450,
        actualQuantity: 440,
        difference: -10,
        price: 5.0,
        differenceAmount: -50.0,
        remark: ''
      }
    ]
    
    checkDetails.value = mockDetails
    loadingCheckDetails.value = false
  }, 1000)
}

// 处理实际数量变化
const handleActualQuantityChange = (row) => {
  // 更新差异数量和差异金额
  row.difference = row.actualQuantity - row.systemQuantity
  row.differenceAmount = row.difference * row.price
}

// 处理保存盘点结果
const handleSaveCheckDetails = () => {
  checkLoading.value = true
  
  // 模拟API调用
  setTimeout(() => {
    checkLoading.value = false
    checkDialogVisible.value = false
    ElMessage.success('盘点结果保存成功')
    fetchCheckRecords()
  }, 1000)
}

// 处理完成盘点
const handleCompleteCheck = (row) => {
  ElMessageBox.confirm(
    `确定要完成盘点 "${row.checkNo}" 吗？`,
    '完成盘点',
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
    console.log('完成盘点:', row)
    ElMessage.success('盘点已完成')
    fetchCheckRecords()
  })
  .catch(() => {
    // 取消完成
  })
}

// 处理盘点结果
const handleProcessResult = (row) => {
  ElMessageBox.confirm(
    `确定要处理盘点差异 "${row.checkNo}" 吗？处理后将调整系统库存。`,
    '处理盘点差异',
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
    console.log('处理盘点差异:', row)
    ElMessage.success('盘点差异处理成功')
    fetchCheckRecords()
  })
  .catch(() => {
    // 取消处理
  })
}

// 处理删除盘点
const handleDeleteCheck = (row) => {
  ElMessageBox.confirm(
    `确定要删除盘点记录 "${row.checkNo}" 吗？删除后无法恢复。`,
    '删除盘点记录',
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
    console.log('删除盘点:', row)
    ElMessage.success('盘点记录删除成功')
    fetchCheckRecords()
  })
  .catch(() => {
    // 取消删除
  })
}

// 处理对话框关闭
const handleDialogClose = () => {
  // 重置表单
  if (checkFormRef.value) {
    checkFormRef.value.resetFields()
  }
}

// 处理盘点对话框关闭
const handleCheckDialogClose = () => {
  checkDetails.value = []
  currentCheck.value = {}
}

// 处理保存盘点计划
const handleSaveCheck = async () => {
  if (!checkFormRef.value) return
  
  try {
    const valid = await checkFormRef.value.validate()
    if (valid) {
      dialogLoading.value = true
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      if (checkForm.id) {
        // 编辑盘点计划
        console.log('编辑盘点计划:', checkForm)
        ElMessage.success('盘点计划编辑成功')
      } else {
        // 新增盘点计划
        console.log('新增盘点计划:', checkForm)
        ElMessage.success('盘点计划新增成功')
      }
      
      // 关闭对话框
      dialogVisible.value = false
      
      // 刷新盘点记录
      fetchCheckRecords()
    }
  } catch (error) {
    console.error('保存盘点计划失败:', error)
    ElMessage.error('保存盘点计划失败')
  } finally {
    dialogLoading.value = false
  }
}

// 分页相关
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  fetchCheckRecords()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  fetchCheckRecords()
}

// 页面挂载时获取盘点记录
onMounted(() => {
  fetchCheckRecords()
})

// 选择的记录
const selectedRecords = ref([])
</script>

<style scoped>
.inventory-check-container {
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
}

.inventory-table-container {
  margin-bottom: 20px;
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

/* 差异数量样式 */
.different-count {
  color: #f56c6c;
  font-weight: bold;
}

.difference-value {
  color: #f56c6c;
  font-weight: bold;
}

/* 盘点操作对话框样式 */
.check-loading {
  margin: 20px 0;
}

.check-summary {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 30px;
  margin-top: 20px;
  padding: 15px;
  background-color: #f0f2f5;
  border-radius: 4px;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
}

.summary-value {
  font-weight: bold;
  color: #333;
}

.summary-value.difference {
  color: #f56c6c;
}
</style>