<template>
  <div class="use-report-container">
    <h2 class="page-title">领用报表</h2>
    
    <el-card shadow="hover" class="report-card">
      <template #header>
        <div class="card-header">
          <span>领用报表</span>
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
            <el-select
              v-model="department"
              placeholder="请选择部门"
              clearable
              style="width: 180px"
            >
              <el-option label="采购部" value="采购部" />
              <el-option label="仓库管理部" value="仓库管理部" />
              <el-option label="生产部" value="生产部" />
              <el-option label="财务部" value="财务部" />
              <el-option label="行政部" value="行政部" />
              <el-option label="技术部" value="技术部" />
            </el-select>
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
          <el-radio-button label="use_detail">领用明细表</el-radio-button>
          <el-radio-button label="use_summary">领用汇总表</el-radio-button>
        </el-radio-group>
      </div>
      
      <!-- 领用明细表 -->
      <div v-if="reportType === 'use_detail'" class="report-content">
        <el-table
          v-loading="loading"
          :data="useDetailData"
          style="width: 100%"
          border
        >
          <el-table-column prop="useDate" label="领用日期" width="150" />
          <el-table-column prop="username" label="领用人" width="120" />
          <el-table-column prop="department" label="领用部门" width="150" />
          <el-table-column prop="materialName" label="物资名称" width="180" />
          <el-table-column prop="model" label="型号规格" width="150" />
          <el-table-column prop="quantity" label="领用数量" width="120" />
          <el-table-column prop="unit" label="单位" width="80" />
          <el-table-column prop="useReason" label="领用原因" width="200" />
          <el-table-column prop="approver" label="审批人" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusTagType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" />
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            layout="total, sizes, prev, pager, next, jumper"
            :total="useDetailTotal"
            :page-size="pageSize"
            :current-page="currentPage"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
      
      <!-- 领用汇总表 -->
      <div v-else-if="reportType === 'use_summary'" class="report-content">
        <!-- 汇总类型选择 -->
        <div class="summary-type-selector">
          <el-radio-group v-model="summaryType" @change="handleSummaryTypeChange">
            <el-radio-button label="by_department">按部门汇总</el-radio-button>
            <el-radio-button label="by_material">按物资汇总</el-radio-button>
            <el-radio-button label="by_time">按时间汇总</el-radio-button>
          </el-radio-group>
        </div>
        
        <!-- 图表区域 -->
        <div class="chart-container">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>领用金额统计</span>
              </div>
            </template>
            <div ref="useChartRef" class="chart"></div>
          </el-card>
        </div>
        
        <!-- 汇总表格 -->
        <el-card shadow="hover" class="summary-table-card">
          <template #header>
            <div class="summary-table-header">
              <span>{{ getSummaryTableTitle() }}</span>
            </div>
          </template>
          <el-table
            v-loading="loading"
            :data="useSummaryData"
            style="width: 100%"
            border
          >
            <!-- 按部门汇总 -->
            <template v-if="summaryType === 'by_department'">
              <el-table-column prop="department" label="部门" width="150" />
              <el-table-column prop="totalQuantity" label="总领用数量" width="150" />
              <el-table-column prop="totalAmount" label="总领用金额" width="150" :formatter="currencyFormatter" />
              <el-table-column prop="averageAmount" label="平均领用金额" width="150" :formatter="currencyFormatter" />
              <el-table-column prop="useCount" label="领用次数" width="120" />
            </template>
            
            <!-- 按物资汇总 -->
            <template v-else-if="summaryType === 'by_material'">
              <el-table-column prop="materialName" label="物资名称" width="200" />
              <el-table-column prop="model" label="型号规格" width="150" />
              <el-table-column prop="totalQuantity" label="总领用数量" width="150" />
              <el-table-column prop="totalAmount" label="总领用金额" width="150" :formatter="currencyFormatter" />
              <el-table-column prop="useCount" label="领用次数" width="120" />
              <el-table-column prop="unitPrice" label="单价" width="120" :formatter="currencyFormatter" />
            </template>
            
            <!-- 按时间汇总 -->
            <template v-else-if="summaryType === 'by_time'">
              <el-table-column prop="period" label="时间周期" width="150" />
              <el-table-column prop="totalQuantity" label="总领用数量" width="150" />
              <el-table-column prop="totalAmount" label="总领用金额" width="150" :formatter="currencyFormatter" />
              <el-table-column prop="dailyAverage" label="日均领用金额" width="150" :formatter="currencyFormatter" />
              <el-table-column prop="useCount" label="领用次数" width="120" />
            </template>
          </el-table>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const reportType = ref('use_detail')
