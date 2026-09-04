<template>
  <div>
    <el-card shadow="never" class="table-card">
      <div class="toolbar">
        <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>新增类型</el-button>
        <span class="tip">已被报修单引用的类型不允许删除。</span>
      </div>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="类型名称" min-width="180" />
        <el-table-column prop="sort" label="排序" width="140" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑类型' : '新增类型'" width="420px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="类型名称" prop="name">
          <el-input v-model="form.name" placeholder="如：灯管、水龙头、空调" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="1" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getRepairTypes, createRepairType, updateRepairType, deleteRepairType } from '@/api/daily'

const loading = ref(false)
const list = ref([])
const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref(null)
const editing = ref(null)
const emptyForm = { name: '', sort: 99 }
const form = reactive({ ...emptyForm })
const rules = { name: [{ required: true, message: '请输入类型名称', trigger: 'blur' }] }

async function load() {
  loading.value = true
  try {
    list.value = await getRepairTypes()
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  editing.value = row ? row.id : null
  form.name = row ? row.name : ''
  form.sort = row ? row.sort : 99
  dialogVisible.value = true
}

async function onSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (editing.value) {
      await updateRepairType(editing.value, { ...form })
    } else {
      await createRepairType({ ...form })
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

function onDelete(row) {
  ElMessageBox.confirm(`确定删除报修类型「${row.name}」吗？`, '删除确认', { type: 'warning' })
    .then(async () => {
      await deleteRepairType(row.id)
      ElMessage.success('删除成功')
      load()
    })
    .catch(() => {})
}

onMounted(load)
</script>

<style scoped>
.table-card { border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.toolbar { display: flex; align-items: center; gap: 12px; margin-bottom: 14px; }
.tip { color: #8a94a6; font-size: 12px; }
</style>