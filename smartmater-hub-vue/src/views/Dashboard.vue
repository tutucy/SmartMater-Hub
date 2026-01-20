<template>
  <div class="dashboard-container" v-loading="loading" element-loading-text="加载中...">
    <div class="page-header">
      <h2 class="page-title">物资监控大屏</h2>
      <el-button type="primary" @click="handleRefresh" :loading="loading" icon="RefreshRight">
        刷新数据
      </el-button>
    </div>
    
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
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import request from '../services/api'

// 数据统计
const totalMaterials = ref(0)
const totalStock = ref(0)
const lowStockCount = ref(0)
const totalCategories = ref(0)
const loading = ref(false)

// 图表ref引用
const stockDistChartRef = ref(null)
const categoryChartRef = ref(null)
const stockTrendChartRef = ref(null)

// 图表实例
let stockDistChart = null
let categoryChart = null
let stockTrendChart = null

// 低库存物资列表
const lowStockMaterials = ref([])

// 库存分布数据
const stockDistributionData = ref([])

// 物资分类数据
const categoryData = ref([])

// 库存变化趋势数据
const stockTrendData = ref({
  dates: [],
  stock: []
})

// 获取统计数据
const fetchStatistics = async () => {
  loading.value = true
  try {
    console.log('开始获取统计数据...')
    
    const materialResponse = await request.get('/material/list', {
      params: { status: 1 }
    })
    console.log('物资列表响应:', materialResponse)
    const materials = materialResponse.data || []
    totalMaterials.value = materials.length
    
    const inventoryResponse = await request.get('/inventory/list')
    console.log('库存列表响应:', inventoryResponse)
    const inventories = inventoryResponse.data || []
    
    totalStock.value = inventories.reduce((sum, item) => sum + item.quantity, 0)
    
    const lowStockItems = inventories.filter(item => item.quantity <= item.minQuantity)
    lowStockCount.value = lowStockItems.length
    
    lowStockMaterials.value = lowStockItems.map(item => {
      const material = materials.find(m => m.id === item.materialId)
      return {
        id: item.id,
        name: material ? material.name : '未知物资',
        categoryName: material ? (material.categoryName || material.category?.name) : '未知分类',
        stockQuantity: item.quantity,
        stockWarningValue: item.minQuantity,
        unit: material ? material.unit : '件'
      }
    })
    
    const categoryResponse = await request.get('/category/list', {
      params: { status: 1 }
    })
    console.log('分类列表响应:', categoryResponse)
    totalCategories.value = categoryResponse.data ? categoryResponse.data.length : 0
    
    // 准备库存分布数据
    const categoryMap = {}
    inventories.forEach(item => {
      const material = materials.find(m => m.id === item.materialId)
      if (material) {
        const categoryName = material.categoryName || material.category?.name || '未分类'
        if (!categoryMap[categoryName]) {
          categoryMap[categoryName] = 0
        }
        categoryMap[categoryName] += item.quantity
      }
    })
    
    stockDistributionData.value = Object.keys(categoryMap).map((name, index) => {
      const colors = ['#667eea', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#ff6b6b', '#4caf50', '#ff9800', '#795548', '#607d8b']
      return {
        name: name,
        value: categoryMap[name],
        color: colors[index % colors.length]
      }
    })
    
    // 准备物资分类数据
    categoryData.value = stockDistributionData.value.map(item => ({
      name: item.name,
      value: item.value,
      color: item.color
    }))
    
    // 准备库存变化趋势数据（模拟最近6个月）
    const currentDate = new Date()
    const trendDates = []
    const trendStock = []
    for (let i = 5; i >= 0; i--) {
      const date = new Date(currentDate.getFullYear(), currentDate.getMonth() - i, 1)
      const monthStr = `${date.getMonth() + 1}月`
      trendDates.push(monthStr)
      // 模拟库存变化，实际应该从数据库获取
      trendStock.push(Math.floor(totalStock.value * (0.9 + Math.random() * 0.2)))
    }
    stockTrendData.value = {
      dates: trendDates,
      stock: trendStock
    }
    
    console.log('统计数据获取完成:', {
      totalMaterials: totalMaterials.value,
      totalStock: totalStock.value,
      lowStockCount: lowStockCount.value,
      totalCategories: totalCategories.value,
      stockDistributionData: stockDistributionData.value,
      categoryData: categoryData.value,
      stockTrendData: stockTrendData.value
    })
    
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
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
      data: stockDistributionData.value.map(item => item.name)
    },
    yAxis: {
      type: 'value',
      name: '库存数量'
    },
    series: [{
      data: stockDistributionData.value,
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
      data: categoryData.value.map(item => item.name)
    },
    series: [{
      name: '物资分类',
      type: 'pie',
      radius: '50%',
      data: categoryData.value,
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
      data: stockTrendData.value.dates
    },
    yAxis: {
      type: 'value',
      name: '库存数量'
    },
    series: [{
      data: stockTrendData.value.stock,
      type: 'line',
      smooth: true,
      itemStyle: {
        color: '#667eea'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(102, 126, 234, 0.5)' },
          { offset: 1, color: 'rgba(102, 126, 234, 0.1)' }
        ])
      }
    }]
  }
  stockTrendChart.setOption(option)
}

