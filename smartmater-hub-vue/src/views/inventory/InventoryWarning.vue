<template>
  <div class="inventory-warning-container">
    <h2 class="page-title">库存预警与监控</h2>
    
    <el-row :gutter="20">
      <!-- 预警统计卡片 -->
      <el-col :span="24">
        <el-card shadow="hover" class="stats-card">
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ totalMaterials }}</div>
                <div class="stat-label">总物资数</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number warning">{{ lowStockCount }}</div>
                <div class="stat-label">低库存物资</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number danger">{{ outOfStockCount }}</div>
                <div class="stat-label">缺货物资</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number success">{{ normalStockCount }}</div>
                <div class="stat-label">正常库存物资</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      
      <!-- 预警列表 -->
      <el-col :span="16">
        <el-card shadow="hover" class="warning-card">
          <template #header>
            <div class="card-header">
              <span>库存预警列表</span>
              <el-button type="primary" @click="handleRefresh" icon="RefreshRight">
                刷新
              </el-button>
            </div>
          </template>
          
          <!-- 预警搜索 -->
          <div class="warning-search">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索物资名称、型号规格或品牌"
              clearable
              prefix-icon="Search"
              @keyup.enter="fetchWarningMaterials"
              style="width: 350px; margin-right: 10px;"
            />
            <el-select
              v-model="searchWarningType"
              placeholder="预警类型"
              clearable
              style="width: 150px; margin-right: 10px;"
            >
              <el-option label="全部类型" value="" />
              <el-option label="低库存" value="low" />
              <el-option label="缺货" value="out" />
            </el-select>
          </div>
          
          <!-- 预警列表 -->
          <div class="warning-table-container">
            <el-table
              v-loading="loading"
              :data="warningMaterials"
              style="width: 100%"
              border
              @selection-change="handleSelectionChange"
            >
              <el-table-column type="selection" width="55" />
              <el-table-column prop="materialName" label="物资名称" min-width="200" />
              <el-table-column prop="specification" label="型号规格" width="150" />
              <el-table-column prop="brand" label="品牌" width="120" />
              <el-table-column prop="unit" label="单位" width="80" />
              <el-table-column prop="stockQuantity" label="当前库存" width="120" align="right">
                <template #default="scope">
                  <span :class="getStockStatusClass(scope.row)">
                    {{ scope.row.stockQuantity }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="stockWarningValue" label="预警值" width="120" align="right" />
              <el-table-column prop="stockUpperValue" label="上限值" width="120" align="right" />
              <el-table-column prop="categoryName" label="分类" width="120" />
              <el-table-column prop="supplierName" label="供应商" width="120" />
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="handleViewMaterial(scope.row)" icon="View">
                    查看
                  </el-button>
                  <el-button type="success" size="small" @click="handleSetWarning(scope.row)" icon="Setting">
                    设置预警
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          
          <!-- 分页 -->
          <div class="pagination">
            <el-pagination
              layout="total, sizes, prev, pager, next, jumper"
              :total="totalWarning"
              :page-size="pageSize"
              :current-page="currentPage"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </el-col>
      
      <!-- 库存监控图表 -->
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>库存动态监控</span>
            </div>
          </template>
          
          <div class="chart-selector">
            <el-select
              v-model="selectedMaterialId"
              placeholder="选择物资"
              clearable
              style="width: 100%; margin-bottom: 20px;"
              @change="handleMaterialSelect"
            >
              <el-option
                v-for="material in materials"
                :key="material.id"
                :label="`${material.name} (${material.specification})`"
                :value="material.id"
              />
            </el-select>
            
            <!-- ARIMA-LSTM算法控制按钮 -->
            <div class="algorithm-controls">
              <el-button 
                type="primary" 
                @click="trainModel"
                :loading="algorithmLoading"
                :disabled="modelTrained"
                style="width: 100%; margin-bottom: 10px;"
                icon="StarFilled"
              >
                {{ modelTrained ? '模型已训练' : '训练ARIMA-LSTM模型' }}
              </el-button>
              
              <el-button 
                type="success" 
                @click="predictStock"
                :loading="algorithmLoading"
                :disabled="!modelTrained"
                style="width: 100%;"
                icon="TrendCharts"
              >
                预测未来15天库存趋势
              </el-button>
            </div>
          </div>
          
          <div class="chart-container">
            <div ref="chartRef" class="chart"></div>
          </div>
          
          <!-- 算法状态提示 -->
          <div v-if="modelTrained" class="algorithm-status">
            <el-alert
              title="模型状态"
              type="success"
              description="ARIMA-LSTM混合模型已训练完成，可以进行库存预测。"
              :closable="false"
              style="margin-top: 20px;"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 设置预警对话框 -->
    <el-dialog
      v-model="warningDialogVisible"
      :title="`设置预警 - ${currentMaterial.materialName}`"
      width="500px"
      @close="handleWarningDialogClose"
    >
      <el-form
        ref="warningFormRef"
        :model="warningForm"
        :rules="warningRules"
        label-position="right"
        label-width="100px"
      >
        <el-form-item label="当前库存" prop="currentStock">
          <el-input
            v-model="warningForm.currentStock"
            placeholder="当前库存"
            readonly
          />
        </el-form-item>
        
        <el-form-item label="库存预警值" prop="stockWarningValue">
          <el-input-number
            v-model="warningForm.stockWarningValue"
            :min="0"
            placeholder="请输入库存预警值"
            style="width: 100%;"
          />
        </el-form-item>
        
        <el-form-item label="库存上限值" prop="stockUpperValue">
          <el-input-number
            v-model="warningForm.stockUpperValue"
            :min="1"
            placeholder="请输入库存上限值"
            style="width: 100%;"
          />
        </el-form-item>
        
        <el-form-item label="预警方式">
          <el-checkbox-group v-model="warningForm.warningMethods">
            <el-checkbox label="系统消息">系统消息</el-checkbox>
            <el-checkbox label="邮件通知">邮件通知</el-checkbox>
            <el-checkbox label="短信通知">短信通知</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="warningDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="warningDialogLoading" @click="handleSaveWarning">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
</div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch, nextTick } from 'vue'
import request from '../../services/api'
import { ElMessage, ElMessageBox } from 'element-plus'

