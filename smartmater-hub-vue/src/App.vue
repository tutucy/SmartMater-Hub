<template>
  <div id="app">
    <Navbar v-if="isLoggedIn" />
    <div class="main-content" :class="{ 'with-navbar': isLoggedIn }">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from './components/Navbar.vue'

const route = useRoute()

const isLoggedIn = computed(() => {
  return route.path !== '/login' && route.path !== '/register'
})
</script>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: #f5f7fa;
  overflow-x: hidden;
}

/* 基础样式 */
#app {
  width: 100%;
  min-height: 100vh;
  overflow-y: auto;
}

.main-content {
  padding-top: 0;
  transition: padding-top 0.3s ease;
  min-height: calc(100vh - 64px);
}

.main-content.with-navbar {
  padding-top: 64px;
}

/* CSS变量定义 */
:root {
  /* 颜色系统 */
  --primary-color: #409eff;
  --success-color: #67c23a;
  --warning-color: #e6a23c;
  --danger-color: #f56c6c;
  --info-color: #909399;
  
  /* 文本颜色 */
  --text-primary: #303133;
  --text-regular: #606266;
  --text-secondary: #909399;
  --text-placeholder: #c0c4cc;
  
  /* 边框颜色 */
  --border-color: #dcdfe6;
  --border-light: #e4e7ed;
  --border-lighter: #ebeef5;
  --border-extra-light: #f2f6fc;
  
  /* 背景颜色 */
  --bg-primary: #ffffff;
  --bg-secondary: #f5f7fa;
  --bg-tertiary: #f0f2f5;
  --bg-dark: #1f2d3d;
  
  /* 字体大小 */
  --font-size-xs: 12px;
  --font-size-sm: 14px;
  --font-size-base: 16px;
  --font-size-lg: 18px;
  --font-size-xl: 20px;
  --font-size-xxl: 24px;
  --font-size-xxxl: 32px;
  
  /* 间距 */
  --spacing-xs: 4px;
  --spacing-sm: 8px;
  --spacing-base: 12px;
  --spacing-lg: 16px;
  --spacing-xl: 20px;
  --spacing-xxl: 24px;
  
  /* 圆角 */
  --border-radius-sm: 2px;
  --border-radius-base: 4px;
  --border-radius-lg: 8px;
  --border-radius-xl: 12px;
  --border-radius-xxl: 16px;
  
  /* 阴影 */
  --shadow-base: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  --shadow-hover: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
  --shadow-active: 0 6px 20px 0 rgba(0, 0, 0, 0.2);
  
  /* 过渡 */
  --transition-base: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  --transition-fast: all 0.15s cubic-bezier(0.645, 0.045, 0.355, 1);
  --transition-slow: all 0.5s cubic-bezier(0.645, 0.045, 0.355, 1);
}
</style>
