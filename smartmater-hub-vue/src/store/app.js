import { defineStore } from 'pinia'

// 定义应用全局状态管理store
export const useAppStore = defineStore('app', {
  // 状态
  state: () => ({
    // 侧边栏折叠状态
    sidebarCollapsed: false,
    // 页面加载状态
    loading: false,
    // 通知消息队列
    notifications: [],
    // 当前激活的菜单
    activeMenu: '',
    // 面包屑导航数据
    breadcrumbs: [],
    // 主题颜色
    theme: 'default'
  }),

  // 计算属性
  getters: {
    // 获取当前主题
    getTheme: (state) => state.theme,
    // 获取面包屑导航数据
    getBreadcrumbs: (state) => state.breadcrumbs
  },

  // 方法
  actions: {
    // 切换侧边栏折叠状态
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },

    // 设置侧边栏折叠状态
    setSidebarCollapsed(collapsed) {
      this.sidebarCollapsed = collapsed
    },

    // 设置页面加载状态
    setLoading(status) {
      this.loading = status
    },

    // 添加通知消息
    addNotification(notification) {
      const id = Date.now() + Math.random().toString(36).substr(2, 5)
      const newNotification = {
        id,
        type: 'info',
        duration: 3000,
        ...notification
      }
      
      this.notifications.push(newNotification)
      
      // 自动移除通知
      if (newNotification.duration > 0) {
        setTimeout(() => {
          this.removeNotification(id)
        }, newNotification.duration)
      }
    },

    // 移除通知消息
    removeNotification(id) {
      this.notifications = this.notifications.filter(notify => notify.id !== id)
    },

    // 清空所有通知
    clearNotifications() {
      this.notifications = []
    },

    // 设置当前激活的菜单
    setActiveMenu(menu) {
      this.activeMenu = menu
    },

    // 设置面包屑导航
    setBreadcrumbs(breadcrumbs) {
      this.breadcrumbs = breadcrumbs
    },

    // 设置主题
    setTheme(theme) {
      this.theme = theme
    }
  }
})
