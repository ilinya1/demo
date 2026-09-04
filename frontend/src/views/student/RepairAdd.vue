<template>
  <el-card shadow="never" style="max-width: 720px">
    <template #header>提交报修</template>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="报修人">
            <el-input :model-value="userStore.user.name" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="报修位置">
            <el-input :model-value="dorm ? `${dorm.buildingName} ${dorm.roomNo} 室` : '未入住'" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话">
            <el-input v-model="form.contactPhone" placeholder="留空则使用备案号码" maxlength="20" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="报修物品" prop="typeId">
            <el-select v-model="form.typeId" placeholder="请选择报修物品" :disabled="!dorm" style="width: 100%">
              <el-option v-for="t in types" :key="t.id" :label="t.name" :value="t.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="问题描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请详细描述问题情况" :disabled="!dorm" />
      </el-form-item>
      <el-form-item label="上传图片">
        <el-upload list-type="picture-card" :auto-upload="false" accept="image/*"
                   :on-change="onFileChange" :on-remove="onRemove" :limit="3" :disabled="!dorm">
          <el-icon><Plus /></el-icon>
        </el-upload>
        <div class="tip">支持 jpg/png，最多 3 张。</div>
      </el-form-item>

      <div class="actions">
        <el-button @click="formRef.resetFields(); form.images = []">重置</el-button>
        <el-button type="primary" :disabled="!dorm" :loading="submitting" @click="submit">提交报修</el-button>
      </div>
    </el-form>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getCurrentRoom } from '@/api/checkin'
import { getRepairTypes, addRepair } from '@/api/daily'

const router = useRouter()
const userStore = useUserStore()
const dorm = ref(null)
const types = ref([])
const formRef = ref()
const submitting = ref(false)
const form = reactive({ typeId: '', description: '', contactPhone: '', images: [] })

const rules = {
  typeId: [{ required: true, message: '请选择报修物品', trigger: 'change' }],
  description: [{ required: true, message: '请描述问题情况', trigger: 'blur' }]
}

function onFileChange(file) {
  if (!file.raw) return
  const reader = new FileReader()
  reader.onload = (e) => {
    form.images.push(e.target.result)
    file.url = e.target.result
  }
  reader.readAsDataURL(file.raw)
}
function onRemove(file) {
  const idx = form.images.indexOf(file.url)
  if (idx > -1) form.images.splice(idx, 1)
}

async function submit() {
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  submitting.value = true
  try {
    await addRepair({
      studentId: userStore.user.studentId,
      roomId: dorm.value.roomId,
      typeId: form.typeId,
      description: form.description,
      contactPhone: form.contactPhone,
      images: form.images
    })
    ElMessage.success('报修提交成功，请等待处理')
    router.push('/student/my-repair')
  } catch (e) {
    if (e && e.msg) ElMessage.error(e.msg)
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  types.value = (await getRepairTypes()) || []
  const room = await getCurrentRoom(userStore.user.studentId)
  dorm.value = room.dorm
})
</script>

<style scoped>
.tip { line-height: 1.6; color: var(--d-ink-2); font-size: 12px; }
.actions { display: flex; gap: 12px; }
</style>