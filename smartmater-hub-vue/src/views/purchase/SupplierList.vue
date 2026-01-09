<template>
  <div class="supplier-list-container">
    <h2 class="page-title">供应商管理</h2>
    
    <el-card shadow="hover" class="supplier-card">
      <template #header>
        <div class="card-header">
          <span>供应商列表</span>
          <el-button type="primary" @click="handleAddSupplier" icon="Plus">
            新增供应商
          </el-button>
        </div>
      </template>
      
      <!-- 供应商搜索 -->
      <div class="supplier-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索供应商名称、联系人或电话"
          clearable
          prefix-icon="Search"
          @keyup.enter="fetchSuppliers"
          style="width: 350px; margin-right: 10px;"
        />
        <el-select
          v-model="searchCreditLevel"
          placeholder="选择信用等级"
          clearable
          style="width: 150px; margin-right: 10px;"
        >
          <el-option label="全部等级" value="" />
          <el-option label="A级" value="A" />
          <el-option label="B级" value="B" />
          <el-option label="C级" value="C" />
          <el-option label="D级" value="D" />
        </el-select>
        <el-button type="primary" @click="fetchSuppliers" icon="RefreshRight">
          刷新
        </el-button>
      </div>
      
      <!-- 供应商列表 -->
      <div class="supplier-table-container">
        <el-table
          v-loading="loading"
          :data="suppliers"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="supplierNo" label="供应商编号" width="150" />
          <el-table-column prop="supplierName" label="供应商名称" min-width="200" />
          <el-table-column prop="contactPerson" label="联系人" width="120" />
          <el-table-column prop="contactPhone" label="联系电话" width="150" />
          <el-table-column prop="email" label="邮箱" width="180" />
          <el-table-column prop="address" label="地址" min-width="200" />
          <el-table-column prop="creditLevel" label="信用等级" width="120">
            <template #default="scope">
              <el-tag :type="getCreditLevelTagType(scope.row.creditLevel)">
                {{ scope.row.creditLevel }}级
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="totalScore" label="综合评分" width="120">
            <template #default="scope">
              <el-rate
                v-model="scope.row.totalScore"
                :max="5"
                :disabled="true"
                show-score
              />
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-switch
                v-model="scope.row.status"
                active-value="1"
                inactive-value="0"
                @change="handleStatusChange(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleViewSupplier(scope.row)" icon="View">
                查看
              </el-button>
              <el-button type="success" size="small" @click="handleEditSupplier(scope.row)" icon="Edit">
                编辑
              </el-button>
              <el-button type="warning" size="small" @click="handleEvaluateSupplier(scope.row)" icon="Star">
                评价
              </el-button>
              <el-button type="danger" size="small" @click="handleDeleteSupplier(scope.row)" icon="Delete">
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
    
    <!-- 添加/编辑供应商对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="supplierFormRef"
        :model="supplierForm"
        :rules="supplierRules"
        label-position="right"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商编号" prop="supplierNo">
              <el-input
                v-model="supplierForm.supplierNo"
                placeholder="请输入供应商编号"
                clearable
                :disabled="!!supplierForm.id"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商名称" prop="supplierName">
              <el-input
                v-model="supplierForm.supplierName"
                placeholder="请输入供应商名称"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input
                v-model="supplierForm.contactPerson"
                placeholder="请输入联系人"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input
                v-model="supplierForm.contactPhone"
                placeholder="请输入联系电话"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="supplierForm.email"
                type="email"
                placeholder="请输入邮箱"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="信用等级" prop="creditLevel">
              <el-select
                v-model="supplierForm.creditLevel"
                placeholder="请选择信用等级"
                clearable
              >
                <el-option label="A级" value="A" />
                <el-option label="B级" value="B" />
                <el-option label="C级" value="C" />
                <el-option label="D级" value="D" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="地址" prop="address">
              <el-input
                v-model="supplierForm.address"
                placeholder="请输入地址"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="开户行" prop="bankName">
              <el-input
                v-model="supplierForm.bankName"
                placeholder="请输入开户行"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="银行账号" prop="bankAccount">
              <el-input
                v-model="supplierForm.bankAccount"
                placeholder="请输入银行账号"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="税号" prop="taxNumber">
              <el-input
                v-model="supplierForm.taxNumber"
                placeholder="请输入税号"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input
                v-model="supplierForm.remark"
                placeholder="请输入备注"
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
          <el-button type="primary" :loading="dialogLoading" @click="handleSaveSupplier">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 供应商评价对话框 -->
    <el-dialog
      v-model="evaluateDialogVisible"
      title="供应商评价"
      width="600px"
    >
      <el-form
        ref="evaluateFormRef"
        :model="evaluateForm"
        :rules="evaluateRules"
        label-position="right"
        label-width="120px"
      >
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input v-model="evaluateForm.supplierName" disabled />
        </el-form-item>
        
        <el-form-item label="产品质量" prop="productQuality">
          <el-rate
            v-model="evaluateForm.productQuality"
            :max="5"
            show-score
            text-color="#ff9900"
          />
        </el-form-item>
        
        <el-form-item label="交货及时性" prop="deliveryTimeliness">
          <el-rate
            v-model="evaluateForm.deliveryTimeliness"
            :max="5"
            show-score
            text-color="#ff9900"
          />
        </el-form-item>
        
        <el-form-item label="服务态度" prop="serviceAttitude">
          <el-rate
            v-model="evaluateForm.serviceAttitude"
            :max="5"
            show-score
            text-color="#ff9900"
          />
        </el-form-item>
        
        <el-form-item label="综合评价" prop="totalScore">
          <el-rate
            v-model="evaluateForm.totalScore"
            :max="5"
            show-score
            text-color="#ff9900"
          />
        </el-form-item>
        
        <el-form-item label="评价内容" prop="content">
          <el-input
            v-model="evaluateForm.content"
            placeholder="请输入评价内容"
            type="textarea"
            rows="4"
            clearable
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="evaluateDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="evaluateDialogLoading" @click="handleSaveEvaluate">
            保存评价
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

