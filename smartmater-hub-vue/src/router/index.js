import { createRouter, createWebHistory } from 'vue-router'

// 路由配置
const routes = [
  // 公共路由
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/auth/Register.vue'),
    meta: { title: '注册', requiresAuth: false }
  },
  // 主布局路由
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { title: '物资监控大屏', requiresAuth: true }
  },
  // 用户管理路由
  {
    path: '/user/list',
    name: 'UserList',
    component: () => import('../views/user/UserList.vue'),
    meta: { title: '用户列表', requiresAuth: true }
  },
  {
    path: '/user/role',
    name: 'RoleList',
    component: () => import('../views/user/RoleList.vue'),
    meta: { title: '角色管理', requiresAuth: true }
  },
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: () => import('../views/user/UserProfile.vue'),
    meta: { title: '个人信息', requiresAuth: true }
  },
  // 物资管理路由
  {
    path: '/material/category',
    name: 'MaterialCategory',
    component: () => import('../views/material/CategoryList.vue'),
    meta: { title: '物资分类', requiresAuth: true }
  },
  {
    path: '/material/list',
    name: 'MaterialList',
    component: () => import('../views/material/MaterialList.vue'),
    meta: { title: '物资列表', requiresAuth: true }
  },
  {
    path: '/material/add',
    name: 'MaterialAdd',
    component: () => import('../views/material/MaterialAdd.vue'),
    meta: { title: '新增物资', requiresAuth: true }
  },
  // 库存管理路由
  {
    path: '/inventory/in',
    name: 'InventoryIn',
    component: () => import('../views/inventory/InventoryIn.vue'),
    meta: { title: '库存入库', requiresAuth: true }
  },
  {
    path: '/inventory/out',
    name: 'InventoryOut',
    component: () => import('../views/inventory/InventoryOut.vue'),
    meta: { title: '库存出库', requiresAuth: true }
  },
  {
    path: '/inventory/check',
    name: 'InventoryCheck',
    component: () => import('../views/inventory/InventoryCheck.vue'),
    meta: { title: '库存盘点', requiresAuth: true }
  },
  {
    path: '/inventory/warning',
    name: 'InventoryWarning',
    component: () => import('../views/inventory/InventoryWarning.vue'),
    meta: { title: '库存预警', requiresAuth: true }
  },
  // 采购管理路由
  {
    path: '/purchase/requirement',
    name: 'PurchaseRequirement',
    component: () => import('../views/purchase/RequirementList.vue'),
    meta: { title: '采购需求', requiresAuth: true }
  },
  {
    path: '/purchase/supplier',
    name: 'SupplierList',
    component: () => import('../views/purchase/SupplierList.vue'),
    meta: { title: '供应商管理', requiresAuth: true }
  },
  {
    path: '/purchase/order',
    name: 'PurchaseOrder',
    component: () => import('../views/purchase/PurchaseOrderList.vue'),
    meta: { title: '采购订单', requiresAuth: true }
  },
  // 报表管理路由
  {
    path: '/report/inventory',
    name: 'InventoryReport',
    component: () => import('../views/report/InventoryReport.vue'),
    meta: { title: '库存报表', requiresAuth: true }
  },
  {
    path: '/report/purchase',
    name: 'PurchaseReport',
    component: () => import('../views/report/PurchaseReport.vue'),
    meta: { title: '采购报表', requiresAuth: true }
  },
  {
    path: '/report/use',
    name: 'UseReport',
    component: () => import('../views/report/UseReport.vue'),
    meta: { title: '领用报表', requiresAuth: true }
  },
  // 系统管理路由
  {
    path: '/system/log',
    name: 'SystemLog',
    component: () => import('../views/system/LogList.vue'),
    meta: { title: '系统日志', requiresAuth: true }
  },
  {
    path: '/system/config',
    name: 'SystemConfig',
    component: () => import('../views/system/Config.vue'),
    meta: { title: '系统配置', requiresAuth: true }
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由前置守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = `${to.meta.title} - 智能物资管理系统`
  
  // 检查是否需要登录
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const isLoggedIn = localStorage.getItem('token') !== null
  
  if (requiresAuth && !isLoggedIn) {
    // 需要登录但未登录，重定向到登录页
    next('/login')
  } else if (to.path === '/login' && isLoggedIn) {
    // 已登录但访问登录页，重定向到首页
    next('/home')
  } else {
    // 其他情况正常跳转
    next()
  }
})

export default router
