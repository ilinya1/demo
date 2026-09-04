import request from './request'

// ---- 系统参数 ---
/** GET /api/settings/params */
export function getSystemParams() {
  return request({ url: '/settings/params', method: 'get' })
}
/** PUT /api/settings/params {list:[{key,name,value}]} */
export function updateSystemParams(list) {
  return request({ url: '/settings/params', method: 'put', data: { list } })
}
/** POST /api/settings/params/reset 恢复默认值 */
export function resetSystemParams() {
  return request({ url: '/settings/params/reset', method: 'post' })
}

// ---- 退宿原因字典 ----
/** GET /api/daily/checkout-reasons */
export function getCheckoutReasons() {
  return request({ url: '/daily/checkout-reasons', method: 'get' })
}
/** POST /api/daily/checkout-reasons {name,sort} */
export function createCheckoutReason(data) {
  return request({ url: '/daily/checkout-reasons', method: 'post', data })
}
/** PUT /api/daily/checkout-reasons/{id} {name,sort} */
export function updateCheckoutReason(id, data) {
  return request({ url: `/daily/checkout-reasons/${id}`, method: 'put', data })
}
/** DELETE /api/daily/checkout-reasons/{id} */
export function deleteCheckoutReason(id) {
  return request({ url: `/daily/checkout-reasons/${id}`, method: 'delete' })
}

// ---- 个人中心 ----
/** GET /api/profile?role=..&username=.. */
export function getProfile(role, username) {
  return request({ url: '/profile', method: 'get', params: { role, username } })
}
/** PUT /api/profile?role=..&username=.. {phone,emergency/email} */
export function updateProfile(role, username, data) {
  return request({ url: '/profile', method: 'put', params: { role, username }, data })
}
/** POST /api/auth/change-password {username,oldPassword,newPassword} */
export function changePassword(data) {
  return request({ url: '/auth/change-password', method: 'post', data })
}

export default {
  getSystemParams, updateSystemParams, resetSystemParams,
  getCheckoutReasons, createCheckoutReason, updateCheckoutReason, deleteCheckoutReason,
  getProfile, updateProfile, changePassword
}