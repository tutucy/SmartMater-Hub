<template>
  <div class="requirement-list-container">
    <h2 class="page-title">采购需求管理</h2>
    
    <el-card shadow="hover" class="requirement-card">
      <template #header>
        <div class="card-header">
          <span>需求列表</span>
          <el-button type="primary" @click="handleAddRequirement" icon="Plus">
            新增需求
          </el-button>
        </div>
      </template>
      
      <!-- 需求搜索 -->
      <div class="requirement-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索需求编号、需求名称或申请人"
          clearable
          prefix-icon="Search"
          @keyup.enter="fetchRequirements"
          style="width: 350px; margin-right: 10px;"
        />
        <el-select
          v-model="searchStatus"
          placeholder="选择需求状态"
          clearable
          style="width: 150px; margin-right: 10px;"
        >
          <el-option label="全部状态" value="" />
          <el-option label="待审批" value="pending" />
          <el-option label="已批准" value="approved" />
          <el-option label="已拒绝" value="rejected" />
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
        <el-button type="primary" @click="fetchRequirements" icon="RefreshRight">
          刷新
        </el-button>
      </div>
      
      <!-- 需求列表 -->
      <div class="requirement-table-container">
        <el-table
          v-loading="loading"
          :data="requirements"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="reqNo" label="需求编号" width="180" />
          <el-table-column prop="reqName" label="需求名称" min-width="200" />
          <el-table-column prop="applicant" label="申请人" width="120" />
          <el-table-column prop="applicantDept" label="申请部门" width="150" />
          <el-table-column prop="reqDate" label="申请日期" width="150" />
          <el-table-column prop="estimatedDate" label="预计使用日期" width="150" />
          <el-table-column prop="totalQuantity" label="总数量" width="100" />
          <el-table-column prop="totalAmount" label="总金额" width="150" :formatter="currencyFormatter" />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-tag :type="getStatusTagType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="approver" label="审批人" width="120" />
          <el-table-column prop="approveTime" label="审批时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleViewRequirement(scope.row)" icon="View">
                查看
              </el-button>
              <el-button type="success" size="small" @click="handleApproveRequirement(scope.row)" :disabled="scope.row.status !== 'pending'" icon="Check">
                审批
              </el-button>
              <el-button type="danger" size="small" @click="handleDeleteRequirement(scope.row)" :disabled="scope.row.status === 'approved' || scope.row.status === 'completed'" icon="Delete">
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
    
    <!-- 需求详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="requirementFormRef"
        :model="requirementForm"
        :rules="requirementRules"
        label-position="right"
        label-width="120px"
      >
        <el-form-item label="需求编号" prop="reqNo">
          <el-input
            v-model="requirementForm.reqNo"
            placeholder="请输入需求编号"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="需求名称" prop="reqName">
          <el-input
            v-model="requirementForm.reqName"
            placeholder="请输入需求名称"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="申请部门" prop="applicantDept">
          <el-select
            v-model="requirementForm.applicantDept"
            placeholder="请选择申请部门"
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
        
        <el-form-item label="申请人" prop="applicant">
          <el-input
            v-model="requirementForm.applicant"
            placeholder="请输入申请人"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="申请日期" prop="reqDate">
          <el-date-picker
            v-model="requirementForm.reqDate"
            type="date"
            placeholder="请选择申请日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item label="预计使用日期" prop="estimatedDate">
          <el-date-picker
            v-model="requirementForm.estimatedDate"
            type="date"
            placeholder="请选择预计使用日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item label="需求原因" prop="reqReason">
          <el-input
            v-model="requirementForm.reqReason"
            placeholder="请输入需求原因"
            type="textarea"
            rows="3"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input
            v-model="requirementForm.remark"
            placeholder="请输入备注"
            type="textarea"
            rows="2"
            clearable
          />
        </el-form-item>
        
        <el-divider>需求明细</el-divider>
        
        <el-table
          :data="requirementForm.details"
          style="width: 100%"
          border
          :row-key="row => row.id"
        >
          <el-table-column prop="materialName" label="物资名称" width="180">
            <template #default="scope">
              <el-select
                v-model="scope.row.materialId"
                placeholder="选择物资"
                filterable
                @change="handleMaterialChange(scope.$index)"
              >
                <el-option
                  v-for="material in materials"
                  :key="material.id"
                  :label="material.name"
                  :value="material.id"
                >
                  {{ material.name }} - {{ material.specification }}
                </el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="specification" label="规格型号" width="150" />
          <el-table-column prop="quantity" label="数量" width="100">
            <template #default="scope">
              <el-input-number
                v-model="scope.row.quantity"
                :min="1"
                @change="handleQuantityChange"
              />
            </template>
          </el-table-column>
          <el-table-column prop="unit" label="单位" width="80" />
          <el-table-column prop="estimatedPrice" label="预计单价" width="120">
            <template #default="scope">
              <el-input-number
                v-model="scope.row.estimatedPrice"
                :min="0"
                :precision="2"
                @change="handlePriceChange"
              />
            </template>
          </el-table-column>
          <el-table-column prop="estimatedAmount" label="预计金额" width="120" align="right">
            <template #default="scope">
              ¥{{ (scope.row.quantity * scope.row.estimatedPrice).toFixed(2) }}
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
          <el-button type="primary" :loading="dialogLoading" @click="handleSaveRequirement">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import request from '../../services/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const requirements = ref([])
