<template>
  <div class="login-container">
    <el-card shadow="hover" class="login-card">
      <template #header>
        <div class="card-header">
          <h2>智能物资管理系统</h2>
          <p>用户登录</p>
        </div>
      </template>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-position="top"
        class="login-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
          <el-link type="primary" :underline="false" class="forgot-password">忘记密码？</el-link>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" class="login-btn">登录</el-button>
          <el-button type="default" @click="$router.push('/register')" class="register-btn">立即注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import request from '../../services/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  rememberMe: false
})

// 登录表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    const valid = await loginFormRef.value.validate()
    if (valid) {
      loading.value = true
      
      // 调用登录API
      // 由于我们使用的是mock数据，这里模拟API调用
      // 实际项目中应该使用request.post('/login', loginForm)
      
      // 模拟登录请求延迟
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      // 模拟登录成功，实际项目中应该调用真实API
      const mockToken = 'mock-jwt-token-' + Date.now()
      const mockUserInfo = {
        id: 1,
        username: loginForm.username,
        name: '管理员',
        role: 'admin',
        email: 'admin@example.com',
        phone: '13800138000',
        department: '技术部',
        position: '系统管理员'
      }
      
      // 保存登录状态到Pinia
      userStore.loginSuccess(mockUserInfo, mockToken)
      
      // 显示登录成功消息
      ElMessage.success('登录成功')
      
      // 登录成功，跳转到首页
      router.push('/home')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 450px;
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

.login-form {
  padding: 0 20px 20px 20px;
}

.login-form :deep(.el-form-item__label) {
  color: #333;
  font-weight: 500;
}

.login-btn {
  width: 100%;
  margin-bottom: 10px;
}

.register-btn {
  width: 100%;
}

.forgot-password {
  float: right;
}
</style>
