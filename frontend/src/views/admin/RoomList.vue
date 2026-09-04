<template>
  <div>
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="楼栋">
          <el-select v-model="query.buildingId" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="b in buildingOptions" :key="b.id" :label="b.buildingName" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="query.roomNo" placeholder="如：101" clearable style="width: 120px" />
        </el-form-item>
        <el-form-item label="房型">
          <el-select v-model="query.roomType" placeholder="全部" clearable style="width: 120px">
            <el-option v-for="t in roomTypes" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 130px">
            <el-option v-for="s in statuses" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch"><el-icon><Search /></el-icon>查询</el-button>
          <el-button @click="onReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <div class="toolbar">
        <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon>新增房间</el-button>
      </div>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="buildingName" label="楼栋" width="110" />
        <el-table-column prop="roomNo" label="房间号" width="100" />
        <el-table-column prop="roomType" label="房型" width="100" />
        <el-table-column prop="capacity" label="容纳人数" width="100" />
        <el-table-column prop="occupiedCount" label="已住" width="80" />
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="170" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link @click="showBeds(row)">床位</el-button>
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

    <!-- 新增 / 编辑房间 -->
    <el-dialog v-model="dialogVisible" :title="editing ? '编辑房间' : '新增房间'" width="480" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="所属楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择楼栋" style="width: 100%">
            <el-option v-for="b in buildingOptions" :key="b.id" :label="b.buildingName" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" prop="roomNo">
          <el-input v-model="form.roomNo" placeholder="如：101" />
        </el-form-item>
        <el-form-item label="容纳人数" prop="capacity">
          <el-select v-model="form.capacity" placeholder="请选择" style="width: 100%">
            <el-option :value="4" label="4人间（4 人）" />
            <el-option :value="6" label="6人间（6 人）" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 床位分布 -->
    <el-dialog v-model="bedDialogVisible" :title="`床位分布 · ${currentRoom?.buildingName} ${currentRoom?.roomNo}室`" width="520" destroy-on-close>
      <div v-if="bedsLoading" v-loading="true" class="beds-loading" />
      <div v-else class="bed-grid">
        <div
          v-for="b in beds"
          :key="b.bedId"
          class="bed"
          :class="b.status === '占用' ? 'occupied' : 'free'"
        >
          <div class="bed-no">{{ b.bedNo }} 号床</div>
          <div class="bed-status">{{ b.status }}</div>
          <div v-if="b.studentName" class="bed-stu">{{ b.studentName }}（{{ b.studentId }}）</div>
        </div>
      </div>
      <div class="beds-empty" v-if="!bedsLoading && !beds.length">暂无床位信息</div>
      <template #footer>
        <el-button @click="bedDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { getRooms, addRoom, updateRoom, deleteRoom, getRoomOptions, getRoomBeds } from '@/api/room'

const statuses = ['空闲', '部分入住', '已满', '维修']
const roomTypes = ref([])
const buildingOptions = ref([])

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ page: 1, pageSize: 10, buildingId: '', roomNo: '', roomType: '', status: '' })

const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref(null)
const editing = ref(null)
const emptyForm = { buildingId: '', roomNo: '', capacity: 4 }
const form = reactive({ ...emptyForm })

const rules = {
  buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  roomNo: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  capacity: [{ required: true, message: '请选择容纳人数', trigger: 'change' }]
}

// 床位弹窗
const bedDialogVisible = ref(false)
const bedsLoading = ref(false)
const beds = ref([])
const currentRoom = ref(null)

async function loadOptions() {
  const res = await getRoomOptions()
  buildingOptions.value = res.buildings
  roomTypes.value = res.types
}

async function load() {
  loading.value = true
  try {
    const res = await getRooms({ ...query })
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function onSearch() { query.page = 1; load() }
function onReset() { Object.assign(query, { buildingId: '', roomNo: '', roomType: '', status: '' }); query.page = 1; load() }
function onPage(p) { query.page = p; load() }

function openDialog(row) {
  Object.assign(form, emptyForm)
  editing.value = null
  if (row) {
    editing.value = row.id
    Object.assign(form, { buildingId: row.buildingId, roomNo: row.roomNo, capacity: row.capacity })
  }
  dialogVisible.value = true
}

async function onSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (editing.value) {
      await updateRoom(editing.value, { ...form })
    } else {
      await addRoom({ ...form })
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

function onDelete(row) {
  ElMessageBox.confirm(`确定删除「${row.buildingName} ${row.roomNo}室」吗？`, '删除确认', { type: 'warning' }).then(async () => {
    await deleteRoom(row.id)
    ElMessage.success('删除成功')
    load()
  }).catch(() => {})
}

async function showBeds(row) {
  currentRoom.value = row
  bedDialogVisible.value = true
  bedsLoading.value = true
  try {
    beds.value = await getRoomBeds(row.id)
  } finally {
    bedsLoading.value = false
  }
}

function statusTag(s) {
  return s === '空闲' ? 'info' : s === '部分入住' ? 'warning' : s === '已满' ? 'success' : 'danger'
}

onMounted(() => { load(); loadOptions() })
</script>

<style scoped>
.search-card { margin-bottom: 14px; border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.search-card :deep(.el-card__body) { padding: 14px 18px 2px; }
.table-card { border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.toolbar { display: flex; gap: 10px; margin-bottom: 14px; }
.pager { margin-top: 14px; justify-content: flex-end; }
.beds-loading { height: 80px; }
.bed-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.bed { border: 1px solid var(--d-border); border-radius: 10px; padding: 12px 14px; background: var(--d-surface); }
.bed.free { border-left: 3px solid #67c23a; }
.bed.occupied { border-left: 3px solid var(--d-primary); }
.bed-no { font-size: 14px; font-weight: 700; color: var(--d-ink); }
.bed-status { font-size: 12px; color: var(--d-muted); margin-top: 2px; }
.bed-stu { font-size: 13px; color: var(--d-ink); margin-top: 4px; }
.beds-empty { color: var(--d-muted); text-align: center; padding: 30px 0; }
</style>