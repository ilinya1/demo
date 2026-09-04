<template>
  <el-card shadow="never" class="card">
    <template #header>修改密码</template>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 560px">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input v-model="form.oldPassword" type="password" show-password placeholder="请输入原密码" />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="form.newPassword" type="password" show-password placeholder="6-20 位" />
      </el-form-item>
      <el-form-item label="确认新密码" prop="confirm">
        <el-input v-model="form.confirm" type="password" show-password placeholder="请再次输入新密码" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="changing" @click="submit">确认修改</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { changePassword } from '@/api/settings'

// 供两个角色（管理员/学生）的个人中心复用；改密成功后统一注销并回登录页
const router = useRouter()
const userStore = useUserStore()
const username = userStore.user.username

const formRef = ref()
const changing = ref(false)
const form = reactive({ oldPassword: '', newPassword: '', confirm: '' })
const rules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '新密码长度应为 6-20 位', trigger: 'blur' }
  ],
  confirm: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (r, v, cb) => (v === form.newPassword ? cb() : cb(new Error('两次输入的新密码不一致'))),
      trigger: 'blur'
    }
  ]
}
async function submit() {
  await formRef.value.validate()
  changing.value = true
  try {
    await changePassword({ username, oldPassword: form.oldPassword, newPassword: form.newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    await new Promise((resolve) => setTimeout(resolve, 800))
    userStore.logout()
    router.push('/login')
  } finally {
    changing.value = false
  }
}

defineOptions({ name: 'PasswordForm' })
</script>

<style scoped>
.card {
  border: 1px solid var(--d-border);
  box-shadow: var(--d-shadow);
}
</style>