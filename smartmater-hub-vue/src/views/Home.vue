<template>
  <div class="main-container">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: appStore.sidebarCollapsed || isMobile }">
      <div class="sidebar-header">
        <div class="logo">
          <el-icon class="logo-icon"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg></el-icon>
          <span class="logo-text">智能物资管理系统</span>
        </div>
      </div>
      
      <div class="sidebar-menu">
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical-demo"
          background-color="#2a3042"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          @select="handleMenuSelect"
          router
          :collapse="appStore.sidebarCollapsed || isMobile"
        >
          <!-- 物资监控大屏 -->
          <el-menu-item index="/dashboard">
            <el-icon><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg></el-icon>
            <template #title>物资监控大屏</template>
          </el-menu-item>
          
          <!-- 首页 -->
          <el-menu-item index="/home">
            <el-icon><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path><polyline points="9 22 9 12 15 12 15 22"></polyline></svg></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          
          <!-- 用户管理 -->
          <el-sub-menu index="user">
            <template #title>
              <el-icon><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M22 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/user/list">用户列表</el-menu-item>
            <el-menu-item index="/user/role">角色管理</el-menu-item>
            <el-menu-item index="/user/profile">个人信息</el-menu-item>
          </el-sub-menu>
          
          <!-- 物资管理 -->
          <el-sub-menu index="material">
            <template #title>
              <el-icon><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path></svg></el-icon>
              <span>物资管理</span>
            </template>
            <el-menu-item index="/material/category">物资分类</el-menu-item>
            <el-menu-item index="/material/list">物资列表</el-menu-item>
            <el-menu-item index="/material/add">新增物资</el-menu-item>
          </el-sub-menu>
          
          <!-- 库存管理 -->
          <el-sub-menu index="inventory">
            <template #title>
              <el-icon><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path></svg></el-icon>
              <span>库存管理</span>
            </template>
            <el-menu-item index="/inventory/in">库存入库</el-menu-item>
            <el-menu-item index="/inventory/out">库存出库</el-menu-item>
            <el-menu-item index="/inventory/check">库存盘点</el-menu-item>
            <el-menu-item index="/inventory/warning">库存预警</el-menu-item>
          </el-sub-menu>
          
          <!-- 采购管理 -->
          <el-sub-menu index="purchase">
            <template #title>
              <el-icon><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="9" cy="21" r="1"></circle><circle cx="20" cy="21" r="1"></circle><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path></svg></el-icon>
              <span>采购管理</span>
            </template>
            <el-menu-item index="/purchase/requirement">采购需求</el-menu-item>
            <el-menu-item index="/purchase/supplier">供应商管理</el-menu-item>
            <el-menu-item index="/purchase/order">采购订单</el-menu-item>
          </el-sub-menu>
          
          <!-- 报表管理 -->
          <el-sub-menu index="report">
            <template #title>
              <el-icon><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 3v18h18"></path><path d="M19 9H5"></path><path d="M16 14H5"></path><path d="M12 19H5"></path></svg></el-icon>
              <span>报表管理</span>
            </template>
            <el-menu-item index="/report/inventory">库存报表</el-menu-item>
            <el-menu-item index="/report/purchase">采购报表</el-menu-item>
            <el-menu-item index="/report/use">领用报表</el-menu-item>
          </el-sub-menu>
          
          <!-- 系统管理 -->
          <el-sub-menu index="system">
            <template #title>
              <el-icon><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="3" width="20" height="14" rx="2" ry="2"></rect><line x1="8" y1="21" x2="16" y2="21"></line><line x1="12" y1="17" x2="12" y2="21"></line></svg></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/system/log">系统日志</el-menu-item>
            <el-menu-item index="/system/config">系统配置</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>
    </aside>
    
    <!-- 主内容区域 -->
    <main class="main-content">
      <!-- 顶部导航栏 -->
      <header class="top-header">
        <div class="header-left">
          <el-button type="text" @click="toggleSidebar" class="sidebar-toggle">
            <el-icon><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="3" y1="12" x2="21" y2="12"></line><line x1="3" y1="6" x2="21" y2="6"></line><line x1="3" y1="18" x2="21" y2="18"></line></svg></el-icon>
          </el-button>
        </div>
        
        <div class="header-right">
          <el-dropdown class="user-dropdown">
            <span class="user-info">
              <el-avatar :size="40" :src="userAvatar"></el-avatar>
              <span class="user-name">{{ userStore.userInfo.name || '管理员' }}</span>
              <el-icon class="el-icon--right"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="6 9 12 15 18 9"></polyline></svg></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/user/profile')">
                  <el-icon><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      
      <!-- 内容区域 -->
      <section class="content-wrapper">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component v-if="Component" :is="Component" />
          </transition>
        </router-view>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { useAppStore } from '../store/app'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 使用Pinia store
