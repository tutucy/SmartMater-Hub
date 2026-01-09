<template>
  <div class="config-container">
    <h2 class="page-title">系统配置</h2>
    
    <!-- 配置类型标签页 -->
    <el-tabs v-model="activeTab" class="config-tabs">
      <!-- 数据备份与恢复 -->
      <el-tab-pane label="数据备份与恢复" name="backup_restore">
        <el-card shadow="hover" class="config-card">
          <template #header>
            <div class="card-header">
              <span>数据备份与恢复</span>
            </div>
          </template>
          
          <div class="backup-restore-content">
            <!-- 备份配置 -->
            <div class="backup-config">
              <h3 class="section-title">备份配置</h3>
              <el-form :model="backupConfig" label-position="right" label-width="120px">
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="自动备份">
                      <el-switch v-model="backupConfig.autoBackup" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="备份周期" :disabled="!backupConfig.autoBackup">
                      <el-select v-model="backupConfig.backupCycle" :disabled="!backupConfig.autoBackup">
                        <el-option label="每天" value="daily" />
                        <el-option label="每周" value="weekly" />
                        <el-option label="每月" value="monthly" />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="备份时间" :disabled="!backupConfig.autoBackup">
                      <el-time-picker
                        v-model="backupConfig.backupTime"
                        format="HH:mm"
                        value-format="HH:mm"
                        :disabled="!backupConfig.autoBackup"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="备份保留数量" :disabled="!backupConfig.autoBackup">
                      <el-input-number
                        v-model="backupConfig.backupRetainCount"
                        :min="1"
                        :max="30"
                        :disabled="!backupConfig.autoBackup"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="24">
                    <el-form-item label="备份路径">
                      <el-input v-model="backupConfig.backupPath" disabled />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-form-item>
                  <el-button type="primary" @click="handleSaveBackupConfig">保存配置</el-button>
                </el-form-item>
              </el-form>
            </div>
            
            <!-- 手动备份 -->
            <div class="manual-backup">
              <h3 class="section-title">手动备份</h3>
              <div class="backup-actions">
                <el-button type="success" @click="handleManualBackup" icon="Upload">
                  立即备份
                </el-button>
                <el-button @click="handleBackupHistory" icon="History">
                  备份历史
                </el-button>
              </div>
            </div>
            
            <!-- 数据恢复 -->
            <div class="data-restore">
              <h3 class="section-title">数据恢复</h3>
              <el-upload
                class="restore-upload"
                action="#"
                :auto-upload="false"
                :show-file-list="false"
                :on-change="handleRestoreFileChange"
              >
                <el-button type="warning" icon="Download">
                  选择备份文件
                </el-button>
              </el-upload>
              <div v-if="restoreFileName" class="restore-file-info">
                <span>已选择文件: {{ restoreFileName }}</span>
                <el-button type="danger" @click="handleDataRestore" icon="RefreshRight" :disabled="!restoreFileName">
                  开始恢复
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-tab-pane>
      
      <!-- 系统参数配置 -->
      <el-tab-pane label="系统参数配置" name="system_params">
        <el-card shadow="hover" class="config-card">
          <template #header>
            <div class="card-header">
              <span>系统参数配置</span>
            </div>
          </template>
          
          <div class="system-params-content">
            <el-form :model="systemParams" label-position="right" label-width="150px">
              <!-- 基础配置 -->
              <h3 class="section-title">基础配置</h3>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="系统名称">
                    <el-input v-model="systemParams.systemName" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="系统版本">
                    <el-input v-model="systemParams.systemVersion" disabled />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="版权信息">
                    <el-input v-model="systemParams.copyright" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="系统状态">
                    <el-switch v-model="systemParams.systemStatus" />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <!-- 库存预警配置 -->
              <h3 class="section-title">库存预警配置</h3>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="低库存预警比例">
                    <el-input-number
                      v-model="systemParams.lowStockWarningRatio"
                      :min="0"
                      :max="100"
                      :precision="1"
                      :step="0.5"
                    />
                    <span class="unit">%</span>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="库存上限预警比例">
                    <el-input-number
                      v-model="systemParams.maxStockWarningRatio"
                      :min="100"
                      :max="500"
                      :precision="1"
                      :step="5"
                    />
                    <span class="unit">%</span>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="预警通知方式">
                    <el-checkbox-group v-model="systemParams.warningNotificationMethods">
                      <el-checkbox label="邮件" />
                      <el-checkbox label="短信" />
                      <el-checkbox label="系统消息" />
                    </el-checkbox-group>
                  </el-form-item>
                </el-col>
              </el-row>
              
              <!-- 采购配置 -->
              <h3 class="section-title">采购配置</h3>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="采购审批流程">
                    <el-select v-model="systemParams.purchaseApprovalFlow">
                      <el-option label="单级审批" value="single" />
                      <el-option label="多级审批" value="multi" />
                      <el-option label="自动审批" value="auto" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="采购订单有效期">
                    <el-input-number
                      v-model="systemParams.purchaseOrderValidity"
                      :min="1"
                      :max="30"
                    />
                    <span class="unit">天</span>
                  </el-form-item>
                </el-col>
              </el-row>
              
              <!-- 保存按钮 -->
              <el-form-item>
                <el-button type="primary" @click="handleSaveSystemParams">保存配置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('backup_restore')

