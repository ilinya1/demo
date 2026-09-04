<template>
  <div>
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="楼栋名称">
          <el-input v-model="query.buildingName" placeholder="请输入楼栋名称" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="管理员">
          <el-input v-model="query.manager" placeholder="请输入管理员" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch"><el-icon><Search /></el-icon>查询</el-button>
          <el-button @click="onReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <div class="toolbar">
        <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>新增楼栋</el-button>
      </div>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="buildingName" label="楼栋名称" width="150" />
        <el-table-column prop="floorCount" label="楼层数" width="100" />
        <el-table-column prop="roomCount" label="房间数" width="100" />
        <el-table-column prop="manager" label="管理员" width="140" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        class="pager"
        layout="total, prev, pager, next"
        :total="total"
        :page-size="query.pageSize"
        :current-page="query.page"
        background
        @current-change="onPage"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑楼栋' : '新增楼栋'" width="480" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="楼栋名称" prop="buildingName">
          <el-input v-model="form.buildingName" placeholder="如：1号楼" />
        </el-form-item>
        <el-form-item label="楼层数" prop="floorCount">
          <el-input-number v-model="form.floorCount" :min="1" :max="30" />
        </el-form-item>
        <el-form-item label="房间数" prop="roomCount">
          <el-input-number v-model="form.roomCount" :min="1" :max="1000" />
        </el-form-item>
        <el-form-item label="管理员" prop="manager">
          <el-input v-model="form.manager" placeholder="请输入管理员姓名" />
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
import { Search, Plus } from '@element-plus/icons-vue'
import { getBuildings, addBuilding, updateBuilding, deleteBuilding } from '@/api/building'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ page: 1, pageSize: 10, buildingName: '', manager: '' })

const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref(null)
const editing = ref(null)
const emptyForm = { buildingName: '', floorCount: 6, roomCount: 120, manager: '' }
const form = reactive({ ...emptyForm })

const rules = {
  buildingName: [{ required: true, message: '请输入楼栋名称', trigger: 'blur' }],
  manager: [{ required: true, message: '请输入管理员', trigger: 'blur' }]
}

async function load() {
  loading.value = true
  try {
    const res = await getBuildings({ ...query })
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function onSearch() { query.page = 1; load() }
function onReset() { Object.assign(query, { buildingName: '', manager: '' }); query.page = 1; load() }
function onPage(p) { query.page = p; load() }

function openDialog(row) {
  Object.assign(form, emptyForm)
  editing.value = null
  if (row) {
    editing.value = row.id
    Object.assign(form, row)
  }
  dialogVisible.value = true
}

async function onSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (editing.value) {
      await updateBuilding(editing.value, { ...form })
    } else {
      await addBuilding({ ...form })
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

function onDelete(row) {
  ElMessageBox.confirm(`确定删除楼栋「${row.buildingName}」吗？`, '删除确认', { type: 'warning' }).then(async () => {
    await deleteBuilding(row.id)
    ElMessage.success('删除成功')
    load()
  }).catch(() => {})
}

onMounted(load)
</script>

<style scoped>
.search-card { margin-bottom: 14px; border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.search-card :deep(.el-card__body) { padding: 14px 18px 2px; }
.table-card { border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.toolbar { display: flex; gap: 10px; margin-bottom: 14px; }
.pager { margin-top: 14px; justify-content: flex-end; }
</style>