// 日常管理 mock（卫生检查 / 报修管理）
// 字段对齐 init.sql 的 hygiene_record / repair_order / repair_type。
import { ok, fail } from './util'
import { getBuildings, getRooms, getStudents } from './baseData'

const now = () => {
  const d = new Date()
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`
}
const buildingById = (id) => getBuildings({ pageSize: 100 }).data.list.find((b) => b.id === Number(id)) || null
const roomById = (id) => getRooms({ pageSize: 100 }).data.list.find((r) => r.id === Number(id)) || null
const studentById = (sid) => getStudents({ pageSize: 100 }).data.list.find((s) => s.studentId === sid) || null
const rid = Number(1e10 * Math.random()).toString(36).toUpperCase() // mock

// 报修类型字典（对齐 init.sql）
const repairTypes = [
  { id: 1, name: '灯管', sort: 1 },
  { id: 2, name: '水龙头', sort: 2 },
  { id: 3, name: '空调', sort: 3 },
  { id: 4, name: '门锁', sort: 4 },
  { id: 5, name: '床铺', sort: 5 },
  { id: 6, name: '桌椅', sort: 6 },
  { id: 7, name: '其他', sort: 99 }
]
export function getRepairTypes() {
  return ok([...repairTypes].sort((a, b) => a.sort - b.sort))
}
export function createRepairType(d) {
  const name = (d.name || '').trim()
  if (!name) return fail('请输入类型名称')
  if (repairTypes.some((x) => x.name === name)) return fail('该报修类型已存在')
  repairTypes.push({ id: Math.max(0, ...repairTypes.map((x) => x.id)) + 1, name, sort: Number(d.sort == null ? 99 : d.sort) })
  return ok(null)
}
export function updateRepairType(id, d) {
  const t = repairTypes.find((x) => x.id === Number(id))
  if (!t) return fail('报修类型不存在')
  const name = (d.name || '').trim()
  if (!name) return fail('请输入类型名称')
  if (repairTypes.some((x) => x.id !== Number(id) && x.name === name)) return fail('该报修类型已存在')
  t.name = name
  t.sort = Number(d.sort == null ? t.sort : d.sort)
  return ok(null)
}
export function deleteRepairType(id) {
  const idx = repairTypes.findIndex((x) => x.id === Number(id))
  if (idx === -1) return fail('报修类型不存在')
  if (repairOrders.some((o) => o.typeId === Number(id))) return fail('该类型已被报修单引用，无法删除')
  repairTypes.splice(idx, 1)
  return ok(null)
}

// 卫生照片占位图（data URI，避免引外部网络图）
const PLACEHOLDER = 'data:image/svg+xml;utf8,' + encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="120" height="80">' +
  '<rect width="120" height="80" fill="#eef2f7"/>' +
  '<text x="60" y="44" font-size="12" fill="#8a94a6" text-anchor="middle">宿舍检查照片</text></svg>'
)

// ---- 卫生检查记录 ----
let hygieneSeq = 100
let hygieneRecords = [
  { id: 1, checkDate: '2026-09-01', checker: '张宿管', buildingId: 1, roomId: 2, score: 92, result: '优秀', deductItems: [], photos: [PLACEHOLDER], comment: '整体整洁，继续保持' },
  { id: 2, checkDate: '2026-08-25', checker: '张宿管', buildingId: 1, roomId: 2, score: 73, result: '合格', deductItems: ['地面不干净', '垃圾未倒'], photos: [], comment: '地面有杂物，注意清扫' },
  { id: 3, checkDate: '2026-08-20', checker: '李宿管', buildingId: 2, roomId: 4, score: 55, result: '不合格', deductItems: ['地面不干净', '被子未叠', '违规电器'], photos: [PLACEHOLDER], comment: '发现违规电器，已责令整改' },
  { id: 4, checkDate: '2026-08-15', checker: '张宿管', buildingId: 1, roomId: 3, score: 85, result: '合格', deductItems: ['物品摆放凌乱'], photos: [], comment: '物品摆放需规整' },
  { id: 5, checkDate: '2026-08-02', checker: '李宿管', buildingId: 2, roomId: 5, score: 95, result: '优秀', deductItems: [], photos: [], comment: '' }
]

function decorateHygiene(r) {
  const room = roomById(r.roomId)
  return {
    ...r,
    buildingName: buildingById(r.buildingId)?.buildingName || '-',
    roomNo: room ? room.roomNo : '-'
  }
}

export function listHygiene({ checkDate, buildingId, roomId, result, page = 1, pageSize = 10 } = {}) {
  let rows = [...hygieneRecords]
  if (checkDate) rows = rows.filter((r) => r.checkDate === checkDate)
  if (buildingId) rows = rows.filter((r) => r.buildingId === Number(buildingId))
  if (roomId) rows = rows.filter((r) => r.roomId === Number(roomId))
  if (result) rows = rows.filter((r) => r.result === result)
  rows.sort((a, b) => (a.checkDate + String(a.id)).localeCompare(b.checkDate + String(b.id)) * -1)
  const total = rows.length
  const start = (page - 1) * pageSize
  return ok({ list: rows.slice(start, start + pageSize).map(decorateHygiene), total })
}

export function addHygiene(d) {
  if (!d.checkDate || !d.checker || !d.buildingId || !d.roomId) return fail('请完善楼栋、房间、检查人与检查日期')
  if (d.score === undefined || d.score === null || d.score === '') return fail('请确定评分（100 分起扣）')
  const score = Number(d.score)
  const result = score >= 90 ? '优秀' : score >= 60 ? '合格' : '不合格'
  const deduct = Array.isArray(d.deductItems) ? d.deductItems : []
  const photos = Array.isArray(d.photos) ? d.photos : []
  if (score < 60 || deduct.includes('违规电器')) {
    if (photos.length === 0) return fail('评分低于 60 或涉及违规电器时，必须上传现场照片')
  }
  hygieneRecords.unshift({
    id: ++hygieneSeq, checkDate: d.checkDate, checker: d.checker,
    buildingId: Number(d.buildingId), roomId: Number(d.roomId),
    score, result, deductItems: deduct, photos, comment: d.comment || ''
  })
  return ok(null)
}

// ---- 报修 ----
let repairSeq = 100
let repairOrders = [
  { id: 1, orderNo: 'BX20260901001', studentId: '2023010101', buildingId: 1, roomId: 2, typeId: 1, description: '宿舍灯管闪烁', contactPhone: '13800001234', images: [], status: '待处理', handlerName: '', handlerPhone: '', handleDesc: '', createTime: '2026-09-01 10:30', handleTime: null },
  { id: 2, orderNo: 'BX20260901002', studentId: '2023010102', buildingId: 1, roomId: 2, typeId: 2, description: '卫生间水龙头漏水', contactPhone: '13900005678', images: [], status: '处理中', handlerName: '张宿管', handlerPhone: '13800000001', handleDesc: '已联系维修，今日处理', createTime: '2026-09-01 14:20', handleTime: '2026-09-02 09:00' },
  { id: 3, orderNo: 'BX20260831001', studentId: '2023010103', buildingId: 1, roomId: 2, typeId: 3, description: '空调不制冷', contactPhone: '13700009012', images: [], status: '已完成', handlerName: '李师傅', handlerPhone: '13800000002', handleDesc: '已加氟修复', createTime: '2026-08-31 09:15', handleTime: '2026-08-31 17:40' },
  { id: 4, orderNo: 'BX20260830001', studentId: '2023010105', buildingId: 1, roomId: 2, typeId: 4, description: '门锁损坏无法打开', contactPhone: '13300002223', images: [], status: '已完成', handlerName: '王师傅', handlerPhone: '13800000003', handleDesc: '已更换锁芯', createTime: '2026-08-30 16:40', handleTime: '2026-08-31 10:12' },
  { id: 5, orderNo: 'BX20260820001', studentId: '2023010101', buildingId: 1, roomId: 2, typeId: 6, description: '椅子松动摇晃', contactPhone: '13800001234', images: [], status: '已完成', handlerName: '王师傅', handlerPhone: '13800000003', handleDesc: '已更换椅子配件，修复牢固', createTime: '2026-08-20 15:20', handleTime: '2026-08-21 10:10' }
]

function decorateRepair(o) {
  const stu = studentById(o.studentId)
  const type = repairTypes.find((t) => t.id === o.typeId)
  const room = roomById(o.roomId)
  return {
    ...o,
    typeName: type ? type.name : '-',
    studentName: stu ? stu.name : '-',
    buildingName: buildingById(o.buildingId)?.buildingName || '-',
    roomNo: room ? room.roomNo : '-'
  }
}

export function listRepair({ orderNo, buildingId, studentId, status, page = 1, pageSize = 10 } = {}) {
  let rows = [...repairOrders]
  if (orderNo) rows = rows.filter((o) => o.orderNo.includes(orderNo))
  if (buildingId) rows = rows.filter((o) => o.buildingId === Number(buildingId))
  if (studentId) rows = rows.filter((o) => o.studentId === studentId)
  if (status) rows = rows.filter((o) => o.status === status)
  rows.sort((a, b) => b.createTime.localeCompare(a.createTime))
  const total = rows.length
  const start = (page - 1) * pageSize
  return ok({ list: rows.slice(start, start + pageSize).map(decorateRepair), total })
}

export function getRepair(id) {
  const o = repairOrders.find((x) => x.id === Number(id))
  if (!o) return fail('报修单不存在')
  return ok(decorateRepair(o))
}

export function handleRepair(id, d) {
  const o = repairOrders.find((x) => x.id === Number(id))
  if (!o) return fail('报修单不存在')
  if (!d.handlerName || !d.handlerPhone) return fail('请填写处理人与联系电话')
  const status = d.status || o.status
  if (status === '已完成' && !d.handleDesc) return fail('完成处理请填写处理说明')
  o.status = status
  o.handlerName = d.handlerName
  o.handlerPhone = d.handlerPhone
  o.handleDesc = d.handleDesc || o.handleDesc
  o.handleTime = o.handleTime || now()
  return ok(null)
}

// ---- 学生端 · 提交报修 ----
export function createRepair(d) {
  const stu = studentById(d.studentId)
  if (!stu) return fail('学生不存在')
  const room = roomById(d.roomId)
  if (!room) return fail('宿舍信息不存在')
  if (!d.typeId) return fail('请选择报修物品')
  if (!d.description || !d.description.trim()) return fail('请描述问题情况')
  const date = now().slice(0, 10).replace(/-/g, '')
  repairOrders.unshift({
    id: ++repairSeq,
    orderNo: `BX${date}${String(repairOrders.length + 1).padStart(3, '0')}`,
    studentId: stu.studentId,
    buildingId: room.buildingId,
    roomId: room.id,
    typeId: Number(d.typeId),
    description: d.description.trim(),
    contactPhone: d.contactPhone || stu.contactPhone || '',
    images: Array.isArray(d.images) ? d.images : [],
    status: '待处理',
    handlerName: '', handlerPhone: '', handleDesc: '',
    createTime: now(), handleTime: null
  })
  return ok(null)
}