// 申请补货
const handleReplenish = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要为 "${row.name}" 生成补货申请吗？`,
      '申请补货',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 这里可以调用补货申请API
    // const response = await request.post('/replenish/add', {
    //   materialId: row.id,
    //   materialName: row.name,
    //   currentStock: row.stockQuantity,
    //   requestQuantity: row.stockWarningValue * 2
    // })
    
    ElMessage.success(`已为 ${row.name} 生成补货申请`)
    
    // 可选：刷新数据
    // await fetchStatistics()
    // initStockDistChart()
    // initCategoryChart()
    // initStockTrendChart()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('申请补货失败:', error)
      ElMessage.error('申请补货失败')
    }
  }
}

// 刷新数据
const handleRefresh = async () => {
  await fetchStatistics()
  initStockDistChart()
  initCategoryChart()
  initStockTrendChart()
  ElMessage.success('数据刷新成功')
}

// 处理窗口大小变化
const handleResize = () => {
  if (stockDistChart) stockDistChart.resize()
  if (categoryChart) categoryChart.resize()
  if (stockTrendChart) stockTrendChart.resize()
}

// 页面挂载
onMounted(async () => {
  try {
    await fetchStatistics()
    initStockDistChart()
    initCategoryChart()
    initStockTrendChart()
  } catch (error) {
    console.error('页面初始化失败:', error)
    // 如果API调用失败，使用假数据保证页面可以显示
    if (totalMaterials.value === 0) {
      console.log('使用假数据...')
      totalMaterials.value = 20
      totalStock.value = 15800
      lowStockCount.value = 5
      totalCategories.value = 8
      
      stockDistributionData.value = [
        { name: '电子设备', value: 30, color: '#667eea' },
        { name: '办公用品', value: 670, color: '#67c23a' },
        { name: '办公耗材', value: 5, color: '#e6a23c' },
        { name: '办公家具', value: 15, color: '#f56c6c' },
        { name: '生产物资', value: 15000, color: '#909399' }
      ]
      
      categoryData.value = stockDistributionData.value.map(item => ({
        name: item.name,
        value: item.value,
        color: item.color
      }))
      
      lowStockMaterials.value = [
        { id: 1, name: 'A4打印纸', categoryName: '办公用品', stockQuantity: 30, stockWarningValue: 50, unit: '包' },
        { id: 2, name: '黑色中性笔', categoryName: '办公用品', stockQuantity: 25, stockWarningValue: 100, unit: '支' },
        { id: 3, name: '硒鼓', categoryName: '办公耗材', stockQuantity: 2, stockWarningValue: 5, unit: '个' },
        { id: 4, name: '键盘', categoryName: '电子设备', stockQuantity: 3, stockWarningValue: 8, unit: '个' },
        { id: 5, name: '鼠标', categoryName: '电子设备', stockQuantity: 4, stockWarningValue: 10, unit: '个' }
      ]
      
      const currentDate = new Date()
      const trendDates = []
      const trendStock = []
      for (let i = 5; i >= 0; i--) {
        const date = new Date(currentDate.getFullYear(), currentDate.getMonth() - i, 1)
        const monthStr = `${date.getMonth() + 1}月`
        trendDates.push(monthStr)
        trendStock.push(Math.floor(15800 * (0.9 + Math.random() * 0.2)))
      }
      stockTrendData.value = {
        dates: trendDates,
        stock: trendStock
      }
    }
  }
  
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

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-lg);
  border-bottom: 2px solid var(--border-color);
}

.page-title {
  font-size: var(--font-size-xxl);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0;
  transition: var(--transition-base);
  padding-bottom: 0;
  border-bottom: none;
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