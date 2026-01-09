<template>
  <div class="dashboard-container">
    <h2 class="page-title">物资监控大屏</h2>
    
    <!-- 数据统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-number">{{ totalMaterials }}</div>
              <div class="stat-label">总物资数</div>
            </div>
            <div class="stat-icon materials">
              <el-icon><svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path></svg></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-number">{{ totalStock }}</div>
              <div class="stat-label">总库存量</div>
            </div>
            <div class="stat-icon stock">
              <el-icon><svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path></svg></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-number">{{ lowStockCount }}</div>
              <div class="stat-label">低库存预警</div>
            </div>
            <div class="stat-icon warning">
              <el-icon><svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-number">{{ totalCategories }}</div>
              <div class="stat-label">物资分类</div>
            </div>
            <div class="stat-icon categories">
              <el-icon><svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 3a3 3 0 0 0-3 3v12a3 3 0 0 0 3 3 3 3 0 0 0 3-3 3 3 0 0 0-3-3H6a3 3 0 0 0-3 3 3 3 0 0 0 3 3 3 3 0 0 0 3-3V6a3 3 0 0 0-3-3 3 3 0 0 0-3 3 3 3 0 0 0 3 3h12a3 3 0 0 0 3-3 3 3 0 0 0-3-3z"></path></svg></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>库存分布</span>
            </div>
          </template>
          <div ref="stockDistChartRef" class="chart" style="height: 400px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>物资分类占比</span>
            </div>
          </template>
          <div ref="categoryChartRef" class="chart" style="height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>库存变化趋势</span>
            </div>
          </template>
          <div ref="stockTrendChartRef" class="chart" style="height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 低库存预警列表 -->
    <el-card shadow="hover" class="warning-card">
      <template #header>
        <div class="card-header">
          <span>低库存预警</span>
        </div>
      </template>
      <el-table :data="lowStockMaterials" style="width: 100%" border>
        <el-table-column prop="name" label="物资名称" width="200" />
        <el-table-column prop="categoryName" label="分类" width="150" />
        <el-table-column prop="stockQuantity" label="当前库存" width="120" align="center" />
        <el-table-column prop="stockWarningValue" label="预警值" width="120" align="center" />
        <el-table-column prop="unit" label="单位" width="80" align="center" />
        <el-table-column label="操作" width="150" align="center">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleReplenish(scope.row)">
              申请补货
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

// 数据统计
const totalMaterials = ref(127)
const totalStock = ref(15800)
const lowStockCount = ref(15)
const totalCategories = ref(8)

// 图表ref引用
const stockDistChartRef = ref(null)
const categoryChartRef = ref(null)
const stockTrendChartRef = ref(null)

// 图表实例
let stockDistChart = null
let categoryChart = null
let stockTrendChart = null

// 低库存物资列表
const lowStockMaterials = ref([
  { id: 1, name: 'A4打印纸', categoryName: '文具', stockQuantity: 30, stockWarningValue: 50, unit: '包' },
  { id: 2, name: '黑色中性笔', categoryName: '文具', stockQuantity: 25, stockWarningValue: 100, unit: '支' },
  { id: 3, name: '硒鼓', categoryName: '耗材', stockQuantity: 2, stockWarningValue: 5, unit: '个' },
  { id: 4, name: '键盘', categoryName: '电子设备', stockQuantity: 3, stockWarningValue: 8, unit: '个' },
  { id: 5, name: '鼠标', categoryName: '电子设备', stockQuantity: 4, stockWarningValue: 10, unit: '个' }
])

// 库存分布数据
const stockDistributionData = [
  { name: '文具', value: 670, color: '#409eff' },
  { name: '耗材', value: 5, color: '#67c23a' },
  { name: '电子设备', value: 30, color: '#e6a23c' },
  { name: '家具', value: 15, color: '#f56c6c' },
  { name: '生产物资', value: 15000, color: '#909399' }
]

// 物资分类数据
const categoryData = [
  { name: '文具', value: 45, color: '#409eff' },
  { name: '耗材', value: 15, color: '#67c23a' },
  { name: '电子设备', value: 20, color: '#e6a23c' },
  { name: '家具', value: 10, color: '#f56c6c' },
  { name: '生产物资', value: 10, color: '#909399' }
]