// 引入 ECharts
import * as echarts from 'echarts'

// 引入ARIMA-LSTM混合算法
import { trainHybridModel, predictHybridModel, generateMockData } from '../../algorithms/arima-lstm'

const warningMaterials = ref([])
const materials = ref([])
const loading = ref(false)
const totalWarning = ref(0)
const totalMaterials = ref(0)
const lowStockCount = ref(0)
const outOfStockCount = ref(0)
const normalStockCount = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const searchWarningType = ref('')

// 图表相关
const chartRef = ref(null)
let chartInstance = null
const selectedMaterialId = ref('')

// 对话框相关
const warningDialogVisible = ref(false)
const warningDialogLoading = ref(false)
const warningFormRef = ref(null)
const currentMaterial = ref({})

// 预警设置表单
const warningForm = reactive({
  id: '',
  materialId: '',
  currentStock: 0,
  stockWarningValue: 0,
  stockUpperValue: 1000,
  warningMethods: ['系统消息']
})

// 预警表单验证规则
const warningRules = {
  stockWarningValue: [
    { required: true, message: '请输入库存预警值', trigger: 'blur' },
    { type: 'number', min: 0, message: '预警值必须大于等于 0', trigger: 'blur' }
  ],
  stockUpperValue: [
    { required: true, message: '请输入库存上限值', trigger: 'blur' },
    { type: 'number', min: 1, message: '上限值必须大于 0', trigger: 'blur' }
  ]
}

