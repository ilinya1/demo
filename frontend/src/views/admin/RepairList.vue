<template>
  <el-card shadow="never">
    <div class="toolbar">
      <div class="filters">
        <el-input v-model="query.orderNo" placeholder="报修单号" clearable style="width: 180px" @keyup.enter="load" @clear="load" />
        <el-select v-model="query.buildingId" placeholder="全部楼栋" clearable style="width: 150px" @change="load">
          <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id" />
        </el-select>
        <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 130px" @change="load">
          <el-option label="待处理" value="待处理" /><el-option label="处理中" value="处理中" /><el-option label="已完成" value="已完成" />
        </el-select>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
    </div>

    <el-table :data="rows" v-loading="loading" border>
      <el-table-column prop="orderNo" label="报修单号" width="150" />
      <el-table-column prop="studentName" label="报修人" width="90" />
      <el-table-column prop="buildingName" label="楼栋" width="80" />
      <el-table-column prop="roomNo" label="房间" width="70" />
      <el-table-column prop="typeName" label="报修物品" width="90" />
      <el-table-column prop="description" label="问题描述" min-width="140" show-overflow-tooltip />
      <el-table-column prop="createTime" label="提交时间" width="150" />
      <el-table-column prop="handlerName" label="处理人" width="90">
        <template #default="{ row }">{{ row.handlerName || '—' }}</template>
      </el-table-column>
      <el-table-column prop="handlerPhone" label="联系电话" width="110">
        <template #default="{ row }">{{ row.handlerPhone || '—' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.status)">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="90">
        <template #default="{ row }">
          <el-button type="primary" link @click="open(row)">{{ row.status === '已完成' ? '详情' : '处理' }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pager" background layout="total, prev, pager, next, sizes"
                   :total="total" :page-size="query.pageSize" :current-page="query.page"
                   @current-change="(p) => { query.page = p; load() }" @size-change="(s) => { query.pageSize = s; query.page = 1; load() }" />
  </el-card>

  <el-dialog v-model="dialog" :title="current.status === '已完成' ? '报修详情' : '处理报修'" width="620px" :close-on-click-modal="false">
    <el-descriptions :column="2" border class="detail">
      <el-descriptions-item label="报修单号">{{ current.orderNo }}</el-descriptions-item>
      <el-descriptions-item label="报修人">{{ current.studentName }}（{{ current.studentId }}）</el-descriptions-item>
      <el-descriptions-item label="位置">{{ current.buildingName }} {{ current.roomNo }} 室</el-descriptions-item>
      <el-descriptions-item label="报修物品">{{ current.typeName }}</el-descriptions-item>
      <el-descriptions-item label="联系电话">{{ current.contactPhone }}</el-descriptions-item>
      <el-descriptions-item label="提交时间">{{ current.createTime }}</el-descriptions-item>
      <el-descriptions-item label="问题描述" :span="2">{{ current.description }}</el-descriptions-item>
      <el-descriptions-item label="状态" :span="2">
        <el-tag :type="statusTag(current.status)">{{ current.status }}</el-tag>
        <span v-if="current.handleTime" style="margin-left: 10px; color:#909399">上次处理：{{ current.handleTime }}</span>
      </el-descriptions-item>
    </el-descriptions>

    <template v-if="editable">
      <el-divider content-position="left">处理信息</el-divider>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="处理人" prop="handlerName">
          <el-input v-model="form.handlerName" placeholder="请输入处理人姓名" style="width: 220px" />
        </el-form-item>
        <el-form-item label="联系电话" prop="handlerPhone">
          <el-input v-model="form.handlerPhone" placeholder="请输入联系电话" style="width: 220px" />
        </el-form-item>
        <el-form-item label="处理状态">
          <el-radio-group v-model="form.status">
            <el-radio value="处理中">处理中</el-radio>
            <el-radio value="已完成">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理说明" prop="handleDesc">
          <el-input v-model="form.handleDesc" type="textarea" :rows="3" placeholder="填写处理说明（标记完成时必填）" style="width: 100%" />
        </el-form-item>
      </el-form>
    </template>
    <template v-else>
      <el-descriptions :column="1" border class="detail" style="margin-top: 12px">
        <el-descriptions-item label="处理人">{{ current.handlerName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ current.handlerPhone }}</el-descriptions-item>
        <el-descriptions-item label="处理说明">{{ current.handleDesc || '—' }}</el-descriptions-item>
      </el-descriptions>
    </template>

    <template #footer>
      <el-button @click="dialog = false">取消</el-button>
      <el-button v-if="editable" type="primary" :loading="saving" @click="save">保存处理</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getRepairList, handleRepair } from '@/api/daily'
import { getBuildings } from '@/api/building'

const query = reactive({ orderNo: '', buildingId: '', status: '', page: 1, pageSize: 10 })
const rows = ref([])
const total = ref(0)
const loading = ref(false)
const buildings = ref([])
const dialog = ref(false)
const current = ref({})
const form = reactive({ handlerName: '', handlerPhone: '', status: '处理中', handleDesc: '' })
const formRef = ref()
const saving = ref(false)
const editable = computed(() => current.value.status !== '已完成')

const rules = {
  handlerName: [{ required: true, message: '请输入处理人', trigger: 'blur' }],
  handlerPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

function statusTag(s) {
  return s === '已完成' ? 'success' : s === '处理中' ? 'warning' : 'danger'
}

async function load() {
  loading.value = true
  const res = await getRepairList({ ...query })
  rows.value = res.list
  total.value = res.total
  loading.value = false
}
function reset() {
  query.orderNo = ''; query.buildingId = ''; query.status = ''; query.page = 1; load()
}
function open(row) {
  current.value = { ...row }
  form.handlerName = row.handlerName || ''
  form.handlerPhone = row.handlerPhone || ''
  form.status = '处理中'
  form.handleDesc = row.handleDesc || ''
  dialog.value = true
}
async function save() {
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  if (form.status === '已完成' && !form.handleDesc) {
    ElMessage.warning('标记完成时请填写处理说明')
    return
  }
  saving.value = true
  try {
    await handleRepair(current.value.id, { ...form })
    ElMessage.success('处理结果已保存')
    dialog.value = false
    load()
  } catch (e) {
    if (e && e.msg) ElMessage.error(e.msg)
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  buildings.value = (await getBuildings()).list
  load()
})
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; gap: 16px; margin-bottom: 16px; flex-wrap: wrap; }
.filters { display: flex; gap: 12px; flex-wrap: wrap; }
.pager { margin-top: 16px; justify-content: flex-end; }
.detail { margin-bottom: 8px; }
</style>