<template>
  <div class="user-profile-container">
    <h2 class="page-title">个人信息</h2>
    
    <el-card shadow="hover" class="profile-card">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
        </div>
      </template>
      
      <el-form
        ref="profileFormRef"
        :model="userInfo"
        :rules="profileRules"
        label-position="right"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="userInfo.username"
                placeholder="请输入用户名"
                readonly
              />
            </el-form-item>
            
            <el-form-item label="姓名" prop="name">
              <el-input
                v-model="userInfo.name"
                placeholder="请输入姓名"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="userInfo.email"
                type="email"
                placeholder="请输入邮箱"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="电话" prop="phone">
              <el-input
                v-model="userInfo.phone"
                placeholder="请输入电话"
                clearable
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="部门" prop="department">
              <el-input
                v-model="userInfo.department"
                placeholder="请输入部门"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="职位" prop="position">
              <el-input
                v-model="userInfo.position"
                placeholder="请输入职位"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="角色" prop="role">
              <el-input
                v-model="userInfo.role"
                placeholder="请输入角色"
                readonly
              />
            </el-form-item>
            
            <el-form-item label="最后登录时间" prop="lastLoginTime">
              <el-input
                v-model="userInfo.lastLoginTime"
                placeholder="最后登录时间"
                readonly
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider />
        
        <h3 class="section-title">修改密码</h3>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入原密码"
                clearable
                show-password
              />
            </el-form-item>
            
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码"
                clearable
                show-password
              />
            </el-form-item>
            
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请确认新密码"
                clearable
                show-password
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <div class="form-actions">
        <el-button type="primary" :loading="saving" @click="handleSaveProfile">
          保存信息
        </el-button>
        <el-button type="success" :loading="updatingPassword" @click="handleUpdatePassword">
          更新密码
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../services/api'
import { ElMessage } from 'element-plus'

const profileFormRef = ref(null)
const saving = ref(false)
const updatingPassword = ref(false)

// 用户信息
const userInfo = reactive({
  username: 'admin',
  name: '管理员',
  email: 'admin@example.com',
  phone: '13800138000',
  department: '技术部',
  position: '系统管理员',
  role: 'admin',
  lastLoginTime: '2024-01-01 10:00:00'
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const profileRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请输入部门', trigger: 'blur' }
  ],
  position: [
    { required: true, message: '请输入职位', trigger: 'blur' }
  ]
}

// 密码验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 验证确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 保存个人信息
const handleSaveProfile = async () => {
  if (!profileFormRef.value) return
  
  try {
    const valid = await profileFormRef.value.validate()
    if (valid) {
      saving.value = true
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      console.log('保存个人信息:', userInfo)
      ElMessage.success('个人信息保存成功')
    }
  } catch (error) {
    console.error('保存个人信息失败:', error)
    ElMessage.error('保存个人信息失败')
  } finally {
    saving.value = false
  }
}

// 更新密码
const handleUpdatePassword = async () => {
  if (!profileFormRef.value) return
  
  try {
    // 验证密码表单
    const valid = await profileFormRef.value.validateField(['oldPassword', 'newPassword', 'confirmPassword'])
    if (valid) {
      updatingPassword.value = true
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      console.log('更新密码:', passwordForm)
      ElMessage.success('密码更新成功')
      
      // 清空密码表单
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
    }
  } catch (error) {
    console.error('更新密码失败:', error)
    ElMessage.error('更新密码失败')
  } finally {
    updatingPassword.value = false
  }
}

// 页面挂载时获取用户信息
onMounted(() => {
  // 模拟获取用户信息
  console.log('获取用户信息')
})
</script>

<style scoped>
.user-profile-container {
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

.profile-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>