const suppliers = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const searchCreditLevel = ref('')
const selectedSuppliers = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增供应商')
const dialogLoading = ref(false)
const supplierFormRef = ref(null)

// 评价对话框相关
const evaluateDialogVisible = ref(false)
const evaluateDialogLoading = ref(false)
const evaluateFormRef = ref(null)

// 供应商表单数据
const supplierForm = reactive({
  id: '',
  supplierNo: '',
  supplierName: '',
  contactPerson: '',
  contactPhone: '',
  email: '',
  address: '',
  bankName: '',
  bankAccount: '',
  taxNumber: '',
  creditLevel: 'A',
  totalScore: 4.5,
  status: '1',
  createTime: '',
  remark: ''
})

// 供应商表单验证规则
const supplierRules = {
  supplierNo: [
    { required: true, message: '请输入供应商编号', trigger: 'blur' }
  ],
  supplierName: [
    { required: true, message: '请输入供应商名称', trigger: 'blur' }
  ],
  contactPerson: [
    { required: true, message: '请输入联系人', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  creditLevel: [
    { required: true, message: '请选择信用等级', trigger: 'change' }
  ]
}

// 评价表单数据
const evaluateForm = reactive({
  id: '',
  supplierId: '',
  supplierName: '',
  productQuality: 4,
  deliveryTimeliness: 4,
  serviceAttitude: 4,
  totalScore: 4,
  content: ''
})

// 评价表单验证规则
const evaluateRules = {
  productQuality: [
    { required: true, message: '请评价产品质量', trigger: 'change' }
  ],
  deliveryTimeliness: [
    { required: true, message: '请评价交货及时性', trigger: 'change' }
  ],
  serviceAttitude: [
    { required: true, message: '请评价服务态度', trigger: 'change' }
  ],
  totalScore: [
    { required: true, message: '请给出综合评价', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入评价内容', trigger: 'blur' },
    { min: 10, max: 500, message: '评价内容长度在 10 到 500 个字符', trigger: 'blur' }
  ]
}

// 获取供应商列表
const fetchSuppliers = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟供应商列表数据
    const mockSuppliers = [
      {
        id: 1,
        supplierNo: 'SP202401001',
        supplierName: '供应商A',
        contactPerson: '张三',
        contactPhone: '13800138000',
        email: 'supplierA@example.com',
        address: '北京市朝阳区XXX路123号',
        bankName: '中国工商银行北京分行',
        bankAccount: '6222021234567890123',
        taxNumber: '91110000XXXXXXXXXX',
        creditLevel: 'A',
        totalScore: 4.8,
        status: '1',
        createTime: '2024-01-01 10:00:00'
      },
      {
        id: 2,
        supplierNo: 'SP202401002',
        supplierName: '供应商B',
        contactPerson: '李四',
        contactPhone: '13800138001',
        email: 'supplierB@example.com',
        address: '上海市浦东新区XXX路456号',
        bankName: '中国建设银行上海分行',
        bankAccount: '6227001234567890123',
        taxNumber: '91310000XXXXXXXXXX',
        creditLevel: 'B',
        totalScore: 4.2,
        status: '1',
        createTime: '2024-01-02 14:30:00'
      },
      {
        id: 3,
        supplierNo: 'SP202401003',
        supplierName: '供应商C',
        contactPerson: '王五',
        contactPhone: '13800138002',
        email: 'supplierC@example.com',
        address: '广州市天河区XXX路789号',
        bankName: '中国农业银行广州分行',
        bankAccount: '6228481234567890123',
        taxNumber: '91440101XXXXXXXXXX',
        creditLevel: 'A',
        totalScore: 4.5,
        status: '1',
        createTime: '2024-01-03 09:15:00'
      }
    ]
    
    // 模拟搜索功能
    let filteredSuppliers = mockSuppliers
    
    // 根据关键词过滤
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filteredSuppliers = filteredSuppliers.filter(supplier => 
        supplier.supplierNo.toLowerCase().includes(keyword) ||
        supplier.supplierName.toLowerCase().includes(keyword) ||
        supplier.contactPerson.toLowerCase().includes(keyword) ||
        supplier.contactPhone.includes(keyword)
      )
    }
    
    // 根据信用等级过滤
    if (searchCreditLevel.value) {
      filteredSuppliers = filteredSuppliers.filter(supplier => supplier.creditLevel === searchCreditLevel.value)
    }
    
    suppliers.value = filteredSuppliers
    total.value = filteredSuppliers.length
  } catch (error) {
    console.error('获取供应商列表失败:', error)
    ElMessage.error('获取供应商列表失败')
  } finally {
    loading.value = false
  }
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedSuppliers.value = selection
}

