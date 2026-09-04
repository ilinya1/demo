<template>
  <div class="settings-page">
    <el-card shadow="never" class="card">
      <el-tabs v-model="active">
        <!-- 系统参数 -->
        <el-tab-pane label="系统参数" name="params">
          <el-form label-width="120px" class="params-form" style="max-width: 640px">
            <el-form-item v-for="p in params" :key="p.key" :label="p.name">
              <el-input v-model="p.value" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="savingParams" @click="saveParams">保存参数</el-button>
              <el-button :loading="savingParams" @click="resetParams">恢复默认</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 退宿原因字典 -->
        <el-tab-pane label="退宿原因字典" name="reasons">
          <div class="toolbar">
            <el-button type="primary" @click="openReason()"><el-icon><Plus /></el-icon>新增原因</el-button>
          </div>
          <el-table :data="reasons" v-loading="loadingReasons" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="原因名称" min-width="180" />
            <el-table-column prop="sort" label="排序" width="120" />
            <el-table-column label="操作" width="140" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="openReason(row)">编辑</el-button>
                <el-button link type="danger" @click="delReason(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 退宿原因编辑弹窗 -->
    <el-dialog v-model="reasonDialog" :title="editingReason ? '编辑退宿原因' : '新增退宿原因'" width="420px" destroy-on-close>
      <el-form ref="reasonFormRef" :model="reasonForm" :rules="reasonRules" label-width="90px">
        <el-form-item label="原因名称" prop="name">
          <el-input v-model="reasonForm.name" placeholder="请输入退宿原因，如：毕业离校" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="reasonForm.sort" :min="1" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reasonDialog = false">取消</el-button>
        <el-button type="primary" :loading="savingReason" @click="saveReason">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getSystemParams, updateSystemParams, resetSystemParams,
  getCheckoutReasons, createCheckoutReason, updateCheckoutReason, deleteCheckoutReason
} from '@/api/settings'

const active = ref('params')

// ---- 系统参数 ----
const params = ref([])
const savingParams = ref(false)
async function loadParams() {
  params.value = await getSystemParams()
}
async function saveParams() {
  savingParams.value = true
  try {
    await updateSystemParams(params.value)
    ElMessage.success('系统参数已保存')
  } finally {
    savingParams.value = false
  }
}
async function resetParams() {
  await ElMessageBox.confirm('确定将系统参数恢复为默认值吗？', '恢复默认', { type: 'warning' })
  savingParams.value = true
  try {
    await resetSystemParams()
    ElMessage.success('已恢复为默认值')
    loadParams()
  } finally {
    savingParams.value = false
  }
}

// ---- 退宿原因字典 ----
const reasons = ref([])
const loadingReasons = ref(false)
async function loadReasons() {
  loadingReasons.value = true
  try {
    reasons.value = await getCheckoutReasons()
  } finally {
    loadingReasons.value = false
  }
}

const reasonDialog = ref(false)
const savingReason = ref(false)
const reasonFormRef = ref()
const editingReason = ref(null)
const reasonForm = reactive({ name: '', sort: 99 })
const reasonRules = {
  name: [{ required: true, message: '请输入原因名称', trigger: 'blur' }]
}

function openReason(row) {
  editingReason.value = row ? row.id : null
  reasonForm.name = row ? row.name : ''
  reasonForm.sort = row ? row.sort : 99
  reasonDialog.value = true
}
async function saveReason() {
  await reasonFormRef.value.validate()
  savingReason.value = true
  try {
    if (editingReason.value) {
      await updateCheckoutReason(editingReason.value, { ...reasonForm })
    } else {
      await createCheckoutReason({ ...reasonForm })
    }
    ElMessage.success('保存成功')
    reasonDialog.value = false
    loadReasons()
  } finally {
    savingReason.value = false
  }
}
function delReason(row) {
  ElMessageBox.confirm(`确定删除退宿原因「${row.name}」吗？`, '删除确认', { type: 'warning' })
    .then(async () => {
      await deleteCheckoutReason(row.id)
      ElMessage.success('删除成功')
      loadReasons()
    })
    .catch(() => {})
}

onMounted(async () => {
  await Promise.all([loadParams(), loadReasons()])
})
</script>

<style scoped>
.card { border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.params-form { margin-top: 8px; }
.toolbar { margin-bottom: 14px; }
</style>