const userStore = useUserStore()
const appStore = useAppStore()

// 用户信息
const userAvatar = ref('https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png')

// 响应式设计：检测是否为移动设备
const isMobile = ref(false)

// 计算当前激活的菜单
const activeMenu = computed(() => {
  const currentPath = router.currentRoute.value.path
  return currentPath
})

// 检查屏幕尺寸
const checkScreenSize = () => {
  isMobile.value = window.innerWidth < 768
}

// 切换侧边栏
const toggleSidebar = () => {
  appStore.toggleSidebar()
}

// 处理菜单选择
const handleMenuSelect = (key) => {
  router.push(key)
}

// 处理退出登录
const handleLogout = () => {
  // 使用store的logout方法清除登录状态
  userStore.logout()
  
  // 显示退出成功消息
  ElMessage.success('退出成功')
  
  // 跳转到登录页
  router.push('/login')
}

// 页面挂载时检查登录状态和屏幕尺寸
onMounted(() => {
  // 使用store检查登录状态
  if (!userStore.isLoggedIn) {
    router.push('/login')
  }
  
  // 初始化屏幕尺寸检查
  checkScreenSize()
  
  // 添加窗口大小变化监听
  window.addEventListener('resize', checkScreenSize)
})

// 页面卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener('resize', checkScreenSize)
})
</script>

<style scoped>
.main-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background-color: var(--bg-secondary);
}

/* 侧边栏样式 - 现代化设计 */
.sidebar {
  width: 260px;
  background: linear-gradient(180deg, #2a3042 0%, #1e2330 100%);
  color: #bfcbd9;
  height: 100%;
  transition: all var(--transition-base);
  overflow: hidden;
  position: relative;
  z-index: 100;
  box-shadow: 2px 0 20px rgba(0, 0, 0, 0.2);
  border-right: 1px solid rgba(255, 255, 255, 0.08);
}

.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at top right, rgba(102, 126, 234, 0.2) 0%, transparent 50%),
              radial-gradient(circle at bottom left, rgba(102, 126, 234, 0.1) 0%, transparent 50%);
  pointer-events: none;
  transition: var(--transition-base);
  animation: sidebarGlow 8s ease-in-out infinite alternate;
}

@keyframes sidebarGlow {
  0% {
    opacity: 0.5;
  }
  100% {
    opacity: 1;
  }
}

.sidebar.collapsed {
  width: 76px;
}

.sidebar-header {
  padding: 0 var(--spacing-xl);
  height: 64px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
  z-index: 1;
  background: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(15px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.logo {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  transition: var(--transition-base);
  padding: var(--spacing-sm) 0;
}

.logo-icon {
  font-size: 32px;
  color: var(--primary-color);
  transition: var(--transition-base);
  filter: drop-shadow(0 2px 12px rgba(64, 158, 255, 0.4));
  animation: logoPulse 2s ease-in-out infinite alternate;
}

@keyframes logoPulse {
  0% {
    transform: scale(1);
    filter: drop-shadow(0 2px 8px rgba(64, 158, 255, 0.3));
  }
  100% {
    transform: scale(1.05);
    filter: drop-shadow(0 2px 16px rgba(64, 158, 255, 0.5));
  }
}

.logo-text {
  font-size: 19px;
  font-weight: 700;
  color: #fff;
  transition: var(--transition-base);
  letter-spacing: 0.8px;
  text-shadow: 0 2px 6px rgba(0, 0, 0, 0.4);
  background: linear-gradient(135deg, #fff 0%, var(--primary-light) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sidebar.collapsed .logo-text {
  display: none;
}

.sidebar-menu {
  height: calc(100% - 64px);
  overflow-y: auto;
  padding: var(--spacing-md) 0;
  position: relative;
  z-index: 1;
  scrollbar-width: thin;
  scrollbar-color: rgba(255, 255, 255, 0.2) transparent;
}

.sidebar-menu::-webkit-scrollbar {
  width: 6px;
}

.sidebar-menu::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: var(--border-radius-round);
}

.sidebar-menu::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0.3) 100%);
  border-radius: var(--border-radius-round);
  transition: var(--transition-fast);
}

.sidebar-menu::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.3) 0%, rgba(255, 255, 255, 0.4) 100%);
}

/* 菜单样式覆盖 - 现代化设计 */
:deep(.el-menu) {
  background: transparent !important;
  border-right: none !important;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  color: #bfcbd9 !important;
  transition: all var(--transition-base) !important;
  border-radius: var(--border-radius-2xl) 0 0 var(--border-radius-2xl) !important;
  margin: 0 var(--spacing-sm) !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05) !important;
  height: var(--height-xl) !important;
  line-height: var(--height-xl) !important;
  position: relative;
  overflow: hidden;
}

