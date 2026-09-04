// 住宿业务 mock（入住登记 / 退宿处理 / 入住记录）
// 与 baseData 联动：入住/退宿会更新学生住宿状态与房间床位占用，保证各页面演示一致。
import { ok, fail } from './util'
import { getStudents, getRooms, getBeds, occupyBed, freeBed, updateStudentHousing, getBuildings } from './baseData'

function now() {
  const d = new Date()
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`
}
function findStudent(studentId) {
  return getStudents({ studentId, pageSize: 100 }).data.list.find((s) => s.studentId === studentId) || null
}
function roomById(id) {
  return getRooms({ pageSize: 100 }).data.list.find((r) => r.id === Number(id)) || null
}

// ---- 入住记录 ----
let seq = 100
let checkInRecords = [
  { id: 1, studentId: '2023010101', studentName: '王小明', className: '软工2301', buildingName: '1号楼', roomId: 2, roomNo: '102', bedNo: 1, checkInTime: '2023-09-01', checkOutTime: null, source: 'apply', status: '在住', remark: '' },
  { id: 2, studentId: '2023010102', studentName: '李小红', className: '软工2301', buildingName: '1号楼', roomId: 2, roomNo: '102', bedNo: 2, checkInTime: '2023-09-01', checkOutTime: null, source: 'apply', status: '在住', remark: '' },
  { id: 3, studentId: '2023010105', studentName: '王凯', className: '软工2301', buildingName: '1号楼', roomId: 2, roomNo: '102', bedNo: 3, checkInTime: '2023-09-01', checkOutTime: null, source: 'apply', status: '在住', remark: '' },
  { id: 4, studentId: '2023010301', studentName: '陈雨萱', className: '计科2301', buildingName: '1号楼', roomId: 3, roomNo: '103', bedNo: 1, checkInTime: '2023-09-01', checkOutTime: null, source: 'apply', status: '在住', remark: '' },
  { id: 5, studentId: '2023010401', studentName: '周杰', className: '机设2301', buildingName: '1号楼', roomId: 2, roomNo: '102', bedNo: 4, checkInTime: '2024-09-02', checkOutTime: null, source: 'manual', status: '在住', remark: '' },
  { id: 6, studentId: '2023010201', studentName: '张小飞', className: '软工2302', buildingName: '2号楼', roomId: 5, roomNo: '201', bedNo: 1, checkInTime: '2023-09-01', checkOutTime: null, source: 'apply', status: '在住', remark: '' },
  { id: 7, studentId: '2022010101', studentName: '赵敏', className: '英语2201', buildingName: '3号楼', roomId: 8, roomNo: '301', bedNo: 1, checkInTime: '2022-09-01', checkOutTime: '2026-06-30', source: 'apply', status: '已退宿', remark: '毕业离校' },
  { id: 8, studentId: '2022010203', studentName: '孙悦', className: '英语2201', buildingName: '3号楼', roomId: 9, roomNo: '302', bedNo: 2, checkInTime: '2022-09-01', checkOutTime: '2026-06-30', source: 'apply', status: '已退宿', remark: '毕业离校' }
]

// ---- 退宿申请 ----
let checkoutApplications = [
  { id: 1, applyNo: 'TS20260902001', studentId: '2023010101', studentName: '王小明', college: '计算机学院', buildingName: '1号楼', roomNo: '102', bedNo: 1, roomId: 2, reason: '毕业离校', planDate: '2026-09-10', description: '即将毕业离校，计划9月10日办理退宿交接。', status: '待审核', rejectReason: '', createTime: '2026-09-02 14:20', auditTime: null },
  { id: 2, applyNo: 'TS20260902003', studentId: '2023010301', studentName: '陈雨萱', college: '计算机学院', buildingName: '1号楼', roomNo: '103', bedNo: 1, roomId: 3, reason: '调宿', planDate: '2026-09-12', description: '申请调宿至其它楼栋。', status: '待审核', rejectReason: '', createTime: '2026-09-02 09:05', auditTime: null },
  { id: 3, applyNo: 'TS20260820001', studentId: '2022010101', studentName: '赵敏', college: '外国语学院', buildingName: '3号楼', roomNo: '301', bedNo: 1, roomId: 8, reason: '毕业离校', planDate: '2026-08-22', description: '毕业离校办理退宿。', status: '已通过', rejectReason: '', createTime: '2026-08-20 09:10', auditTime: '2026-08-21 10:00' }
]

function oneStudent(studentId) {
  const s = findStudent(studentId)
  if (!s) return fail('学生不存在')
  return ok(s)
}
// 学生单条（入住 / 直接退宿带出）
export function getStudent(studentId) {
  return oneStudent(decodeURIComponent(studentId))
}

// 入住用：该楼栋有剩余床位的房间
export function checkinRooms(buildingId) {
  const list = getRooms({ buildingId, pageSize: 100 }).data.list
    .filter((r) => r.occupiedCount < r.capacity)
    .map((r) => ({ id: r.id, roomNo: r.roomNo, freeBeds: r.capacity - r.occupiedCount }))
  return ok(list)
}

// 入住用：房间空闲床位号
export function checkinFreeBeds(roomId) {
  const beds = getBeds(roomId)
  const free = beds.data.filter((b) => b.status === '空闲').map((b) => b.bedNo)
  return ok(free)
}

// 入住登记
export function submitCheckin(d) {
  const stu = findStudent(d.studentId)
  if (!stu) return fail('学生不存在')
  if (stu.housingStatus === '在住') return fail('该学生当前已在住，不能重复入住')
  const room = roomById(d.roomId)
  if (!room) return fail('房间不存在')
  const beds = getBeds(d.roomId)
  const bed = beds.data.find((b) => b.bedNo === Number(d.bedNo))
  if (!bed || bed.status !== '空闲') return fail('该床位已被占用，请重新选择')
  checkInRecords.unshift({
    id: ++seq,
    studentId: stu.studentId,
    studentName: stu.name,
    className: stu.className,
    buildingName: room.buildingName,
    roomId: room.id,
    roomNo: room.roomNo,
    bedNo: Number(d.bedNo),
    checkInTime: d.checkInDate || now().slice(0, 10),
    checkOutTime: null,
    source: 'manual',
    status: '在住',
    remark: d.remark || ''
  })
  occupyBed(room.id, d.bedNo, stu.studentId)
  updateStudentHousing(stu.studentId, '在住')
  return ok(null)
}

// 入住记录列表
export function listCheckinRecords(params = {}) {
  let list = checkInRecords.filter((r) => {
    if (params.studentId && !r.studentId.includes(params.studentId)) return false
    if (params.studentName && !r.studentName.includes(params.studentName)) return false
    if (params.buildingName && r.buildingName !== params.buildingName) return false
    if (params.status && r.status !== params.status) return false
    return true
  })
  return ok(page(list, params))
}

// 退宿申请列表
export function listCheckoutApps(params = {}) {
  let list = checkoutApplications.filter((a) => {
    if (params.applyNo && !a.applyNo.includes(params.applyNo)) return false
    if (params.studentId && !a.studentId.includes(params.studentId)) return false
    if (params.status && a.status !== params.status) return false
    return true
  })
  return ok(page(list, params))
}

function doCheckout(record, checkoutDate, source) {
  record.status = '已退宿'
  record.checkOutTime = checkoutDate || now().slice(0, 10)
  record.source = source
  freeBed(record.roomId, record.bedNo, record.studentId)
  updateStudentHousing(record.studentId, '已退宿')
}

// 退宿申请审核
export function auditCheckoutApp(id, d) {
  const app = checkoutApplications.find((a) => a.id === Number(id))
  if (!app) return fail('申请不存在')
  if (d.approve) {
    app.status = '已通过'
    app.auditTime = now()
    const rec = checkInRecords.find((r) => r.studentId === app.studentId && r.status === '在住')
    if (rec) doCheckout(rec, app.planDate, 'apply')
  } else {
    app.status = '已驳回'
    app.rejectReason = d.rejectReason || ''
    app.auditTime = now()
  }
  return ok(null)
}

// 直接退宿（跳过申请）
export function directCheckout(d) {
  const stu = findStudent(d.studentId)
  if (!stu) return fail('学生不存在')
  if (stu.housingStatus !== '在住') return fail('该学生当前不在住')
  const rec = checkInRecords.find((r) => r.studentId === stu.studentId && r.status === '在住')
  if (rec) doCheckout(rec, d.checkoutDate, 'direct')
  return ok(null)
}

function page(list, params = {}) {
  const p = Number(params.page || 1)
  const size = Number(params.pageSize || 10)
  const start = (p - 1) * size
  return { list: list.slice(start, start + size), total: list.length }
}

// 供楼栋下拉（退宿/入住复用一键）
export function buildingOptions() {
  return ok(getBuildings({ pageSize: 100 }).data.list.map((b) => ({ id: b.id, buildingName: b.buildingName })))
}

function currentRecord(studentId) {
  return checkInRecords.find((r) => r.studentId === studentId && r.status === '在住') || null
}

// ---- 学生端 · 我的宿舍 ----
// 返回当前在住信息 + 室友；未入住时 dorm 为 null
export function currentRoom(studentId) {
  const stu = findStudent(studentId)
  if (!stu) return fail('学生不存在')
  const rec = currentRecord(studentId)
  if (!rec) return ok({ student: { studentId: stu.studentId, name: stu.name, gender: stu.gender, className: stu.className }, dorm: null, roommates: [] })
  const roommates = checkInRecords
    .filter((r) => r.roomId === rec.roomId && r.status === '在住' && r.studentId !== studentId)
    .map((r) => ({ studentId: r.studentId, name: r.studentName, bedNo: r.bedNo }))
  return ok({
    student: { studentId: stu.studentId, name: stu.name, gender: stu.gender, className: stu.className },
    dorm: { buildingName: rec.buildingName, roomNo: rec.roomNo, bedNo: rec.bedNo, roomId: rec.roomId, checkInTime: rec.checkInTime },
    roommates
  })
}

// ---- 学生端 · 提交退宿申请 ----
export function submitCheckoutApply(studentId, d) {
  const stu = findStudent(studentId)
  if (!stu) return fail('学生不存在')
  const rec = currentRecord(studentId)
  if (!rec) return fail('当前不在住，无需办理退宿')
  if (!d.reason || !d.planDate) return fail('请填写退宿原因与计划退宿日期')
  if (checkoutApplications.some((a) => a.studentId === studentId && a.status === '待审核')) {
    return fail('您有未审核的退宿申请，请等待审核')
  }
  const date = now().slice(0, 10).replace(/-/g, '')
  checkoutApplications.unshift({
    id: ++seq,
    applyNo: `TS${date}${String(checkoutApplications.length + 1).padStart(3, '0')}`,
    studentId: stu.studentId,
    studentName: stu.name,
    college: stu.college,
    buildingName: rec.buildingName,
    roomNo: rec.roomNo,
    bedNo: rec.bedNo,
    roomId: rec.roomId,
    reason: d.reason,
    planDate: d.planDate,
    description: d.description || '',
    status: '待审核',
    rejectReason: '',
    createTime: now(),
    auditTime: null
  })
  return ok(null)
}

// 供退宿原因字典删除时校验引用（对齐报修类型的引用校验约定）
export function checkoutReasonInUse(name) {
  return checkoutApplications.some((a) => a.reason === name)
}

// ---- 学生端 · 撤销待审核申请（撤销即删除该申请，不新增枚举）----
export function cancelCheckoutApp(studentId, id) {
  const idx = checkoutApplications.findIndex((a) => a.id === Number(id))
  if (idx === -1) return fail('申请不存在')
  if (checkoutApplications[idx].studentId !== studentId) return fail('无权操作该申请')
  if (checkoutApplications[idx].status !== '待审核') return fail('仅待审核状态的申请可撤销')
  checkoutApplications.splice(idx, 1)
  return ok(null)
}