const materials = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const searchStatus = ref('')
const dateRange = ref([])
const selectedRequirements = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增需求')
const dialogLoading = ref(false)
const requirementFormRef = ref(null)

// 需求表单数据
const requirementForm = reactive({
  id: '',
  reqNo: '',
  reqName: '',
  applicantDept: '',
  applicant: '',
  reqDate: new Date().toISOString().split('T')[0],
  estimatedDate: '',
  reqReason: '',
  totalQuantity: 0,
  totalAmount: 0,
  status: 'pending',
  approver: '',
  approveTime: '',
  remark: '',
  details: [
    {
      id: 1,
      materialId: '',
      materialName: '',
      specification: '',
      unit: '',
      quantity: 1,
      estimatedPrice: 0,
      estimatedAmount: 0
    }
  ]
})

// 需求表单验证规则
const requirementRules = {
  reqNo: [
    { required: true, message: '请输入需求编号', trigger: 'blur' }
  ],
  reqName: [
    { required: true, message: '请输入需求名称', trigger: 'blur' }
  ],
  applicantDept: [
    { required: true, message: '请选择申请部门', trigger: 'change' }
  ],
  applicant: [
    { required: true, message: '请输入申请人', trigger: 'blur' }
  ],
  reqDate: [
    { required: true, message: '请选择申请日期', trigger: 'change' }
  ],
  estimatedDate: [
    { required: true, message: '请选择预计使用日期', trigger: 'change' }
  ],
  reqReason: [
    { required: true, message: '请输入需求原因', trigger: 'blur' }
  ]
}

// 总数量计算
const totalQuantity = computed(() => {
  return requirementForm.details.reduce((sum, detail) => sum + (detail.quantity || 0), 0)
})

// 总金额计算
const totalAmount = computed(() => {
  return requirementForm.details.reduce((sum, detail) => sum + (detail.quantity || 0) * (detail.estimatedPrice || 0), 0)
})

