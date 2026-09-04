<template>
  <div class="apply-page">
    <el-card shadow="never">
      <template #header>提交退宿申请</template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="110px" class="apply-form">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="申请人">
              <el-input :model-value="info.student.name || '-'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学号">
              <el-input :model-value="info.student.studentId || '-'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前宿舍">
              <el-input :model-value="dorm ? `${dorm.buildingName} ${dorm.roomNo}室 ${dorm.bedNo}床` : '未入住'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入住时间">
              <el-input :model-value="dorm?.checkInTime || '-'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退宿原因" prop="reason">
              <el-select v-model="form.reason" placeholder="请选择退宿原因" :disabled="!dorm" style="width: 100%">
                <el-option v-for="o in reasons" :key="o" :label="o" :value="o" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划退宿日期" prop="planDate">
              <el-date-picker v-model="form.planDate" type="date" placeholder="请选择计划退宿日期"
                              value-format="YYYY-MM-DD" :disabled="!dorm" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="申请说明">
              <el-input v-model="form.description" type="textarea" :rows="3"
                        placeholder="选填，说明退宿原因及物品交接安排" :disabled="!dorm" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="form-actions">
        <el-button type="primary" :disabled="!dorm" :loading="submitting" @click="submit">提交申请</el-button>
      </div>
    </el-card>

    <el-card shadow="never" class="list-card">
      <template #header>我的退宿申请记录</template>
      <el-table :data="records" border :empty-text="'暂无退宿申请记录'">
        <el-table-column prop="applyNo" label="申请编号" width="170" />
        <el-table-column prop="reason" label="退宿原因" width="120" />
        <el-table-column prop="planDate" label="计划退宿日期" width="130" />
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="120">
          <template #default="{ row }">
            <el-button v-if="row.status === '待审核'" type="danger" link @click="cancel(row)">撤销</el-button>
            <el-button v-else type="primary" link @click="viewDetail(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getCurrentRoom, getCheckoutApps, submitCheckoutApply, cancelCheckoutApp } from '@/api/checkin'
import { getCheckoutReasons } from '@/api/settings'

const userStore = useUserStore()
const studentId = userStore.user.studentId

const info = ref({ student: {}, dorm: null, roommates: [] })
const dorm = ref(null)
const records = ref([])
const submitting = ref(false)
const formRef = ref()
const reasons = ref([])
async function loadReasons() {
  reasons.value = await getCheckoutReasons().then((list) => list.map((x) => x.name))
}
const form = reactive({ reason: '', planDate: '', description: '' })

const rules = {
  reason: [{ required: true, message: '请选择退宿原因', trigger: 'change' }],
  planDate: [{ required: true, message: '请选择计划退宿日期', trigger: 'change' }]
}

function statusTag(s) {
  return s === '已通过' ? 'success' : s === '已驳回' ? 'danger' : 'warning'
}

async function loadRoom() {
  const res = await getCurrentRoom(studentId)
  info.value = res
  dorm.value = res.dorm
}

async function loadRecords() {
  const res = await getCheckoutApps({ studentId, pageSize: 100 })
  records.value = res.list
}

async function submit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    await submitCheckoutApply({ studentId, ...form })
    ElMessage.success('退宿申请提交成功，请等待管理员审核')
    form.reason = ''
    form.planDate = ''
    form.description = ''
    await loadRecords()
  } finally {
    submitting.value = false
  }
}

function cancel(row) {
  ElMessageBox.confirm('确定撤销该退宿申请吗？', '撤销确认', { type: 'warning' })
    .then(async () => {
      await cancelCheckoutApp(row.id, studentId)
      ElMessage.success('已撤销申请')
      await loadRecords()
    })
    .catch(() => {})
}

function viewDetail(row) {
  const msg = row.status === '已驳回'
    ? `申请编号：${row.applyNo}\n驳回原因：${row.rejectReason || '（无）'}`
    : `申请编号：${row.applyNo}\n审核时间：${row.auditTime || '-'}\n当前状态：${row.status}`
  ElMessageBox.alert(msg, '申请详情')
}

onMounted(async () => {
  await Promise.all([loadRoom(), loadRecords(), loadReasons()])
})
</script>

<style scoped>
.apply-page { display: flex; flex-direction: column; gap: 16px; }
.form-actions { text-align: right; }
</style>