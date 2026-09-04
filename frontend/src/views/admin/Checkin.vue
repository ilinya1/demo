<template>
  <div>
    <el-card shadow="never" class="main-card">
      <el-steps :active="3" align-center class="steps">
        <el-step title="选择学生" />
        <el-step title="选择房间" />
        <el-step title="选择床位" />
        <el-step title="确认入住" />
      </el-steps>

      <el-form :model="form" label-width="96px" class="form">
        <el-form-item label="学生学号" required>
          <div class="row">
            <el-input v-model="form.studentId" placeholder="输入学号查询学生" style="width: 260px" @keyup.enter="onQueryStudent" />
            <el-button type="primary" :loading="querying" @click="onQueryStudent"><el-icon><Search /></el-icon>查询</el-button>
          </div>
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input :model-value="student?.name || ''" disabled />
        </el-form-item>
        <el-form-item label="学院">
          <el-input :model-value="student?.college || ''" disabled />
        </el-form-item>
        <el-form-item label="班级">
          <el-input :model-value="student?.className || ''" disabled />
        </el-form-item>
        <el-divider />
        <el-form-item :label="student?.housingStatus === '在住' ? '当前状态' : '选择楼栋'" required>
          <template v-if="student?.housingStatus === '在住'">
            <span class="tips-danger">该学生当前已在住（{{ student?.housingStatus }}），不能重复入住</span>
          </template>
          <el-select v-else v-model="form.buildingId" placeholder="请选择楼栋" :disabled="!student" style="width: 260px" @change="onBuildingChange">
            <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" required>
          <el-select v-model="form.roomId" placeholder="请先选择楼栋" :disabled="!rooms.length" style="width: 300px" @change="onRoomChange">
            <el-option v-for="r in rooms" :key="r.id" :label="`${r.roomNo}（剩余 ${r.freeBeds} 床）`" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="床位号" required>
          <el-select v-model="form.bedNo" placeholder="请先选择房间" :disabled="!beds.length" style="width: 200px">
            <el-option v-for="b in beds" :key="b" :label="`${b} 号床`" :value="b" />
          </el-select>
        </el-form-item>
        <el-form-item label="入住日期" required>
          <el-date-picker v-model="form.checkInDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 220px" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" placeholder="选填" style="width: 420px" />
        </el-form-item>
      </el-form>

      <div class="actions">
        <el-button type="primary" :loading="saving" :disabled="!canSubmit" @click="onSubmit">确认入住</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getStudentById, getBuildingOptions, getCheckinRooms, getFreeBeds, submitCheckin } from '@/api/checkin'

const student = ref(null)
const buildings = ref([])
const rooms = ref([])
const beds = ref([])
const querying = ref(false)
const saving = ref(false)

const form = reactive({ studentId: '', buildingId: '', roomId: '', bedNo: null, checkInDate: '', remark: '' })

const canSubmit = computed(() =>
  student.value && student.value.housingStatus !== '在住' && form.buildingId && form.roomId && form.bedNo != null && form.checkInDate
)

async function onQueryStudent() {
  if (!form.studentId) return ElMessage.warning('请输入学号')
  querying.value = true
  try {
    student.value = await getStudentById(form.studentId)
    resetPlacement()
    ElMessage.success(`已带出学生：${student.value.name}`)
  } finally {
    querying.value = false
  }
}

function resetPlacement() {
  form.buildingId = ''
  form.roomId = ''
  form.bedNo = null
  rooms.value = []
  beds.value = []
}

async function onBuildingChange(buildingId) {
  form.roomId = ''
  form.bedNo = null
  beds.value = []
  rooms.value = await getCheckinRooms(buildingId)
}

async function onRoomChange(roomId) {
  form.bedNo = null
  beds.value = await getFreeBeds(roomId)
}

async function onSubmit() {
  saving.value = true
  try {
    await submitCheckin({ ...form })
    ElMessage.success('入住登记成功')
    resetAll()
  } finally {
    saving.value = false
  }
}

function resetAll() {
  form.studentId = ''
  form.checkInDate = ''
  form.remark = ''
  student.value = null
  resetPlacement()
}

onMounted(async () => {
  buildings.value = await getBuildingOptions()
  form.checkInDate = new Date().toISOString().slice(0, 10)
})
</script>

<style scoped>
.main-card { border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.steps { max-width: 720px; margin: 0 auto 24px; }
.form { max-width: 760px; padding-top: 8px; }
.row { display: flex; gap: 10px; }
.tips-danger { color: var(--el-color-danger); font-size: 13px; line-height: 32px; }
.actions { text-align: right; max-width: 760px; }
</style>