:deep(.el-menu-item::before),
:deep(.el-sub-menu__title::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: var(--gradient-primary);
  transform: scaleY(0);
  transform-origin: center;
  transition: var(--transition-transform);
}

:deep(.el-menu-item:hover::before),
:deep(.el-sub-menu__title:hover::before),
:deep(.el-menu-item.is-active::before),
:deep(.el-sub-menu__title.is-active::before) {
  transform: scaleY(1);
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: rgba(64, 158, 255, 0.15) !important;
  color: var(--primary-light) !important;
  transform: translateX(6px) !important;
}

:deep(.el-menu-item.is-active) {
  background-color: rgba(64, 158, 255, 0.25) !important;
  color: #fff !important;
  transform: translateX(6px) !important;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

:deep(.el-sub-menu__title.is-active) {
  background-color: rgba(64, 158, 255, 0.2) !important;
  color: var(--primary-light) !important;
  transform: translateX(6px) !important;
}

:deep(.el-sub-menu .el-menu-item) {
  padding-left: calc(var(--spacing-xl) * 3) !important;
  margin: 0 var(--spacing-sm) 0 var(--spacing-lg) !important;
  border-left: 2px solid transparent;
  transition: all var(--transition-base) !important;
}

:deep(.el-sub-menu .el-menu-item:hover) {
  border-left-color: var(--primary-color);
  background-color: rgba(64, 158, 255, 0.12) !important;
}

:deep(.el-sub-menu .el-menu-item.is-active) {
  border-left-color: var(--primary-color);
  background-color: rgba(64, 158, 255, 0.15) !important;
  color: var(--primary-light) !important;
}

/* 菜单图标样式 */
:deep(.el-menu-item .el-icon),
:deep(.el-sub-menu__title .el-icon) {
  font-size: var(--font-size-lg) !important;
  transition: var(--transition-transform), var(--transition-color) !important;
  margin-right: var(--spacing-md) !important;
  color: inherit;
}

:deep(.el-menu-item:hover .el-icon),
:deep(.el-sub-menu__title:hover .el-icon),
:deep(.el-menu-item.is-active .el-icon),
:deep(.el-sub-menu__title.is-active .el-icon) {
  color: var(--primary-color) !important;
  transform: scale(1.1) !important;
}

/* 菜单文字样式 */
:deep(.el-menu-item span),
:deep(.el-sub-menu__title span) {
  transition: var(--transition-color) !important;
  font-weight: var(--font-weight-medium) !important;
  font-size: var(--font-size-base) !important;
  letter-spacing: 0.3px !important;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: var(--bg-secondary);
  transition: var(--transition-base);
}

/* 顶部导航栏 - 现代化设计 */
.top-header {
  height: 64px;
  background: linear-gradient(90deg, var(--bg-primary) 0%, var(--bg-tertiary) 100%);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-xxl);
  box-shadow: var(--shadow-light);
  z-index: 50;
  transition: var(--transition-base);
  backdrop-filter: blur(15px);
  position: relative;
}

.top-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--gradient-primary);
  animation: headerGlow 4s ease-in-out infinite alternate;
}

@keyframes headerGlow {
  0% {
    opacity: 0.6;
  }
  100% {
    opacity: 1;
    box-shadow: 0 0 20px rgba(64, 158, 255, 0.3);
  }
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
}

.sidebar-toggle {
  font-size: 24px;
  color: var(--text-regular);
  transition: var(--transition-base);
  border-radius: var(--border-radius-2xl);
  padding: var(--spacing-sm);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  width: var(--height-large);
  height: var(--height-large);
  background: var(--bg-primary);
  border: 2px solid var(--border-color);
  box-shadow: var(--shadow-base);
}

.sidebar-toggle:hover {
  color: var(--primary-color);
  background-color: var(--primary-opacity);
  border-color: var(--primary-color);
  transform: scale(1.08);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
}

/* 用户信息 - 现代化设计 */
.user-dropdown {
  cursor: pointer;
  transition: var(--transition-base);
  border-radius: var(--border-radius-2xl);
  padding: var(--spacing-xs) var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-base);
}

.user-dropdown:hover {
  background-color: var(--bg-secondary);
  border-color: var(--primary-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.user-info :deep(.el-avatar) {
  transition: var(--transition-base);
  border: 3px solid var(--primary-color);
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.4);
  animation: avatarGlow 3s ease-in-out infinite alternate;
}

@keyframes avatarGlow {
  0% {
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
  }
  100% {
    box-shadow: 0 4px 16px rgba(64, 158, 255, 0.5);
  }
}

.user-info :deep(.el-avatar:hover) {
  transform: scale(1.15);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.5);
}

