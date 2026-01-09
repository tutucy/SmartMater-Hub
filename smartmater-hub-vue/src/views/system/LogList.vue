<template>
  <div class="log-list-container">
    <h2 class="page-title">系统日志</h2>
    
    <el-card shadow="hover" class="log-card">
      <template #header>
        <div class="card-header">
          <span>操作日志列表</span>
          <div class="header-actions">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DD HH:mm"
            />
            <el-select
              v-model="operationType"
              placeholder="操作类型"
              clearable
              style="width: 150px"
            >
              <el-option label="登录" value="login" />
              <el-option label="退出" value="logout" />
              <el-option label="新增" value="add" />
              <el-option label="修改" value="update" />
              <el-option label="删除" value="delete" />
              <el-option label="查询" value="query" />
              <el-option label="导出" value="export" />
            </el-select>
            <el-select
              v-model="module"
              placeholder="操作模块"
              clearable
              style="width: 150px"
            >
              <el-option label="用户管理" value="user" />
              <el-option label="物资管理" value="material" />
              <el-option label="库存管理" value="inventory" />
              <el-option label="采购管理" value="purchase" />
              <el-option label="报表管理" value="report" />
              <el-option label="系统管理" value="system" />
            </el-select>
            <el-input
              v-model="operator"
              placeholder="操作用户"
              clearable
              style="width: 150px"
            />
            <el-button type="primary" @click="fetchLogData" icon="RefreshRight">
              查询
            </el-button>
            <el-button type="success" @click="handleExport" icon="Download">
              导出日志
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 日志列表 -->
      <div class="log-table-container">
        <el-table
          v-loading="loading"
          :data="logData"
          style="width: 100%"
          border
          :row-key="row => row.id"
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="operator" label="操作用户" width="120" />
          <el-table-column prop="operationType" label="操作类型" width="120">
            <template #default="scope">
              <el-tag :type="getOperationTypeTagType(scope.row.operationType)">
                {{ getOperationTypeText(scope.row.operationType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="module" label="操作模块" width="120">
            <template #default="scope">
              <el-tag type="info">{{ getModuleText(scope.row.module) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="operationContent" label="操作内容" min-width="200" />
          <el-table-column prop="ipAddress" label="IP地址" width="150" />
          <el-table-column prop="userAgent" label="浏览器" width="200" />
          <el-table-column prop="operationTime" label="操作时间" width="180" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status ? 'success' : 'danger'">
                {{ scope.row.status ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="150" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const dateRange = ref([])
const operationType = ref('')
const module = ref('')
const operator = ref('')
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const logData = ref([])

// 获取日志数据
const fetchLogData = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟日志数据
    logData.value = [
      {
        id: 1,
        operator: 'admin',
        operationType: 'login',
        module: 'system',
        operationContent: '用户登录系统',
        ipAddress: '127.0.0.1',
        userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
        operationTime: '2024-01-15 10:30:25',
        status: true,
        remark: ''
      },
      {
        id: 2,
        operator: 'admin',
        operationType: 'add',
        module: 'user',
        operationContent: '新增用户：testuser',
        ipAddress: '127.0.0.1',
        userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
        operationTime: '2024-01-15 10:35:12',
        status: true,
        remark: ''
      },
      {
        id: 3,
        operator: 'admin',
        operationType: 'update',
        module: 'material',
        operationContent: '修改物资信息：笔记本电脑',
        ipAddress: '127.0.0.1',
        userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
        operationTime: '2024-01-15 11:20:30',
        status: true,
        remark: ''
      },
      {
        id: 4,
        operator: 'purchaser1',
        operationType: 'add',
        module: 'purchase',
        operationContent: '新增采购订单：PO202401001',
        ipAddress: '192.168.1.100',
        userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
        operationTime: '2024-01-15 14:15:45',
        status: true,
        remark: ''
      },
      {
        id: 5,
        operator: 'warehouse1',
        operationType: 'update',
        module: 'inventory',
        operationContent: '库存入库：打印纸 100箱',
        ipAddress: '192.168.1.101',
        userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
        operationTime: '2024-01-15 15:30:18',
        status: true,
        remark: ''
      },
      {
        id: 6,
        operator: 'admin',
        operationType: 'logout',
        module: 'system',
        operationContent: '用户退出系统',
        ipAddress: '127.0.0.1',
        userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
        operationTime: '2024-01-15 17:45:30',
        status: true,
        remark: ''
      }
    ]
    
    // 模拟数据过滤
    let filteredData = [...logData.value]
    
    // 根据操作类型过滤
    if (operationType.value) {
      filteredData = filteredData.filter(item => item.operationType === operationType.value)
    }
    
    // 根据模块过滤
    if (module.value) {
      filteredData = filteredData.filter(item => item.module === module.value)
    }
    
    // 根据操作用户过滤
    if (operator.value) {
      filteredData = filteredData.filter(item => item.operator.includes(operator.value))
    }
    
    total.value = filteredData.length
    // 模拟分页
    const startIndex = (currentPage.value - 1) * pageSize.value
    const endIndex = startIndex + pageSize.value
    logData.value = filteredData.slice(startIndex, endIndex)
  } catch (error) {
    console.error('获取日志数据失败:', error)
    ElMessage.error('获取日志数据失败')
  } finally {
    loading.value = false
  }
}

// 获取操作类型标签类型
const getOperationTypeTagType = (type) => {
  const typeMap = {
    'login': 'primary',
    'logout': 'info',
    'add': 'success',
    'update': 'warning',
    'delete': 'danger',
    'query': 'info',
    'export': 'success'
  }
  return typeMap[type] || 'info'
}

// 获取操作类型文本
const getOperationTypeText = (type) => {
  const textMap = {
    'login': '登录',
    'logout': '退出',
    'add': '新增',
    'update': '修改',
    'delete': '删除',
    'query': '查询',
    'export': '导出'
  }
  return textMap[type] || type
}

// 获取模块文本
const getModuleText = (module) => {
  const textMap = {
    'user': '用户管理',
    'material': '物资管理',
    'inventory': '库存管理',
    'purchase': '采购管理',
    'report': '报表管理',
    'system': '系统管理'
  }
  return textMap[module] || module
}

// 处理导出
const handleExport = () => {
  ElMessage.success('日志导出功能开发中')
}

// 分页相关
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  fetchLogData()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  fetchLogData()
}

// 页面挂载时获取日志数据
onMounted(() => {
  fetchLogData()
})
</script>

<style scoped>
.log-list-container {
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

.log-card {
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

.log-table-container {
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>