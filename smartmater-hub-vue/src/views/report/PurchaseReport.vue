<template>
  <div class="purchase-report-container">
    <h2 class="page-title">采购报表</h2>
    
    <el-card shadow="hover" class="report-card">
      <template #header>
        <div class="card-header">
          <span>采购报表</span>
          <div class="header-actions">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
            <el-button type="primary" @click="fetchReportData" icon="RefreshRight">
              刷新数据
            </el-button>
            <el-button type="success" @click="handleExport" icon="Download">
              导出报表
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 报表类型选择 -->
      <div class="report-type-selector">
        <el-radio-group v-model="reportType" @change="handleReportTypeChange">
          <el-radio-button label="purchase_order">采购订单报表</el-radio-button>
          <el-radio-button label="purchase_analysis">采购分析报表</el-radio-button>
        </el-radio-group>
      </div>
      
      <!-- 采购订单报表 -->
      <div v-if="reportType === 'purchase_order'" class="report-content">
        <el-table
          v-loading="loading"
          :data="purchaseOrderData"
          style="width: 100%"
          border
        >
          <el-table-column prop="orderNo" label="订单号" width="180" />
          <el-table-column prop="orderDate" label="采购日期" width="150" />
          <el-table-column prop="supplierName" label="供应商" width="180" />
          <el-table-column prop="materialName" label="物资名称" width="180" />
          <el-table-column prop="model" label="型号规格" width="150" />
          <el-table-column prop="quantity" label="采购数量" width="120" />
          <el-table-column prop="unitPrice" label="单价" width="120" :formatter="currencyFormatter" />
          <el-table-column prop="totalAmount" label="总金额" width="120" :formatter="currencyFormatter" />
          <el-table-column prop="orderStatus" label="订单状态" width="120">
            <template #default="scope">
              <el-tag :type="getStatusTagType(scope.row.orderStatus)">
                {{ getStatusText(scope.row.orderStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="paymentStatus" label="付款状态" width="120">
            <template #default="scope">
              <el-tag :type="getPaymentStatusTagType(scope.row.paymentStatus)">
                {{ getPaymentStatusText(scope.row.paymentStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" />
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            layout="total, sizes, prev, pager, next, jumper"
            :total="purchaseOrderTotal"
            :page-size="pageSize"
            :current-page="currentPage"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
      
      <!-- 采购分析报表 -->
      <div v-else-if="reportType === 'purchase_analysis'" class="report-content">
        <!-- 图表区域 -->
        <div class="chart-container">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>按供应商统计采购金额</span>
              </div>
            </template>
            <div ref="supplierChartRef" class="chart"></div>
          </el-card>
          
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>按物资分类统计采购金额</span>
              </div>
            </template>
            <div ref="categoryChartRef" class="chart"></div>
          </el-card>
          
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>采购价格波动趋势</span>
              </div>
            </template>
            <div ref="priceTrendChartRef" class="chart"></div>
          </el-card>
        </div>
        
        <!-- 供应商采购排名 -->
        <el-card shadow="hover" class="analysis-card">
          <template #header>
            <div class="analysis-header">
              <span>供应商采购排名</span>
            </div>
          </template>
          <el-table
            v-loading="loading"
            :data="supplierRankingData"
            style="width: 100%"
            border
          >
            <el-table-column prop="rank" label="排名" width="80" />
            <el-table-column prop="supplierName" label="供应商" width="200" />
            <el-table-column prop="purchaseCount" label="采购次数" width="120" />
            <el-table-column prop="totalAmount" label="采购总金额" width="150" :formatter="currencyFormatter" />
            <el-table-column prop="avgOrderAmount" label="平均订单金额" width="150" :formatter="currencyFormatter" />
            <el-table-column prop="satisfaction" label="满意度" width="120">
              <template #default="scope">
                <el-rate
                  v-model="scope.row.satisfaction"
                  :max="5"
                  :disabled="true"
                  allow-half
                />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const reportType = ref('purchase_order')
const dateRange = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

// 采购订单报表数据
const purchaseOrderData = ref([])
const purchaseOrderTotal = ref(0)

// 采购分析报表数据
const supplierRankingData = ref([])

// 图表实例引用
const supplierChartRef = ref(null)
const categoryChartRef = ref(null)
const priceTrendChartRef = ref(null)
let supplierChart = null
let categoryChart = null
let priceTrendChart = null

// 获取报表数据
const fetchReportData = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    if (reportType.value === 'purchase_order') {
      // 模拟采购订单报表数据
      purchaseOrderData.value = [
        {
          orderNo: 'PO202401001',
          orderDate: '2024-01-10',
          supplierName: '供应商A',
          materialName: '办公用品套装',
          model: '标准型',
          quantity: 50,
          unitPrice: 120,
          totalAmount: 6000,
          orderStatus: 'completed',
          paymentStatus: 'paid',
          remark: ''
        },
        {
          orderNo: 'PO202401002',
          orderDate: '2024-01-15',
          supplierName: '供应商B',
          materialName: '打印纸',
          model: 'A4 70g',
          quantity: 100,
          unitPrice: 25,
          totalAmount: 2500,
          orderStatus: 'completed',
          paymentStatus: 'paid',
          remark: ''
        },
        {
          orderNo: 'PO202402001',
          orderDate: '2024-02-05',
          supplierName: '供应商A',
          materialName: '办公椅',
          model: '人体工学',
          quantity: 20,
          unitPrice: 800,
          totalAmount: 16000,
          orderStatus: 'pending',
          paymentStatus: 'unpaid',
          remark: '加急订单'
        },
        {
          orderNo: 'PO202402002',
          orderDate: '2024-02-10',
          supplierName: '供应商C',
          materialName: '服务器',
          model: 'DELL R740',
          quantity: 2,
          unitPrice: 25000,
          totalAmount: 50000,
          orderStatus: 'shipped',
          paymentStatus: 'partial',
          remark: '项目采购'
        }
      ]
      purchaseOrderTotal.value = purchaseOrderData.value.length
    } else {
      // 模拟采购分析报表数据
      supplierRankingData.value = [
        { rank: 1, supplierName: '供应商A', purchaseCount: 15, totalAmount: 125000, avgOrderAmount: 8333, satisfaction: 4.5 },
        { rank: 2, supplierName: '供应商B', purchaseCount: 12, totalAmount: 98000, avgOrderAmount: 8167, satisfaction: 4.2 },
        { rank: 3, supplierName: '供应商C', purchaseCount: 8, totalAmount: 185000, avgOrderAmount: 23125, satisfaction: 4.8 }
      ]
      
      // 初始化图表
      initSupplierChart()
      initCategoryChart()
      initPriceTrendChart()
    }
  } catch (error) {
    console.error('获取报表数据失败:', error)
    ElMessage.error('获取报表数据失败')
  } finally {
    loading.value = false
  }
}

// 处理报表类型变更
const handleReportTypeChange = () => {
  fetchReportData()
}

// 初始化供应商统计图表
const initSupplierChart = () => {
  if (supplierChartRef.value) {
    if (supplierChart) {
      supplierChart.dispose()
    }
    supplierChart = echarts.init(supplierChartRef.value)
    
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'value',
        name: '采购金额（元）',
        nameLocation: 'middle',
        nameGap: 30
      },
      yAxis: {
        type: 'category',
        data: ['供应商A', '供应商B', '供应商C']
      },
      series: [
        {
          name: '采购金额',
          type: 'bar',
          data: [125000, 98000, 185000],
          itemStyle: {
            color: '#409EFF'
          }
        }
      ]
    }
    
    supplierChart.setOption(option)
  }
}

// 初始化分类统计图表
const initCategoryChart = () => {
  if (categoryChartRef.value) {
    if (categoryChart) {
      categoryChart.dispose()
    }
    categoryChart = echarts.init(categoryChartRef.value)
    
    const option = {
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '采购金额',
          type: 'pie',
          radius: '50%',
          data: [
            { value: 150000, name: '办公用品' },
            { value: 120000, name: 'IT设备' },
            { value: 80000, name: '生产物资' },
            { value: 58000, name: '其他' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    
    categoryChart.setOption(option)
  }
}

// 初始化价格趋势图表
const initPriceTrendChart = () => {
  if (priceTrendChartRef.value) {
    if (priceTrendChart) {
      priceTrendChart.dispose()
    }
    priceTrendChart = echarts.init(priceTrendChartRef.value)
    
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['2023-11', '2023-12', '2024-01', '2024-02', '2024-03']
      },
      yAxis: {
        type: 'value',
        name: '平均采购单价（元）'
      },
      series: [
        {
          name: '打印纸',
          type: 'line',
          data: [23, 24, 25, 26, 25],
          smooth: true
        },
        {
          name: '办公椅',
          type: 'line',
          data: [780, 790, 800, 810, 805],
          smooth: true
        },
        {
          name: '服务器',
          type: 'line',
          data: [24500, 24800, 25000, 25200, 25100],
          smooth: true
        }
      ]
    }
    
    priceTrendChart.setOption(option)
  }
}

// 获取订单状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'shipped': 'primary',
    'completed': 'success',
    'cancelled': 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取订单状态文本
const getStatusText = (status) => {
  const textMap = {
    'pending': '待处理',
    'shipped': '已发货',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return textMap[status] || status
}

// 获取付款状态标签类型
const getPaymentStatusTagType = (status) => {
  const typeMap = {
    'unpaid': 'danger',
    'partial': 'warning',
    'paid': 'success'
  }
  return typeMap[status] || 'info'
}

// 获取付款状态文本
const getPaymentStatusText = (status) => {
  const textMap = {
    'unpaid': '未付款',
    'partial': '部分付款',
    'paid': '已付款'
  }
  return textMap[status] || status
}

// 货币格式化
const currencyFormatter = (row, column, cellValue) => {
  return '¥' + cellValue.toLocaleString()
}

// 处理导出
const handleExport = () => {
  ElMessage.success('报表导出功能开发中')
}

// 分页相关
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  fetchReportData()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  fetchReportData()
}

// 监听窗口大小变化，调整图表大小
const handleResize = () => {
  if (supplierChart) supplierChart.resize()
  if (categoryChart) categoryChart.resize()
  if (priceTrendChart) priceTrendChart.resize()
}

onMounted(() => {
  fetchReportData()
  window.addEventListener('resize', handleResize)
})

// 组件卸载前清理
const beforeUnmount = () => {
  window.removeEventListener('resize', handleResize)
  if (supplierChart) supplierChart.dispose()
  if (categoryChart) categoryChart.dispose()
  if (priceTrendChart) priceTrendChart.dispose()
}
</script>

<style scoped>
.purchase-report-container {
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
  flex-wrap: wrap;
  gap: 10px;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.report-type-selector {
  margin: 20px 0;
  padding: 10px;
  background-color: #f0f2f5;
  border-radius: 4px;
}

.report-content {
  margin-top: 20px;
}

.chart-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  height: 350px;
}

.chart {
  width: 100%;
  height: calc(100% - 50px);
}

.analysis-card {
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.chart-header {
  display: flex;
  justify-content: center;
  font-weight: bold;
}
</style>