// 获取预警物资列表
const fetchWarningMaterials = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟物资数据
    const mockMaterials = [
      { 
        id: 1, 
        materialName: 'A4打印纸', 
        specification: '80g 500张/包', 
        brand: '得力', 
        unit: '包', 
        stockQuantity: 150, 
        stockWarningValue: 50, 
        stockUpperValue: 500, 
        categoryName: '文具', 
        supplierName: '得力办公' 
      },
      { 
        id: 2, 
        materialName: '黑色中性笔', 
        specification: '0.5mm', 
        brand: '晨光', 
        unit: '支', 
        stockQuantity: 300, 
        stockWarningValue: 100, 
        stockUpperValue: 1000, 
        categoryName: '文具', 
        supplierName: '晨光文具' 
      },
      { 
        id: 3, 
        materialName: '鼠标', 
        specification: '有线 USB', 
        brand: '罗技', 
        unit: '个', 
        stockQuantity: 20, 
        stockWarningValue: 10, 
        stockUpperValue: 100, 
        categoryName: '电子设备', 
        supplierName: '科技数码' 
      },
      { 
        id: 4, 
        materialName: '键盘', 
        specification: '机械键盘 青轴', 
        brand: '雷蛇', 
        unit: '个', 
        stockQuantity: 8, 
        stockWarningValue: 5, 
        stockUpperValue: 50, 
        categoryName: '电子设备', 
        supplierName: '科技数码' 
      },
      { 
        id: 5, 
        materialName: '文件夹', 
        specification: 'A4 塑料材质', 
        brand: '齐心', 
        unit: '个', 
        stockQuantity: 450, 
        stockWarningValue: 200, 
        stockUpperValue: 2000, 
        categoryName: '文具', 
        supplierName: '齐心办公' 
      },
      { 
        id: 6, 
        materialName: '订书机', 
        specification: '标准型', 
        brand: '得力', 
        unit: '个', 
        stockQuantity: 0, 
        stockWarningValue: 20, 
        stockUpperValue: 100, 
        categoryName: '文具', 
        supplierName: '得力办公' 
      },
      { 
        id: 7, 
        materialName: '硒鼓', 
        specification: 'HP LaserJet Pro M404n', 
        brand: '惠普', 
        unit: '个', 
        stockQuantity: 5, 
        stockWarningValue: 3, 
        stockUpperValue: 20, 
        categoryName: '耗材', 
        supplierName: '惠普耗材' 
      },
      { 
        id: 8, 
        materialName: '打印机', 
        specification: '激光打印机', 
        brand: '惠普', 
        unit: '台', 
        stockQuantity: 2, 
        stockWarningValue: 5, 
        stockUpperValue: 20, 
        categoryName: '电子设备', 
        supplierName: '惠普耗材' 
      },
      { 
        id: 9, 
        materialName: '办公椅', 
        specification: '人体工学椅', 
        brand: '永艺', 
        unit: '把', 
        stockQuantity: 10, 
        stockWarningValue: 8, 
        stockUpperValue: 50, 
        categoryName: '家具', 
        supplierName: '办公家具' 
      },
      { 
        id: 10, 
        materialName: '办公桌', 
        specification: '1.4m', 
        brand: '永艺', 
        unit: '张', 
        stockQuantity: 5, 
        stockWarningValue: 3, 
        stockUpperValue: 20, 
        categoryName: '家具', 
        supplierName: '办公家具' 
      },
      { 
        id: 11, 
        materialName: '会议桌', 
        specification: '长桌', 
        brand: '永艺', 
        unit: '张', 
        stockQuantity: 1, 
        stockWarningValue: 2, 
        stockUpperValue: 10, 
        categoryName: '家具', 
        supplierName: '办公家具' 
      },
      { 
        id: 12, 
        materialName: '文件柜', 
        specification: '铁皮柜', 
        brand: '虎牌', 
        unit: '个', 
        stockQuantity: 8, 
        stockWarningValue: 5, 
        stockUpperValue: 30, 
        categoryName: '家具', 
        supplierName: '办公家具' 
      }
    ]
    
    // 计算统计数据
    totalMaterials.value = mockMaterials.length
    lowStockCount.value = mockMaterials.filter(item => 
      item.stockQuantity > 0 && item.stockQuantity <= item.stockWarningValue
    ).length
    outOfStockCount.value = mockMaterials.filter(item => 
      item.stockQuantity <= 0
    ).length
    normalStockCount.value = mockMaterials.filter(item => 
      item.stockQuantity > item.stockWarningValue
    ).length
    
    // 模拟搜索功能
    let filteredMaterials = mockMaterials
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filteredMaterials = mockMaterials.filter(material => 
        material.materialName.toLowerCase().includes(keyword) ||
        material.specification.toLowerCase().includes(keyword) ||
        material.brand.toLowerCase().includes(keyword)
      )
    }
    
    if (searchWarningType.value) {
      if (searchWarningType.value === 'low') {
        filteredMaterials = filteredMaterials.filter(material => 
          material.stockQuantity > 0 && material.stockQuantity <= material.stockWarningValue
        )
      } else if (searchWarningType.value === 'out') {
        filteredMaterials = filteredMaterials.filter(material => 
          material.stockQuantity <= 0
        )
      }
    }
    
    warningMaterials.value = filteredMaterials
    totalWarning.value = filteredMaterials.length
    materials.value = mockMaterials
    
    // 初始化图表
    nextTick(() => {
      initChart()
    })
  } catch (error) {
    console.error('获取预警物资失败:', error)
    ElMessage.error('获取预警物资失败')
  } finally {
    loading.value = false
  }
}

