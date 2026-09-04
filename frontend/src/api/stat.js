import request from './request'

/** 入住统计：GET /api/stats/occupancy -> {cards, byBuilding, trend} */
export function getStatOccupancy() {
  return request({ url: '/stats/occupancy', method: 'get' })
}
/** 卫生统计：GET /api/stats/hygiene -> {cards, byWeek, statusDist} */
export function getStatHygiene() {
  return request({ url: '/stats/hygiene', method: 'get' })
}
/** 报修统计：GET /api/stats/repair -> {cards, byType, trend} */
export function getStatRepair() {
  return request({ url: '/stats/repair', method: 'get' })
}

export default { getStatOccupancy, getStatHygiene, getStatRepair }