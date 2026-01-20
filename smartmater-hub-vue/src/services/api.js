import axios from 'axios'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'

// 创建axios实例
const api = axios.create({
  baseURL: '/api', // API基础URL，可根据环境配置
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    
    // 添加token到请求头
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }
    
    return config
  },
  (error) => {
    // 请求错误处理
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    // 统一处理响应数据
    const res = response.data
    
    // 根据业务码处理响应
    if (res.code === 200) {
      // 请求成功
      return res
    } else {
      // 请求失败，显示错误信息
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  (error) => {
    // 处理HTTP错误
    let message = '网络请求失败'
    
    if (error.response) {
      // 服务器返回错误状态码
      const { status, data } = error.response
      
      // 如果后端返回了Result对象，使用Result中的message
      if (data && data.code !== undefined) {
        message = data.message || '请求失败'
      } else {
        switch (status) {
          case 400:
            message = data.message || '请求参数错误'
            break
          case 401:
            message = '未授权，请重新登录'
            // 清除登录状态，跳转到登录页
            const userStore = useUserStore()
            userStore.logout()
            window.location.href = '/login'
            break
          case 403:
            message = '拒绝访问'
            break
          case 404:
            message = '请求的资源不存在'
            break
          case 500:
            message = data.message || '服务器内部错误'
            break
          case 502:
            message = '网关错误'
            break
          case 503:
            message = '服务不可用'
            break
          case 504:
            message = '网关超时'
            break
          default:
            message = `请求失败（${status}）`
        }
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应
      message = '网络连接失败，请检查网络'
    } else {
      // 请求配置错误
      message = error.message
    }
    
    // 显示错误信息
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// 封装常用HTTP方法
const request = {
  // GET请求
  get(url, params = {}) {
    return api.get(url, { params })
  },
  
  // POST请求
  post(url, data = {}) {
    return api.post(url, data)
  },
  
  // PUT请求
  put(url, data = {}) {
    return api.put(url, data)
  },
  
  // DELETE请求
  delete(url, params = {}) {
    return api.delete(url, { params })
  },
  
  // PATCH请求
  patch(url, data = {}) {
    return api.patch(url, data)
  },
  
  // 上传文件
  upload(url, formData) {
    return api.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}

export default request
