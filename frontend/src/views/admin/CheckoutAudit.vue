<template>
  <div>
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="申请编号">
          <el-input v-model="query.applyNo" placeholder="请输入申请编号" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="query.studentId" placeholder="请输入学号" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
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
        <span class="tips">收到学生退宿申请，请及时处理</span>
        <el-button type="primary" @click="openDirect"><el-icon><Plus /></el-icon>直接退宿</el-button>
      </div>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="applyNo" label="申请编号" width="150" />
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="college" label="学院" width="130" />
        <el-table-column label="当前宿舍" width="170">
          <template #default="{ row }">{{ row.buildingName }} {{ row.roomNo }}室 {{ row.bedNo }}号床</template>
        </el-table-column>
        <el-table-column prop="reason" label="退宿原因" width="110" />
        <el-table-column prop="planDate" label="计划退宿日期" width="120" />
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === '待审核'" link type="primary" @click="openAudit(row)">审核</el-button>
            <el-button v-else link @click="openDetail(row)">查看</el-button>
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

    <!-- 审核弹窗 -->
    <el-dialog v-model="auditVisible" title="退宿申请审核" width="620" destroy-on-close>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请编号">{{ cur?.applyNo }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ cur?.studentName }}（{{ cur?.studentId }}）</el-descriptions-item>
        <el-descriptions-item label="学院">{{ cur?.college }}</el-descriptions-item>
        <el-descriptions-item label="当前宿舍">{{ cur?.buildingName }} {{ cur?.roomNo }}室 {{ cur?.bedNo }}号床</el-descriptions-item>
        <el-descriptions-item label="退宿原因">{{ cur?.reason }}</el-descriptions-item>
        <el-descriptions-item label="计划退宿日期">{{ cur?.planDate }}</el-descriptions-item>
        <el-descriptions-item label="申请说明" :span="2">{{ cur?.description || '—' }}</el-descriptions-item>
      </el-descriptions>
      <el-input
        v-model="auditReason"
        class="reason"
        type="textarea"
        :rows="2"
        placeholder="通过请留空；驳回请填写原因，便于学生查看"
      />
      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="danger" :loading="auditing" @click="onAudit(false)">驳回</el-button>
        <el-button type="success" :loading="auditing" @click="onAudit(true)">通过</el-button>
      </template>
    </el-dialog>

    <!-- 直接退宿弹窗 -->
    <el-dialog v-model="directVisible" title="直接退宿" width="560" destroy-on-close>
      <el-form :model="direct" label-width="90px">
        <el-form-item label="学号" required>
          <el-input v-model="direct.studentId" placeholder="输入学号，回车带出学生信息" @keyup.enter="onLoadDirectStudent" />
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input :model-value="directStudent?.name || ''" disabled />
        </el-form-item>
        <el-form-item label="当前宿舍">
          <el-input :model-value="directCurrent" disabled />
        </el-form-item>
        <el-form-item label="退宿日期" required>
          <el-date-picker v-model="direct.checkoutDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="退宿原因" required>
          <el-select v-model="direct.reason" placeholder="请选择" style="width: 100%">
            <el-option v-for="r in reasons" :key="r" :label="r" :value="r" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="direct.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
      </el-form>
      <p class="warn">直接退宿将跳过学生申请，立即登记退宿并空出床铺。</p>
      <template #footer>
        <el-button @click="directVisible = false">取消</el-button>
        <el-button type="primary" :loading="directing" @click="onDirect">确认退宿</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { getCheckoutApps, auditCheckoutApp, directCheckout, getStudentById, getCheckinRecords } from '@/api/checkin'

const statuses = ['待审核', '已通过', '已驳回']
const reasons = ['毕业离校', '休学', '退学', '调宿', '其他']

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ page: 1, pageSize: 10, applyNo: '', studentId: '', status: '' })

const auditVisible = ref(false)
const auditing = ref(false)
const cur = ref(null)
const auditReason = ref('')

const directVisible = ref(false)
const directing = ref(false)
const direct = reactive({ studentId: '', checkoutDate: '', reason: '', remark: '' })
const directStudent = ref(null)

const directCurrent = computed(() => {
  if (!directStudent.value) return ''
  const rec = inHouse.value.find((r) => r.studentId === directStudent.value.studentId)
  return rec ? `${rec.buildingName} ${rec.roomNo}室 ${rec.bedNo}号床` : '未在住'
})
const inHouse = ref([])

async function load() {
  loading.value = true
  try {
    const res = await getCheckoutApps({ ...query })
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function onSearch() { query.page = 1; load() }
function onReset() { Object.assign(query, { applyNo: '', studentId: '', status: '' }); query.page = 1; load() }
function onPage(p) { query.page = p; load() }

function statusTag(s) {
  return s === '待审核' ? 'warning' : s === '已通过' ? 'success' : 'danger'
}

// 详情查看（已处理）
function openDetail(row) {
  cur.value = row
  auditReason.value = row.rejectReason || ''
  auditVisible.value = false
  ElMessage.info('已处理：' + row.status)
}
function openAudit(row) {
  cur.value = row
  auditReason.value = ''
  auditVisible.value = true
}

async function onAudit(approve) {
  if (!approve && !auditReason.value.trim()) return ElMessage.warning('驳回时请填写审核意见')
  auditing.value = true
  try {
    await auditCheckoutApp(cur.value.id, { approve, rejectReason: auditReason.value })
    ElMessage.success(approve ? '已通过，退宿生效' : '已驳回申请')
    auditVisible.value = false
    load()
  } finally {
    auditing.value = false
  }
}

function openDirect() {
  Object.assign(direct, { studentId: '', checkoutDate: new Date().toISOString().slice(0, 10), reason: '', remark: '' })
  directStudent.value = null
  directVisible.value = true
}

async function onLoadDirectStudent() {
  if (!direct.studentId) return
  try {
    directStudent.value = await getStudentById(direct.studentId)
  } catch (e) { }
}

async function onDirect() {
  if (!direct.studentId) return ElMessage.warning('请输入学号')
  if (!direct.checkoutDate) return ElMessage.warning('请选择退宿日期')
  if (!direct.reason) return ElMessage.warning('请选择退宿原因')
  directing.value = true
  try {
    await directCheckout({ ...direct })
    ElMessage.success('退宿已直接办理，床铺已空出')
    directVisible.value = false
    load()
  } finally {
    directing.value = false
  }
}

// 用于带出"当前宿舍"（复用入住记录中在住信息）
async function loadInHouse() {
  const res = await getCheckinRecords({ status: '在住', pageSize: 100 })
  inHouse.value = res.list
}

onMounted(() => { load(); loadInHouse() })
</script>

<style scoped>
.search-card { margin-bottom: 14px; border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.search-card :deep(.el-card__body) { padding: 14px 18px 2px; }
.table-card { border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 14px; }
.tips { color: var(--d-muted); font-size: 13px; }
.pager { margin-top: 14px; justify-content: flex-end; }
.reason { margin-top: 16px; }
.warn { color: #f56c6c; font-size: 12px; margin: 0; }
</style>