// 获取库存状态样式
const getStockStatusClass = (row) => {
  if (row.stockQuantity <= 0) {
    return 'stock-out'
  } else if (row.stockQuantity <= row.stockWarningValue) {
    return 'stock-low'
  }
  return 'stock-normal'
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedMaterials.value = selection
}

// 处理查看物资
const handleViewMaterial = (row) => {
  console.log('查看物资:', row)
  ElMessage.info('查看物资详情功能开发中...')
}

// 处理设置预警
const handleSetWarning = (row) => {
  currentMaterial.value = row
  // 填充表单数据
  Object.assign(warningForm, {
    id: row.id,
    materialId: row.id,
    currentStock: row.stockQuantity,
    stockWarningValue: row.stockWarningValue,
    stockUpperValue: row.stockUpperValue,
    warningMethods: ['系统消息']
  })
  warningDialogVisible.value = true
}

// 处理保存预警设置
const handleSaveWarning = async () => {
  if (!warningFormRef.value) return
  
  try {
    const valid = await warningFormRef.value.validate()
    if (valid) {
      warningDialogLoading.value = true
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      console.log('保存预警设置:', warningForm)
      ElMessage.success('预警设置保存成功')
      
      // 关闭对话框
      warningDialogVisible.value = false
      
      // 刷新数据
      fetchWarningMaterials()
    }
  } catch (error) {
    console.error('保存预警设置失败:', error)
    ElMessage.error('保存预警设置失败')
  } finally {
    warningDialogLoading.value = false
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
  
  // 模拟库存趋势数据
  const mockChartData = {
    dates: ['1月', '2月', '3月', '4月', '5月', '6月'],
    stock: [120, 132, 101, 134, 90, 230]
  }
  
  // 图表配置
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c} {a}'
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
      data: mockChartData.dates
    },
    yAxis: {
      type: 'value',
      name: '库存数量'
    },
    series: [
      {
        name: '库存',
        type: 'line',
        data: mockChartData.stock,
        smooth: true,
        itemStyle: {
          color: '#409eff'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0, color: 'rgba(64, 158, 255, 0.3)'
            }, {
              offset: 1, color: 'rgba(64, 158, 255, 0.1)'
            }]
          }
        }
      }
    ]
  }
  
  // 设置图表选项
  chartInstance.setOption(option)
  
  // 监听窗口大小变化，调整图表大小
  window.addEventListener('resize', handleResize)
}

// 处理图表大小调整
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 处理物资选择变化
const handleMaterialSelect = (value) => {
  console.log('选择物资:', value)
  // 模拟更新图表数据
  nextTick(() => {
    if (chartInstance) {
      // 随机生成新数据
      const randomData = Array.from({ length: 6 }, () => Math.floor(Math.random() * 300) + 50)
      chartInstance.setOption({
        series: [{
          data: randomData
        }]
      })
    }
  })
}

// 处理刷新
const handleRefresh = () => {
  fetchWarningMaterials()
}

// 处理对话框关闭
const handleWarningDialogClose = () => {
  // 重置表单
  if (warningFormRef.value) {
    warningFormRef.value.resetFields()
  }
  currentMaterial.value = {}
}

// 分页相关
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  fetchWarningMaterials()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  fetchWarningMaterials()
}

// 页面挂载时获取预警物资列表
onMounted(() => {
  fetchWarningMaterials()
  
  // 页面卸载时清理资源
  window.addEventListener('beforeunload', () => {
    if (chartInstance) {
      chartInstance.dispose()
      chartInstance = null
    }
    window.removeEventListener('resize', handleResize)
  })
})

// 选择的记录
const selectedMaterials = ref([])

// 算法相关状态
const algorithmLoading = ref(false)
const modelTrained = ref(false)
let hybridModel = null
let trainingData = []

