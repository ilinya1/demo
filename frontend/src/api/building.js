import request from './request'

/** 楼栋列表：GET /api/buildings?page&pageSize&buildingName&manager -> {list,total} */
export function getBuildings(params) {
  return request({ url: '/buildings', method: 'get', params })
}
export function addBuilding(data) {
  return request({ url: '/buildings', method: 'post', data })
}
export function updateBuilding(id, data) {
  return request({ url: `/buildings/${id}`, method: 'put', data })
}
export function deleteBuilding(id) {
  return request({ url: `/buildings/${id}`, method: 'delete' })
}

export default { getBuildings, addBuilding, updateBuilding, deleteBuilding }