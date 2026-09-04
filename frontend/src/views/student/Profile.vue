<template>
  <div class="profile-page">
    <!-- 个人资料 -->
    <el-card shadow="never" class="card">
      <template #header>个人资料</template>
      <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="90px" class="info-form" style="max-width: 600px">
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="姓名"><el-input :model-value="profile.name" disabled /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="学号"><el-input :model-value="profile.username" disabled /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="性别"><el-input :model-value="profile.gender || '-'" disabled /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="班级"><el-input :model-value="profile.className || '-'" disabled /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="学院"><el-input :model-value="profile.college || '-'" disabled /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="专业"><el-input :model-value="profile.major || '-'" disabled /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="在校状态"><el-input :model-value="profile.academicStatus || '-'" disabled /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="住宿状态"><el-input :model-value="profile.housingStatus || '-'" disabled /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系电话" prop="phone"><el-input v-model="infoForm.phone" placeholder="请输入联系电话" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="紧急联系人"><el-input v-model="infoForm.emergency" placeholder="请输入紧急联系人" /></el-form-item></el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" :loading="savingInfo" @click="saveInfo">保存资料</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 修改密码（公共组件，管理/学生复用） -->
    <PasswordForm />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import PasswordForm from '@/components/PasswordForm.vue'
import { getProfile, updateProfile } from '@/api/settings'

const userStore = useUserStore()
const role = 'STUDENT'
const username = userStore.user.studentId || userStore.user.username

const profile = ref({})
const infoFormRef = ref()
const infoForm = reactive({ phone: '', emergency: '' })
const savingInfo = ref(false)
const infoRules = {
  phone: [{ pattern: /^[\d-]{6,20}$/, message: '联系电话格式不正确', trigger: 'blur' }]
}

async function loadProfile() {
  profile.value = await getProfile(role, username)
  infoForm.phone = profile.value.phone || ''
  infoForm.emergency = profile.value.emergency || ''
}
async function saveInfo() {
  await infoFormRef.value.validate()
  savingInfo.value = true
  try {
    await updateProfile(role, username, { ...infoForm })
    ElMessage.success('资料已保存')
    loadProfile()
  } finally {
    savingInfo.value = false
  }
}

onMounted(loadProfile)
</script>

<style scoped>
.profile-page { display: flex; flex-direction: column; gap: 16px; }
.card { border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
</style>