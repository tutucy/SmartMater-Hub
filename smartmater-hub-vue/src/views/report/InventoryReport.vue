<template>
  <div class="inventory-report-container">
    <h2 class="page-title">库存报表</h2>
    
    <el-card shadow="hover" class="report-card">
      <template #header>
        <div class="card-header">
          <span>库存明细表</span>
          <div class="header-actions">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 300px; margin-right: 10px;"
            />
            <el-select
              v-model="categoryId"
              placeholder="选择物资分类"
              clearable
              style="width: 150px; margin-right: 10px;"
            >
              <el-option label="全部分类" value="" />
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
            <el-button type="primary" @click="fetchInventoryDetails" icon="Search">
              查询
            </el-button>
            <el-button type="success" @click="handleExportExcel" icon="DocumentCopy">
              导出Excel
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 库存明细表 -->
      <div class="table-container">
        <el-table
          v-loading="loading"
          :data="inventoryDetails"
          style="width: 100%"
          border
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="materialName" label="物资名称" min-width="200" />
          <el-table-column prop="specification" label="型号规格" width="150" />
          <el-table-column prop="brand" label="品牌" width="120" />
          <el-table-column prop="unit" label="单位" width="80" />
          <el-table-column prop="categoryName" label="分类" width="120" />
          <el-table-column prop="supplierName" label="供应商" width="120" />
          <el-table-column prop="stockQuantity" label="库存数量" width="120" align="right" />
          <el-table-column prop="price" label="单价(元)" width="120" align="right" :formatter="formatPrice" />
          <el-table-column prop="stockAmount" label="库存金额(元)" width="150" align="right" :formatter="formatPrice" />
          <el-table-column prop="stockWarningValue" label="预警值" width="120" align="right" />
          <el-table-column prop="lastInboundDate" label="最后入库时间" width="180" />
          <el-table-column prop="lastOutboundDate" label="最后出库时间" width="180" />
        </el-table>
      </div>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <el-card shadow="hover" class="report-card">
      <template #header>
        <div class="card-header">
          <span>库存汇总表</span>
          <div class="header-actions">
            <el-select
              v-model="summaryType"
              placeholder="汇总类型"
              style="width: 150px; margin-right: 10px;"
              @change="handleSummaryTypeChange"
            >
              <el-option label="按分类汇总" value="category" />
              <el-option label="按供应商汇总" value="supplier" />
              <el-option label="按库存状态汇总" value="status" />
            </el-select>
            <el-button type="primary" @click="handleExportSummary" icon="DocumentCopy">
              导出Excel
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 库存汇总表 -->
      <div class="summary-container">
        <el-table
          v-loading="summaryLoading"
          :data="inventorySummary"
          style="width: 100%"
          border
        >
          <el-table-column 
            prop="name" 
            :label="summaryType === 'category' ? '分类名称' : summaryType === 'supplier' ? '供应商名称' : '库存状态'" 
            min-width="200" 
          />
          <el-table-column prop="count" label="物资数量" width="120" align="right" />
          <el-table-column prop="totalQuantity" label="总库存数量" width="150" align="right" />
          <el-table-column prop="totalAmount" label="总库存金额(元)" width="180" align="right" :formatter="formatPrice" />
          <el-table-column prop="avgPrice" label="平均单价(元)" width="150" align="right" :formatter="formatPrice" />
          <el-table-column prop="maxPrice" label="最高单价(元)" width="150" align="right" :formatter="formatPrice" />
          <el-table-column prop="minPrice" label="最低单价(元)" width="150" align="right" :formatter="formatPrice" />
        </el-table>
      </div>
      
      <!-- 汇总图表 -->
      <div class="chart-container">
        <div ref="chartRef" class="chart" style="height: 400px;"></div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch, nextTick } from 'vue'
import request from '../../services/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'

const inventoryDetails = ref([])
const inventorySummary = ref([])
const categories = ref([])
const loading = ref(false)
const summaryLoading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dateRange = ref([])
const categoryId = ref('')
const summaryType = ref('category')
const chartRef = ref(null)
let chartInstance = null

// 格式化价格
const formatPrice = (row, column, cellValue) => {
  return `¥${cellValue.toFixed(2)}`
}

