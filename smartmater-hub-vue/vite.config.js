import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
// 引入vite-plugin-mock
import { viteMockServe } from 'vite-plugin-mock'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // 使用更简单的mock配置
    viteMockServe({
      mockPath: './mock',
      localEnabled: true,
      // 关闭生产环境mock
      prodEnabled: false,
      // 简化配置
      supportTs: false,
      // 关闭自动注入，避免兼容性问题
      injectCode: false
    })
  ],
  // 配置server选项，确保mock请求被正确处理
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api')
      }
    }
  }
})
