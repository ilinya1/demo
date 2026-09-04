import request from './request'

/** 卫生检查：GET /api/daily/hygiene?page=..&pageSize=..&checkDate=..&buildingId=..&result=.. */
export function getHygieneList(params) {
  return request({ url: '/daily/hygiene', method: 'get', params })
}
/** 新增卫生检查：POST /api/daily/hygiene */
export function addHygiene(data) {
  return request({ url: '/daily/hygiene', method: 'post', data })
}

/** 报修类型字典：GET /api/daily/repair-types */
export function getRepairTypes() {
  return request({ url: '/daily/repair-types', method: 'get' })
}
/** 新增报修类型：POST /api/daily/repair-types {name,sort} */
export function createRepairType(data) {
  return request({ url: '/daily/repair-types', method: 'post', data })
}
/** 修改报修类型：PUT /api/daily/repair-types/{id} */
export function updateRepairType(id, data) {
  return request({ url: `/daily/repair-types/${id}`, method: 'put', data })
}
/** 删除报修类型：DELETE /api/daily/repair-types/{id} */
export function deleteRepairType(id) {
  return request({ url: `/daily/repair-types/${id}`, method: 'delete' })
}
/** 报修列表：GET /api/daily/repairs?page=..&pageSize=..&orderNo=..&buildingId=..&status=..&studentId=.. */
export function getRepairList(params) {
  return request({ url: '/daily/repairs', method: 'get', params })
}
/** 提交报修：POST /api/daily/repairs {studentId, roomId, typeId, description, contactPhone, images} */
export function addRepair(data) {
  return request({ url: '/daily/repairs', method: 'post', data })
}
/** 报修详情：GET /api/daily/repair/{id} */
export function getRepairDetail(id) {
  return request({ url: `/daily/repair/${id}`, method: 'get' })
}
/** 处理报修：PUT /api/daily/repair/{id} {handlerName, handlerPhone, status, handleDesc} */
export function handleRepair(id, data) {
  return request({ url: `/daily/repair/${id}`, method: 'put', data })
}

export default {
  getHygieneList, addHygiene, getRepairTypes, createRepairType, updateRepairType, deleteRepairType,
  getRepairList, getRepairDetail, handleRepair
}