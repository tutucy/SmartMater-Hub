import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

// 引入Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 引入Element Plus图标库
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 引入Vue Router
import router from './router'

// 引入Pinia
import { createPinia } from 'pinia'

const app = createApp(App)

// 创建Pinia实例
const pinia = createPinia()

// 使用Element Plus
app.use(ElementPlus)

// 注册所有Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用Vue Router
app.use(router)

// 使用Pinia
app.use(pinia)

app.mount('#app')