// 库存变化趋势数据
const stockTrendData = {
  dates: ['1月', '2月', '3月', '4月', '5月', '6月'],
  stock: [12000, 13500, 14200, 13800, 14500, 15800]
}

// 初始化库存分布图表
const initStockDistChart = () => {
  if (!stockDistChartRef.value) return
  
  stockDistChart = echarts.init(stockDistChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: stockDistributionData.map(item => item.name)
    },
    yAxis: {
      type: 'value',
      name: '库存数量'
    },
    series: [{
      data: stockDistributionData,
      type: 'bar',
      itemStyle: {
        color: function(params) {
          return params.data.color
        }
      },
      label: {
        show: true,
        position: 'top',
        formatter: '{c}'
      }
    }]
  }
  stockDistChart.setOption(option)
}

// 初始化物资分类占比图表
const initCategoryChart = () => {
  if (!categoryChartRef.value) return
  
  categoryChart = echarts.init(categoryChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: categoryData.map(item => item.name)
    },
    series: [{
      name: '物资分类',
      type: 'pie',
      radius: '50%',
      data: categoryData,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  categoryChart.setOption(option)
}

// 初始化库存变化趋势图表
const initStockTrendChart = () => {
  if (!stockTrendChartRef.value) return
  
  stockTrendChart = echarts.init(stockTrendChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: stockTrendData.dates
    },
    yAxis: {
      type: 'value',
      name: '库存数量'
    },
    series: [{
      data: stockTrendData.stock,
      type: 'line',
      smooth: true,
      itemStyle: {
        color: '#409eff'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
        ])
      }
    }]
  }
  stockTrendChart.setOption(option)
}

// 申请补货
const handleReplenish = (row) => {
  ElMessage.success(`已为 ${row.name} 生成补货申请`)
}

// 处理窗口大小变化
const handleResize = () => {
  if (stockDistChart) stockDistChart.resize()
  if (categoryChart) categoryChart.resize()
  if (stockTrendChart) stockTrendChart.resize()
}

// 页面挂载
onMounted(() => {
  initStockDistChart()
  initCategoryChart()
  initStockTrendChart()
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
})

// 页面卸载
onUnmounted(() => {
  // 销毁图表实例
  if (stockDistChart) stockDistChart.dispose()
  if (categoryChart) categoryChart.dispose()
  if (stockTrendChart) stockTrendChart.dispose()
  
  // 移除事件监听
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.dashboard-container {
  padding: var(--spacing-xl);
  background-color: var(--bg-secondary);
  min-height: calc(100vh - 100px);
  transition: var(--transition-base);
}

.page-title {
  font-size: var(--font-size-xxl);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-xxl);
  transition: var(--transition-base);
  padding-bottom: var(--spacing-lg);
  border-bottom: 2px solid var(--border-color);
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.stat-cards {
  margin-bottom: var(--spacing-xl);
}

.stat-card {
  height: 130px;
  display: flex;
  align-items: center;
  border-radius: var(--border-radius-xl);
  overflow: hidden;
  position: relative;
  background: linear-gradient(135deg, var(--bg-primary) 0%, var(--bg-secondary) 100%);
  transition: var(--transition-base);
  border: 1px solid var(--border-color);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--primary-color);
  transition: var(--transition-base);
}

.stat-card:nth-child(1)::before {
  background: var(--primary-color);
}

.stat-card:nth-child(2)::before {
  background: var(--success-color);
}

.stat-card:nth-child(3)::before {
  background: var(--warning-color);
}

.stat-card:nth-child(4)::before {
  background: var(--danger-color);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-hover);
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 0 var(--spacing-xl);
  position: relative;
  z-index: 1;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 36px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--spacing-xs);
  transition: var(--transition-base);
  line-height: 1.2;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--text-regular);
  transition: var(--transition-base);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 500;
}

.stat-icon {
  font-size: 52px;
  opacity: 0.1;
  transition: var(--transition-base);
}

.stat-icon.materials {
  color: var(--primary-color);
}

.stat-icon.stock {
  color: var(--success-color);
}

