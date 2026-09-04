// 全局设置与个人中心 mock（系统参数 / 字典 / 个人资料 / 修改密码）
// 数据字段对齐后续 sys_parameter / dict 表契约；前端 camelCase 展示。
import { ok, fail } from './util'
import { getStudents } from './baseData'
import { getLoginUsers } from './authData'
import { checkoutReasonInUse } from './checkin'

// ---- 系统参数 ----
// defaultSysParams 为内置默认值（幂等），sysParams 为可变更的工作副本
const defaultSysParams = [
  { key: 'systemName', name: '系统名称', value: '学生宿舍管理系统' },
  { key: 'welcomeMessage', name: '登录欢迎语', value: '欢迎使用学生宿舍管理系统' },
  { key: 'contactPhone', name: '联系电话', value: '0571-88888888' },
  { key: 'contactEmail', name: '联系邮箱', value: 'dorm@example.edu.cn' }
]
const sysParams = defaultSysParams.map((x) => ({ ...x }))

export function getSystemParams() {
  return ok(sysParams.map((x) => ({ ...x })))
}

export function updateSystemParams(d) {
  const items = Array.isArray(d) ? d : (d.list || [])
  if (!items.length) return fail('参数不能为空')
  items.forEach((it) => {
    const p = sysParams.find((x) => x.key === it.key)
    if (p) p.value = it.value ?? ''
  })
  return ok(null)
}

/** 恢复系统参数为内置默认值 */
export function resetSystemParams() {
  defaultSysParams.forEach((d) => {
    const p = sysParams.find((x) => x.key === d.key)
    if (p) p.value = d.value
  })
  return ok(null)
}

// ---- 退宿原因字典 ----
let reasonSeq = 100
const checkoutReasons = [
  { id: 1, name: '毕业离校', sort: 1 },
  { id: 2, name: '休学', sort: 2 },
  { id: 3, name: '退学', sort: 3 },
  { id: 4, name: '调宿', sort: 4 },
  { id: 5, name: '其他', sort: 99 }
]

export function getCheckoutReasons() {
  return ok([...checkoutReasons].sort((a, b) => a.sort - b.sort))
}

export function createCheckoutReason(d) {
  const name = (d.name || '').trim()
  if (!name) return fail('请输入原因名称')
  if (checkoutReasons.some((x) => x.name === name)) return fail('该退宿原因已存在')
  checkoutReasons.push({ id: ++reasonSeq, name, sort: Number(d.sort == null ? 99 : d.sort) })
  return ok(null)
}

export function updateCheckoutReason(id, d) {
  const r = checkoutReasons.find((x) => x.id === Number(id))
  if (!r) return fail('退宿原因不存在')
  if (checkoutReasons.some((x) => x.id !== Number(id) && x.name === d.name)) return fail('该退宿原因已存在')
  r.name = (d.name || '').trim()
  r.sort = Number(d.sort == null ? r.sort : d.sort)
  return ok(null)
}

export function deleteCheckoutReason(id) {
  const idx = checkoutReasons.findIndex((x) => x.id === Number(id))
  if (idx === -1) return fail('退宿原因不存在')
  if (checkoutReasonInUse(checkoutReasons[idx].name)) return fail('该原因已被退宿申请引用，无法删除')
  checkoutReasons.splice(idx, 1)
  return ok(null)
}

// ---- 个人资料 ----
export function getProfile(role, username) {
  if (role === 'ADMIN') {
    const u = getLoginUsers().find((x) => x.username === username)
    if (!u) return fail('账号不存在')
    return ok({
      username: u.username, name: u.name, role: 'ADMIN', roleName: '管理员',
      phone: u.phone || '', email: u.email || ''
    })
  }
  const stu = getStudents({ pageSize: 100 }).data.list.find((s) => s.studentId === username)
  if (!stu) return fail('学生不存在')
  return ok({
    username: stu.studentId, name: stu.name, role: 'STUDENT', roleName: '学生',
    gender: stu.gender, college: stu.college, major: stu.major, className: stu.className,
    phone: stu.contactPhone, emergency: stu.emergencyContact,
    academicStatus: stu.academicStatus, housingStatus: stu.housingStatus
  })
}

export function updateProfile(role, username, d) {
  if (role === 'ADMIN') {
    const u = getLoginUsers().find((x) => x.username === username)
    if (!u) return fail('账号不存在')
    if (d.phone !== undefined) u.phone = d.phone
    if (d.email !== undefined) u.email = d.email
    return ok(null)
  }
  const stu = getStudents({ pageSize: 100 }).data.list.find((s) => s.studentId === username)
  if (!stu) return fail('学生不存在')
  if (d.phone !== undefined) stu.contactPhone = d.phone
  if (d.emergency !== undefined) stu.emergencyContact = d.emergency
  return ok(null)
}

// ---- 修改密码 ----
export function changePassword(username, oldPassword, newPassword) {
  const u = getLoginUsers().find((x) => x.username === username)
  if (!u) return fail('账号不存在')
  if (oldPassword !== u.password) return fail('原密码错误')
  if (!newPassword || newPassword.length < 6 || newPassword.length > 20) return fail('新密码长度应为 6-20 位')
  u.password = newPassword
  return ok(null)
}