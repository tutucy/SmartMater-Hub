<template>
  <div class="navbar-container">
    <div class="navbar">
      <div class="navbar-brand">
        <el-icon class="brand-icon"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 7h-9"></path><path d="M14 17H5"></path><circle cx="17" cy="17" r="3"></circle><path d="M3 12h4"></path></svg></el-icon>
        <span class="brand-text">智能物资管理系统</span>
      </div>
      
      <div class="navbar-menu">
        <div 
          v-for="menu in menuItems" 
          :key="menu.title"
          class="menu-item"
          :class="{ active: isActive(menu.path) }"
          @mouseenter="handleMouseEnter(menu)"
          @mouseleave="handleMouseLeave"
        >
          <el-icon class="menu-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="menu.icon"></svg>
          </el-icon>
          <span class="menu-text">{{ menu.title }}</span>
          
          <!-- 下拉菜单 -->
          <transition name="dropdown">
            <div v-show="activeMenu === menu.title" class="dropdown-menu">
              <div class="dropdown-header">
                <span class="dropdown-title">{{ menu.title }}</span>
              </div>
              <div 
                v-for="(item, index) in menu.items" 
                :key="item.title"
                class="dropdown-item"
                :class="{ 'dropdown-item-last': index === menu.items.length - 1 }"
                @click="handleMenuClick(item.path)"
              >
                <el-icon class="dropdown-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#667eea" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="item.icon"></svg>
                </el-icon>
                <span class="dropdown-item-text">{{ item.title }}</span>
              </div>
            </div>
          </transition>
        </div>
      </div>
      
      <div class="navbar-user">
        <el-dropdown @command="handleUserCommand" trigger="click">
          <div class="user-info">
            <el-avatar :size="32" class="user-avatar">
              {{ userName.charAt(0) }}
            </el-avatar>
            <span class="user-name">{{ userName }}</span>
            <el-icon class="dropdown-arrow"><svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="6 9 12 15"></polyline></svg></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu class="user-dropdown-menu">
              <el-dropdown-item command="profile">
                <el-icon><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg></el-icon>
                个人信息
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <el-icon><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 17 21"></polyline><line x1="21" y1="9" x2="16" y2="9"></line><line x1="21" y1="17" x2="16" y2="17"></line></svg></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const userName = computed(() => {
  return localStorage.getItem('userName') || '管理员'
})

const activeMenu = ref('')

