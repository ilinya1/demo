<template>
  <el-card shadow="never">
    <div class="toolbar">
      <div class="filters">
        <el-select v-model="query.buildingId" placeholder="全部楼栋" clearable style="width: 150px" @change="load">
          <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id" />
        </el-select>
        <el-date-picker v-model="query.checkDate" type="date" value-format="YYYY-MM-DD" placeholder="检查日期" style="width: 160px" @change="load" />
        <el-select v-model="query.result" placeholder="全部结果" clearable style="width: 130px" @change="load">
          <el-option label="优秀" value="优秀" /><el-option label="合格" value="合格" /><el-option label="不合格" value="不合格" />
        </el-select>
        <el-button @click="reset">重置</el-button>
      </div>
      <el-button type="primary" @click="goAdd">＋ 新增检查</el-button>
    </div>

    <el-table :data="rows" v-loading="loading" border>
      <el-table-column prop="checkDate" label="检查日期" width="120" />
      <el-table-column prop="buildingName" label="楼栋" width="90" />
      <el-table-column prop="roomNo" label="房间号" width="90" />
      <el-table-column prop="score" label="评分" width="90" />
      <el-table-column label="结果" width="90">
        <template #default="{ row }">
          <el-tag :type="row.result === '优秀' ? 'success' : row.result === '合格' ? 'warning' : 'danger'">
            {{ row.result }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="照片" width="90">
        <template #default="{ row }">
          <el-image v-if="row.photos && row.photos.length" :src="row.photos[0]" :preview-src-list="row.photos"
                    preview-teleported fit="cover" style="width: 54px; height: 36px; border-radius: 4px" />
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column prop="checker" label="检查人" width="100" />
      <el-table-column prop="comment" label="评语" min-width="160" show-overflow-tooltip />
      <el-table-column label="操作" width="90">
        <template #default="{ row }">
          <el-button type="primary" link @click="view(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pager" background layout="total, prev, pager, next, sizes"
                   :total="total" :page-size="query.pageSize" :current-page="query.page"
                   @current-change="(p) => { query.page = p; load() }" @size-change="(s) => { query.pageSize = s; query.page = 1; load() }" />
  </el-card>

  <el-dialog v-model="detailDialog" title="卫生检查详情" width="520px">
    <el-descriptions :column="2" border v-if="current">
      <el-descriptions-item label="检查日期">{{ current.checkDate }}</el-descriptions-item>
      <el-descriptions-item label="检查人">{{ current.checker }}</el-descriptions-item>
      <el-descriptions-item label="楼栋 / 房间">{{ current.buildingName }} {{ current.roomNo }} 室</el-descriptions-item>
      <el-descriptions-item label="评分">{{ current.score }} 分</el-descriptions-item>
      <el-descriptions-item label="结果">
        <el-tag :type="current.result === '优秀' ? 'success' : current.result === '合格' ? 'warning' : 'danger'">{{ current.result }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="评语">{{ current.comment || '—' }}</el-descriptions-item>
      <el-descriptions-item label="扣分项" :span="2">
        {{ (current.deductItems && current.deductItems.length) ? current.deductItems.join('、') : '无' }}
      </el-descriptions-item>
      <el-descriptions-item label="现场照片" :span="2">
        <div class="photos" v-if="current.photos && current.photos.length">
          <el-image v-for="(p, i) in current.photos" :key="i" :src="p" :preview-src-list="current.photos"
                    preview-teleported fit="cover" style="width: 100px; height: 72px; border-radius: 6px" />
        </div>
        <span v-else>—</span>
      </el-descriptions-item>
    </el-descriptions>
  </el-dialog>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getHygieneList } from '@/api/daily'
import { getBuildings } from '@/api/building'

const router = useRouter()
const query = reactive({ checkDate: '', buildingId: '', result: '', page: 1, pageSize: 10 })
const rows = ref([])
const total = ref(0)
const loading = ref(false)
const buildings = ref([])
const detailDialog = ref(false)
const current = ref(null)

async function load() {
  loading.value = true
  const res = await getHygieneList({ ...query })
  rows.value = res.list
  total.value = res.total
  loading.value = false
}
function reset() {
  query.checkDate = ''; query.buildingId = ''; query.result = ''; query.page = 1; load()
}
function goAdd() { router.push('/admin/hygiene-add') }
function view(row) { current.value = row; detailDialog.value = true }

onMounted(async () => {
  buildings.value = (await getBuildings()).list
  load()
})
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; gap: 16px; margin-bottom: 16px; flex-wrap: wrap; }
.filters { display: flex; gap: 12px; flex-wrap: wrap; }
.pager { margin-top: 16px; justify-content: flex-end; }
.photos { display: flex; gap: 8px; flex-wrap: wrap; }
</style>