const summaryType = ref('by_department')
const dateRange = ref([])
const department = ref('')
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

// 领用明细表数据
const useDetailData = ref([])
const useDetailTotal = ref(0)

// 领用汇总表数据
const useSummaryData = ref([])

// 图表实例引用
const useChartRef = ref(null)
let useChart = null

// 获取报表数据
const fetchReportData = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    if (reportType.value === 'use_detail') {
      // 模拟领用明细表数据
      useDetailData.value = [
        {
          useDate: '2024-01-15',
          username: '张三',
          department: '技术部',
          materialName: '笔记本电脑',
          model: 'ThinkPad X1 Carbon',
          quantity: 1,
          unit: '台',
          useReason: '项目开发需求',
          approver: '李四',
          status: 'approved',
          remark: ''
        },
        {
          useDate: '2024-01-16',
          username: '王五',
          department: '行政部',
          materialName: '打印纸',
          model: 'A4 70g',
          quantity: 10,
          unit: '箱',
          useReason: '日常办公需要',
          approver: '赵六',
          status: 'approved',
          remark: ''
        },
        {
          useDate: '2024-01-17',
          username: '孙七',
          department: '生产部',
          materialName: '螺丝',
          model: 'M6×20',
          quantity: 500,
          unit: '个',
          useReason: '生产线维修',
          approver: '周八',
          status: 'approved',
          remark: ''
        },
        {
          useDate: '2024-01-18',
          username: '吴九',
          department: '财务部',
          materialName: '财务软件',
          model: '金蝶KIS',
          quantity: 1,
          unit: '套',
          useReason: '财务系统升级',
          approver: '郑十',
          status: 'pending',
          remark: '待审批'
        }
      ]
      useDetailTotal.value = useDetailData.value.length
    } else {
      // 模拟领用汇总表数据
      handleSummaryTypeChange()
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

// 处理汇总类型变更
const handleSummaryTypeChange = () => {
  // 根据汇总类型生成不同的数据
  if (summaryType.value === 'by_department') {
    useSummaryData.value = [
      { department: '技术部', totalQuantity: 15, totalAmount: 285000, averageAmount: 19000, useCount: 8 },
      { department: '行政部', totalQuantity: 45, totalAmount: 18000, averageAmount: 400, useCount: 12 },
      { department: '生产部', totalQuantity: 1200, totalAmount: 65000, averageAmount: 542, useCount: 15 },
      { department: '财务部', totalQuantity: 3, totalAmount: 45000, averageAmount: 15000, useCount: 3 },
      { department: '采购部', totalQuantity: 8, totalAmount: 22000, averageAmount: 2750, useCount: 5 }
    ]
  } else if (summaryType.value === 'by_material') {
    useSummaryData.value = [
      { materialName: '笔记本电脑', model: 'ThinkPad X1 Carbon', totalQuantity: 5, totalAmount: 250000, useCount: 5, unitPrice: 50000 },
      { materialName: '打印纸', model: 'A4 70g', totalQuantity: 40, totalAmount: 12000, useCount: 10, unitPrice: 300 },
      { materialName: '螺丝', model: 'M6×20', totalQuantity: 1000, totalAmount: 5000, useCount: 8, unitPrice: 5 },
      { materialName: '财务软件', model: '金蝶KIS', totalQuantity: 2, totalAmount: 40000, useCount: 2, unitPrice: 20000 }
    ]
  } else if (summaryType.value === 'by_time') {
    useSummaryData.value = [
      { period: '2024-01-01至2024-01-07', totalQuantity: 35, totalAmount: 85000, dailyAverage: 12143, useCount: 10 },
      { period: '2024-01-08至2024-01-14', totalQuantity: 48, totalAmount: 120000, dailyAverage: 17143, useCount: 12 },
      { period: '2024-01-15至2024-01-21', totalQuantity: 52, totalAmount: 150000, dailyAverage: 21429, useCount: 15 },
      { period: '2024-01-22至2024-01-28', totalQuantity: 38, totalAmount: 95000, dailyAverage: 13571, useCount: 9 }
    ]
  }
  
  // 初始化图表
  initUseChart()
}

// 初始化领用统计图表
const initUseChart = () => {
  if (useChartRef.value) {
    if (useChart) {
      useChart.dispose()
    }
    useChart = echarts.init(useChartRef.value)
    
    let option = {}
    
    if (summaryType.value === 'by_department') {
      // 按部门统计柱状图
      option = {
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
          type: 'category',
          data: useSummaryData.value.map(item => item.department),
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          name: '领用金额（元）'
        },
        series: [
          {
            name: '领用金额',
            type: 'bar',
            data: useSummaryData.value.map(item => item.totalAmount),
            itemStyle: {
              color: '#67C23A'
            }
          }
        ]
      }
    } else if (summaryType.value === 'by_material') {
      // 按物资统计饼图
      option = {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '领用金额',
            type: 'pie',
            radius: '50%',
            data: useSummaryData.value.map(item => ({
              name: item.materialName,
              value: item.totalAmount
            })),
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
    } else if (summaryType.value === 'by_time') {
      // 按时间统计折线图
      option = {
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
          data: useSummaryData.value.map(item => item.period)
        },
        yAxis: {
          type: 'value',
          name: '领用金额（元）'
        },
        series: [
          {
            name: '领用金额',
            type: 'line',
            data: useSummaryData.value.map(item => item.totalAmount),
            smooth: true,
            itemStyle: {
              color: '#E6A23C'
            }
          }
        ]
      }
    }
    
    useChart.setOption(option)
  }
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger',
    'canceled': 'info'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    'pending': '待审批',
    'approved': '已批准',
    'rejected': '已拒绝',
    'canceled': '已取消'
  }
  return textMap[status] || status
}

