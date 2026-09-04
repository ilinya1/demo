import request from './request'

/** 房间选项（楼栋下拉 + 房间类型）：GET /api/rooms/options -> {buildings:[{id,buildingName}],types:[...]} */
export function getRoomOptions() {
  return request({ url: '/rooms/options', method: 'get' })
}
/** 房间列表：GET /api/rooms?page&pageSize&buildingId&roomNo&status&roomType -> {list,total} */
export function getRooms(params) {
  return request({ url: '/rooms', method: 'get', params })
}
export function addRoom(data) {
  return request({ url: '/rooms', method: 'post', data })
}
export function updateRoom(id, data) {
  return request({ url: `/rooms/${id}`, method: 'put', data })
}
export function deleteRoom(id) {
  return request({ url: `/rooms/${id}`, method: 'delete' })
}
/** 房间床位分布：GET /api/rooms/{id}/beds -> [{bedId,bedNo,status,studentId,studentName}] */
export function getRoomBeds(id) {
  return request({ url: `/rooms/${id}/beds`, method: 'get' })
}

export default { getRoomOptions, getRooms, addRoom, updateRoom, deleteRoom, getRoomBeds }