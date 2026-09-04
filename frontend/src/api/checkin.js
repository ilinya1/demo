import request from './request'

/** 学生单条（带出学生信息）：GET /api/students/{studentId} */
export function getStudentById(studentId) {
  return request({ url: `/students/${studentId}`, method: 'get' })
}
/** 入住可选楼栋；GET /api/buildings/options -> [{id,buildingName}] */
export function getBuildingOptions() {
  return request({ url: '/buildings/options', method: 'get' })
}
/** 该楼栋有剩余床位的房间：GET /api/checkin/rooms?buildingId -> [{id,roomNo,freeBeds}] */
export function getCheckinRooms(buildingId) {
  return request({ url: '/checkin/rooms', method: 'get', params: { buildingId } })
}
/** 房间空闲床位号：GET /api/checkin/rooms/{id}/free-beds -> number[] */
export function getFreeBeds(roomId) {
  return request({ url: `/checkin/rooms/${roomId}/free-beds`, method: 'get' })
}
/** 办理入住：POST /api/checkin */
export function submitCheckin(data) {
  return request({ url: '/checkin', method: 'post', data })
}

/** 入住记录：GET /api/checkin-records?page&pageSize&studentId&studentName&buildingName&status -> {list,total} */
export function getCheckinRecords(params) {
  return request({ url: '/checkin-records', method: 'get', params })
}

/** 退宿申请列表：GET /api/checkout-applications?page&pageSize&applyNo&studentId&status -> {list,total} */
export function getCheckoutApps(params) {
  return request({ url: '/checkout-applications', method: 'get', params })
}
/** 退宿申请审核：POST /api/checkout-applications/{id}/audit {approve, rejectReason} */
export function auditCheckoutApp(id, data) {
  return request({ url: `/checkout-applications/${id}/audit`, method: 'post', data })
}
/** 直接退宿：POST /api/checkout/direct {studentId, checkoutDate, reason, remark} */
export function directCheckout(data) {
  return request({ url: '/checkout/direct', method: 'post', data })
}

/** 我的宿舍：GET /api/student/current-room?studentId -> {student, dorm|null, roommates[]} */
export function getCurrentRoom(studentId) {
  return request({ url: '/student/current-room', method: 'get', params: { studentId } })
}
/** 提交退宿申请：POST /api/checkout-applications {studentId, reason, planDate, description} */
export function submitCheckoutApply(data) {
  return request({ url: '/checkout-applications', method: 'post', data })
}
/** 撤销待审核退宿申请：POST /api/checkout-applications/{id}/cancel {studentId} */
export function cancelCheckoutApp(id, studentId) {
  return request({ url: `/checkout-applications/${id}/cancel`, method: 'post', data: { studentId } })
}

export default {
  getStudentById,
  getBuildingOptions,
  getCheckinRooms,
  getFreeBeds,
  submitCheckin,
  getCheckinRecords,
  getCheckoutApps,
  auditCheckoutApp,
  directCheckout
}