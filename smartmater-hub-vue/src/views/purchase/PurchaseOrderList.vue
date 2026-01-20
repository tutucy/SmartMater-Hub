<template>
  <div class="purchase-order-list-container">
    <h2 class="page-title">采购订单管理</h2>
    
    <el-card shadow="hover" class="order-card">
      <template #header>
        <div class="card-header">
          <span>采购订单列表</span>
          <el-button type="primary" @click="handleAddOrder" icon="Plus">
            新增订单
          </el-button>
        </div>
      </template>
      
      <!-- 订单搜索 -->
      <div class="order-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索订单号、供应商名称或物资名称"
          clearable
          prefix-icon="Search"
          @keyup.enter="fetchOrders"
          style="width: 350px; margin-right: 10px;"
        />
        <el-select
          v-model="searchStatus"
          placeholder="选择订单状态"
          clearable
          style="width: 150px; margin-right: 10px;"
        >
          <el-option label="全部状态" value="" />
          <el-option label="待审核" value="0" />
          <el-option label="已审核" value="1" />
          <el-option label="已入库" value="2" />
          <el-option label="已取消" value="3" />
        </el-select>
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
        <el-button type="primary" @click="fetchOrders" icon="RefreshRight">
          刷新
        </el-button>
      </div>
      
      <!-- 订单列表 -->
      <div class="order-table-container">
        <el-table
          v-loading="loading"
          :data="orders"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="orderNo" label="订单编号" width="180" />
          <el-table-column prop="supplierName" label="供应商" width="180" />
          <el-table-column prop="totalAmount" label="订单金额" width="150" align="right" :formatter="formatAmount" />
          <el-table-column prop="orderDate" label="下单日期" width="180" />
          <el-table-column prop="expectedDate" label="预计到货日期" width="180" />
          <el-table-column prop="actualDate" label="实际到货日期" width="180" />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-tag :type="getStatusColor(scope.row.status)">
                {{ getStatusName(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="operator" label="操作人" width="120" />
          <el-table-column label="操作" width="300" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleViewOrder(scope.row)" icon="View">
                查看
              </el-button>
              <el-button 
                v-if="scope.row.status === 'pending'" 
                type="success" 
                size="small" 
                @click="handleApproveOrder(scope.row)" 
                icon="Check"
              >
                审批
              </el-button>
              <el-button 
                v-if="['approved', 'shipped'].includes(scope.row.status)" 
                type="warning" 
                size="small" 
                @click="handleTrackOrder(scope.row)" 
                icon="Location"
              >
                跟踪
              </el-button>
              <el-button 
                v-if="['pending', 'approved'].includes(scope.row.status)" 
                type="info" 
                size="small" 
                @click="handleChangeOrder(scope.row)" 
                icon="EditPen"
              >
                变更
              </el-button>
              <el-button 
                v-if="scope.row.status === 'pending'" 
                type="danger" 
                size="small" 
                @click="handleCancelOrder(scope.row)" 
                icon="CircleClose"
              >
                取消
              </el-button>
            </template>
          </el-table-column>
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
    
    <!-- 新增订单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="900px"
      @close="handleDialogClose"
    >
      <el-form
        ref="orderFormRef"
        :model="orderForm"
        :rules="orderRules"
        label-position="right"
        label-width="100px"
      >
        <el-form-item label="订单编号" prop="orderNo">
          <el-input
            v-model="orderForm.orderNo"
            placeholder="系统自动生成"
            readonly
          />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplierId">
              <el-select
                v-model="orderForm.supplierId"
                placeholder="请选择供应商"
                clearable
                @change="handleSupplierChange"
              >
                <el-option
                  v-for="supplier in suppliers"
                  :key="supplier.id"
                  :label="supplier.supplierName"
                  :value="supplier.id"
                />
              </el-select>
            </el-form-item>
            
            <el-form-item label="下单日期" prop="orderDate">
              <el-date-picker
                v-model="orderForm.orderDate"
                type="datetime"
                placeholder="请选择下单日期"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%;"
              />
            </el-form-item>
            
            <el-form-item label="预计到货日期" prop="expectedDate">
              <el-date-picker
                v-model="orderForm.expectedDate"
                type="date"
                placeholder="请选择预计到货日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;"
              />
            </el-form-item>
            
            <el-form-item label="付款方式" prop="paymentMethod">
              <el-select
                v-model="orderForm.paymentMethod"
                placeholder="请选择付款方式"
                clearable
              >
                <el-option label="银行转账" value="bank" />
                <el-option label="支付宝" value="alipay" />
                <el-option label="微信支付" value="wechat" />
                <el-option label="货到付款" value="cod" />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="订单状态" prop="status">
              <el-select
                v-model="orderForm.status"
                placeholder="请选择订单状态"
                clearable
              >
                <el-option label="待审核" value="pending" />
                <el-option label="已审核" value="approved" />
                <el-option label="已入库" value="received" />
                <el-option label="已取消" value="canceled" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="操作人" prop="operator">
              <el-input
                v-model="orderForm.operator"
                placeholder="系统自动填充"
                readonly
              />
            </el-form-item>
            
            <el-form-item label="备注" prop="remark">
              <el-input
                v-model="orderForm.remark"
                placeholder="请输入备注信息"
                type="textarea"
                rows="3"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 订单明细 -->
        <el-divider>订单明细</el-divider>
        
        <el-table
          v-loading="detailLoading"
          :data="orderForm.details"
          style="width: 100%"
          border
        >
          <el-table-column type="selection" width="55" />
          <el-table-column label="物资信息" min-width="200">
            <template #default="scope">
              <el-select
                v-model="scope.row.materialId"
                placeholder="请选择物资"
                @change="handleMaterialChange(scope.row)"
              >
                <el-option
                  v-for="material in materials"
                  :key="material.id"
                  :label="`${material.name} (${material.specification})`"
                  :value="material.id"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="materialName" label="物资名称" width="150" />
          <el-table-column prop="specification" label="型号规格" width="150" />
          <el-table-column prop="unit" label="单位" width="80" />
          <el-table-column prop="quantity" label="采购数量" width="120">
            <template #default="scope">
              <el-input-number
                v-model="scope.row.quantity"
                :min="1"
                @change="handleQuantityChange"
              />
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="120">
            <template #default="scope">
              <el-input-number
                v-model="scope.row.price"
                :min="0"
                :precision="2"
                @change="handlePriceChange"
              />
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="120" align="right">
            <template #default="scope">
              ¥{{ (scope.row.quantity * scope.row.price).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" width="150">
            <template #default="scope">
              <el-input
                v-model="scope.row.remark"
                placeholder="请输入备注"
                clearable
                size="small"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="scope">
              <el-button 
                type="danger" 
                size="small" 
                @click="handleRemoveDetail(scope.$index)" 
                icon="Delete"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="detail-actions">
          <el-button type="success" @click="handleAddDetail" icon="Plus">
            添加明细
          </el-button>
          <div class="total-info">
            <span>总数量: {{ totalQuantity }} | 总金额: ¥{{ totalAmount.toFixed(2) }}</span>
          </div>
        </div>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="dialogLoading" @click="handleSaveOrder">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`订单详情 - ${currentOrder.orderNo}`"
      width="1000px"
      @close="handleDetailDialogClose"
    >
      <div v-loading="detailLoading" class="order-detail-container">
        <!-- 订单基本信息 -->
        <el-descriptions :column="2" border class="order-info">
          <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="供应商">{{ currentOrder.supplierName }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">¥{{ currentOrder.totalAmount ? currentOrder.totalAmount.toFixed(2) : '0.00' }}</el-descriptions-item>
          <el-descriptions-item label="下单日期">{{ currentOrder.orderDate }}</el-descriptions-item>
          <el-descriptions-item label="预计到货日期">{{ currentOrder.expectedDate }}</el-descriptions-item>
          <el-descriptions-item label="实际到货日期">{{ currentOrder.actualDate || '未到货' }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusColor(currentOrder.status)">
              {{ getStatusName(currentOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="操作人">{{ currentOrder.operator }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ currentOrder.updateTime }}</el-descriptions-item>
        </el-descriptions>
        
        <!-- 订单明细 -->
        <h3 class="detail-title">订单明细</h3>
        <el-table
          :data="orderDetails"
          style="width: 100%"
          border
          max-height="300"
        >
          <el-table-column prop="materialName" label="物资名称" width="200" />
          <el-table-column prop="specification" label="规格型号" width="180" />
          <el-table-column prop="unit" label="单位" width="80" />
          <el-table-column prop="quantity" label="采购数量" width="120" align="center" />
          <el-table-column prop="price" label="单价" width="120" align="right">
            <template #default="scope">
              ¥{{ scope.row.price ? scope.row.price.toFixed(2) : '0.00' }}
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="120" align="right">
            <template #default="scope">
              ¥{{ scope.row.amount ? scope.row.amount.toFixed(2) : '0.00' }}
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="150" />
        </el-table>
        
        <!-- 合计信息 -->
        <div class="summary-info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="物资种类">{{ orderDetails.length }} 种</el-descriptions-item>
            <el-descriptions-item label="总数量">{{ totalDetailQuantity }} 件</el-descriptions-item>
            <el-descriptions-item label="总金额">¥{{ totalDetailAmount.toFixed(2) }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <!-- 备注信息 -->
        <h3 class="detail-title">备注信息</h3>
        <div class="remark-info">
          <el-input
            v-model="currentOrder.remark"
            type="textarea"
            :rows="3"
            placeholder="暂无备注"
            readonly
          />
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 订单跟踪对话框 -->
    <el-dialog
      v-model="trackDialogVisible"
      :title="`订单跟踪 - ${currentOrder.orderNo}`"
      width="600px"
      @close="handleTrackDialogClose"
    >
      <div class="track-info">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="供应商">{{ currentOrder.supplierName }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">{{ formatAmount(null, null, currentOrder.totalAmount) }}</el-descriptions-item>
          <el-descriptions-item label="下单日期">{{ currentOrder.orderDate }}</el-descriptions-item>
          <el-descriptions-item label="预计到货日期">{{ currentOrder.expectedDate }}</el-descriptions-item>
          <el-descriptions-item label="实际到货日期">{{ currentOrder.actualDate || '未到货' }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">{{ getStatusName(currentOrder.status) }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="logistics-info">
          <h3>物流信息</h3>
          <el-timeline>
            <el-timeline-item v-for="(item, index) in logisticsLogs" :key="index" :timestamp="item.time">
              {{ item.content }}
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="trackDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import request from '../../services/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const orders = ref([])
const suppliers = ref([])
const materials = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const searchSupplierId = ref('')
const searchStatus = ref('')
const dateRange = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增采购订单')
const dialogLoading = ref(false)
const detailLoading = ref(false)
const orderFormRef = ref(null)
const currentOrder = ref({})

// 跟踪对话框相关
const trackDialogVisible = ref(false)
const logisticsLogs = ref([])

// 订单详情对话框相关
const detailDialogVisible = ref(false)
const orderDetails = ref([])

// 订单表单数据
const orderForm = reactive({
  id: '',
  orderNo: '',
  supplierId: '',
  supplierName: '',
  totalAmount: 0,
  orderDate: new Date().toISOString().slice(0, 19).replace('T', ' '),
  expectedDate: '',
  actualDate: '',
  paymentMethod: 'bank',
  status: 'pending',
  operator: '当前用户',
  remark: '',
  details: [
    {
      materialId: '',
      materialName: '',
      specification: '',
      unit: '',
      quantity: 1,
      price: 0,
      amount: 0,
      remark: ''
    }
  ]
})

// 订单表单验证规则
const orderRules = {
  supplierId: [
    { required: true, message: '请选择供应商', trigger: 'change' }
  ],
  orderDate: [
    { required: true, message: '请选择下单日期', trigger: 'change' }
  ],
  expectedDate: [
    { required: true, message: '请选择预计到货日期', trigger: 'change' }
  ]
}

// 计算总数量和总金额
const totalQuantity = computed(() => {
  return orderForm.details.reduce((sum, item) => sum + item.quantity, 0)
})

const totalAmount = computed(() => {
  return orderForm.details.reduce((sum, item) => sum + (item.quantity * item.price), 0)
})

// 订单详情合计
const totalDetailQuantity = computed(() => {
  return orderDetails.value.reduce((sum, item) => sum + item.quantity, 0)
})

const totalDetailAmount = computed(() => {
  return orderDetails.value.reduce((sum, item) => sum + (item.quantity * item.price), 0)
})

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    // 调用后端API
    const response = await request.get('/purchase-order/list', {
      params: {
        orderNo: searchKeyword.value || undefined,
        supplierId: searchSupplierId.value || undefined,
        status: searchStatus.value || undefined
      }
    })
    
    orders.value = response.data || []
    total.value = orders.value.length
    
    // 获取供应商列表
    const supplierResponse = await request.get('/supplier/list', {
      params: {
        status: 1
      }
    })
    suppliers.value = supplierResponse.data || []
    
    // 获取物资列表
    const materialResponse = await request.get('/material/list', {
      params: {
        status: 1
      }
    })
    materials.value = materialResponse.data || []
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    0: '待审核',
    1: '已审核',
    2: '已入库',
    3: '已取消'
  }
  return statusMap[status] || status
}

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    0: 'info',
    1: 'primary',
    2: 'success',
    3: 'danger'
  }
  return colorMap[status] || 'info'
}

// 格式化金额
const formatAmount = (row, column, cellValue) => {
  return `¥${cellValue.toFixed(2)}`
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedOrders.value = selection
}

// 处理新增订单
const handleAddOrder = () => {
  dialogTitle.value = '新增采购订单'
  // 重置表单
  if (orderFormRef.value) {
    orderFormRef.value.resetFields()
  }
  // 生成订单编号
  const now = new Date()
  const orderNo = `PO${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}${String(Math.floor(Math.random() * 1000)).padStart(3, '0')}`
  // 重置表单数据
  Object.assign(orderForm, {
    id: '',
    orderNo: orderNo,
    supplierId: '',
    supplierName: '',
    totalAmount: 0,
    orderDate: now.toISOString().slice(0, 19).replace('T', ' '),
    expectedDate: '',
    actualDate: '',
    paymentMethod: 'bank',
    status: 'pending',
    operator: '当前用户',
    remark: '',
    details: [
      {
        materialId: '',
        materialName: '',
        specification: '',
        unit: '',
        quantity: 1,
        price: 0,
        amount: 0,
        remark: ''
      }
    ]
  })
  dialogVisible.value = true
}

// 处理查看订单
const handleViewOrder = async (row) => {
  try {
    detailLoading.value = true
    currentOrder.value = row
    
    // 获取订单详情
    const response = await request.get(`/purchase-order/${row.id}`)
    const orderData = response.data
    
    // 更新当前订单信息
    Object.assign(currentOrder.value, orderData)
    
    // 获取订单明细
    const itemResponse = await request.get('/purchase-order-item/list', {
      params: {
        orderId: row.id
      }
    })
    orderDetails.value = itemResponse.data || []
    
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  } finally {
    detailLoading.value = false
  }
}

// 处理审批订单
const handleApproveOrder = (row) => {
  ElMessageBox.confirm(
    `确定要审批订单 "${row.orderNo}" 吗？`,
    '审批订单',
    {
      confirmButtonText: '通过',
      cancelButtonText: '拒绝',
      type: 'success'
    }
  )
  .then(() => {
    // 模拟API调用
    return new Promise(resolve => setTimeout(resolve, 800))
  })
  .then(() => {
    console.log('审批通过订单:', row)
    ElMessage.success('订单审批通过')
    // 刷新订单列表
    fetchOrders()
  })
  .catch((action) => {
    if (action === 'cancel') {
      // 拒绝审批
      ElMessageBox.prompt(
        '请输入拒绝原因:',
        '拒绝订单',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputType: 'textarea'
        }
      )
      .then(({ value }) => {
        console.log('拒绝订单:', row, '原因:', value)
        ElMessage.success('订单已拒绝')
        // 刷新订单列表
        fetchOrders()
      })
      .catch(() => {
        // 取消拒绝
      })
    }
  })
}

// 处理跟踪订单
const handleTrackOrder = (row) => {
  currentOrder.value = row
  // 模拟物流日志
  logisticsLogs.value = [
    {
      time: '2024-01-05 10:00:00',
      content: '订单已确认'
    },
    {
      time: '2024-01-06 14:30:00',
      content: '商品已出库'
    },
    {
      time: '2024-01-07 09:00:00',
      content: '商品在运输中'
    },
    {
      time: '2024-01-08 16:00:00',
      content: '商品已到达本地仓库'
    }
  ]
  trackDialogVisible.value = true
}

// 处理变更订单
const handleChangeOrder = (row) => {
  console.log('变更订单:', row)
  ElMessage.info('变更订单功能开发中...')
}

// 处理取消订单
const handleCancelOrder = (row) => {
  ElMessageBox.confirm(
    `确定要取消订单 "${row.orderNo}" 吗？`,
    '取消订单',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
  .then(() => {
    // 模拟API调用
    return new Promise(resolve => setTimeout(resolve, 800))
  })
  .then(() => {
    console.log('取消订单:', row)
    ElMessage.success('订单已取消')
    // 刷新订单列表
    fetchOrders()
  })
  .catch(() => {
    // 取消操作
  })
}

// 处理供应商变化
const handleSupplierChange = (value) => {
  const supplier = suppliers.value.find(item => item.id === value)
  if (supplier) {
    orderForm.supplierName = supplier.supplierName
  }
}

// 处理物资变化
const handleMaterialChange = (row) => {
  const material = materials.value.find(item => item.id === row.materialId)
  if (material) {
    row.materialName = material.name
    row.specification = material.specification
    row.unit = material.unit
    row.price = material.price
  }
}

// 处理数量变化
const handleQuantityChange = () => {
  // 数量变化时自动计算金额
}

// 处理价格变化
const handlePriceChange = () => {
  // 价格变化时自动计算金额
}

// 处理添加明细
const handleAddDetail = () => {
  orderForm.details.push({
    materialId: '',
    materialName: '',
    specification: '',
    unit: '',
    quantity: 1,
    price: 0,
    amount: 0,
    remark: ''
  })
}

// 处理删除明细
const handleRemoveDetail = (index) => {
  if (orderForm.details.length > 1) {
    orderForm.details.splice(index, 1)
  } else {
    ElMessage.warning('至少保留一条明细记录')
  }
}

// 处理对话框关闭
const handleDialogClose = () => {
  // 重置表单
  if (orderFormRef.value) {
    orderFormRef.value.resetFields()
  }
  currentOrder.value = {}
}

// 处理跟踪对话框关闭
const handleTrackDialogClose = () => {
  trackDialogVisible.value = false
  logisticsLogs.value = []
}

// 处理详情对话框关闭
const handleDetailDialogClose = () => {
  detailDialogVisible.value = false
  orderDetails.value = []
}

// 处理保存订单
const handleSaveOrder = async () => {
  if (!orderFormRef.value) return
  
  try {
    const valid = await orderFormRef.value.validate()
    if (valid) {
      dialogLoading.value = true
      
      // 检查明细是否完整
      const hasEmptyMaterial = orderForm.details.some(item => !item.materialId)
      if (hasEmptyMaterial) {
        ElMessage.warning('请选择完整的物资信息')
        return
      }
      
      // 状态映射：字符串 -> 整数（根据数据库定义）
      const statusMap = {
        'pending': 0,
        'approved': 1,
        'received': 2,
        'canceled': 3
      }
      
      // 准备提交数据
      const submitData = {
        ...orderForm,
        status: statusMap[orderForm.status] || 0
      }
      
      if (orderForm.id) {
        // 编辑订单
        await request.put('/purchase-order/update', submitData)
        ElMessage.success('订单编辑成功')
      } else {
        // 新增订单
        await request.post('/purchase-order/add', submitData)
        ElMessage.success('订单新增成功')
      }
      
      // 关闭对话框
      dialogVisible.value = false
      
      // 刷新订单列表
      fetchOrders()
    }
  } catch (error) {
    console.error('保存订单失败:', error)
    ElMessage.error('保存订单失败')
  } finally {
    dialogLoading.value = false
  }
}

// 分页相关
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  fetchOrders()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  fetchOrders()
}

// 页面挂载时获取订单列表
onMounted(() => {
  fetchOrders()
})

// 选择的记录
const selectedOrders = ref([])
</script>

<style scoped>
.purchase-order-list-container {
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

.order-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-search {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.order-table-container {
  margin-bottom: 20px;
  overflow-x: auto;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

.order-detail-container {
  padding: 20px;
}

.order-info {
  margin-bottom: 30px;
}

.detail-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 20px 0 15px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.summary-info {
  margin: 20px 0;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.remark-info {
  margin-top: 20px;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .purchase-order-list-container {
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
  
  .order-search {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .order-search .el-input,
  .order-search .el-select,
  .order-search .el-date-picker {
    width: 100% !important;
    margin-right: 0 !important;
    margin-left: 0 !important;
  }
  
  .pagination {
    justify-content: center;
  }
  
  /* 表格响应式处理 */
  .order-table-container {
    width: 100%;
    overflow-x: auto;
  }
  
  /* 对话框响应式 */
  .el-dialog {
    max-width: 95vw !important;
    width: auto !important;
    margin: 10px !important;
  }
  
  /* 表单响应式 */
  .el-form-item {
    margin-bottom: 12px;
  }
  
  /* 明细操作响应式 */
  .detail-actions {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
}

@media screen and (max-width: 480px) {
  .card-header .el-button {
    width: 100%;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.detail-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.total-info {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.track-info {
  padding: 20px 0;
}

.logistics-info {
  margin-top: 20px;
}

.logistics-info h3 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}
</style>