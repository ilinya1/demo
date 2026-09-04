import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/preview',
    name: 'preview',
    component: () => import('@/views/PreviewDemo.vue'),
    meta: { title: 'UI 方案预览' }
  },
  {
    path: '/',
    redirect: '/admin/dashboard'
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { role: 'ADMIN', title: '管理员端' },
    children: [
      {
        path: 'dashboard',
        name: 'admin-dashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '仪表盘总览' }
      },
      {
        path: 'stat-occupancy',
        name: 'admin-stat-occupancy',
        component: () => import('@/views/admin/StatOccupancy.vue'),
        meta: { title: '入住统计' }
      },
      {
        path: 'stat-hygiene',
        name: 'admin-stat-hygiene',
        component: () => import('@/views/admin/StatHygiene.vue'),
        meta: { title: '卫生统计' }
      },
      {
        path: 'stat-repair',
        name: 'admin-stat-repair',
        component: () => import('@/views/admin/StatRepair.vue'),
        meta: { title: '报修统计' }
      },
      {
        path: 'students',
        name: 'admin-students',
        component: () => import('@/views/admin/StudentList.vue'),
        meta: { title: '学生/班级管理' }
      },
      {
        path: 'buildings',
        name: 'admin-buildings',
        component: () => import('@/views/admin/BuildingList.vue'),
        meta: { title: '楼栋管理' }
      },
      {
        path: 'rooms',
        name: 'admin-rooms',
        component: () => import('@/views/admin/RoomList.vue'),
        meta: { title: '房间管理' }
      },
      {
        path: 'checkin',
        name: 'admin-checkin',
        component: () => import('@/views/admin/Checkin.vue'),
        meta: { title: '入住登记' }
      },
      {
        path: 'checkout-audit',
        name: 'admin-checkout-audit',
        component: () => import('@/views/admin/CheckoutAudit.vue'),
        meta: { title: '退宿处理' }
      },
      {
        path: 'checkin-record',
        name: 'admin-checkin-record',
        component: () => import('@/views/admin/CheckinRecord.vue'),
        meta: { title: '入住记录' }
      },
      {
        path: 'hygiene-list',
        name: 'admin-hygiene-list',
        component: () => import('@/views/admin/HygieneList.vue'),
        meta: { title: '卫生检查' }
      },
      {
        path: 'hygiene-add',
        name: 'admin-hygiene-add',
        component: () => import('@/views/admin/HygieneAdd.vue'),
        meta: { title: '新增卫生检查' }
      },
      {
        path: 'repair-list',
        name: 'admin-repair-list',
        component: () => import('@/views/admin/RepairList.vue'),
        meta: { title: '报修管理' }
      },
      {
        path: 'settings',
        name: 'admin-settings',
        component: () => import('@/views/admin/Settings.vue'),
        meta: { title: '系统设置' }
      },
      {
        path: 'repair-type',
        name: 'admin-repair-type',
        component: () => import('@/views/admin/RepairType.vue'),
        meta: { title: '报修类型' }
      },
      {
        path: 'profile',
        name: 'admin-profile',
        component: () => import('@/views/admin/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  },
  {
    path: '/student',
    component: () => import('@/layouts/StudentLayout.vue'),
    redirect: '/student/my-room',
    meta: { role: 'STUDENT', title: '学生端' },
    children: [
      {
        path: 'my-room',
        name: 'student-my-room',
        component: () => import('@/views/student/MyRoom.vue'),
        meta: { title: '我的入住信息' }
      },
      {
        path: 'my-hygiene',
        name: 'student-my-hygiene',
        component: () => import('@/views/student/MyHygiene.vue'),
        meta: { title: '我的卫生检查' }
      },
      {
        path: 'checkout-apply',
        name: 'student-checkout-apply',
        component: () => import('@/views/student/CheckoutApply.vue'),
        meta: { title: '退宿申请' }
      },
      {
        path: 'repair-add',
        name: 'student-repair-add',
        component: () => import('@/views/student/RepairAdd.vue'),
        meta: { title: '提交报修' }
      },
      {
        path: 'my-repair',
        name: 'student-my-repair',
        component: () => import('@/views/student/MyRepair.vue'),
        meta: { title: '报修进度' }
      },
      {
        path: 'profile',
        name: 'student-profile',
        component: () => import('@/views/student/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫：校验登录与角色
router.beforeEach((to, from, next) => {
  const user = useUserStore()
  if (to.path === '/login' || to.path === '/preview') {
    next()
    return
  }
  if (!user.token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  // 校验目标区域角色
  const targetRole = to.matched.find((r) => r.meta.role)?.meta.role
  if (targetRole && targetRole !== user.role) {
    next(user.role === 'ADMIN' ? '/admin/dashboard' : '/student/my-room')
    return
  }
  next()
})

export default router