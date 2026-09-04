<template>
  <div class="sc-page">
    <!-- 左：班级列表 -->
    <div class="sc-left">
      <el-card shadow="never" class="class-card">
        <template #header>
          <div class="class-head">
            <span class="head-title">班级</span>
            <el-button type="primary" size="small" @click="openClassDialog()"><el-icon><Plus /></el-icon>新增班级</el-button>
          </div>
        </template>
        <div class="class-filter">
          <el-input v-model="classQuery.name" placeholder="搜索班级/学院" clearable :prefix-icon="Search" size="small" @input="loadClasses" />
        </div>
        <div class="class-list" v-loading="loadingClasses">
          <div
            v-for="c in classList" :key="c.id"
            class="class-item" :class="{ active: selectedClass && selectedClass.id === c.id }"
            @click="selectClass(c)"
          >
            <div class="ci-top">
              <span class="ci-name">{{ c.name }}</span>
              <el-tag size="small" effect="plain">{{ c.grade }}</el-tag>
            </div>
            <div class="ci-sub">{{ c.college }} · {{ c.major }}</div>
            <div class="ci-sub">学生 {{ c.studentCount }} · 住宿 {{ c.boardingCount }}</div>
            <div class="ci-ops" @click.stop>
              <el-button link type="primary" size="small" @click="openClassDialog(c)">编辑</el-button>
              <el-button link type="danger" size="small" @click="onDeleteClass(c)">删除</el-button>
            </div>
          </div>
          <el-empty v-if="!loadingClasses && classList.length === 0" description="暂无班级" :image-size="60" />
        </div>
      </el-card>
    </div>

    <!-- 右：选中班级的学生 -->
    <div class="sc-right">
      <el-card shadow="never" class="search-card">
        <el-form :inline="true" :model="query" @submit.prevent>
          <el-form-item>
            <el-tag type="info" effect="light">{{ selectedClass ? `当前班级：${selectedClass.name}` : '请选择左侧班级' }}</el-tag>
          </el-form-item>
          <el-form-item label="学号">
            <el-input v-model="query.studentId" placeholder="请输入学号" clearable style="width: 150px" />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="query.name" placeholder="请输入姓名" clearable style="width: 130px" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.academicStatus" placeholder="全部" clearable style="width: 120px">
              <el-option v-for="s in academicStatuses" :key="s" :label="s" :value="s" />
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
          <span class="tb-title">{{ selectedClass ? selectedClass.name + ' 学生' : '学生' }}</span>
          <el-button type="primary" :disabled="!selectedClass" @click="openStudentDialog()"><el-icon><Plus /></el-icon>新增学生</el-button>
          <el-button :disabled="!selectedClass" @click="onExport"><el-icon><Download /></el-icon>导出</el-button>
        </div>

        <el-table :data="list" v-loading="loading" stripe>
          <el-table-column prop="studentId" label="学号" width="120" />
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="gender" label="性别" width="70" />
          <el-table-column prop="major" label="专业" width="170" show-overflow-tooltip />
          <el-table-column prop="contactPhone" label="联系方式" width="130" />
          <el-table-column prop="academicStatus" label="在校状态" width="90">
            <template #default="{ row }">
              <el-tag :type="statusTag(row.academicStatus)">{{ row.academicStatus }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="housingStatus" label="住宿状态" width="100">
            <template #default="{ row }">
              <el-tag :type="housingTag(row.housingStatus)" effect="plain">{{ row.housingStatus }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="openStudentDialog(row)">编辑</el-button>
              <el-button link type="danger" @click="onDeleteStudent(row)">删除</el-button>
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

    <!-- 班级新增 / 编辑弹窗 -->
    <el-dialog v-model="classDialogVisible" :title="classEditing ? '编辑班级' : '新增班级'" width="520" destroy-on-close>
      <el-form ref="classFormRef" :model="classForm" :rules="classRules" label-width="90px">
        <el-form-item label="班级名称" prop="name">
          <el-input v-model="classForm.name" placeholder="请输入班级名称，如：软工2301" />
        </el-form-item>
        <el-form-item label="所属学院" prop="college">
          <el-select v-model="classForm.college" placeholder="请选择学院" style="width: 100%">
            <el-option v-for="c in colleges" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="classForm.major" placeholder="请输入专业" />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-input-number v-model="classForm.grade" :min="2018" :max="2030" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="班主任">
          <el-input v-model="classForm.headTeacher" placeholder="请输入班主任姓名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="classDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingClass" @click="onSaveClass">保存</el-button>
      </template>
    </el-dialog>

    <!-- 学生新增 / 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.studentId ? '编辑学生' : '新增学生'" width="560" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="form.studentId" :disabled="!!form.studentId" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="班级" prop="className">
          <el-select v-model="form.className" placeholder="请选择班级" style="width: 100%" @change="onClassChange">
            <el-option v-for="c in allClasses" :key="c.name" :label="c.name" :value="c.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="学院" prop="college">
          <el-select v-model="form.college" placeholder="请选择学院" style="width: 100%">
            <el-option v-for="c in colleges" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="form.major" placeholder="请输入专业" />
        </el-form-item>
        <el-form-item label="联系方式" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="紧急联系人">
          <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSaveStudent">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Download } from '@element-plus/icons-vue'
import { getStudents, addStudent, updateStudent, deleteStudent } from '@/api/student'
import { getClasses, addClass, updateClass, deleteClass } from '@/api/class'

const colleges = ['计算机学院', '机械工程学院', '外国语学院']
const academicStatuses = ['在校', '毕业', '退学']

// ================= 班级（左） =================
const classQuery = reactive({ name: '' })
const classList = ref([])
const allClasses = ref([]) // 学生弹窗班级下拉用全量
const loadingClasses = ref(false)
const selectedClass = ref(null)

const classDialogVisible = ref(false)
const savingClass = ref(false)
const classFormRef = ref(null)
const classEditing = ref(null)
const emptyClassForm = { name: '', college: '', major: '', grade: 2023, headTeacher: '' }
const classForm = reactive({ ...emptyClassForm })
const classRules = {
  name: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  college: [{ required: true, message: '请选择学院', trigger: 'change' }],
  major: [{ required: true, message: '请输入专业', trigger: 'blur' }],
  grade: [{ required: true, message: '请输入年级', trigger: 'blur' }]
}

async function loadClasses() {
  loadingClasses.value = true
  try {
    const res = await getClasses({ name: classQuery.name, page: 1, pageSize: 100 })
    classList.value = res.list
    // 保持选中：原选中仍存在则保留，否则回退到第一项
    const keep = selectedClass.value && classList.value.find((c) => c.id === selectedClass.value.id)
    const next = keep || classList.value[0] || null
    if (next && (!selectedClass.value || next.id !== selectedClass.value.id)) {
      selectedClass.value = null // 触发重新拉取学生
      selectClass(next)
    } else if (!next) {
      selectedClass.value = null
      loadStudents()
    }
  } finally {
    loadingClasses.value = false
  }
}

async function loadAllClasses() {
  const res = await getClasses({ page: 1, pageSize: 100 })
  allClasses.value = res.list
}

function selectClass(c) {
  selectedClass.value = c
  query.page = 1
  loadStudents()
}

function openClassDialog(row) {
  Object.assign(classForm, emptyClassForm)
  classEditing.value = null
  if (row) {
    classEditing.value = row.id
    Object.assign(classForm, row)
  }
  classDialogVisible.value = true
}

async function onSaveClass() {
  await classFormRef.value.validate()
  savingClass.value = true
  try {
    if (classEditing.value) {
      await updateClass(classEditing.value, { ...classForm })
    } else {
      await addClass({ ...classForm })
    }
    ElMessage.success('保存成功')
    classDialogVisible.value = false
    await Promise.all([loadClasses(), loadAllClasses()])
  } finally {
    savingClass.value = false
  }
}

function onDeleteClass(row) {
  ElMessageBox.confirm(`确定删除班级「${row.name}」吗？班内若有学生将无法删除。`, '删除确认', { type: 'warning' }).then(async () => {
    await deleteClass(row.id)
    ElMessage.success('删除成功')
    if (selectedClass.value && selectedClass.value.id === row.id) selectedClass.value = null
    await Promise.all([loadClasses(), loadAllClasses()])
  }).catch(() => {})
}

// ================= 学生（右） =================
const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ page: 1, pageSize: 10, studentId: '', name: '', academicStatus: '' })

