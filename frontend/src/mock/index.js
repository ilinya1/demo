// 前端 mock（契约驱动，mock 阶段替代真实后端）
// 说明：数据字段严格对齐开发设计文档接口契约与 v4 数据库字段。
// 后端实现后，设置 VITE_USE_MOCK=false 即切换到真实接口。

import { ok, fail } from './util'
import { handleLogin } from './authData'
import {
  getStudents, createStudent, updateStudent, deleteStudent,
  getClasses, createClass, updateClass, deleteClass,
  getBuildings, createBuilding, updateBuilding, deleteBuilding,
  getRooms, createRoom, updateRoom, deleteRoom,
  getBeds, roomOptions
} from './baseData'
import { statOccupancy, statHygiene, statRepair } from './stats'
import { getRepairTypes, createRepairType, updateRepairType, deleteRepairType, listHygiene, addHygiene, listRepair, getRepair, handleRepair, createRepair } from './daily'
import {
  getSystemParams, updateSystemParams, resetSystemParams,
  getCheckoutReasons, createCheckoutReason, updateCheckoutReason, deleteCheckoutReason,
  getProfile, updateProfile, changePassword
} from './settings'
import {
  getStudent,
  checkinRooms,
  checkinFreeBeds,
  submitCheckin,
  listCheckinRecords,
  listCheckoutApps,
  auditCheckoutApp,
  directCheckout,
  buildingOptions,
  currentRoom,
  submitCheckoutApply,
  cancelCheckoutApp
} from './checkin'

// 认证逻辑/登录账号抽至独立 authData.js，避免模块环引用

// ---- 仪表盘统计（对齐原型数值：1286 / 6 / 1240 / 86.5%）----
function dashboardStats() {
  return ok({
    studentCount: 1286,
    buildingCount: 6,
    roomCount: 1240,
    occupancyRate: 0.865
  })
}

// ---- 各楼栋入住率 ----
function buildingOccupancy() {
  return ok([
    { building: '1号楼', rate: 0.95 },
    { building: '2号楼', rate: 0.88 },
    { building: '3号楼', rate: 0.92 },
    { building: '4号楼', rate: 0.79 },
    { building: '5号楼', rate: 0.84 },
    { building: '6号楼', rate: 0.86 }
  ])
}

// ---- 近 4 周卫生平均分 ----
function hygieneTrend() {
  return ok([
    { week: '第1周', score: 82 },
    { week: '第2周', score: 85 },
    { week: '第3周', score: 88 },
    { week: '第4周', score: 87 }
  ])
}

