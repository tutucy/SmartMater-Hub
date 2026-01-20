<template>
  <div class="role-list-container">
    <h2 class="page-title">角色管理</h2>
    
    <el-card shadow="hover" class="role-card">
      <template #header>
        <div class="card-header">
          <span>角色列表</span>
          <el-button type="primary" @click="handleAddRole" icon="Plus">
            添加角色
          </el-button>
        </div>
      </template>
      
      <!-- 角色搜索 -->
      <div class="role-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索角色名称或描述"
          clearable
          prefix-icon="Search"
          @keyup.enter="fetchRoles"
        />
        <el-button type="primary" @click="fetchRoles" icon="RefreshRight">
          刷新
        </el-button>
      </div>
      
      <!-- 角色列表 -->
      <div class="role-table-container">
        <el-table
          v-loading="loading"
          :data="roleList"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="角色名称" width="150" />
          <el-table-column prop="code" label="角色编码" width="150" />
          <el-table-column prop="description" label="角色描述" min-width="200" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-switch
                v-model="scope.row.status"
                active-value="1"
                inactive-value="0"
                @change="handleStatusChange(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column prop="updateTime" label="更新时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleEditRole(scope.row)" icon="Edit">
                编辑
              </el-button>
              <el-button type="success" size="small" @click="handlePermissionConfig(scope.row)" icon="Setting">
                权限配置
              </el-button>
              <el-button type="danger" size="small" @click="handleDeleteRole(scope.row)" icon="Delete">
                删除
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
    
    <!-- 添加/编辑角色对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleRules"
        label-position="right"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="name">
          <el-input
            v-model="roleForm.name"
            placeholder="请输入角色名称"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="角色编码" prop="code">
          <el-input
            v-model="roleForm.code"
            placeholder="请输入角色编码"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="角色描述" prop="description">
          <el-input
            v-model="roleForm.description"
            type="textarea"
            placeholder="请输入角色描述"
            rows="3"
          />
        </el-form-item>
        
        <el-form-item label="状态">
          <el-switch
            v-model="roleForm.status"
            active-value="1"
            inactive-value="0"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="dialogLoading" @click="handleSaveRole">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../services/api'
import { ElMessage, ElMessageBox } from 'element-plus'

// 响应式数据
const searchKeyword = ref('')
const roleList = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedRoles = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('添加角色')
const dialogLoading = ref(false)
const roleFormRef = ref(null)

// 角色表单数据
const roleForm = reactive({
  id: '',
  name: '',
  code: '',
  description: '',
  status: '1'
})

// 角色表单验证规则
const roleRules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 20, message: '角色名称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '角色编码只能包含字母、数字和下划线', trigger: 'blur' }
  ]
}

// 获取角色列表
const fetchRoles = async () => {
  loading.value = true
  try {
    const response = await request.get('/role/list', {
      params: {
        keyword: searchKeyword.value,
        page: currentPage.value,
        size: pageSize.value
      }
    })
    
    roleList.value = response.data || []
    total.value = response.total || 0
  } catch (error) {
    console.error('获取角色列表失败:', error)
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedRoles.value = selection
}

// 处理添加角色
const handleAddRole = () => {
  dialogTitle.value = '添加角色'
  // 重置表单
  if (roleFormRef.value) {
    roleFormRef.value.resetFields()
  }
  // 重置表单数据
  Object.assign(roleForm, {
    id: '',
    name: '',
    code: '',
    description: '',
    status: '1'
  })
  dialogVisible.value = true
}

// 处理编辑角色
const handleEditRole = (row) => {
  dialogTitle.value = '编辑角色'
  // 填充表单数据
  Object.assign(roleForm, { ...row })
  dialogVisible.value = true
}

// 处理保存角色
const handleSaveRole = async () => {
  if (!roleFormRef.value) return
  
  try {
    const valid = await roleFormRef.value.validate()
    if (valid) {
      dialogLoading.value = true
      
      if (roleForm.id) {
        await request.put('/role/update', roleForm)
        ElMessage.success('角色编辑成功')
      } else {
        await request.post('/role/add', roleForm)
        ElMessage.success('角色添加成功')
      }
      
      dialogVisible.value = false
      fetchRoles()
    }
  } catch (error) {
    console.error('保存角色失败:', error)
    ElMessage.error('保存角色失败')
  } finally {
    dialogLoading.value = false
  }
}

// 处理对话框关闭
const handleDialogClose = () => {
  // 重置表单
  if (roleFormRef.value) {
    roleFormRef.value.resetFields()
  }
}

// 处理权限配置
const handlePermissionConfig = (row) => {
  console.log('配置角色权限:', row)
  ElMessage.info('权限配置功能开发中')
}

// 处理删除角色
const handleDeleteRole = (row) => {
  ElMessageBox.confirm(
    `确定要删除角色 "${row.name}" 吗？删除后无法恢复。`,
    '删除角色',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
  .then(async () => {
    try {
      await request.delete('/role/delete', { id: row.id })
      ElMessage.success('角色删除成功')
      fetchRoles()
    } catch (error) {
      console.error('删除角色失败:', error)
      ElMessage.error('删除角色失败')
    }
  })
  .catch(() => {
  })
}

// 处理状态变化
const handleStatusChange = async (row) => {
  try {
    await request.put('/role/update', {
      id: row.id,
      status: row.status
    })
    ElMessage.success('角色状态更新成功')
  } catch (error) {
    console.error('更新角色状态失败:', error)
    ElMessage.error('更新角色状态失败')
    row.status = row.status === '1' ? '0' : '1'
  }
}

// 分页相关
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  fetchRoles()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  fetchRoles()
}

// 页面挂载时获取角色列表
onMounted(() => {
  fetchRoles()
})
</script>

<style scoped>
.role-list-container {
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

.role-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.role-search {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
}

.role-search .el-input {
  width: 300px;
}

.role-table-container {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
