import { defineStore } from 'pinia'

// 定义用户状态管理store
export const useUserStore = defineStore('user', {
  // 状态
  state: () => ({
    // 用户信息
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
    // 登录状态
    isLoggedIn: localStorage.getItem('token') !== null,
    // 登录令牌
    token: localStorage.getItem('token') || null,
    // 用户权限列表
    permissions: [],
    // 用户角色
    role: ''
  }),

  // 计算属性
  getters: {
    // 获取用户名
    getUserName: (state) => state.userInfo.name || '',
    // 获取用户角色
    getUserRole: (state) => state.userInfo.role || '',
    // 检查用户是否有某个权限
    hasPermission: (state) => (permission) => {
      return state.permissions.includes(permission)
    }
  },

  // 方法
  actions: {
    // 登录成功，保存用户信息
    loginSuccess(userInfo, token) {
      this.userInfo = userInfo
      this.token = token
      this.isLoggedIn = true
      this.role = userInfo.role || ''
      
      // 保存到localStorage
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      localStorage.setItem('token', token)
    },

    // 登出，清除用户信息
    logout() {
      this.userInfo = {}
      this.token = null
      this.isLoggedIn = false
      this.permissions = []
      this.role = ''
      
      // 清除localStorage
      localStorage.removeItem('userInfo')
      localStorage.removeItem('token')
    },

    // 更新用户信息
    updateUserInfo(userInfo) {
      this.userInfo = { ...this.userInfo, ...userInfo }
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
    },

    // 设置用户权限
    setPermissions(permissions) {
      this.permissions = permissions
    }
  }
})
