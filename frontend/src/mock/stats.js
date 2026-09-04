// 统计报表 mock（并入仪表盘子菜单：入住 / 卫生 / 报修）
import { ok } from './util'

export function statOccupancy() {
  return ok({
    cards: { totalBeds: 3870, occupiedBeds: 3348, rate: 0.865 },
    byBuilding: [
      { building: '1号楼', total: 650, occupied: 550, rate: 0.846 },
      { building: '2号楼', total: 650, occupied: 620, rate: 0.954 },
      { building: '3号楼', total: 650, occupied: 580, rate: 0.892 },
      { building: '4号楼', total: 650, occupied: 490, rate: 0.754 },
      { building: '5号楼', total: 650, occupied: 610, rate: 0.938 },
      { building: '6号楼', total: 620, occupied: 498, rate: 0.803 }
    ],
    trend: [
      { month: '4月', checkIn: 132, checkOut: 96 },
      { month: '5月', checkIn: 118, checkOut: 104 },
      { month: '6月', checkIn: 96, checkOut: 120 },
      { month: '7月', checkIn: 62, checkOut: 88 },
      { month: '8月', checkIn: 145, checkOut: 72 },
      { month: '9月', checkIn: 128, checkOut: 60 }
    ]
  })
}

export function statHygiene() {
  return ok({
    cards: { total: 110, excellent: 42, pass: 61, fail: 7, avg: 85.5 },
    byWeek: [
      { week: '第1周', avg: 82 },
      { week: '第2周', avg: 85 },
      { week: '第3周', avg: 88 },
      { week: '第4周', avg: 87 }
    ],
    statusDist: [
      { name: '优秀', value: 42 },
      { name: '合格', value: 61 },
      { name: '不合格', value: 7 }
    ]
  })
}

export function statRepair() {
  return ok({
    cards: { total: 156, done: 148, rate: 0.949 },
    byType: [
      { type: '水电', count: 68 },
      { type: '家具', count: 45 },
      { type: '门窗', count: 24 },
      { type: '其他', count: 19 }
    ],
    trend: [
      { month: '4月', submit: 22, done: 18 },
      { month: '5月', submit: 26, done: 25 },
      { month: '6月', submit: 30, done: 28 },
      { month: '7月', submit: 18, done: 17 },
      { month: '8月', submit: 14, done: 13 },
      { month: '9月', submit: 46, done: 47 }
    ]
  })
}