// 训练ARIMA-LSTM混合模型
const trainModel = async () => {
  algorithmLoading.value = true
  try {
    // 生成模拟库存数据（实际应用中应使用真实历史数据）
    trainingData = generateMockData(100, { trend: 0.5, seasonality: 20, noise: 0.2 })
    
    // 训练混合模型
    hybridModel = await trainHybridModel(trainingData, {
      windowSize: 7,
      lstmEpochs: 30,
      lstmBatchSize: 16
    })
    
    modelTrained.value = true
    ElMessage.success('ARIMA-LSTM混合模型训练完成！')
    
    // 训练完成后更新图表
    if (chartInstance) {
      updateChartWithPredictions()
    }
  } catch (error) {
    console.error('模型训练失败:', error)
    ElMessage.error('模型训练失败，请检查控制台日志')
  } finally {
    algorithmLoading.value = false
  }
}

// 使用混合模型进行预测
const predictStock = async (steps = 15) => {
  if (!modelTrained.value || !hybridModel) {
    ElMessage.warning('请先训练模型')
    return
  }
  
  algorithmLoading.value = true
  try {
    // 使用训练好的模型进行预测
    const predictionResult = await predictHybridModel(hybridModel, trainingData, steps)
    
    // 更新图表显示预测结果
    if (chartInstance) {
      updateChartWithPredictions(predictionResult)
    }
    
    ElMessage.success(`成功预测未来${steps}天的库存趋势！`)
  } catch (error) {
    console.error('预测失败:', error)
    ElMessage.error('预测失败，请检查控制台日志')
  } finally {
    algorithmLoading.value = false
  }
}

// 更新图表以显示预测结果
const updateChartWithPredictions = (predictionResult = null) => {
  if (!chartInstance) return
  
  // 准备历史数据
  const dates = Array.from({ length: 30 }, (_, i) => `${i + 1}日`)
  const historyData = trainingData.slice(-30)
  
  let chartOption = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c} {a}'
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
      data: dates
    },
    yAxis: {
      type: 'value',
      name: '库存数量'
    },
    series: [
      {
        name: '历史库存',
        type: 'line',
        data: historyData,
        smooth: true,
        itemStyle: {
          color: '#409eff'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0, color: 'rgba(64, 158, 255, 0.3)'
            }, {
              offset: 1, color: 'rgba(64, 158, 255, 0.1)'
            }]
          }
        }
      }
    ]
  }
  
  // 如果有预测结果，添加预测数据到图表
  if (predictionResult) {
    const { predictions } = predictionResult
    const futureDates = Array.from({ length: predictions.hybrid.length }, (_, i) => `预测${i + 1}日`)
    
    // 合并日期和数据
    chartOption.xAxis.data = [...dates, ...futureDates]
    
    // 添加预测结果系列
    chartOption.series.push(
      {
        name: 'ARIMA-LSTM预测',
        type: 'line',
        data: [...Array(30).fill(null), ...predictions.hybrid],
        smooth: true,
        itemStyle: {
          color: '#f56c6c'
        },
        lineStyle: {
          type: 'dashed'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0, color: 'rgba(245, 108, 108, 0.3)'
            }, {
              offset: 1, color: 'rgba(245, 108, 108, 0.1)'
            }]
          }
        }
      }
    )
  }
  
  // 更新图表
  chartInstance.setOption(chartOption)
}
</script>

<style scoped>
.inventory-warning-container {
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

/* 统计卡片样式 */
.stats-card {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px 0;
  background-color: #fafafa;
  border-radius: 8px;
}

.stat-number {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #303133;
}

.stat-number.warning {
  color: #e6a23c;
}

.stat-number.danger {
  color: #f56c6c;
}

.stat-number.success {
  color: #67c23a;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 预警卡片样式 */
.warning-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.warning-search {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.warning-table-container {
  margin-bottom: 20px;
}

/* 图表卡片样式 */
.chart-card {
  margin-bottom: 20px;
}

.chart-selector {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
}

.chart {
  width: 100%;
  height: 100%;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 库存状态样式 */
.stock-normal {
  color: #67c23a;
  font-weight: bold;
}

.stock-low {
  color: #e6a23c;
  font-weight: bold;
}

.stock-out {
  color: #f56c6c;
  font-weight: bold;
}

/* 算法控制按钮样式 */
.algorithm-controls {
  margin-bottom: 20px;
}

/* 算法状态提示样式 */
.algorithm-status {
  margin-top: 20px;
}

/* 调整图表高度以适应新增内容 */
.chart-container {
  height: 250px;
}
</style>