import request from './request'

/** 登录：POST /api/auth/login  body: {username, password} -> {token, user} */
export function login(username, password) {
  return request({ url: '/auth/login', method: 'post', data: { username, password } })
}

/** 退出登录：POST /api/auth/logout */
export function logout() {
  return request({ url: '/auth/logout', method: 'post' })
}

export default { login, logout }