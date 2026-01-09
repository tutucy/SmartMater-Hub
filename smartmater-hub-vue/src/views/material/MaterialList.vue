<template>
  <div class="material-list-container">
    <h2 class="page-title">物资信息管理</h2>
    
    <el-card shadow="hover" class="material-card">
      <template #header>
        <div class="card-header">
          <span>物资列表</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAddMaterial" icon="Plus">
              添加物资
            </el-button>
            <el-button type="success" @click="showImportDialog = true" icon="Upload">
              批量导入
            </el-button>
            <el-button type="info" @click="handleDownloadTemplate" icon="DocumentDownload">
              下载模板
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 物资搜索 -->
      <div class="material-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索物资名称、型号规格或品牌"
          clearable
          prefix-icon="Search"
          @keyup.enter="fetchMaterials"
          style="width: 350px; margin-right: 10px;"
        />
        <el-select
          v-model="searchCategoryId"
          placeholder="选择物资分类"
          clearable
          style="width: 200px; margin-right: 10px;"
        >
          <el-option label="全部分类" value="" />
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
        <el-select
          v-model="searchStockStatus"
          placeholder="库存状态"
          clearable
          style="width: 150px; margin-right: 10px;"
        >
          <el-option label="全部状态" value="" />
          <el-option label="正常" value="normal" />
          <el-option label="低库存" value="low" />
          <el-option label="缺货" value="out" />
        </el-select>
        <el-button type="primary" @click="fetchMaterials" icon="RefreshRight">
          刷新
        </el-button>
      </div>
      
      <!-- 物资列表 -->
      <div class="material-table-container">
        <el-table
          v-loading="loading"
          :data="materialList"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="物资名称" min-width="180" />
          <el-table-column prop="specification" label="型号规格" min-width="150" />
          <el-table-column prop="brand" label="品牌" width="120" />
          <el-table-column prop="unit" label="单位" width="80" />
          <el-table-column prop="categoryName" label="分类" width="150" />
          <el-table-column prop="supplierName" label="供应商" width="150" />
          <el-table-column prop="stockQuantity" label="库存数量" width="120" align="center">
            <template #default="scope">
              <span :class="getStockStatusClass(scope.row)">
                {{ scope.row.stockQuantity }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="stockWarningValue" label="库存预警值" width="120" align="center" />
          <el-table-column prop="price" label="单价(元)" width="120" align="right" :formatter="formatPrice" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
                {{ scope.row.status === '1' ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleViewMaterial(scope.row)" icon="View">
                查看
              </el-button>
              <el-button type="success" size="small" @click="handleEditMaterial(scope.row)" icon="Edit">
                编辑
              </el-button>
              <el-button type="danger" size="small" @click="handleDeleteMaterial(scope.row)" icon="Delete">
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
    
    <!-- 添加/编辑物资对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="materialFormRef"
        :model="materialForm"
        :rules="materialRules"
        label-position="top"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="物资名称" prop="name">
              <el-input
                v-model="materialForm.name"
                placeholder="请输入物资名称"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="型号规格" prop="specification">
              <el-input
                v-model="materialForm.specification"
                placeholder="请输入型号规格"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="品牌" prop="brand">
              <el-input
                v-model="materialForm.brand"
                placeholder="请输入品牌"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="单位" prop="unit">
              <el-input
                v-model="materialForm.unit"
                placeholder="请输入单位（如：件、盒、千克等）"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="所属分类" prop="categoryId">
              <el-select
                v-model="materialForm.categoryId"
                placeholder="请选择所属分类"
                clearable
              >
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                />
              </el-select>
            </el-form-item>
            
            <el-form-item label="供应商" prop="supplierId">
              <el-select
                v-model="materialForm.supplierId"
                placeholder="请选择供应商"
                clearable
              >
                <el-option
                  v-for="supplier in suppliers"
                  :key="supplier.id"
                  :label="supplier.name"
                  :value="supplier.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="库存数量" prop="stockQuantity">
              <el-input-number
                v-model="materialForm.stockQuantity"
                :min="0"
                placeholder="请输入库存数量"
                style="width: 100%;"
              />
            </el-form-item>
            
            <el-form-item label="库存预警值" prop="stockWarningValue">
              <el-input-number
                v-model="materialForm.stockWarningValue"
                :min="0"
                placeholder="请输入库存预警值"
                style="width: 100%;"
              />
            </el-form-item>
            
            <el-form-item label="单价(元)" prop="price">
              <el-input-number
                v-model="materialForm.price"
                :min="0"
                :precision="2"
                placeholder="请输入单价"
                style="width: 100%;"
              />
            </el-form-item>
            
            <el-form-item label="物资描述" prop="description">
              <el-input
                v-model="materialForm.description"
                placeholder="请输入物资描述"
                type="textarea"
                rows="3"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="状态">
              <el-switch
                v-model="materialForm.status"
                active-value="1"
                inactive-value="0"
              />
            </el-form-item>
            
            <el-form-item label="物资图片">
              <el-upload
                v-model:file-list="materialForm.imageList"
                action="#"
                list-type="picture-card"
                :auto-upload="false"
                :on-preview="handlePictureCardPreview"
                :on-remove="handleRemove"
              >
                <el-icon><Plus /></el-icon>
                <template #tip>
                  <div class="el-upload__tip">
                    只能上传jpg/png文件，且不超过500kb
                  </div>
                </template>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <!-- 图片预览对话框 -->
      <el-dialog v-model="previewVisible" title="图片预览" width="500px">
        <img w-full :src="previewImage" alt="Preview Image" />
      </el-dialog>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="dialogLoading" @click="handleSaveMaterial">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 批量导入对话框 -->
    <el-dialog
      v-model="showImportDialog"
      title="批量导入物资"
      width="600px"
      @close="handleImportDialogClose"
    >
      <el-upload
        ref="uploadRef"
        :auto-upload="false"
        :file-list="importFileList"
        :on-change="handleFileChange"
        accept=".xlsx, .xls"
        drag
        multiple
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到此处或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            仅支持.xlsx和.xls格式文件，建议使用系统提供的模板
          </div>
        </template>
      </el-upload>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showImportDialog = false">取消</el-button>
          <el-button type="primary" :loading="importLoading" @click="handleImport">
            开始导入
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
import { UploadFilled, Plus } from '@element-plus/icons-vue'

const materialList = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const searchCategoryId = ref('')
const searchStockStatus = ref('')
const categories = ref([])
const suppliers = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('添加物资')
const dialogLoading = ref(false)
const materialFormRef = ref(null)

// 图片预览
const previewVisible = ref(false)
const previewImage = ref('')

// 批量导入
const showImportDialog = ref(false)
const importLoading = ref(false)
const importFileList = ref([])
const uploadRef = ref(null)

// 物资表单数据
const materialForm = reactive({
  id: '',
  name: '',
  specification: '',
  brand: '',
  unit: '',
  categoryId: '',
  supplierId: '',
  stockQuantity: 0,
  stockWarningValue: 0,
  price: 0,
  description: '',
  status: '1',
  imageList: []
})

// 物资表单验证规则
const materialRules = {
  name: [
    { required: true, message: '请输入物资名称', trigger: 'blur' },
    { min: 2, max: 100, message: '物资名称长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  specification: [
    { required: true, message: '请输入型号规格', trigger: 'blur' },
    { max: 100, message: '型号规格不超过 100 个字符', trigger: 'blur' }
  ],
  brand: [
    { required: true, message: '请输入品牌', trigger: 'blur' },
    { max: 50, message: '品牌不超过 50 个字符', trigger: 'blur' }
  ],
  unit: [
    { required: true, message: '请输入单位', trigger: 'blur' },
    { max: 20, message: '单位不超过 20 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择所属分类', trigger: 'change' }
  ],
  supplierId: [
    { required: true, message: '请选择供应商', trigger: 'change' }
  ],
  stockQuantity: [
    { required: true, message: '请输入库存数量', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存数量必须大于等于 0', trigger: 'blur' }
  ],
  stockWarningValue: [
    { required: true, message: '请输入库存预警值', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存预警值必须大于等于 0', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入单价', trigger: 'blur' },
    { type: 'number', min: 0, message: '单价必须大于等于 0', trigger: 'blur' }
  ]
}

// 获取物资列表
const fetchMaterials = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟物资列表数据
    const mockMaterials = [
      {
        id: 1,
        name: 'A4打印纸',
        specification: '80g 500张/包',
        brand: '得力',
        unit: '包',
        categoryId: 2,
        categoryName: '文具',
        supplierId: 1,
        supplierName: '得力办公',
        stockQuantity: 150,
        stockWarningValue: 50,
        price: 25.5,
        description: '高品质A4打印纸，适合日常办公使用',
        status: '1',
        createTime: '2024-01-01 10:00:00',
        imageList: []
      },
      {
        id: 2,
        name: '黑色中性笔',
        specification: '0.5mm',
        brand: '晨光',
        unit: '支',
        categoryId: 2,
        categoryName: '文具',
        supplierId: 2,
        supplierName: '晨光文具',
        stockQuantity: 300,
        stockWarningValue: 100,
        price: 2.5,
        description: '经典黑色中性笔，书写流畅',
        status: '1',
        createTime: '2024-01-02 14:30:00',
        imageList: []
      },
      {
        id: 3,
        name: '鼠标',
        specification: '有线 USB',
        brand: '罗技',
        unit: '个',
        categoryId: 5,
        categoryName: '电子设备',
        supplierId: 3,
        supplierName: '科技数码',
        stockQuantity: 20,
        stockWarningValue: 10,
        price: 89.0,
        description: '人体工学设计，舒适耐用',
        status: '1',
        createTime: '2024-01-03 09:15:00',
        imageList: []
      },
      {
        id: 4,
        name: '键盘',
        specification: '机械键盘 青轴',
        brand: '雷蛇',
        unit: '个',
        categoryId: 5,
        categoryName: '电子设备',
        supplierId: 3,
        supplierName: '科技数码',
        stockQuantity: 8,
        stockWarningValue: 5,
        price: 299.0,
        description: '机械键盘，打字手感好',
        status: '1',
        createTime: '2024-01-04 16:45:00',
        imageList: []
      },
      {
        id: 5,
        name: '文件夹',
        specification: 'A4 塑料材质',
        brand: '齐心',
        unit: '个',
        categoryId: 2,
        categoryName: '文具',
        supplierId: 4,
        supplierName: '齐心办公',
        stockQuantity: 450,
        stockWarningValue: 200,
        price: 5.0,
        description: '耐用塑料文件夹，方便整理文档',
        status: '1',
        createTime: '2024-01-05 11:30:00',
        imageList: []
      },
      {
        id: 6,
        name: '订书机',
        specification: '标准型',
        brand: '得力',
        unit: '个',
        categoryId: 2,
        categoryName: '文具',
        supplierId: 1,
        supplierName: '得力办公',
        stockQuantity: 35,
        stockWarningValue: 20,
        price: 18.5,
        description: '得力经典订书机，适合办公使用',
        status: '1',
        createTime: '2024-01-06 15:20:00',
        imageList: []
      },
      {
        id: 7,
        name: '硒鼓',
        specification: 'HP LaserJet Pro M404n',
        brand: '惠普',
        unit: '个',
        categoryId: 3,
        categoryName: '耗材',
        supplierId: 5,
        supplierName: '惠普耗材',
        stockQuantity: 5,
        stockWarningValue: 3,
        price: 499.0,
        description: '原装硒鼓，打印质量稳定',
        status: '1',
        createTime: '2024-01-07 10:45:00',
        imageList: []
      }
    ]
    
    // 模拟分类数据
    const mockCategories = [
      { id: 2, name: '文具' },
      { id: 3, name: '耗材' },
      { id: 5, name: '电子设备' }
    ]
    
    // 模拟供应商数据
    const mockSuppliers = [
      { id: 1, name: '得力办公' },
      { id: 2, name: '晨光文具' },
      { id: 3, name: '科技数码' },
      { id: 4, name: '齐心办公' },
      { id: 5, name: '惠普耗材' }
    ]
    
    // 模拟搜索功能
    let filteredMaterials = mockMaterials
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filteredMaterials = mockMaterials.filter(material => 
        material.name.toLowerCase().includes(keyword) ||
        material.specification.toLowerCase().includes(keyword) ||
        material.brand.toLowerCase().includes(keyword)
      )
    }
    
    if (searchCategoryId.value) {
      filteredMaterials = filteredMaterials.filter(material => 
        material.categoryId === parseInt(searchCategoryId.value)
      )
    }
    
    if (searchStockStatus.value) {
      filteredMaterials = filteredMaterials.filter(material => {
        if (searchStockStatus.value === 'normal') {
          return material.stockQuantity > material.stockWarningValue
        } else if (searchStockStatus.value === 'low') {
          return material.stockQuantity > 0 && material.stockQuantity <= material.stockWarningValue
        } else if (searchStockStatus.value === 'out') {
          return material.stockQuantity <= 0
        }
        return true
      })
    }
    
    materialList.value = filteredMaterials
    total.value = filteredMaterials.length
    categories.value = mockCategories
    suppliers.value = mockSuppliers
  } catch (error) {
    console.error('获取物资列表失败:', error)
    ElMessage.error('获取物资列表失败')
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

// 格式化价格
const formatPrice = (row, column, cellValue) => {
  return `¥${cellValue.toFixed(2)}`
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedMaterials.value = selection
}

// 处理添加物资
const handleAddMaterial = () => {
  dialogTitle.value = '添加物资'
  // 重置表单
  if (materialFormRef.value) {
    materialFormRef.value.resetFields()
  }
  // 重置表单数据
  Object.assign(materialForm, {
    id: '',
    name: '',
    specification: '',
    brand: '',
    unit: '',
    categoryId: '',
    supplierId: '',
    stockQuantity: 0,
    stockWarningValue: 0,
    price: 0,
    description: '',
    status: '1',
    imageList: []
  })
  dialogVisible.value = true
}

// 处理查看物资
const handleViewMaterial = (row) => {
  console.log('查看物资:', row)
  ElMessage.info('查看物资详情功能开发中...')
}

// 处理编辑物资
const handleEditMaterial = (row) => {
  dialogTitle.value = '编辑物资'
  // 填充表单数据
  Object.assign(materialForm, row)
  dialogVisible.value = true
}

// 处理保存物资
const handleSaveMaterial = async () => {
  if (!materialFormRef.value) return
  
  try {
    const valid = await materialFormRef.value.validate()
    if (valid) {
      dialogLoading.value = true
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      if (materialForm.id) {
        // 编辑物资
        console.log('编辑物资:', materialForm)
        ElMessage.success('物资编辑成功')
      } else {
        // 添加物资
        console.log('添加物资:', materialForm)
        ElMessage.success('物资添加成功')
      }
      
      // 关闭对话框
      dialogVisible.value = false
      
      // 刷新物资列表
      fetchMaterials()
    }
  } catch (error) {
    console.error('保存物资失败:', error)
    ElMessage.error('保存物资失败')
  } finally {
    dialogLoading.value = false
  }
}

// 处理对话框关闭
const handleDialogClose = () => {
  // 重置表单
  if (materialFormRef.value) {
    materialFormRef.value.resetFields()
  }
}

// 处理删除物资
const handleDeleteMaterial = (row) => {
  ElMessageBox.confirm(
    `确定要删除物资 "${row.name}" 吗？删除后无法恢复。`,
    '删除物资',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
  .then(() => {
    // 调用删除物资API
    // 模拟API调用
    return new Promise(resolve => setTimeout(resolve, 800))
  })
  .then(() => {
    console.log('删除物资:', row)
    ElMessage.success('物资删除成功')
    // 刷新物资列表
    fetchMaterials()
  })
  .catch(() => {
    // 取消删除
  })
}

// 图片预览
const handlePictureCardPreview = (file) => {
  previewImage.value = file.url
  previewVisible.value = true
}

// 图片删除
const handleRemove = (file, fileList) => {
  materialForm.imageList = fileList
}

// 分页相关
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  fetchMaterials()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  fetchMaterials()
}

// 下载模板
const handleDownloadTemplate = () => {
  ElMessage.info('下载模板功能开发中...')
}

// 批量导入相关
const selectedMaterials = ref([])

// 处理文件变更
const handleFileChange = (file, fileList) => {
  importFileList.value = fileList
}

// 处理开始导入
const handleImport = () => {
  if (importFileList.value.length === 0) {
    ElMessage.warning('请选择要导入的文件')
    return
  }
  
  importLoading.value = true
  
  // 模拟导入过程
  setTimeout(() => {
    importLoading.value = false
    ElMessage.success(`成功导入 ${importFileList.value.length} 个文件`)
    showImportDialog.value = false
    fetchMaterials()
  }, 2000)
}

// 处理导入对话框关闭
const handleImportDialogClose = () => {
  importFileList.value = []
}

// 页面挂载时获取物资列表
onMounted(() => {
  fetchMaterials()
})
</script>

<style scoped>
.material-list-container {
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

.material-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.material-search {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.material-table-container {
  margin-bottom: 20px;
  overflow-x: auto;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .material-list-container {
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
  
  .material-search {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .material-search .el-input,
  .material-search .el-select {
    width: 100% !important;
    margin-right: 0 !important;
  }
  
  .pagination {
    justify-content: center;
  }
  
  /* 表格响应式处理 */
  .material-table-container {
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
}

@media screen and (max-width: 480px) {
  .header-actions {
    flex-direction: column;
  }
  
  .header-actions .el-button {
    width: 100%;
    margin-right: 0;
  }
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

/* 图片上传样式 */
:deep(.el-upload-list__item) {
  margin-right: 10px;
  margin-bottom: 10px;
}
</style>