// 获取需求列表
const fetchRequirements = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟需求列表数据
    const mockRequirements = [
      {
        id: 1,
        reqNo: 'REQ202401001',
        reqName: '办公用品采购需求',
        applicantDept: '行政部',
        applicant: '张三',
        reqDate: '2024-01-15',
        estimatedDate: '2024-01-20',
        totalQuantity: 150,
        totalAmount: 8500,
        status: 'approved',
        approver: '李四',
        approveTime: '2024-01-16 14:30:00',
        remark: '月度办公用品采购'
      },
      {
        id: 2,
        reqNo: 'REQ202401002',
        reqName: '生产物资采购需求',
        applicantDept: '生产部',
        applicant: '王五',
        reqDate: '2024-01-17',
        estimatedDate: '2024-01-25',
        totalQuantity: 500,
        totalAmount: 25000,
        status: 'pending',
        approver: '',
        approveTime: '',
        remark: '生产线物资补充'
      },
      {
        id: 3,
        reqNo: 'REQ202401003',
        reqName: 'IT设备采购需求',
        applicantDept: '技术部',
        applicant: '赵六',
        reqDate: '2024-01-18',
        estimatedDate: '2024-02-05',
        totalQuantity: 20,
        totalAmount: 150000,
        status: 'completed',
        approver: '李四',
        approveTime: '2024-01-19 10:15:00',
        remark: '新员工设备采购'
      }
    ]
    
    // 模拟物资数据
    materials.value = [
      { id: 1, name: '打印纸', specification: 'A4 70g', unit: '箱' },
      { id: 2, name: '笔记本电脑', specification: 'ThinkPad X1', unit: '台' },
      { id: 3, name: '办公椅', specification: '人体工学', unit: '把' },
      { id: 4, name: '螺丝', specification: 'M6×20', unit: '个' },
      { id: 5, name: '鼠标', specification: '无线', unit: '个' }
    ]
    
    // 模拟搜索功能
    let filteredRequirements = mockRequirements
    
    // 根据关键词过滤
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filteredRequirements = filteredRequirements.filter(req => 
        req.reqNo.toLowerCase().includes(keyword) ||
        req.reqName.toLowerCase().includes(keyword) ||
        req.applicant.toLowerCase().includes(keyword)
      )
    }
    
    // 根据状态过滤
    if (searchStatus.value) {
      filteredRequirements = filteredRequirements.filter(req => req.status === searchStatus.value)
    }
    
    requirements.value = filteredRequirements
    total.value = filteredRequirements.length
  } catch (error) {
    console.error('获取需求列表失败:', error)
    ElMessage.error('获取需求列表失败')
  } finally {
    loading.value = false
  }
}



// 处理添加需求
const handleAddRequirement = () => {
  dialogTitle.value = '新增需求'
  // 重置表单
  if (requirementFormRef.value) {
    requirementFormRef.value.resetFields()
  }
  // 重置表单数据
  Object.assign(requirementForm, {
    id: '',
    reqNo: '',
    reqName: '',
    applicantDept: '',
    applicant: '',
    reqDate: new Date().toISOString().split('T')[0],
    estimatedDate: '',
    reqReason: '',
    totalQuantity: 0,
    totalAmount: 0,
    status: 'pending',
    approver: '',
    approveTime: '',
    remark: '',
    details: [
      {
        id: Date.now(),
        materialId: '',
        materialName: '',
        specification: '',
        unit: '',
        quantity: 1,
        estimatedPrice: 0,
        estimatedAmount: 0
      }
    ]
  })
  dialogVisible.value = true
}

// 处理查看需求
const handleViewRequirement = (row) => {
  dialogTitle.value = '查看需求'
  // 填充表单数据
  Object.assign(requirementForm, { ...row })
  dialogVisible.value = true
}

// 处理保存需求
const handleSaveRequirement = async () => {
  if (!requirementFormRef.value) return
  
  try {
    const valid = await requirementFormRef.value.validate()
    if (valid) {
      dialogLoading.value = true
      
      // 准备需求数据
      const requirementData = { ...requirementForm }
      requirementData.totalQuantity = totalQuantity.value
      requirementData.totalAmount = totalAmount.value
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      if (requirementForm.id) {
        // 编辑需求
        console.log('编辑需求:', requirementData)
        ElMessage.success('需求编辑成功')
      } else {
        // 添加需求
        console.log('添加需求:', requirementData)
        ElMessage.success('需求添加成功')
      }
      
      // 关闭对话框
      dialogVisible.value = false
      
      // 刷新需求列表
      fetchRequirements()
    }
  } catch (error) {
    console.error('保存需求失败:', error)
    ElMessage.error('保存需求失败')
  } finally {
    dialogLoading.value = false
  }
}