// 处理添加供应商
const handleAddSupplier = () => {
  dialogTitle.value = '新增供应商'
  // 重置表单
  if (supplierFormRef.value) {
    supplierFormRef.value.resetFields()
  }
  // 重置表单数据
  Object.assign(supplierForm, {
    id: '',
    supplierNo: '',
    supplierName: '',
    contactPerson: '',
    contactPhone: '',
    email: '',
    address: '',
    bankName: '',
    bankAccount: '',
    taxNumber: '',
    creditLevel: 'A',
    totalScore: 4.5,
    status: '1',
    createTime: '',
    remark: ''
  })
  dialogVisible.value = true
}

// 处理查看供应商
const handleViewSupplier = (row) => {
  dialogTitle.value = '查看供应商'
  // 填充表单数据
  Object.assign(supplierForm, { ...row })
  dialogVisible.value = true
}

// 处理编辑供应商
const handleEditSupplier = (row) => {
  dialogTitle.value = '编辑供应商'
  // 填充表单数据
  Object.assign(supplierForm, { ...row })
  dialogVisible.value = true
}

// 处理保存供应商
const handleSaveSupplier = async () => {
  if (!supplierFormRef.value) return
  
  try {
    const valid = await supplierFormRef.value.validate()
    if (valid) {
      dialogLoading.value = true
      
      // 准备供应商数据
      const supplierData = { ...supplierForm }
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      if (supplierForm.id) {
        // 编辑供应商
        console.log('编辑供应商:', supplierData)
        ElMessage.success('供应商编辑成功')
      } else {
        // 添加供应商
        console.log('添加供应商:', supplierData)
        ElMessage.success('供应商添加成功')
      }
      
      // 关闭对话框
      dialogVisible.value = false
      
      // 刷新供应商列表
      fetchSuppliers()
    }
  } catch (error) {
    console.error('保存供应商失败:', error)
    ElMessage.error('保存供应商失败')
  } finally {
    dialogLoading.value = false
  }
}

// 处理删除供应商
const handleDeleteSupplier = (row) => {
  ElMessageBox.confirm(
    `确定要删除供应商 "${row.supplierName}" 吗？删除后无法恢复。`,
    '删除供应商',
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
    console.log('删除供应商:', row)
    ElMessage.success('供应商删除成功')
    // 刷新供应商列表
    fetchSuppliers()
  })
  .catch(() => {
    // 取消删除
  })
}

// 处理状态变化
const handleStatusChange = (row) => {
  console.log('供应商状态变化:', row)
  // 调用更新供应商状态API
  // 模拟API调用
  ElMessage.success('供应商状态更新成功')
}

// 处理对话框关闭
const handleDialogClose = () => {
  // 重置表单
  if (supplierFormRef.value) {
    supplierFormRef.value.resetFields()
  }
}

// 处理评价供应商
const handleEvaluateSupplier = (row) => {
  // 重置评价表单
  if (evaluateFormRef.value) {
    evaluateFormRef.value.resetFields()
  }
  // 填充评价表单数据
  Object.assign(evaluateForm, {
    id: '',
    supplierId: row.id,
    supplierName: row.supplierName,
    productQuality: 4,
    deliveryTimeliness: 4,
    serviceAttitude: 4,
    totalScore: 4,
    content: ''
  })
  evaluateDialogVisible.value = true
}

// 处理保存评价
const handleSaveEvaluate = async () => {
  if (!evaluateFormRef.value) return
  
  try {
    const valid = await evaluateFormRef.value.validate()
    if (valid) {
      evaluateDialogLoading.value = true
      
      // 准备评价数据
      const evaluateData = { ...evaluateForm }
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      console.log('保存供应商评价:', evaluateData)
      ElMessage.success('供应商评价保存成功')
      
      // 关闭对话框
      evaluateDialogVisible.value = false
      
      // 刷新供应商列表
      fetchSuppliers()
    }
  } catch (error) {
    console.error('保存供应商评价失败:', error)
    ElMessage.error('保存供应商评价失败')
  } finally {
    evaluateDialogLoading.value = false
  }
}

// 获取信用等级标签类型
const getCreditLevelTagType = (level) => {
  const typeMap = {
    'A': 'success',
    'B': 'primary',
    'C': 'warning',
    'D': 'danger'
  }
  return typeMap[level] || 'info'
}

// 分页相关
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  fetchSuppliers()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  fetchSuppliers()
}

// 页面挂载时获取供应商列表
onMounted(() => {
  fetchSuppliers()
})
</script>

<style scoped>
.supplier-list-container {
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

.supplier-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.supplier-search {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
  flex-wrap: wrap;
}

.supplier-table-container {
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
</style>