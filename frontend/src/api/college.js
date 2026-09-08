import request from './request'

/** 学院列表：GET /api/colleges -> [{id,name}] */
export function getColleges() {
  return request({ url: '/colleges', method: 'get' })
}
/** 新增学院：POST /api/colleges {name} */
export function createCollege(data) {
  return request({ url: '/colleges', method: 'post', data })
}
/** 编辑学院（改名会级联班级/学生）：PUT /api/colleges/{id} {name} */
export function updateCollege(id, data) {
  return request({ url: `/colleges/${id}`, method: 'put', data })
}
/** 删除学院（被班级/学生引用时拦截）：DELETE /api/colleges/{id} */
export function deleteCollege(id) {
  return request({ url: `/colleges/${id}`, method: 'delete' })
}

export default { getColleges, createCollege, updateCollege, deleteCollege }