// 处理审批需求
const handleApproveRequirement = (row) => {
  ElMessageBox.confirm(
    `确定要审批需求 "${row.reqName}" 吗？`,
    '审批需求',
    {
      confirmButtonText: '批准',
      cancelButtonText: '拒绝',
      type: 'warning',
      distinguishCancelAndClose: true,
      closeOnClickModal: false,
      closeOnPressEscape: false
    }
  )
  .then(() => {
    // 模拟API调用
    return new Promise(resolve => setTimeout(resolve, 800))
  })
  .then(() => {
    console.log('批准需求:', row)
    ElMessage.success('需求审批通过')
    // 刷新需求列表
    fetchRequirements()
  })
  .catch(action => {
    if (action === 'cancel') {
      // 拒绝审批
      return ElMessageBox.prompt(
        '请输入拒绝原因:',
        '拒绝需求',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPlaceholder: '请输入拒绝原因',
          type: 'warning'
        }
      )
    }
    return Promise.reject()
  })
  .then(({ value }) => {
    // 模拟API调用
    return new Promise(resolve => setTimeout(resolve, 800))
  })
  .then(() => {
    console.log('拒绝需求:', row)
    ElMessage.success('需求已拒绝')
    // 刷新需求列表
    fetchRequirements()
  })
  .catch(() => {
    // 取消操作
  })
}

// 处理删除需求
const handleDeleteRequirement = (row) => {
  ElMessageBox.confirm(
    `确定要删除需求 "${row.reqName}" 吗？删除后无法恢复。`,
    '删除需求',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'danger'
    }
  )
  .then(() => {
    // 模拟API调用
    return new Promise(resolve => setTimeout(resolve, 800))
  })
  .then(() => {
    console.log('删除需求:', row)
    ElMessage.success('需求删除成功')
    // 刷新需求列表
    fetchRequirements()
  })
  .catch(() => {
    // 取消删除
  })
}

// 处理对话框关闭
const handleDialogClose = () => {
  // 重置表单
  if (requirementFormRef.value) {
    requirementFormRef.value.resetFields()
  }
}

// 处理添加明细
const handleAddDetail = () => {
  requirementForm.details.push({
    id: Date.now(),
    materialId: '',
    materialName: '',
    specification: '',
    unit: '',
    quantity: 1,
    estimatedPrice: 0,
    estimatedAmount: 0
  })
}

// 处理删除明细
const handleRemoveDetail = (index) => {
  if (requirementForm.details.length > 1) {
    requirementForm.details.splice(index, 1)
  } else {
    ElMessage.warning('至少保留一条明细')
  }
}

// 处理物资变更
const handleMaterialChange = (index) => {
  const detail = requirementForm.details[index]
  const material = materials.value.find(m => m.id === detail.materialId)
  if (material) {
    detail.materialName = material.name
    detail.specification = material.specification
    detail.unit = material.unit
  }
}

// 处理数量变更
const handleQuantityChange = () => {
  // 数量变化时自动更新总金额
  requirementForm.totalQuantity = totalQuantity.value
  requirementForm.totalAmount = totalAmount.value
}

// 处理价格变更
const handlePriceChange = () => {
  // 价格变化时自动更新总金额
  requirementForm.totalQuantity = totalQuantity.value
  requirementForm.totalAmount = totalAmount.value
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger',
    'completed': 'info'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    'pending': '待审批',
    'approved': '已批准',
    'rejected': '已拒绝',
    'completed': '已完成'
  }
  return textMap[status] || status
}

// 货币格式化
const currencyFormatter = (row, column, cellValue) => {
  return '¥' + cellValue.toLocaleString()
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedRequirements.value = selection
}

// 分页相关
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  fetchRequirements()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  fetchRequirements()
}

// 页面挂载时获取需求列表
onMounted(() => {
  fetchRequirements()
})
</script>

<style scoped>
.requirement-list-container {
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

.requirement-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.requirement-search {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
  flex-wrap: wrap;
}

.requirement-table-container {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

.detail-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
}

.total-info {
  font-weight: bold;
  color: #409EFF;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>