const menuItems = [
  {
    title: '物资监控',
    path: '/dashboard',
    icon: '<rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect>',
    items: [
      { title: '物资监控大屏', path: '/dashboard', icon: '<rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect>' }
    ]
  },
  {
    title: '用户管理',
    path: '/user/list',
    icon: '<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-4-4h-1"></path>',
    items: [
      { title: '用户列表', path: '/user/list', icon: '<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-4-4h-1"></path>' },
      { title: '角色管理', path: '/user/role', icon: '<path d="M12 22s1-3.9 3-9 3-9c0-5.1 4-9 9-9s9 4 9 9c0 2.8 2.2 4 4 4v6"></path><path d="M12 8c-2.2 0-4 1.8-4 4-4s4 1.8 4 4 4c0 2.2-1.8 4-4 4-4s-4 1.8-4 4-4"></path><path d="M15 3h6v6"></path><path d="M10 21v-8a1 1 0 0 0-1-1H8a1 1 0 0 0-1 1v3a1 1 0 0 0 1 1h2"></path><path d="M8 3h4"></path>' },
      { title: '个人信息', path: '/user/profile', icon: '<circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="16"></line><line x1="8" y1="12" x2="16" y2="12"></line>' }
    ]
  },
  {
    title: '物资管理',
    path: '/material/list',
    icon: '<path d="M21 16V8a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v8a2 2 0 0 0 2h8"></path><path d="M3 12h18"></path><path d="M3 6h18"></path><path d="M3 18h18"></path>',
    items: [
      { title: '物资分类', path: '/material/category', icon: '<path d="M4 19.5A2.5 2.5 0 0 1 2.5-2.5H3a2.5 2.5 0 0 1-2.5 2.5v-5A2.5 2.5 0 0 1 2.5-2.5h8.5a2.5 2.5 0 0 1 2.5 2.5v5a2.5 2.5 0 0 1-2.5 2.5H15"></path><path d="M9 14l2 2 4 4"></path><path d="M15 14l-2 2-4 4"></path>' },
      { title: '物资列表', path: '/material/list', icon: '<path d="M21 16V8a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v8a2 2 0 0 0 2h8"></path><path d="M3 12h18"></path><path d="M3 6h18"></path><path d="M3 18h18"></path>' },
      { title: '新增物资', path: '/material/add', icon: '<circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="16"></line><line x1="8" y1="12" x2="16" y2="12"></line>' }
    ]
  },
  {
    title: '库存管理',
    path: '/inventory/in',
    icon: '<path d="M20 7h-9"></path><path d="M14 17H5"></path><circle cx="17" cy="17" r="3"></circle><path d="M3 12h4"></path>',
    items: [
      { title: '库存入库', path: '/inventory/in', icon: '<path d="M20 7h-9"></path><path d="M14 17H5"></path><circle cx="17" cy="17" r="3"></circle><path d="M3 12h4"></path>' },
      { title: '库存出库', path: '/inventory/out', icon: '<path d="M20 7h-9"></path><path d="M14 17H5"></path><circle cx="17" cy="17" r="3"></circle><path d="M3 12h4"></path><path d="M16 21v-2a2 2 0 0 0-4-4h-4"></path>' },
      { title: '库存盘点', path: '/inventory/check', icon: '<path d="M9 11l3 3L22 2"></path><path d="M5 12l-3-3L2 22"></path><path d="M2 22h20"></path><path d="M22 22v-4"></path><path d="M2 18h20"></path>' },
      { title: '库存预警', path: '/inventory/warning', icon: '<path d="M10.29 3.86L1.82 18a2 2 0 0 1.71 3h16.94a2 2 0 0 1.71-3L13.71 3.86a2 2 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line>' }
    ]
  },
  {
    title: '采购管理',
    path: '/purchase/order',
    icon: '<circle cx="9" cy="21" r="1"></circle><circle cx="20" cy="21" r="1"></circle><circle cx="9" cy="8" r="1"></circle><circle cx="20" cy="8" r="1"></circle><line x1="1" y1="8" x2="1" y2="21"></line><line x1="1" y1="14" x2="1" y2="14"></line><line x1="1" y1="17" x2="1" y2="17"></line>',
    items: [
      { title: '采购需求', path: '/purchase/requirement', icon: '<path d="M11 4H4a2 2 0 0 0-2-2v14a2 2 0 0 2 2h14a2 2 0 0 2-2V6a2 2 0 0 0-2-2h-7"></path><path d="M13 2v6"></path><path d="M15 11v6"></path><path d="M17 13v6"></path>' },
      { title: '供应商管理', path: '/purchase/supplier', icon: '<path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="8.5" cy="7" r="4"></circle><polyline points="17 11 19 13 23 13 23 13 23"></polyline><line x1="23" y1="6" x2="15.5" y2="6"></line><path d="M20 21v-2"></path>' },
      { title: '采购订单', path: '/purchase/order', icon: '<circle cx="9" cy="21" r="1"></circle><circle cx="20" cy="21" r="1"></circle><circle cx="9" cy="8" r="1"></circle><circle cx="20" cy="8" r="1"></circle><line x1="1" y1="8" x2="1" y2="21"></line><line x1="1" y1="14" x2="1" y2="14"></line><line x1="1" y1="17" x2="1" y2="17"></line>' }
    ]
  },
  {
    title: '报表管理',
    path: '/report/inventory',
    icon: '<line x1="18" y1="20" x2="18" y2="10"></line><line x1="12" y1="20" x2="12" y2="4"></line><line x1="6" y1="20" x2="6" y2="10"></line>',
    items: [
      { title: '库存报表', path: '/report/inventory', icon: '<line x1="18" y1="20" x2="18" y2="10"></line><line x1="12" y1="20" x2="12" y2="4"></line><line x1="6" y1="20" x2="6" y2="10"></line>' },
      { title: '采购报表', path: '/report/purchase', icon: '<path d="M14 2H6a2 2 0 0 0-2-2v16a2 2 0 0 2 2h12a2 2 0 0 2-2V4a2 2 0 0 0-2-2h-4"></path><line x1="6" y1="18" x2="18" y2="18"></line><line x1="6" y1="14" x2="18" y2="14"></line><line x1="6" y1="10" x2="18" y2="10"></line>' },
      { title: '领用报表', path: '/report/use', icon: '<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle><path d="M1 12h4"></path><path d="M3 12h4"></path><path d="M5 12h14"></path>' }
    ]
  },
  {
    title: '系统管理',
    path: '/system/log',
    icon: '<circle cx="12" cy="12" r="3"></circle><path d="M12 1v2"></path><path d="M12 21v2"></path><path d="M4.22 4.22l-1.42 1.42"></path><path d="M21.22 4.22l-1.42 1.42"></path><path d="M1 12h4"></path><path d="M19 12h4"></path>',
    items: [
      { title: '系统日志', path: '/system/log', icon: '<circle cx="12" cy="12" r="3"></circle><path d="M12 1v2"></path><path d="M12 21v2"></path><path d="M4.22 4.22l-1.42 1.42"></path><path d="M21.22 4.22l-1.42 1.42"></path><path d="M1 12h4"></path><path d="M19 12h4"></path>' },
      { title: '系统配置', path: '/system/config', icon: '<circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33-1.82l-1.67-3.11A4 4 0 0 0-3.37-3.93h-.11"></path><circle cx="6.9" cy="17.8" r="2.8"></circle><circle cx="17" cy="17.8" r="2.8"></circle><circle cx="12" cy="17.8" r="2.8"></circle>' }
    ]
  }
]

