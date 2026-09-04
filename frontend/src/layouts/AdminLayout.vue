<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside d-sidebar">
      <div class="logo"><span class="logo-mark">宿</span><span>{{ systemName }}</span></div>
      <el-menu :default-active="$route.path" router>
        <el-sub-menu index="dash">
          <template #title><el-icon><Odometer /></el-icon><span>仪表盘</span></template>
          <el-menu-item index="/admin/dashboard">仪表盘总览</el-menu-item>
          <el-menu-item index="/admin/stat-occupancy">入住统计</el-menu-item>
          <el-menu-item index="/admin/stat-hygiene">卫生统计</el-menu-item>
          <el-menu-item index="/admin/stat-repair">报修统计</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="base">
          <template #title><el-icon><OfficeBuilding /></el-icon><span>基础数据</span></template>
          <el-menu-item index="/admin/students">学生/班级管理</el-menu-item>
          <el-menu-item index="/admin/buildings">楼栋管理</el-menu-item>
          <el-menu-item index="/admin/rooms">房间管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="stay">
          <template #title><el-icon><HomeFilled /></el-icon><span>住宿业务</span></template>
          <el-menu-item index="/admin/checkin">入住登记</el-menu-item>
          <el-menu-item index="/admin/checkout-audit">退宿处理</el-menu-item>
          <el-menu-item index="/admin/checkin-record">入住记录</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="daily">
          <template #title><el-icon><Notebook /></el-icon><span>日常管理</span></template>
          <el-menu-item index="/admin/hygiene-list">卫生检查</el-menu-item>
          <el-menu-item index="/admin/repair-list">报修管理</el-menu-item>
          <el-menu-item index="/admin/repair-type">报修类型</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="sys">
          <template #title><el-icon><Setting /></el-icon><span>系统设置</span></template>
          <el-menu-item index="/admin/settings">系统参数/字典</el-menu-item>
          <el-menu-item index="/admin/profile">个人中心</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="path">{{ $route.meta.title }}</div>
        <div class="right">
          <el-dropdown @command="onCommand">
            <span class="user"><el-icon><UserFilled /></el-icon>{{ userStore.name }}</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getSystemParams } from '@/api/settings'

const router = useRouter()
const userStore = useUserStore()

// 侧边栏品牌名读取系统参数，由全局设置动态配置
const systemName = ref('宿舍管理系统')
onMounted(async () => {
  try {
    const list = await getSystemParams()
    systemName.value = (list.find((x) => x.key === 'systemName') || {}).value || systemName.value
  } catch (e) { /* 加载失败使用默认文案 */ }
})

function onCommand(cmd) {
  if (cmd === 'profile') router.push('/admin/profile')
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout { height: 100%; }
.aside {
  background: var(--d-sb-glass);
  box-shadow: 2px 0 12px rgba(16, 24, 40, 0.08);
}
.logo {
  height: 60px; display: flex; align-items: center; justify-content: center; gap: 8px;
  color: #fff; font-size: 16px; font-weight: 700; letter-spacing: .5px;
}
.logo-mark {
  width: 28px; height: 28px; border-radius: 8px; background: var(--d-primary);
  color: #fff; font-size: 14px; display: flex; align-items: center; justify-content: center;
}
.header {
  display: flex; align-items: center; justify-content: space-between;
  background: var(--d-surface); border-bottom: 1px solid var(--d-border); height: 60px;
}
.path { font-size: 16px; font-weight: 800; color: var(--d-ink); }
.user { cursor: pointer; display: inline-flex; align-items: center; gap: 6px; color: var(--d-ink); }
.main { background: var(--d-bg); padding: 16px; }
.header :deep(.el-dropdown) { color: var(--d-ink); }
</style>