.stat-icon.warning {
  color: var(--warning-color);
}

.stat-icon.categories {
  color: var(--danger-color);
}

.stat-card:hover .stat-icon {
  opacity: 0.15;
  transform: scale(1.1) rotate(5deg);
}

.chart-row {
  margin-bottom: var(--spacing-xl);
}

.chart-card {
  height: 100%;
  border-radius: var(--border-radius-xl);
  overflow: hidden;
  transition: var(--transition-base);
  border: 1px solid var(--border-color);
}

.chart-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-hover);
}

.chart {
  width: 100%;
  height: 420px;
  transition: var(--transition-base);
}

.warning-card {
  margin-top: var(--spacing-xl);
  border-radius: var(--border-radius-xl);
  overflow: hidden;
  transition: var(--transition-base);
  border: 1px solid var(--border-color);
}

.warning-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-hover);
}

.card-header {
  font-weight: 600;
  font-size: var(--font-size-lg);
  color: var(--text-primary);
  transition: var(--transition-base);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.card-header::before {
  content: '';
  width: 4px;
  height: 18px;
  background: var(--primary-color);
  border-radius: var(--border-radius-round);
}

/* 表格样式优化 */
.warning-card :deep(.el-table) {
  border-radius: 0 0 var(--border-radius-xl) var(--border-radius-xl);
  border: none;
}

.warning-card :deep(.el-table__header-wrapper) {
  border-radius: 0;
}

.warning-card :deep(.el-table__body-wrapper tr:hover > td) {
  background-color: var(--bg-tertiary) !important;
}

/* 响应式设计 */
@media screen and (max-width: 1200px) {
  .dashboard-container {
    padding: var(--spacing-lg);
  }
  
  .chart {
    height: 380px;
  }
}

@media screen and (max-width: 768px) {
  .dashboard-container {
    padding: var(--spacing-lg);
  }
  
  .page-title {
    font-size: var(--font-size-xl);
    margin-bottom: var(--spacing-xl);
  }
  
  .stat-cards {
    margin-bottom: var(--spacing-lg);
  }
  
  .stat-card {
    height: 120px;
  }
  
  .stat-number {
    font-size: 28px;
  }
  
  .stat-icon {
    font-size: 44px;
  }
  
  .chart-row {
    margin-bottom: var(--spacing-lg);
  }
  
  .chart {
    height: 320px;
  }
  
  .warning-card {
    margin-top: var(--spacing-lg);
  }
}

@media screen and (max-width: 480px) {
  .dashboard-container {
    padding: var(--spacing-md);
  }
  
  .page-title {
    font-size: var(--font-size-lg);
    margin-bottom: var(--spacing-lg);
  }
  
  .stat-content {
    flex-direction: column;
    text-align: center;
    padding: 0 var(--spacing-md);
  }
  
  .stat-card {
    height: 140px;
  }
  
  .stat-number {
    font-size: 24px;
  }
  
  .stat-icon {
    margin-top: var(--spacing-sm);
    font-size: 36px;
  }
  
  .chart {
    height: 260px;
  }
  
  .card-header {
    font-size: var(--font-size-base);
  }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .stat-card {
    background: linear-gradient(135deg, var(--bg-primary) 0%, var(--bg-secondary) 100%);
    border-color: var(--border-color);
  }
  
  .page-title {
    color: var(--text-primary);
    border-bottom-color: var(--border-color);
  }
  
  .stat-number {
    color: var(--text-primary);
  }
  
  .stat-label {
    color: var(--text-regular);
  }
  
  .card-header {
    color: var(--text-primary);
  }
  
  .warning-card :deep(.el-table__body-wrapper tr:hover > td) {
    background-color: var(--bg-tertiary) !important;
  }
}

/* 加载状态 */
.stat-card.loading {
  opacity: 0.6;
  pointer-events: none;
}

/* 数据增长动画 */
@keyframes countUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-number {
  animation: countUp 0.8s ease-out forwards;
}

.stat-label {
  animation: countUp 0.8s ease-out 0.2s forwards;
  opacity: 0;
}

.stat-icon {
  animation: countUp 0.8s ease-out 0.4s forwards;
  opacity: 0;
}
</style>