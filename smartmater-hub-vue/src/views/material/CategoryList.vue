<template>
  <div class="category-list-container">
    <h2 class="page-title">物资分类管理</h2>
    
    <el-card shadow="hover" class="category-card">
      <template #header>
        <div class="card-header">
          <span>分类列表</span>
          <el-button type="primary" @click="handleAddCategory" icon="Plus">
            添加分类
          </el-button>
        </div>
      </template>
      
      <!-- 分类搜索 -->
      <div class="category-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索分类名称"
          clearable
          prefix-icon="Search"
          @keyup.enter="fetchCategories"
        />
        <el-select
          v-model="searchParentId"
          placeholder="选择上级分类"
          clearable
          style="width: 200px; margin-left: 10px;"
        >
          <el-option label="无上级分类" value="" />
          <el-option
            v-for="category in categoryOptions"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
        <el-button type="primary" @click="fetchCategories" icon="RefreshRight" style="margin-left: 10px;">
          刷新
        </el-button>
      </div>
      
      <!-- 分类列表 -->
      <div class="category-table-container">
        <el-table
          v-loading="loading"
          :data="categoryList"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="分类名称" width="180" />
          <el-table-column prop="parentName" label="上级分类" width="180" />
          <el-table-column prop="description" label="分类描述" min-width="200" />
          <el-table-column prop="level" label="层级" width="80" align="center" />
          <el-table-column prop="materialCount" label="关联物资数" width="120" align="center" />
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleEditCategory(scope.row)" icon="Edit">
                编辑
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="handleDeleteCategory(scope.row)" 
                icon="Delete"
                :disabled="scope.row.materialCount > 0"
              >
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
    
    <!-- 添加/编辑分类对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryForm"
        :rules="categoryRules"
        label-position="right"
        label-width="80px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input
            v-model="categoryForm.name"
            placeholder="请输入分类名称"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="上级分类">
          <el-select
            v-model="categoryForm.parentId"
            placeholder="选择上级分类"
            clearable
          >
            <el-option label="无上级分类" value="" />
            <el-option
              v-for="category in categoryOptions"
              :key="category.id"
              :label="category.name"
              :value="category.id"
              :disabled="category.id === categoryForm.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="分类描述" prop="description">
          <el-input
            v-model="categoryForm.description"
            placeholder="请输入分类描述"
            type="textarea"
            rows="3"
            clearable
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="dialogLoading" @click="handleSaveCategory">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import request from '../../services/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const categoryList = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const searchParentId = ref('')
const selectedCategories = ref([])
const categoryOptions = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('添加分类')
const dialogLoading = ref(false)
const categoryFormRef = ref(null)

// 分类表单数据
const categoryForm = reactive({
  id: '',
  name: '',
  parentId: '',
  description: ''
})

// 分类表单验证规则
const categoryRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 50, message: '分类名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '分类描述不超过 200 个字符', trigger: 'blur' }
  ]
}

// 获取分类列表
const fetchCategories = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟分类列表数据
    const mockCategories = [
      {
        id: 1,
        name: '办公用品',
        parentId: '',
        parentName: '无',
        description: '办公所需的各种用品',
        level: 1,
        materialCount: 10,
        createTime: '2024-01-01 10:00:00'
      },
      {
        id: 2,
        name: '文具',
        parentId: '1',
        parentName: '办公用品',
        description: '各种办公文具',
        level: 2,
        materialCount: 5,
        createTime: '2024-01-01 10:30:00'
      },
      {
        id: 3,
        name: '耗材',
        parentId: '1',
        parentName: '办公用品',
        description: '办公耗材',
        level: 2,
        materialCount: 3,
        createTime: '2024-01-01 11:00:00'
      },
      {
        id: 4,
        name: '设备',
        parentId: '',
        parentName: '无',
        description: '办公设备',
        level: 1,
        materialCount: 8,
        createTime: '2024-01-02 09:00:00'
      },
      {
        id: 5,
        name: '电子设备',
        parentId: '4',
        parentName: '设备',
        description: '电子办公设备',
        level: 2,
        materialCount: 6,
        createTime: '2024-01-02 09:30:00'
      },
      {
        id: 6,
        name: '家具',
        parentId: '4',
        parentName: '设备',
        description: '办公家具',
        level: 2,
        materialCount: 2,
        createTime: '2024-01-02 10:00:00'
      },
      {
        id: 7,
        name: '生产物资',
        parentId: '',
        parentName: '无',
        description: '生产所需的物资',
        level: 1,
        materialCount: 15,
        createTime: '2024-01-03 14:00:00'
      }
    ]
    
    // 模拟搜索功能
    let filteredCategories = mockCategories
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filteredCategories = mockCategories.filter(category => 
        category.name.toLowerCase().includes(keyword)
      )
    }
    
    if (searchParentId.value) {
      filteredCategories = filteredCategories.filter(category => 
        category.parentId === searchParentId.value
      )
    }
    
    categoryList.value = filteredCategories
    total.value = filteredCategories.length
    
    // 更新分类选项（用于上级分类选择）
    categoryOptions.value = mockCategories
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedCategories.value = selection
}