// 获取库存明细
const fetchInventoryDetails = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟库存明细数据
    const mockDetails = [
      {
        id: 1,
        materialName: 'A4打印纸',
        specification: '80g 500张/包',
        brand: '得力',
        unit: '包',
        categoryId: 2,
        categoryName: '文具',
        supplierId: 1,
        supplierName: '得力办公',
        stockQuantity: 150,
        price: 25.5,
        stockAmount: 3825,
        stockWarningValue: 50,
        lastInboundDate: '2024-01-15 10:30:00',
        lastOutboundDate: '2024-01-20 14:45:00'
      },
      {
        id: 2,
        materialName: '黑色中性笔',
        specification: '0.5mm',
        brand: '晨光',
        unit: '支',
        categoryId: 2,
        categoryName: '文具',
        supplierId: 2,
        supplierName: '晨光文具',
        stockQuantity: 300,
        price: 2.5,
        stockAmount: 750,
        stockWarningValue: 100,
        lastInboundDate: '2024-01-10 09:15:00',
        lastOutboundDate: '2024-01-22 11:20:00'
      },
      {
        id: 3,
        materialName: '鼠标',
        specification: '有线 USB',
        brand: '罗技',
        unit: '个',
        categoryId: 5,
        categoryName: '电子设备',
        supplierId: 3,
        supplierName: '科技数码',
        stockQuantity: 20,
        price: 89.0,
        stockAmount: 1780,
        stockWarningValue: 10,
        lastInboundDate: '2024-01-05 16:30:00',
        lastOutboundDate: '2024-01-18 15:10:00'
      },
      {
        id: 4,
        materialName: '键盘',
        specification: '机械键盘 青轴',
        brand: '雷蛇',
        unit: '个',
        categoryId: 5,
        categoryName: '电子设备',
        supplierId: 3,
        supplierName: '科技数码',
        stockQuantity: 8,
        price: 299.0,
        stockAmount: 2392,
        stockWarningValue: 5,
        lastInboundDate: '2024-01-08 14:20:00',
        lastOutboundDate: '2024-01-15 13:45:00'
      },
      {
        id: 5,
        materialName: '文件夹',
        specification: 'A4 塑料材质',
        brand: '齐心',
        unit: '个',
        categoryId: 2,
        categoryName: '文具',
        supplierId: 4,
        supplierName: '齐心办公',
        stockQuantity: 450,
        price: 5.0,
        stockAmount: 2250,
        stockWarningValue: 200,
        lastInboundDate: '2024-01-12 11:00:00',
        lastOutboundDate: '2024-01-25 09:30:00'
      },
      {
        id: 6,
        materialName: '订书机',
        specification: '标准型',
        brand: '得力',
        unit: '个',
        categoryId: 2,
        categoryName: '文具',
        supplierId: 1,
        supplierName: '得力办公',
        stockQuantity: 35,
        price: 18.5,
        stockAmount: 647.5,
        stockWarningValue: 20,
        lastInboundDate: '2024-01-03 15:45:00',
        lastOutboundDate: '2024-01-20 10:15:00'
      },
      {
        id: 7,
        materialName: '硒鼓',
        specification: 'HP LaserJet Pro M404n',
        brand: '惠普',
        unit: '个',
        categoryId: 3,
        categoryName: '耗材',
        supplierId: 5,
        supplierName: '惠普耗材',
        stockQuantity: 5,
        price: 499.0,
        stockAmount: 2495,
        stockWarningValue: 3,
        lastInboundDate: '2024-01-01 10:30:00',
        lastOutboundDate: '2024-01-10 14:20:00'
      },
      {
        id: 8,
        materialName: '打印机',
        specification: '激光打印机',
        brand: '惠普',
        unit: '台',
        categoryId: 5,
        categoryName: '电子设备',
        supplierId: 5,
        supplierName: '惠普耗材',
        stockQuantity: 2,
        price: 1299.0,
        stockAmount: 2598,
        stockWarningValue: 5,
        lastInboundDate: '2024-01-10 11:00:00',
        lastOutboundDate: '2024-01-15 16:45:00'
      },
      {
        id: 9,
        materialName: '办公椅',
        specification: '人体工学椅',
        brand: '永艺',
        unit: '把',
        categoryId: 6,
        categoryName: '家具',
        supplierId: 6,
        supplierName: '永艺家具',
        stockQuantity: 10,
        price: 1500.0,
        stockAmount: 15000,
        stockWarningValue: 8,
        lastInboundDate: '2024-01-05 09:30:00',
        lastOutboundDate: '2024-01-20 13:15:00'
      },
      {
        id: 10,
        materialName: '办公桌',
        specification: '1.4m',
        brand: '永艺',
        unit: '张',
        categoryId: 6,
        categoryName: '家具',
        supplierId: 6,
        supplierName: '永艺家具',
        stockQuantity: 5,
        price: 1200.0,
        stockAmount: 6000,
        stockWarningValue: 3,
        lastInboundDate: '2024-01-08 14:00:00',
        lastOutboundDate: '2024-01-18 10:30:00'
      }
    ]
    
    // 模拟分类数据
    const mockCategories = [
      { id: 2, name: '文具' },
      { id: 3, name: '耗材' },
      { id: 5, name: '电子设备' },
      { id: 6, name: '家具' }
    ]
    
    // 模拟筛选
    let filteredDetails = mockDetails
    if (categoryId.value) {
      filteredDetails = filteredDetails.filter(item => item.categoryId === parseInt(categoryId.value))
    }
    
    inventoryDetails.value = filteredDetails
    total.value = filteredDetails.length
    categories.value = mockCategories
    
    // 获取库存汇总数据
    fetchInventorySummary()
  } catch (error) {
    console.error('获取库存明细失败:', error)
    ElMessage.error('获取库存明细失败')
  } finally {
    loading.value = false
  }
}

