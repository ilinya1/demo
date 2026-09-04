import request from './request'

/** 班级列表（分页 + 搜索）：GET /api/classes?page&pageSize&grade&college&name -> {list,total} */
export function getClasses(params) {
  return request({ url: '/classes', method: 'get', params })
}
export function addClass(data) {
  return request({ url: '/classes', method: 'post', data })
}
export function updateClass(id, data) {
  return request({ url: `/classes/${id}`, method: 'put', data })
}
export function deleteClass(id) {
  return request({ url: `/classes/${id}`, method: 'delete' })
}

export default { getClasses, addClass, updateClass, deleteClass }