// ---- 路由分发 ----
export function mockHandle(method, url, params, data) {
  const p = url.replace(/^\/api/, '')
  // 认证
  if (method === 'POST' && p === '/auth/login') return handleLogin(data)
  if (method === 'POST' && p === '/auth/logout') return ok(null)
  if (method === 'POST' && p === '/auth/change-password') return changePassword(data.username, data.oldPassword, data.newPassword)

  // 个人中心 · 个人资料
  if (method === 'GET' && p === '/profile') return getProfile(params.role, params.username)
  if (method === 'PUT' && p === '/profile') return updateProfile(params.role, params.username, data)

  // 全局设置 · 系统参数
  if (method === 'GET' && p === '/settings/params') return getSystemParams()
  if (method === 'PUT' && p === '/settings/params') return updateSystemParams(data)
  if (method === 'POST' && p === '/settings/params/reset') return resetSystemParams()

  // 全局设置 · 退宿原因字典
  if (method === 'GET' && p === '/daily/checkout-reasons') return getCheckoutReasons()
  if (method === 'POST' && p === '/daily/checkout-reasons') return createCheckoutReason(data)
  if (method === 'PUT' && /^\/daily\/checkout-reasons\/\d+$/.test(p)) return updateCheckoutReason(p.replace('/daily/checkout-reasons/', ''), data)
  if (method === 'DELETE' && /^\/daily\/checkout-reasons\/\d+$/.test(p)) return deleteCheckoutReason(p.replace('/daily/checkout-reasons/', ''))
  // 仪表盘
  if (method === 'GET' && p === '/dashboard/stats') return dashboardStats()
  if (method === 'GET' && p === '/dashboard/building-occupancy') return buildingOccupancy()
  if (method === 'GET' && p === '/dashboard/hygiene-trend') return hygieneTrend()

  // 统计报表（并入仪表盘）
  if (method === 'GET' && p === '/stats/occupancy') return statOccupancy()
  if (method === 'GET' && p === '/stats/hygiene') return statHygiene()
  if (method === 'GET' && p === '/stats/repair') return statRepair()

  // 基础数据 · 学生
  if (method === 'GET' && p === '/students') return getStudents(params)
  if (method === 'POST' && p === '/students') return createStudent(data)
  if (method === 'PUT' && /^\/students\/[\s\S]+$/.test(p)) return updateStudent(decodeURIComponent(p.replace('/students/', '')), data)
  if (method === 'DELETE' && /^\/students\/[\s\S]+$/.test(p)) return deleteStudent(decodeURIComponent(p.replace('/students/', '')))
  // 学生单条（住宿业务带出学生信息）
  if (method === 'GET' && /^\/students\/[\s\S]+$/.test(p)) return getStudent(p.replace('/students/', ''))

  // 基础数据 · 班级
  if (method === 'GET' && p === '/classes') return getClasses(params)
  if (method === 'POST' && p === '/classes') return createClass(data)
  if (method === 'PUT' && /^\/classes\/\d+$/.test(p)) return updateClass(p.replace('/classes/', ''), data)
  if (method === 'DELETE' && /^\/classes\/\d+$/.test(p)) return deleteClass(p.replace('/classes/', ''))

  // 基础数据 · 楼栋
  if (method === 'GET' && p === '/buildings') return getBuildings(params)
  if (method === 'POST' && p === '/buildings') return createBuilding(data)
  if (method === 'PUT' && /^\/buildings\/\d+$/.test(p)) return updateBuilding(p.replace('/buildings/', ''), data)
  if (method === 'DELETE' && /^\/buildings\/\d+$/.test(p)) return deleteBuilding(p.replace('/buildings/', ''))

  // 基础数据 · 房间 / 床位 / 选项
  if (method === 'GET' && p === '/rooms/options') return roomOptions()
  if (method === 'GET' && p === '/rooms') return getRooms(params)
  if (method === 'POST' && p === '/rooms') return createRoom(data)
  if (method === 'PUT' && /^\/rooms\/\d+$/.test(p)) return updateRoom(p.replace('/rooms/', ''), data)
  if (method === 'DELETE' && /^\/rooms\/\d+$/.test(p)) return deleteRoom(p.replace('/rooms/', ''))
  if (method === 'GET' && /^\/rooms\/\d+\/beds$/.test(p)) return getBeds(p.match(/^\/rooms\/(\d+)\/beds$/)[1])

  // 住宿业务 · 入住登记
  if (method === 'GET' && p === '/buildings/options') return buildingOptions()
  if (method === 'GET' && p === '/checkin/rooms') return checkinRooms(params.buildingId)
  if (method === 'GET' && /^\/checkin\/rooms\/\d+\/free-beds$/.test(p)) return checkinFreeBeds(p.match(/^\/checkin\/rooms\/(\d+)\/free-beds$/)[1])
  if (method === 'POST' && p === '/checkin') return submitCheckin(data)

  // 住宿业务 · 入住记录 / 退宿申请
  if (method === 'GET' && p === '/checkin-records') return listCheckinRecords(params)
  if (method === 'GET' && p === '/checkout-applications') return listCheckoutApps(params)
  if (method === 'POST' && /^\/checkout-applications\/\d+\/audit$/.test(p)) return auditCheckoutApp(p.match(/^\/checkout-applications\/(\d+)\/audit$/)[1], data)
  if (method === 'POST' && /^\/checkout-applications\/\d+\/cancel$/.test(p)) return cancelCheckoutApp(data.studentId, p.match(/^\/checkout-applications\/(\d+)\/cancel$/)[1])
  if (method === 'POST' && p === '/checkout-applications') return submitCheckoutApply(data.studentId, data)
  if (method === 'POST' && p === '/checkout/direct') return directCheckout(data)

  // 住宿业务 · 学生端（我的宿舍）
  if (method === 'GET' && p === '/student/current-room') return currentRoom(params.studentId)

  // 日常管理 · 卫生检查
  if (method === 'GET' && p === '/daily/hygiene') return listHygiene(params)
  if (method === 'POST' && p === '/daily/hygiene') return addHygiene(data)

  // 日常管理 · 报修管理
  if (method === 'GET' && p === '/daily/repair-types') return getRepairTypes()
  if (method === 'POST' && p === '/daily/repair-types') return createRepairType(data)
  if (method === 'PUT' && /^\/daily\/repair-types\/\d+$/.test(p)) return updateRepairType(p.replace('/daily/repair-types/', ''), data)
  if (method === 'DELETE' && /^\/daily\/repair-types\/\d+$/.test(p)) return deleteRepairType(p.replace('/daily/repair-types/', ''))
  if (method === 'GET' && p === '/daily/repairs') return listRepair(params)
  if (method === 'POST' && p === '/daily/repairs') return createRepair(data)
  if (method === 'GET' && /^\/daily\/repair\/\d+$/.test(p)) return getRepair(p.match(/^\/daily\/repair\/(\d+)$/)[1])
  if (method === 'PUT' && /^\/daily\/repair\/\d+$/.test(p)) return handleRepair(p.match(/^\/daily\/repair\/(\d+)$/)[1], data)

  return fail(`mock 未实现: ${method} ${p}`)
}