// 获取库存汇总
const fetchInventorySummary = async () => {
  summaryLoading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 300))
    
    // 根据汇总类型生成不同的汇总数据
    if (summaryType.value === 'category') {
      // 按分类汇总
      inventorySummary.value = [
        {
          name: '文具',
          count: 4,
          totalQuantity: 670,
          totalAmount: 9472.5,
          avgPrice: 14.14,
          maxPrice: 18.5,
          minPrice: 2.5
        },
        {
          name: '耗材',
          count: 1,
          totalQuantity: 5,
          totalAmount: 2495,
          avgPrice: 499,
          maxPrice: 499,
          minPrice: 499
        },
        {
          name: '电子设备',
          count: 3,
          totalQuantity: 30,
          totalAmount: 7785,
          avgPrice: 259.5,
          maxPrice: 1299,
          minPrice: 89
        },
        {
          name: '家具',
          count: 2,
          totalQuantity: 15,
          totalAmount: 21000,
          avgPrice: 1400,
          maxPrice: 1500,
          minPrice: 1200
        }
      ]
    } else if (summaryType.value === 'supplier') {
      // 按供应商汇总
      inventorySummary.value = [
        {
          name: '得力办公',
          count: 2,
          totalQuantity: 185,
          totalAmount: 4472.5,
          avgPrice: 24.18,
          maxPrice: 25.5,
          minPrice: 18.5
        },
        {
          name: '晨光文具',
          count: 1,
          totalQuantity: 300,
          totalAmount: 750,
          avgPrice: 2.5,
          maxPrice: 2.5,
          minPrice: 2.5
        },
        {
          name: '科技数码',
          count: 2,
          totalQuantity: 28,
          totalAmount: 4182,
          avgPrice: 149.36,
          maxPrice: 299,
          minPrice: 89
        },
        {
          name: '齐心办公',
          count: 1,
          totalQuantity: 450,
          totalAmount: 2250,
          avgPrice: 5,
          maxPrice: 5,
          minPrice: 5
        },
        {
          name: '惠普耗材',
          count: 2,
          totalQuantity: 7,
          totalAmount: 5093,
          avgPrice: 727.57,
          maxPrice: 1299,
          minPrice: 499
        },
        {
          name: '永艺家具',
          count: 2,
          totalQuantity: 15,
          totalAmount: 21000,
          avgPrice: 1400,
          maxPrice: 1500,
          minPrice: 1200
        }
      ]
    } else if (summaryType.value === 'status') {
      // 按库存状态汇总
      inventorySummary.value = [
        {
          name: '正常',
          count: 6,
          totalQuantity: 1190,
          totalAmount: 36842.5,
          avgPrice: 30.96,
          maxPrice: 1500,
          minPrice: 2.5
        },
        {
          name: '低库存',
          count: 3,
          totalQuantity: 47,
          totalAmount: 8785,
          avgPrice: 186.91,
          maxPrice: 1299,
          minPrice: 89
        },
        {
          name: '缺货',
          count: 1,
          totalQuantity: 0,
          totalAmount: 0,
          avgPrice: 0,
          maxPrice: 0,
          minPrice: 0
        }
      ]
    }
    
    // 生成图表
    nextTick(() => {
      initChart()
    })
  } catch (error) {
    console.error('获取库存汇总失败:', error)
    ElMessage.error('获取库存汇总失败')
  } finally {
    summaryLoading.value = false
  }
}

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return
  
  // 销毁旧图表实例
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  // 创建新图表实例
  chartInstance = echarts.init(chartRef.value)
  
  // 准备图表数据
  const chartData = {
    xAxisData: inventorySummary.value.map(item => item.name),
    totalAmountData: inventorySummary.value.map(item => item.totalAmount),
    totalQuantityData: inventorySummary.value.map(item => item.totalQuantity)
  }
  
  // 图表配置
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      }
    },
    legend: {
      data: ['总库存金额', '总库存数量']
    },
    xAxis: [
      {
        type: 'category',
        data: chartData.xAxisData,
        axisPointer: {
          type: 'shadow'
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '总库存金额(元)',
        axisLabel: {
          formatter: '¥{value}'
        }
      },
      {
        type: 'value',
        name: '总库存数量',
        axisLabel: {
          formatter: '{value}'
        }
      }
    ],
    series: [
      {
        name: '总库存金额',
        type: 'bar',
        data: chartData.totalAmountData,
        itemStyle: {
          color: '#409eff'
        }
      },
      {
        name: '总库存数量',
        type: 'line',
        yAxisIndex: 1,
        data: chartData.totalQuantityData,
        smooth: true,
        itemStyle: {
          color: '#67c23a'
        }
      }
    ]
  }
  
  // 设置图表选项
  chartInstance.setOption(option)
  
  // 监听窗口大小变化，调整图表大小
  window.addEventListener('resize', handleResize)
}

