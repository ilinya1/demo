<template>
  <div>
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="学号">
          <el-input v-model="query.studentId" placeholder="请输入学号" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="query.studentName" placeholder="请输入姓名" clearable style="width: 130px" />
        </el-form-item>
        <el-form-item label="楼栋">
          <el-select v-model="query.buildingName" placeholder="全部" clearable style="width: 130px">
            <el-option v-for="b in buildings" :key="b.buildingName" :label="b.buildingName" :value="b.buildingName" />
          </el-select>
        </el-form-item>
        <el-form-item label="住宿状态">
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
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="110" />
        <el-table-column prop="buildingName" label="楼栋" width="100" />
        <el-table-column prop="roomNo" label="房间" width="90" />
        <el-table-column label="床位" width="90">
          <template #default="{ row }">{{ row.bedNo }}号床</template>
        </el-table-column>
        <el-table-column prop="checkInTime" label="入住时间" width="120" />
        <el-table-column label="退宿时间" width="120">
          <template #default="{ row }">{{ row.checkOutTime || '—' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="住宿状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '在住' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="来源" width="100">
          <template #default="{ row }">{{ row.source === 'direct' ? '直接退宿' : row.source === 'manual' ? '管理员登记' : '学生申请' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="110" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === '在住'" link type="danger" @click="onCheckout(row)">退宿</el-button>
            <span v-else>—</span>
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
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getCheckinRecords, directCheckout, getBuildingOptions } from '@/api/checkin'

const statuses = ['在住', '已退宿']
const buildings = ref([])

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ page: 1, pageSize: 10, studentId: '', studentName: '', buildingName: '', status: '' })

async function load() {
  loading.value = true
  try {
    const res = await getCheckinRecords({ ...query })
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function onSearch() { query.page = 1; load() }
function onReset() { Object.assign(query, { studentId: '', studentName: '', buildingName: '', status: '' }); query.page = 1; load() }
function onPage(p) { query.page = p; load() }

function onCheckout(row) {
  ElMessageBox.confirm(
    `确定对 ${row.studentName}（${row.studentId}）办理【直接退宿】吗？将从 ${row.buildingName} ${row.roomNo}室 ${row.bedNo}号床 迁出。`,
    '退宿确认',
    { type: 'warning', confirmButtonText: '确认退宿' }
  ).then(async () => {
    await directCheckout({ studentId: row.studentId, checkoutDate: new Date().toISOString().slice(0, 10), reason: '管理员办理', remark: '入住记录页直接退宿' })
    ElMessage.success('已退宿，床铺已空出')
    load()
  }).catch(() => {})
}

onMounted(async () => {
  buildings.value = await getBuildingOptions()
  load()
})
</script>

<style scoped>
.search-card { margin-bottom: 14px; border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.search-card :deep(.el-card__body) { padding: 14px 18px 2px; }
.table-card { border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.pager { margin-top: 14px; justify-content: flex-end; }
</style>