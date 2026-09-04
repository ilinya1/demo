import request from './request'

/** 仪表盘统计卡片：GET /api/dashboard/stats -> {studentCount,buildingCount,roomCount,occupancyRate} */
export function getDashboardStats() {
  return request({ url: '/dashboard/stats' })
}

/** 各楼栋入住率：GET /api/dashboard/building-occupancy -> [{building,rate}] */
export function getBuildingOccupancy() {
  return request({ url: '/dashboard/building-occupancy' })
}

/** 近 4 周卫生平均分：GET /api/dashboard/hygiene-trend -> [{week,score}] */
export function getHygieneTrend() {
  return request({ url: '/dashboard/hygiene-trend' })
}

export default { getDashboardStats, getBuildingOccupancy, getHygieneTrend }