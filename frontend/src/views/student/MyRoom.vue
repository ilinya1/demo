<template>
  <el-card shadow="never">
    <template #header>我的宿舍</template>
    <template v-if="dorm">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学生姓名">{{ info.student.name }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ info.student.studentId }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ info.student.className }}</el-descriptions-item>
        <el-descriptions-item label="住宿状态"><el-tag type="success">在住</el-tag></el-descriptions-item>
        <el-descriptions-item label="楼栋">{{ dorm.buildingName }}</el-descriptions-item>
        <el-descriptions-item label="房间 / 床位">{{ dorm.roomNo }} 室 · {{ bedLabel(dorm.bedNo) }}</el-descriptions-item>
        <el-descriptions-item label="入住时间">{{ dorm.checkInTime }}</el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <h4>室友</h4>
      <el-table :data="info.roommates" border :empty-text="'暂无室友（当前房间仅你一人）'">
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="studentId" label="学号" />
        <el-table-column prop="bedNo" label="床位" :formatter="(r) => bedLabel(r.bedNo)" />
      </el-table>
    </template>
    <el-empty v-else description="当前未入住，暂无在住信息" />
  </el-card>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useUserStore } from '@/store/user'
import { getCurrentRoom } from '@/api/checkin'

const userStore = useUserStore()
const info = ref({ student: {}, dorm: null, roommates: [] })
const dorm = computed(() => info.value.dorm)

function bedLabel(n) {
  return `${n} 床`
}

onMounted(async () => {
  const res = await getCurrentRoom(userStore.user.studentId)
  info.value = res
})
</script>