const isActive = (path) => {
  return route.path === path || route.path.startsWith(path)
}

const handleMouseEnter = (menu) => {
  activeMenu.value = menu.title
}

const handleMouseLeave = () => {
  activeMenu.value = ''
}

const handleMenuClick = (path) => {
  activeMenu.value = ''
  router.push(path)
}

const handleUserCommand = (command) => {
  if (command === 'profile') {
    router.push('/user/profile')
  } else if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('userName')
    ElMessage.success('退出登录成功')
    router.push('/login')
  }
}
</script>

<style scoped>
.navbar-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 12px rgba(102, 126, 234, 0.18);
}

.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-xl);
  height: 64px;
  max-width: 1400px;
  margin: 0 auto;
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex-shrink: 0;
}

.brand-icon {
  color: white;
  font-size: 26px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
}

.brand-text {
  color: white;
  font-size: var(--font-size-xl);
  font-weight: 700;
  letter-spacing: 1px;
  white-space: nowrap;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.navbar-menu {
  display: flex;
  align-items: center;
  gap: var(--spacing-base);
  flex: 1;
  justify-content: center;
}

.menu-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-lg);
  color: rgba(255, 255, 255, 0.9);
  cursor: pointer;
  border-radius: var(--border-radius-lg);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: var(--font-size-base);
  font-weight: 500;
  white-space: nowrap;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.menu-item:hover {
  color: white;
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.menu-item.active {
  color: white;
  background: rgba(255, 255, 255, 0.35);
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
}

.menu-icon {
  font-size: 22px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
}

.menu-text {
  white-space: nowrap;
  font-weight: 500;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  min-width: 220px;
  background: white;
  border-radius: var(--border-radius-xl);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25);
  padding: 0;
  z-index: 1001;
  overflow: hidden;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.dropdown-header {
  padding: var(--spacing-sm) var(--spacing-base);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.dropdown-title {
  color: white;
  font-size: var(--font-size-sm);
  font-weight: 600;
  letter-spacing: 0.5px;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-base) var(--spacing-lg);
  color: var(--text-primary);
  cursor: pointer;
  border-radius: var(--border-radius-base);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: var(--font-size-sm);
  font-weight: 500;
  border-bottom: 1px solid var(--border-lighter);
}

.dropdown-item:last-child {
  border-bottom: none;
}

.dropdown-item:hover {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf4 100%);
  color: var(--primary-color);
  transform: translateX(8px);
}

.dropdown-item-text {
  flex: 1;
  font-weight: 500;
}

.dropdown-icon {
  font-size: 18px;
  color: var(--primary-color);
  transition: transform 0.2s ease;
}

.dropdown-item:hover .dropdown-icon {
  transform: scale(1.1) rotate(5deg);
}

.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.dropdown-enter-to,
.dropdown-leave-from {
  opacity: 1;
  transform: translateY(0);
}

.navbar-user {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  padding: var(--spacing-sm) var(--spacing-base);
  border-radius: var(--border-radius-lg);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.user-avatar {
  background: white;
  color: var(--primary-color);
  font-weight: 700;
  font-size: var(--font-size-lg);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.user-name {
  color: white;
  font-size: var(--font-size-sm);
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.dropdown-arrow {
  color: white;
  font-size: 12px;
  transition: transform 0.2s ease;
}

.user-info:hover .dropdown-arrow {
  transform: rotate(180deg);
}

.user-dropdown-menu {
  min-width: 160px;
  border-radius: var(--border-radius-lg);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.user-dropdown-menu .el-dropdown-menu__item {
  padding: var(--spacing-base) var(--spacing-lg);
  font-size: var(--font-size-sm);
  font-weight: 500;
  border-bottom: 1px solid var(--border-lighter);
}

.user-dropdown-menu .el-dropdown-menu__item:hover {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf4 100%);
  color: var(--primary-color);
}

.user-dropdown-menu .el-dropdown-menu__item:last-child {
  border-bottom: none;
}

@media screen and (max-width: 768px) {
  .navbar {
    padding: 0 var(--spacing-sm);
  }
  
  .brand-text {
    display: none;
  }
  
  .menu-text {
    display: none;
  }
  
  .user-name {
    display: none;
  }
}
</style>
