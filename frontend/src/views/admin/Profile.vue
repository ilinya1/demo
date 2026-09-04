<template>
  <div class="profile-page">
    <!-- 个人资料 -->
    <el-card shadow="never" class="card">
      <template #header>个人资料</template>
      <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="100px" class="info-form" style="max-width: 560px">
        <el-form-item label="用户名">
          <el-input :model-value="profile.username" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input :model-value="profile.name" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-input :model-value="profile.roleName" disabled />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="infoForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="联系邮箱" prop="email">
          <el-input v-model="infoForm.email" placeholder="请输入联系邮箱" />
        </el-form-item>
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
const role = 'ADMIN'
const username = userStore.user.username

const profile = ref({})
const infoFormRef = ref()
const infoForm = reactive({ phone: '', email: '' })
const savingInfo = ref(false)
const infoRules = {
  phone: [{ pattern: /^[\d-]{6,20}$/, message: '联系电话格式不正确', trigger: 'blur' }],
  email: [{ type: 'email', message: '联系邮箱格式不正确', trigger: 'blur' }]
}

async function loadProfile() {
  profile.value = await getProfile(role, username)
  infoForm.phone = profile.value.phone || ''
  infoForm.email = profile.value.email || ''
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