const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref(null)
const editing = ref(null) // 编辑时记录原始学号
const emptyForm = { studentId: '', name: '', gender: '男', className: '', college: '', major: '', contactPhone: '', emergencyContact: '', academicStatus: '在校', housingStatus: '未住' }
const form = reactive({ ...emptyForm })

const rules = {
  studentId: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  className: [{ required: true, message: '请选择班级', trigger: 'change' }],
  college: [{ required: true, message: '请选择学院', trigger: 'change' }],
  major: [{ required: true, message: '请输入专业', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系方式', trigger: 'blur' }]
}

async function loadStudents() {
  loading.value = true
  try {
    const res = await getStudents({ ...query, className: selectedClass.value ? selectedClass.value.name : '' })
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function onSearch() { query.page = 1; loadStudents() }
function onReset() { Object.assign(query, { studentId: '', name: '', academicStatus: '' }); query.page = 1; loadStudents() }
function onPage(p) { query.page = p; loadStudents() }

function onClassChange(name) {
  const c = classList.value.find((x) => x.name === name) || allClasses.value.find((x) => x.name === name)
  if (c) { form.college = c.college; form.major = c.major }
}

function openStudentDialog(row) {
  Object.assign(form, emptyForm)
  editing.value = null
  if (row) {
    editing.value = row.studentId
    Object.assign(form, row)
  } else if (selectedClass.value) {
    // 在当前班级内新增：预填班级并自动带出学院/专业
    form.className = selectedClass.value.name
    form.college = selectedClass.value.college
    form.major = selectedClass.value.major
  }
  dialogVisible.value = true
}

async function onSaveStudent() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (editing.value) {
      await updateStudent(editing.value, { ...form })
    } else {
      await addStudent({ ...form })
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    // 若学生被分到当前选中班级则刷新，否则无论在哪都刷新当前班以便感知
    loadStudents()
    loadAllClasses()
  } finally {
    saving.value = false
  }
}

function statusTag(s) {
  return s === '在校' ? 'success' : s === '退学' ? 'danger' : 'info'
}
function housingTag(s) {
  return s === '在住' ? 'success' : s === '已退宿' ? 'info' : 'warning'
}

function onDeleteStudent(row) {
  ElMessageBox.confirm(`确定删除学生「${row.name}（${row.studentId}）」吗？`, '删除确认', { type: 'warning' }).then(async () => {
    await deleteStudent(row.studentId)
    ElMessage.success('删除成功')
    loadStudents()
  }).catch(() => {})
}

function onExport() {
  if (!list.value.length) { ElMessage.info('当前暂无学生可导出'); return }
  ElMessage.info(`导出功能（原型占位，当前 ${list.value.length} 条）`)
}

onMounted(() => { loadClasses(); loadAllClasses() })
</script>

<style scoped>
.sc-page { display: flex; gap: 14px; align-items: flex-start; }

/* 左列 */
.sc-left { width: 300px; flex: none; }
.class-card { border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.class-head { display: flex; align-items: center; justify-content: space-between; }
.class-head .head-title { font-weight: 800; color: var(--d-ink); }
.class-filter { margin-bottom: 10px; }
.class-list {
  max-height: 640px; overflow-y: auto; display: flex; flex-direction: column; gap: 4px;
}
.class-item {
  position: relative; padding: 8px 10px; border-radius: 8px; cursor: pointer;
  border: 1px solid transparent; transition: background .15s, border-color .15s;
}
.class-item:hover { background: #f5f7fa; }
.class-item.active { background: #ecf5ff; border-color: #d9ecff; }
.ci-top { display: flex; align-items: center; justify-content: space-between; }
.ci-name { font-weight: 700; color: var(--d-ink); }
.ci-sub { font-size: 12px; color: var(--d-muted); margin-top: 2px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.ci-ops { display: none; position: absolute; right: 6px; bottom: 4px; background: transparent; }
.class-item:hover .ci-ops { display: inline-flex; }

/* 右列 */
.sc-right { flex: 1; min-width: 0; }
.search-card { margin-bottom: 14px; border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.search-card :deep(.el-card__body) { padding: 14px 18px 2px; }
.table-card { border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.toolbar { display: flex; gap: 10px; margin-bottom: 14px; align-items: center; }
.toolbar .tb-title { font-weight: 800; color: var(--d-ink); margin-right: auto; }
.pager { margin-top: 14px; justify-content: flex-end; }
</style>