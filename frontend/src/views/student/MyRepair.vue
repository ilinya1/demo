<template>
  <el-card shadow="never">
    <template #header>我的报修记录</template>
    <el-table :data="rows" v-loading="loading" border :empty-text="'暂无报修记录'">
      <el-table-column prop="orderNo" label="报修单号" width="170" />
      <el-table-column prop="typeName" label="报修物品" width="100" />
      <el-table-column prop="description" label="问题描述" min-width="160" show-overflow-tooltip />
      <el-table-column prop="createTime" label="提交时间" width="160" />
      <el-table-column label="处理人（联系电话）" width="170">
        <template #default="{ row }">
          <span v-if="row.handlerName">{{ row.handlerName }}（{{ row.handlerPhone || '—' }}）</span>
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === '已完成' ? 'success' : row.status === '处理中' ? 'warning' : 'danger'">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="处理说明" min-width="180">
        <template #default="{ row }">{{ row.handleDesc || '—' }}</template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useUserStore } from '@/store/user'
import { getRepairList } from '@/api/daily'

const userStore = useUserStore()
const rows = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  const res = await getRepairList({ studentId: userStore.user.studentId, pageSize: 100 })
  rows.value = res.list
  loading.value = false
})
</script>