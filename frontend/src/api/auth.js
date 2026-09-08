import request from './request'

/** 登录：POST /api/auth/login  body: {username, password} -> {token, user} */
export function login(username, password) {
  return request({ url: '/auth/login', method: 'post', data: { username, password } })
}

/** 退出登录：POST /api/auth/logout */
export function logout() {
  return request({ url: '/auth/logout', method: 'post' })
}

/** 管理员重置学生密码为默认值（账号不存在则自动创建）：POST /api/auth/reset-password {username,name} */
export function resetStudentPassword(data) {
  return request({ url: '/auth/reset-password', method: 'post', data })
}

export default { login, logout, resetStudentPassword }