// 获取汇总表格标题
const getSummaryTableTitle = () => {
  const titleMap = {
    'by_department': '按部门汇总统计',
    'by_material': '按物资汇总统计',
    'by_time': '按时间汇总统计'
  }
  return titleMap[summaryType.value] || '领用汇总统计'
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
  if (useChart) {
    useChart.resize()
  }
}

onMounted(() => {
  fetchReportData()
  window.addEventListener('resize', handleResize)
})

// 组件卸载前清理
const beforeUnmount = () => {
  window.removeEventListener('resize', handleResize)
  if (useChart) {
    useChart.dispose()
  }
}
</script>

<style scoped>
.use-report-container {
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

.summary-type-selector {
  margin: 20px 0;
  padding: 10px;
  background-color: #f0f2f5;
  border-radius: 4px;
}

.report-content {
  margin-top: 20px;
}

.chart-container {
  margin-bottom: 20px;
}

.chart-card {
  height: 400px;
  margin-bottom: 20px;
}

.chart {
  width: 100%;
  height: calc(100% - 50px);
}

.chart-header {
  display: flex;
  justify-content: center;
  font-weight: bold;
}

.summary-table-card {
  margin-top: 20px;
}

.summary-table-header {
  display: flex;
  justify-content: center;
  font-weight: bold;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>