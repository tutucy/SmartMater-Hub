<template>
  <div class="user-list-container">
    <h2 class="page-title">用户管理</h2>
    
    <el-card shadow="hover" class="user-card">
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <el-button type="primary" @click="handleAddUser" icon="Plus">
            添加用户
          </el-button>
        </div>
      </template>
      
      <!-- 用户搜索 -->
      <div class="user-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户名、姓名或邮箱"
          clearable
          prefix-icon="Search"
          @keyup.enter="fetchUsers"
        />
        <el-button type="primary" @click="fetchUsers" icon="RefreshRight">
          刷新
        </el-button>
      </div>
      
      <!-- 用户列表 -->
      <div class="user-table-container">
        <el-table
          v-loading="loading"
          :data="userList"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" width="150" />
          <el-table-column prop="realName" label="姓名" width="120" />
          <el-table-column prop="email" label="邮箱" width="200" />
          <el-table-column prop="phone" label="电话" width="150" />
          <el-table-column prop="roleId" label="角色ID" width="120">
            <template #default="scope">
              <el-tag :type="scope.row.roleId === 1 ? 'danger' : 'success'">
                {{ scope.row.roleId === 1 ? '管理员' : '普通用户' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
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
              <el-button type="primary" size="small" @click="handleEditUser(scope.row)" icon="Edit">
                编辑
              </el-button>
              <el-button type="success" size="small" @click="handleResetPassword(scope.row)" icon="Key">
                重置密码
              </el-button>
              <el-button type="danger" size="small" @click="handleDeleteUser(scope.row)" icon="Delete">
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
    
    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-position="right"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="userForm.username"
            placeholder="请输入用户名"
            clearable
            :disabled="!!userForm.id"
          />
        </el-form-item>
        
        <el-form-item label="密码" :required="!userForm.id" prop="password">
          <el-input
            v-model="userForm.password"
            type="password"
            placeholder="请输入密码"
            clearable
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认密码" :required="!userForm.id" prop="confirmPassword">
          <el-input
            v-model="userForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            clearable
            show-password
          />
        </el-form-item>
        
        <el-form-item label="姓名" prop="realName">
          <el-input
            v-model="userForm.realName"
            placeholder="请输入姓名"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="userForm.email"
            type="email"
            placeholder="请输入邮箱"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="电话" prop="phone">
          <el-input
            v-model="userForm.phone"
            placeholder="请输入电话"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="角色" prop="roleId">
          <el-select
            v-model="userForm.roleId"
            placeholder="请选择角色"
            clearable
          >
            <el-option label="管理员" :value="1" />
            <el-option label="普通用户" :value="2" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="userForm.status"
            active-value="1"
            inactive-value="0"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="dialogLoading" @click="handleSaveUser">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../services/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const userList = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const searchRole = ref('')
const searchStatus = ref('')
const selectedUsers = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('添加用户')
const dialogLoading = ref(false)
const userFormRef = ref(null)

// 用户表单数据
const userForm = reactive({
  id: '',
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  email: '',
  phone: '',
  roleId: '',
  status: '1'
})

// 验证两次密码是否一致
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== userForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 用户表单验证规则
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  roleId: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    // 调用后端API
    const response = await request.get('/user/list', {
      params: {
        username: searchKeyword.value || undefined,
        realName: searchKeyword.value || undefined,
        roleId: searchRole.value || undefined,
        status: searchStatus.value || undefined
      }
    })
    
    userList.value = response.data || []
    total.value = userList.value.length
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 处理添加用户
const handleAddUser = () => {
  dialogTitle.value = '添加用户'
  // 重置表单
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
  // 重置表单数据
  Object.assign(userForm, {
    id: '',
    username: '',
    password: '',
    confirmPassword: '',
    realName: '',
    email: '',
    phone: '',
    roleId: '',
    status: '1'
  })
  dialogVisible.value = true
}

// 处理编辑用户
const handleEditUser = (row) => {
  dialogTitle.value = '编辑用户'
  // 填充表单数据，排除密码字段
  const { password, confirmPassword, ...userData } = row
  Object.assign(userForm, { ...userData, password: '', confirmPassword: '' })
  dialogVisible.value = true
}

// 处理保存用户
const handleSaveUser = async () => {
  if (!userFormRef.value) return
  
  try {
    const valid = await userFormRef.value.validate()
    if (valid) {
      dialogLoading.value = true
      
      // 准备用户数据，排除确认密码字段
      const userData = { ...userForm }
      delete userData.confirmPassword
      
      if (userForm.id) {
        // 编辑用户
        await request.put('/user/update', userData)
        ElMessage.success('用户编辑成功')
      } else {
        // 添加用户
        await request.post('/user/add', userData)
        ElMessage.success('用户添加成功')
      }
      
      // 关闭对话框
      dialogVisible.value = false
      
      // 刷新用户列表
      fetchUsers()
    }
  } catch (error) {
    console.error('保存用户失败:', error)
    ElMessage.error('保存用户失败')
  } finally {
    dialogLoading.value = false
  }
}

// 处理对话框关闭
const handleDialogClose = () => {
  // 重置表单
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
}

// 处理重置密码
const handleResetPassword = (row) => {
  ElMessageBox.prompt(
    `确定要重置用户 "${row.username}" 的密码吗？`,
    '重置密码',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'password',
      inputPlaceholder: '请输入新密码',
      type: 'warning'
    }
  )
  .then(async ({ value }) => {
    // 调用重置密码API
    await request.put(`/user/${row.id}/password`, { password: value })
    ElMessage.success('密码重置成功')
  })
  .catch(() => {
    // 取消重置
  })
}

// 处理删除用户
const handleDeleteUser = (row) => {
  ElMessageBox.confirm(
    `确定要删除用户 "${row.username}" 吗？删除后无法恢复。`,
    '删除用户',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
  .then(async () => {
    await request.delete(`/user/${row.id}`)
    ElMessage.success('用户删除成功')
    // 刷新用户列表
    fetchUsers()
  })
  .catch(() => {
    // 取消删除
  })
}

// 处理状态变化
const handleStatusChange = (row) => {
  console.log('用户状态变化:', row)
  // 调用更新用户状态API
  // 模拟API调用
  ElMessage.success('用户状态更新成功')
}

// 分页相关
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  fetchUsers()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  fetchUsers()
}

// 页面挂载时获取用户列表
onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.user-list-container {
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

.user-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-search {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
}

.user-search .el-input {
  width: 300px;
}

.user-table-container {
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
