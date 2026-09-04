<template>
  <el-card shadow="never" style="max-width: 760px">
    <template #header>新增卫生检查</template>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
      <el-form-item label="检查楼栋" prop="buildingId">
        <el-select v-model="form.buildingId" placeholder="请选择楼栋" style="width: 260px" @change="onBuildingChange">
          <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName" :value="b.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="检查房间" prop="roomId">
        <el-select v-model="form.roomId" placeholder="请选择房间" style="width: 260px" :disabled="!form.buildingId">
          <el-option v-for="r in rooms" :key="r.id" :label="`${r.roomNo} 室（${r.roomType}）`" :value="r.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="检查日期" prop="checkDate">
        <el-date-picker v-model="form.checkDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 260px" />
      </el-form-item>
      <el-form-item label="检查人" prop="checker">
        <el-input v-model="form.checker" placeholder="请输入检查人" style="width: 260px" />
      </el-form-item>

      <el-form-item label="扣分项">
        <el-checkbox-group v-model="form.deductItems">
          <el-checkbox v-for="o in deductOptions" :key="o.key" :value="o.key" class="deduct">
            {{ o.label }}（-{{ o.points }}）
          </el-checkbox>
        </el-checkbox-group>
        <div class="score-line">
          当前得分：<b :class="'score ' + scoreClass">{{ score }}</b>　判定结果：
          <el-tag :type="resultTag">{{ result }}</el-tag>
        </div>
      </el-form-item>

      <el-form-item label="现场照片">
        <el-upload list-type="picture-card" :auto-upload="false" accept="image/*"
                   :on-change="onFileChange" :on-remove="onRemove" :limit="5">
          <el-icon><Plus /></el-icon>
        </el-upload>
        <div class="tip">可上传多张现场照片；评分低于 60 或涉及「违规电器」时必须上传照片。</div>
      </el-form-item>

      <el-form-item label="评语">
        <el-input v-model="form.comment" type="textarea" :rows="3" maxlength="200" show-word-limit placeholder="选填，对本次检查的说明" style="width: 100%" />
      </el-form-item>

      <div class="actions">
        <el-button @click="formRef.resetFields(); form.deductItems = []; form.photos = []">重置</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">保存检查</el-button>
      </div>
    </el-form>
  </el-card>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { addHygiene as addApi } from '@/api/daily'
import { getBuildings } from '@/api/building'
import { getRooms } from '@/api/room'

const router = useRouter()
const formRef = ref()
const buildings = ref([])
const rooms = ref([])
const submitting = ref(false)

const deductOptions = [
  { key: '地面不干净', label: '地面不干净', points: 5 },
  { key: '物品摆放凌乱', label: '物品摆放凌乱', points: 3 },
  { key: '被子未叠', label: '被子未叠', points: 5 },
  { key: '桌面脏乱', label: '桌面脏乱', points: 3 },
  { key: '卫生间不洁', label: '卫生间不洁', points: 8 },
  { key: '违规电器', label: '违规电器', points: 15 },
  { key: '垃圾未倒', label: '垃圾未倒', points: 2 }
]

const form = reactive({ buildingId: '', roomId: '', checkDate: '', checker: '', deductItems: [], photos: [], comment: '' })

const rules = {
  buildingId: [{ required: true, message: '请选择检查楼栋', trigger: 'change' }],
  roomId: [{ required: true, message: '请选择检查房间', trigger: 'change' }],
  checkDate: [{ required: true, message: '请选择检查日期', trigger: 'change' }],
  checker: [{ required: true, message: '请输入检查人', trigger: 'blur' }]
}

const score = computed(() => {
  let s = 100
  form.deductItems.forEach((k) => {
    const o = deductOptions.find((d) => d.key === k)
    if (o) s -= o.points
  })
  return s
})
const result = computed(() => (score.value >= 90 ? '优秀' : score.value >= 60 ? '合格' : '不合格'))
const resultTag = computed(() => (result.value === '优秀' ? 'success' : result.value === '合格' ? 'warning' : 'danger'))
const scoreClass = computed(() => (score.value >= 90 ? 'good' : score.value >= 60 ? 'mid' : 'bad'))

async function onBuildingChange() {
  form.roomId = ''
  rooms.value = (await getRooms({ buildingId: form.buildingId, pageSize: 100 })).list
}

function onFileChange(file) {
  if (!file.raw) return
  const reader = new FileReader()
  reader.onload = (e) => {
    form.photos.push(e.target.result)
    file.url = e.target.result
  }
  reader.readAsDataURL(file.raw)
}
function onRemove(file) {
  const idx = form.photos.indexOf(file.url)
  if (idx > -1) form.photos.splice(idx, 1)
}

async function submit() {
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  if (score.value < 60 || form.deductItems.includes('违规电器')) {
    if (form.photos.length === 0) {
      ElMessage.warning('评分低于 60 或涉及违规电器时，必须上传现场照片')
      return
    }
  }
  submitting.value = true
  try {
    await addApi({ ...form })
    ElMessage.success('卫生检查已保存')
    router.push('/admin/hygiene-list')
  } catch (e) {
    if (e && e.msg) ElMessage.error(e.msg)
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  buildings.value = (await getBuildings()).list
})
</script>

<style scoped>
.deduct { display: inline-flex; margin-right: 12px; }
.score-line { margin-top: 10px; font-size: 14px; color: var(--d-ink-2); }
.score { font-size: 20px; }
.score.good { color: #16a34a; }
.score.mid { color: #d97706; }
.score.bad { color: #dc2626; }
.tip { line-height: 1.6; color: var(--d-ink-2); font-size: 12px; }
.actions { display: flex; gap: 12px; }
</style>