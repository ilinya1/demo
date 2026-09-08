// 登录账号数据（独立模块，避免 mock 模块间环引用：settings 需读写账号，index 需登录分发）
import { ok, fail } from './util'

// ---- 登录示例用户（与初始化演示账号一致）----
const users = [
  { username: 'admin', password: '123456', role: 'ADMIN', name: '系统管理员', status: 1 },
  { username: '2023010101', password: '123456', role: 'STUDENT', name: '王小明', status: 1 },
  { username: '2023010102', password: '123456', role: 'STUDENT', name: '李小红', status: 1 }
]

// 供 settings（个人资料 / 改密）读取并修改当前账号（返回同源数组，改动即持久化到登录数据）
export function getLoginUsers() { return users }

// 管理员重置学生密码的默认密码
export const DEFAULT_STUDENT_PASSWORD = '123456'

// 管理员重置某学生密码为默认值；若该生暂无账号则自动创建（保证重置后可用默认密码登录）
export function resetStudentPassword(username, name) {
  let u = users.find((x) => x.username === username)
  if (!u) {
    users.push({ username, password: DEFAULT_STUDENT_PASSWORD, role: 'STUDENT', name: name || username, status: 1 })
  } else {
    u.password = DEFAULT_STUDENT_PASSWORD
    if (name) u.name = name
  }
  return ok(null)
}

// ---- 普通身份 token（简化，仅 mock 用）----
let tokenSeq = 0

export function handleLogin(payload) {
  const u = users.find((x) => x.username === payload.username && x.password === payload.password)
  if (!u) return fail('账号或密码错误')
  tokenSeq += 1
  const user = { role: u.role, name: u.name, username: u.username }
  if (u.role === 'STUDENT') user.studentId = u.username
  return ok({ token: `mock-token-${tokenSeq}`, user })
}