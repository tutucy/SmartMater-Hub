<template>
  <div class="register-container">
    <el-card shadow="hover" class="register-card">
      <template #header>
        <div class="card-header">
          <h2>智能物资管理系统</h2>
          <p>用户注册</p>
        </div>
      </template>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-position="top"
        class="register-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（6-20位）"
            prefix-icon="el-icon-lock"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="el-icon-lock"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item label="姓名" prop="name">
          <el-input
            v-model="registerForm.name"
            placeholder="请输入真实姓名"
            prefix-icon="el-icon-user-filled"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="联系方式" prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号码"
            prefix-icon="el-icon-phone"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="registerForm.email"
            type="email"
            placeholder="请输入邮箱地址"
            prefix-icon="el-icon-message"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="所属部门" prop="department">
          <el-select
            v-model="registerForm.department"
            placeholder="请选择所属部门"
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
        
        <el-form-item label="职位" prop="position">
          <el-input
            v-model="registerForm.position"
            placeholder="请输入职位"
            prefix-icon="el-icon-suitcase"
            clearable
          />
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="registerForm.agreeTerms" prop="agreeTerms">我已阅读并同意 <el-link type="primary">《用户协议》</el-link> 和 <el-link type="primary">《隐私政策》</el-link></el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister" class="register-btn">注册</el-button>
          <el-button type="default" @click="$router.push('/login')" class="login-btn">返回登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../services/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

// 注册表单数据
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  phone: '',
  email: '',
  department: '',
  position: '',
  agreeTerms: false
})

// 注册表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { validator: validateUsernameUnique, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请选择所属部门', trigger: 'change' }
  ],
  position: [
    { required: true, message: '请输入职位', trigger: 'blur' }
  ],
  agreeTerms: [
    { validator: validateAgreeTerms, trigger: 'change' }
  ]
}

// 验证用户名唯一性
const validateUsernameUnique = async (rule, value, callback) => {
  if (!value) {
    return callback()
  }
  
  try {
    // 模拟用户名唯一性校验
    // 实际项目中应该调用API检查用户名是否已存在
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const existingUsernames = ['admin', 'user', 'test']
    if (existingUsernames.includes(value)) {
      callback(new Error('该用户名已被使用'))
    } else {
      callback()
    }
  } catch (error) {
    callback(new Error('用户名校验失败'))
  }
}

// 验证两次密码是否一致
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 验证是否同意条款
const validateAgreeTerms = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请阅读并同意用户协议和隐私政策'))
  } else {
    callback()
  }
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    const valid = await registerFormRef.value.validate()
    if (valid) {
      loading.value = true
      
      // 准备注册数据
      const registerData = {
        username: registerForm.username,
        password: registerForm.password,
        name: registerForm.name,
        phone: registerForm.phone,
        email: registerForm.email,
        department: registerForm.department,
        position: registerForm.position
      }
      
      // 调用注册API
      // 由于我们使用的是mock数据，这里模拟API调用
      // 实际项目中应该使用request.post('/register', registerData)
      
      // 模拟注册请求延迟
      await new Promise(resolve => setTimeout(resolve, 1500))
      
      // 模拟注册成功，实际项目中应该调用真实API
      console.log('注册成功:', registerData)
      
      // 显示注册成功消息
      ElMessage.success('注册成功，请登录')
      
      // 注册成功，跳转到登录页
      router.push({
        path: '/login',
        query: { registered: true }
      })
    }
  } catch (error) {
    console.error('注册失败:', error)
    ElMessage.error('注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 500px;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 24px;
}

.card-header p {
  margin: 0;
  color: #666;
  font-size: 16px;
}

.register-form {
  padding: 0 20px 20px 20px;
}

.register-form :deep(.el-form-item__label) {
  color: #333;
  font-weight: 500;
}

.register-btn {
  width: 100%;
  margin-bottom: 10px;
}

.login-btn {
  width: 100%;
}
</style>