// 处理添加分类
const handleAddCategory = () => {
  dialogTitle.value = '添加分类'
  // 重置表单
  if (categoryFormRef.value) {
    categoryFormRef.value.resetFields()
  }
  // 重置表单数据
  Object.assign(categoryForm, {
    id: '',
    name: '',
    parentId: '',
    description: ''
  })
  dialogVisible.value = true
}

// 处理编辑分类
const handleEditCategory = (row) => {
  dialogTitle.value = '编辑分类'
  // 填充表单数据
  Object.assign(categoryForm, row)
  dialogVisible.value = true
}

// 处理保存分类
const handleSaveCategory = async () => {
  if (!categoryFormRef.value) return
  
  try {
    const valid = await categoryFormRef.value.validate()
    if (valid) {
      dialogLoading.value = true
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      if (categoryForm.id) {
        // 编辑分类
        console.log('编辑分类:', categoryForm)
        ElMessage.success('分类编辑成功')
      } else {
        // 添加分类
        console.log('添加分类:', categoryForm)
        ElMessage.success('分类添加成功')
      }
      
      // 关闭对话框
      dialogVisible.value = false
      
      // 刷新分类列表
      fetchCategories()
    }
  } catch (error) {
    console.error('保存分类失败:', error)
    ElMessage.error('保存分类失败')
  } finally {
    dialogLoading.value = false
  }
}

// 处理对话框关闭
const handleDialogClose = () => {
  // 重置表单
  if (categoryFormRef.value) {
    categoryFormRef.value.resetFields()
  }
}

// 处理删除分类
const handleDeleteCategory = (row) => {
  if (row.materialCount > 0) {
    ElMessage.warning('该分类下有物资关联，无法删除')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除分类 "${row.name}" 吗？删除后无法恢复。`,
    '删除分类',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
  .then(() => {
    // 调用删除分类API
    // 模拟API调用
    return new Promise(resolve => setTimeout(resolve, 800))
  })
  .then(() => {
    console.log('删除分类:', row)
    ElMessage.success('分类删除成功')
    // 刷新分类列表
    fetchCategories()
  })
  .catch(() => {
    // 取消删除
  })
}

// 分页相关
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  fetchCategories()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  fetchCategories()
}

// 页面挂载时获取分类列表
onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.category-list-container {
  padding: var(--spacing-xl);
  background-color: var(--bg-secondary);
  min-height: calc(100vh - 100px);
}

.page-title {
  font-size: var(--font-size-xxl);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-xxl);
  transition: var(--transition-base);
  padding-bottom: var(--spacing-lg);
  border-bottom: 2px solid var(--border-color);
}

.category-card {
  margin-bottom: var(--spacing-xl);
  border-radius: var(--border-radius-xl);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--spacing-lg);
  padding: var(--spacing-lg) 0;
}

.category-search {
  display: flex;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.category-search .el-input {
  width: 320px;
  transition: var(--transition-base);
}

.category-search .el-select {
  width: 220px;
  transition: var(--transition-base);
}

.category-table-container {
  margin-bottom: var(--spacing-xl);
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-light);
}

.pagination {
  display: flex;
  justify-content: flex-end;
  padding: var(--spacing-lg) 0 0 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .category-list-container {
    padding: var(--spacing-lg);
  }
  
  .page-title {
    font-size: var(--font-size-xl);
    margin-bottom: var(--spacing-xl);
  }
  
  .category-search {
    flex-direction: column;
    align-items: stretch;
    gap: var(--spacing-lg);
  }
  
  .category-search .el-input,
  .category-search .el-select {
    width: 100%;
  }
  
  .card-header {
    flex-direction: column;
    align-items: stretch;
    gap: var(--spacing-md);
  }
  
  .card-header span {
    font-size: var(--font-size-base);
  }
  
  .pagination {
    justify-content: center;
    padding: var(--spacing-md) 0 0 0;
  }
}

@media screen and (max-width: 480px) {
  .category-list-container {
    padding: var(--spacing-md);
  }
  
  .page-title {
    font-size: var(--font-size-lg);
    margin-bottom: var(--spacing-lg);
  }
  
  .el-button {
    padding: 8px 16px;
    font-size: var(--font-size-sm);
  }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .page-title {
    color: var(--text-primary);
    border-bottom-color: var(--border-color);
  }
  
  .category-table-container {
    background-color: var(--bg-primary);
    box-shadow: var(--shadow-dark);
  }
}
</style>