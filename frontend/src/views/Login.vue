<template>
  <div class="login-wrap">
    <div class="login-box">
      <div class="brand"><span class="logo-mark">宿</span><span>{{ systemName }}</span></div>
      <p class="sub">{{ welcomeMessage }}</p>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        size="large"
        @keyup.enter="onSubmit"
      >
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="管理员账号或学号" :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="full" :loading="loading" @click="onSubmit">登 录</el-button>
        </el-form-item>
      </el-form>
      <p class="tips">
        演示账号：管理员 admin/123456 · 学生 2023010101/123456<br />
        自动按角色进入管理员端或学生端
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getSystemParams } from '@/api/settings'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 读取系统参数（系统名称 / 登录欢迎语），由全局设置动态配置
const systemName = ref('学生宿舍管理系统')
const welcomeMessage = ref('宿舍入住 · 卫生 · 报修 · 一站式管理')
onMounted(async () => {
  try {
    const list = await getSystemParams()
    const getVal = (k, fb) => (list.find((x) => x.key === k) || {}).value || fb
    systemName.value = getVal('systemName', systemName.value)
    welcomeMessage.value = getVal('welcomeMessage', welcomeMessage.value)
  } catch (e) {
    /* 加载失败使用默认文案 */
  }
})

const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function onSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const user = await userStore.login(form.username, form.password)
    const redirect = route.query.redirect || (user.role === 'ADMIN' ? '/admin/dashboard' : '/student/my-room')
    router.push(redirect)
  } catch (e) {
    /* 错误已由 request 统一提示 */
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrap {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(1000px 500px at 85% 10%, rgba(79, 110, 247, 0.28), transparent 55%),
    radial-gradient(800px 400px at 10% 90%, rgba(79, 110, 247, 0.16), transparent 55%),
    #0f172a;
}
.login-box {
  width: 380px;
  padding: 36px 32px 24px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.35);
}
.brand { display: flex; align-items: center; gap: 10px; font-size: 20px; font-weight: 800; color: var(--d-ink); }
.logo-mark {
  width: 34px; height: 34px; border-radius: 9px; background: var(--d-primary);
  color: #fff; font-size: 16px; display: flex; align-items: center; justify-content: center;
}
.sub { color: var(--d-muted); font-size: 13px; margin: 6px 0 24px; }
.full { width: 100%; }
.tips { color: #999; font-size: 12px; line-height: 1.7; text-align: center; margin: 8px 0 0; }
</style>