<template>
  <div class="material-add-container">
    <h2 class="page-title">新增物资</h2>
    
    <el-card shadow="hover" class="material-card">
      <template #header>
        <div class="card-header">
          <span>物资基本信息</span>
        </div>
      </template>
      
      <el-form
        ref="materialFormRef"
        :model="materialForm"
        :rules="materialRules"
        label-position="right"
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
          </el-col>
          
          <el-col :span="12">
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
            
            <el-form-item label="库存预警值" prop="stockWarningValue">
              <el-input-number
                v-model="materialForm.stockWarningValue"
                :min="0"
                placeholder="请输入库存预警值"
              />
            </el-form-item>
            
            <el-form-item label="单价(元)" prop="price">
              <el-input-number
                v-model="materialForm.price"
                :min="0"
                :precision="2"
                placeholder="请输入单价"
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
          </el-col>
        </el-row>
        
        <el-divider />
        
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
      </el-form>
      
      <!-- 图片预览对话框 -->
      <el-dialog v-model="previewVisible" title="图片预览" width="500px">
        <img w-full :src="previewImage" alt="Preview Image" />
      </el-dialog>
      
      <div class="form-actions">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveMaterial">
          保存
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../services/api'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const router = useRouter()
const materialFormRef = ref(null)
const saving = ref(false)

// 图片预览
const previewVisible = ref(false)
const previewImage = ref('')

// 物资表单数据
const materialForm = reactive({
  name: '',
  specification: '',
  brand: '',
  unit: '',
  categoryId: '',
  supplierId: '',
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
  stockWarningValue: [
    { required: true, message: '请输入库存预警值', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存预警值必须大于等于 0', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入单价', trigger: 'blur' },
    { type: 'number', min: 0, message: '单价必须大于等于 0', trigger: 'blur' }
  ]
}

// 分类数据
const categories = ref([
  { id: 1, name: '办公用品' },
  { id: 2, name: '文具' },
  { id: 3, name: '耗材' },
  { id: 4, name: '设备' },
  { id: 5, name: '电子设备' },
  { id: 6, name: '家具' },
  { id: 7, name: '生产物资' }
])

// 供应商数据
const suppliers = ref([
  { id: 1, name: '得力办公' },
  { id: 2, name: '晨光文具' },
  { id: 3, name: '科技数码' },
  { id: 4, name: '齐心办公' },
  { id: 5, name: '惠普耗材' }
])

// 图片预览
const handlePictureCardPreview = (file) => {
  previewImage.value = file.url
  previewVisible.value = true
}

// 图片删除
const handleRemove = (file, fileList) => {
  materialForm.imageList = fileList
}

// 保存物资
const handleSaveMaterial = async () => {
  if (!materialFormRef.value) return
  
  try {
    const valid = await materialFormRef.value.validate()
    if (valid) {
      saving.value = true
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      console.log('保存物资:', materialForm)
      ElMessage.success('物资添加成功')
      
      // 返回物资列表页
      router.push('/material/list')
    }
  } catch (error) {
    console.error('保存物资失败:', error)
    ElMessage.error('保存物资失败')
  } finally {
    saving.value = false
  }
}

// 取消操作
const handleCancel = () => {
  router.push('/material/list')
}

// 页面挂载时获取数据
onMounted(() => {
  // 模拟获取分类和供应商数据
  console.log('获取分类和供应商数据')
})
</script>

<style scoped>
.material-add-container {
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

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>