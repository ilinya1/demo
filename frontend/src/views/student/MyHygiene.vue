<template>
  <el-card shadow="never">
    <template #header>
      <template v-if="dorm">本宿舍卫生检查记录（{{ dorm.buildingName }} {{ dorm.roomNo }} 室）</template>
      <template v-else>我的卫生检查</template>
    </template>

    <template v-if="dorm">
      <el-table :data="rows" v-loading="loading" border :empty-text="'暂无卫生检查记录'">
        <el-table-column prop="checkDate" label="检查日期" width="140" />
        <el-table-column prop="score" label="评分" width="90" />
        <el-table-column label="结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.result === '优秀' ? 'success' : row.result === '合格' ? 'warning' : 'danger'">{{ row.result }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checker" label="检查人" width="110" />
        <el-table-column label="扣分项" min-width="160">
          <template #default="{ row }">
            {{ (row.deductItems && row.deductItems.length) ? row.deductItems.join('、') : '无' }}
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="评语" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">{{ row.comment || '—' }}</template>
        </el-table-column>
      </el-table>
    </template>
    <el-empty v-else description="当前未入住，暂无卫生检查记录" />
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useUserStore } from '@/store/user'
import { getCurrentRoom } from '@/api/checkin'
import { getHygieneList } from '@/api/daily'

const userStore = useUserStore()
const dorm = ref(null)
const rows = ref([])
const loading = ref(false)

onMounted(async () => {
  const room = await getCurrentRoom(userStore.user.studentId)
  dorm.value = room.dorm
  if (room.dorm) {
    loading.value = true
    const res = await getHygieneList({ roomId: room.dorm.roomId, pageSize: 100 })
    rows.value = res.list
    loading.value = false
  }
})
</script>