// 处理汇总类型变化
const handleSummaryTypeChange = () => {
  fetchInventorySummary()
}

// 处理图表大小调整
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 处理导出Excel
const handleExportExcel = () => {
  ElMessage.success('导出Excel功能开发中...')
}

// 分页相关
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  fetchInventoryDetails()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  fetchInventoryDetails()
}

// 页面挂载时获取数据
onMounted(() => {
  fetchInventoryDetails()
  
  // 页面卸载时清理资源
  window.addEventListener('beforeunload', () => {
    if (chartInstance) {
      chartInstance.dispose()
      chartInstance = null
    }
    window.removeEventListener('resize', handleResize)
  })
})
</script>

<style scoped>
.inventory-report-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 100px);
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.report-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.table-container {
  margin-bottom: 20px;
  overflow-x: auto;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

.chart-container {
  margin-top: 20px;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .inventory-report-container {
    padding: 10px;
  }
  
  .page-title {
    font-size: 20px;
    margin-bottom: 15px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .header-actions {
    width: 100%;
    flex-wrap: wrap;
  }
  
  /* 图表容器响应式 */
  .chart-container {
    width: 100%;
    overflow-x: auto;
  }
  
  /* 调整图表大小 */
  .chart-container :deep(.chart-box) {
    width: 100%;
    min-width: 400px;
    height: 300px;
  }
  
  /* 表格响应式处理 */
  .table-container {
    width: 100%;
    overflow-x: auto;
  }
  
  .pagination {
    justify-content: center;
  }
  
  /* 筛选区域响应式 */
  .el-form {
    width: 100%;
  }
  
  .el-form-item {
    margin-bottom: 12px;
  }
  
  /* 按钮组响应式 */
  .header-actions .el-button {
    flex: 1;
    min-width: 100px;
  }
}

@media screen and (max-width: 480px) {
  .header-actions {
    flex-direction: column;
  }
  
  .header-actions .el-button {
    width: 100%;
    margin-right: 0;
  }
  
  /* 图表容器响应式 */
  .chart-container :deep(.chart-box) {
    min-width: 320px;
    height: 250px;
  }
}
</style>