.user-name {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  transition: var(--transition-base);
  white-space: nowrap;
}

.user-info :deep(.el-icon) {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  transition: var(--transition-base);
  animation: iconRotate 2s ease-in-out infinite;
}

@keyframes iconRotate {
  0%, 100% {
    transform: rotate(0deg);
  }
  50% {
    transform: rotate(180deg);
  }
}

.user-dropdown:hover :deep(.el-icon) {
  color: var(--primary-color);
  animation-play-state: paused;
}

/* 内容区域 */
.content-wrapper {
  flex: 1;
  padding: var(--spacing-xxl);
  overflow-y: auto;
  transition: var(--transition-base);
  scrollbar-width: thin;
  scrollbar-color: var(--border-dark) var(--bg-tertiary);
}

.content-wrapper::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.content-wrapper::-webkit-scrollbar-track {
  background: var(--bg-tertiary);
  border-radius: var(--border-radius-round);
}

.content-wrapper::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, var(--border-dark) 0%, var(--info-color) 100%);
  border-radius: var(--border-radius-round);
  transition: var(--transition-fast);
}

.content-wrapper::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, var(--info-color) 0%, var(--info-dark) 100%);
}

.content-wrapper::-webkit-scrollbar-thumb:active {
  background: linear-gradient(180deg, var(--info-dark) 0%, var(--text-secondary) 100%);
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-base);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 页面过渡效果 */
.page-transition-enter-active,
.page-transition-leave-active {
  transition: all var(--transition-slow);
}

.page-transition-enter-from {
  transform: translateX(20px);
  opacity: 0;
}

.page-transition-leave-to {
  transform: translateX(-20px);
  opacity: 0;
}

/* 响应式设计 */
@media screen and (max-width: 1200px) {
  .content-wrapper {
    padding: var(--spacing-xl);
  }
  
  .top-header {
    padding: 0 var(--spacing-xl);
  }
}

@media screen and (max-width: 768px) {
  .main-container {
    flex-direction: column;
  }
  
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    height: 100%;
    box-shadow: 2px 0 20px rgba(0, 0, 0, 0.2);
    z-index: 1000;
    transition: left var(--transition-base);
  }
  
  .sidebar.collapsed {
    left: -256px;
  }
  
  .main-content {
    width: 100%;
    margin-left: 0;
  }
  
  .top-header {
    padding: 0 var(--spacing-lg);
    height: 56px;
  }
  
  .content-wrapper {
    padding: var(--spacing-lg);
  }
  
  .header-right {
    gap: var(--spacing-md);
  }
  
  .user-info {
    gap: var(--spacing-xs);
  }
  
  .user-name {
    display: none;
  }
  
  .sidebar-toggle {
    font-size: 20px;
    width: var(--height-medium);
    height: var(--height-medium);
  }
  
  .logo-text {
    font-size: 16px;
  }
  
  .logo-icon {
    font-size: 24px;
  }
}

@media screen and (max-width: 480px) {
  .top-header {
    padding: 0 var(--spacing-md);
  }
  
  .content-wrapper {
    padding: var(--spacing-md);
  }
  
  .logo-text {
    font-size: 15px;
  }
  
  .header-right {
    gap: var(--spacing-xs);
  }
  
  .user-dropdown {
    padding: var(--spacing-xs);
  }
  
  .user-info :deep(.el-avatar) {
    width: var(--height-medium);
    height: var(--height-medium);
    font-size: var(--font-size-sm);
  }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .top-header {
    background: linear-gradient(90deg, #1f2024 0%, #2c2d33 100%);
    border-bottom: 1px solid var(--border-color);
  }
  
  .sidebar {
    background: linear-gradient(180deg, #1f2024 0%, #1a1b1f 100%);
  }
  
  .logo-text {
    color: #fff;
  }
  
  .user-name {
    color: #fff;
  }
  
  .sidebar-toggle {
    color: var(--text-regular);
  }
  
  .sidebar-toggle:hover {
    background-color: rgba(64, 158, 255, 0.2);
  }
  
  .user-dropdown:hover {
    background-color: rgba(64, 158, 255, 0.1);
  }
  
  .content-wrapper {
    background-color: var(--bg-secondary);
  }
}

/* 加载动画 */
:deep(.el-loading-mask) {
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
}

:deep(.el-loading-spinner .path) {
  stroke: var(--primary-color);
}

@media (prefers-color-scheme: dark) {
  :deep(.el-loading-mask) {
    background-color: rgba(31, 32, 36, 0.8);
  }
}
</style>
