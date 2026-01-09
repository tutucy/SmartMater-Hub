<script setup>
// App.vue作为路由出口，不再包含具体业务逻辑
</script>

<template>
  <!-- 路由出口，所有页面将在这里渲染 -->
  <router-view />
</template>

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
}

/* 基础样式 */
#app {
  width: 100%;
  height: 100vh;
}
</style>