// 备份配置
const backupConfig = reactive({
  autoBackup: true,
  backupCycle: 'daily',
  backupTime: '02:00',
  backupRetainCount: 7,
  backupPath: '/backup/smart-mater-hub/'
})

// 恢复文件名
const restoreFileName = ref('')

// 系统参数
const systemParams = reactive({
  systemName: '智能物资管理系统',
  systemVersion: '1.0.0',
  copyright: '© 2024 Smart Mater Hub',
  systemStatus: true,
  lowStockWarningRatio: 20,
  maxStockWarningRatio: 200,
  warningNotificationMethods: ['邮件', '系统消息'],
  purchaseApprovalFlow: 'single',
  purchaseOrderValidity: 15
})

// 保存备份配置
const handleSaveBackupConfig = () => {
  ElMessageBox.confirm('确定要保存备份配置吗？', '保存配置', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  .then(() => {
    // 模拟API调用
    return new Promise(resolve => setTimeout(resolve, 1000))
  })
  .then(() => {
    ElMessage.success('备份配置保存成功')
  })
  .catch(() => {
    // 取消操作
  })
}

// 手动备份
const handleManualBackup = () => {
  ElMessageBox.confirm('确定要执行手动备份吗？此操作可能需要几分钟时间。', '手动备份', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  .then(() => {
    // 模拟API调用
    return new Promise(resolve => setTimeout(resolve, 2000))
  })
  .then(() => {
    ElMessage.success('手动备份完成')
  })
  .catch(() => {
    // 取消操作
  })
}

// 查看备份历史
const handleBackupHistory = () => {
  ElMessage.success('备份历史功能开发中')
}

// 处理恢复文件选择
const handleRestoreFileChange = (file) => {
  restoreFileName.value = file.name
}

// 数据恢复
const handleDataRestore = () => {
  ElMessageBox.confirm('数据恢复将覆盖当前系统所有数据，操作不可恢复！确定要继续吗？', '数据恢复', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  })
  .then(() => {
    // 模拟API调用
    return new Promise(resolve => setTimeout(resolve, 3000))
  })
  .then(() => {
    ElMessage.success('数据恢复完成，系统将自动重启')
    restoreFileName.value = ''
  })
  .catch(() => {
    // 取消操作
  })
}

// 保存系统参数
const handleSaveSystemParams = () => {
  ElMessageBox.confirm('确定要保存系统参数配置吗？部分配置可能需要重启系统才能生效。', '保存配置', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  .then(() => {
    // 模拟API调用
    return new Promise(resolve => setTimeout(resolve, 1000))
  })
  .then(() => {
    ElMessage.success('系统参数配置保存成功')
  })
  .catch(() => {
    // 取消操作
  })
}

// 页面挂载时加载配置
onMounted(() => {
  // 模拟加载配置数据
  console.log('加载系统配置')
})
</script>

<style scoped>
.config-container {
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

.config-tabs {
  margin-bottom: 20px;
}

.config-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.backup-restore-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
  padding-bottom: 5px;
  border-bottom: 1px solid #e4e7ed;
}

.backup-config {
  background-color: #fafafa;
  padding: 20px;
  border-radius: 4px;
}

.manual-backup {
  background-color: #fafafa;
  padding: 20px;
  border-radius: 4px;
}

.backup-actions {
  display: flex;
  gap: 10px;
}

.data-restore {
  background-color: #fafafa;
  padding: 20px;
  border-radius: 4px;
}

.restore-upload {
  margin-bottom: 15px;
}

.restore-file-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.system-params-content {
  padding: 20px 0;
}

.unit {
  margin-left: 5px;
  color: #909399;
}
</style>