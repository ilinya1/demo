// 基础数据 mock（班级 / 学生 / 楼栋 / 房间 / 床位）
// 字段对齐开发设计文档接口契约与 v4 数据库；前端用 camelCase 展示。
import { ok } from './util'

// ---- 班级 ----
const classes = [
  { id: 1, name: '软工2301', college: '计算机学院', major: '软件工程', grade: 2023, headTeacher: '刘艳梅', studentCount: 42, boardingCount: 40 },
  { id: 2, name: '软工2302', college: '计算机学院', major: '软件工程', grade: 2023, headTeacher: '李敏', studentCount: 40, boardingCount: 38 },
  { id: 3, name: '计科2301', college: '计算机学院', major: '计算机科学与技术', grade: 2023, headTeacher: '王芳', studentCount: 45, boardingCount: 44 },
  { id: 4, name: '机设2301', college: '机械工程学院', major: '机械设计制造及其自动化', grade: 2023, headTeacher: '张建国', studentCount: 38, boardingCount: 36 },
  { id: 5, name: '英语2201', college: '外国语学院', major: '英语', grade: 2022, headTeacher: '赵艳', studentCount: 30, boardingCount: 18 }
]

// ---- 学院（字典，支持新增/编辑/删除，供班级与学生弹窗下拉）----
const colleges = [
  { id: 1, name: '计算机学院' },
  { id: 2, name: '机械工程学院' },
  { id: 3, name: '外国语学院' }
]

// ---- 学生 ----
function classIdOf(className) {
  const c = classes.find((x) => x.name === className)
  return c ? c.id : null
}
const students = [
  { studentId: '2023010101', name: '王小明', gender: '男', college: '计算机学院', major: '软件工程', classId: 1, className: '软工2301', contactPhone: '13800001234', emergencyContact: '王建国', emergencyPhone: '13911110001', academicStatus: '在校', housingStatus: '在住' },
  { studentId: '2023010102', name: '李小红', gender: '女', college: '计算机学院', major: '软件工程', classId: 1, className: '软工2301', contactPhone: '13900005678', emergencyContact: '李国强', emergencyPhone: '13911110002', academicStatus: '在校', housingStatus: '在住' },
  { studentId: '2023010201', name: '张小飞', gender: '男', college: '计算机学院', major: '软件工程', classId: 2, className: '软工2302', contactPhone: '13700009012', emergencyContact: '张贵', emergencyPhone: '13911110003', academicStatus: '在校', housingStatus: '在住' },
  { studentId: '2023010301', name: '陈雨萱', gender: '女', college: '计算机学院', major: '计算机科学与技术', classId: 3, className: '计科2301', contactPhone: '13500007890', emergencyContact: '陈城', emergencyPhone: '13911110004', academicStatus: '在校', housingStatus: '在住' },
  { studentId: '2023010105', name: '王凯', gender: '男', college: '计算机学院', major: '软件工程', classId: 1, className: '软工2301', contactPhone: '13300002223', emergencyContact: '王大山', emergencyPhone: '13911110005', academicStatus: '在校', housingStatus: '在住' },
  { studentId: '2023020101', name: '刘少军', gender: '男', college: '机械工程学院', major: '机械设计制造及其自动化', classId: 4, className: '机设2301', contactPhone: '13600003456', emergencyContact: '刘军', emergencyPhone: '13911110006', academicStatus: '在校', housingStatus: '未住' },
  { studentId: '2023020102', name: '李娜', gender: '女', college: '机械工程学院', major: '机械设计制造及其自动化', classId: 4, className: '机设2301', contactPhone: '13200003334', emergencyContact: '李平', emergencyPhone: '13911110007', academicStatus: '在校', housingStatus: '未住' },
  { studentId: '2023010401', name: '周杰', gender: '男', college: '机械工程学院', major: '机械设计制造及其自动化', classId: 4, className: '机设2301', contactPhone: '13100004445', emergencyContact: '周涛', emergencyPhone: '13911110008', academicStatus: '在校', housingStatus: '在住' },
  { studentId: '2022010101', name: '赵敏', gender: '女', college: '外国语学院', major: '英语', classId: 5, className: '英语2201', contactPhone: '13400001112', emergencyContact: '赵梦', emergencyPhone: '13911110009', academicStatus: '毕业', housingStatus: '已退宿' },
  { studentId: '2022010203', name: '孙悦', gender: '女', college: '外国语学院', major: '英语', classId: 5, className: '英语2201', contactPhone: '13000005556', emergencyContact: '孙红', emergencyPhone: '13911110010', academicStatus: '毕业', housingStatus: '已退宿' }
]

// ---- 楼栋 ----
const buildings = [
  { id: 1, buildingName: '1号楼', floorCount: 6, roomCount: 120, manager: '张建国' },
  { id: 2, buildingName: '2号楼', floorCount: 6, roomCount: 120, manager: '李明' },
  { id: 3, buildingName: '3号楼', floorCount: 6, roomCount: 120, manager: '王芳' },
  { id: 4, buildingName: '4号楼', floorCount: 6, roomCount: 120, manager: '刘洋' },
  { id: 5, buildingName: '5号楼', floorCount: 6, roomCount: 120, manager: '陈静' },
  { id: 6, buildingName: '6号楼', floorCount: 6, roomCount: 120, manager: '赵磊' }
]

const ROOM_TYPES = ['四人间', '六人间'] // 对齐 init.sql 种子与数据库设计说明 3.5
const roomTypeOf = (capacity) => (Number(capacity) >= 6 ? '六人间' : '四人间')

// ---- 房间 ----
const rooms = [
  { id: 1, buildingId: 1, floor: 1, roomNo: '101', capacity: 4, roomType: '四人间', status: '部分入住', occupiedCount: 2 },
  { id: 2, buildingId: 1, floor: 1, roomNo: '102', capacity: 4, roomType: '四人间', status: '部分入住', occupiedCount: 3 },
  { id: 3, buildingId: 1, floor: 1, roomNo: '103', capacity: 4, roomType: '四人间', status: '已满', occupiedCount: 4 },
  { id: 4, buildingId: 1, floor: 1, roomNo: '104', capacity: 6, roomType: '六人间', status: '空闲', occupiedCount: 0 },
  { id: 5, buildingId: 2, floor: 2, roomNo: '201', capacity: 4, roomType: '四人间', status: '已满', occupiedCount: 4 },
  { id: 6, buildingId: 2, floor: 2, roomNo: '202', capacity: 6, roomType: '六人间', status: '部分入住', occupiedCount: 2 },
  { id: 7, buildingId: 2, floor: 2, roomNo: '203', capacity: 6, roomType: '六人间', status: '已满', occupiedCount: 6 },
  { id: 8, buildingId: 3, floor: 3, roomNo: '301', capacity: 4, roomType: '四人间', status: '部分入住', occupiedCount: 1 },
  { id: 9, buildingId: 3, floor: 3, roomNo: '302', capacity: 6, roomType: '六人间', status: '部分入住', occupiedCount: 3 }
]

const roomStatusOf = (occupied, capacity) => {
  if (occupied === 0) return '空闲'
  if (occupied >= capacity) return '已满'
  return '部分入住'
}

// ---- 分页辅助 ----
function page(list, params = {}) {
  const page = Number(params.page || 1)
  const pageSize = Number(params.pageSize || 10)
  const start = (page - 1) * pageSize
  return { list: list.slice(start, start + pageSize), total: list.length }
}

function cloneNameByRoom(room) {
  const b = buildings.find((x) => x.id === room.buildingId)
  return b ? b.buildingName : null
}

// ============ 学生 ============
export function getStudents(params = {}) {
  let list = students.filter((s) => {
    if (params.studentId && !s.studentId.includes(params.studentId)) return false
    if (params.name && !s.name.includes(params.name)) return false
    if (params.college && s.college !== params.college) return false
    if (params.academicStatus && s.academicStatus !== params.academicStatus) return false
    if (params.className && s.className !== params.className) return false
    return true
  })
  return ok(page(list, params))
}
export function createStudent(d) {
  if (students.some((s) => s.studentId === d.studentId)) return { code: 1, msg: '学号已存在' }
  students.unshift({ ...d, classId: classIdOf(d.className) })
  return ok(null)
}
export function updateStudent(id, d) {
  const i = students.findIndex((s) => s.studentId === id)
  if (i === -1) return { code: 1, msg: '学生不存在' }
  students[i] = { ...students[i], ...d, studentId: id, classId: (d.className != null ? classIdOf(d.className) : null) || students[i].classId }
  return ok(null)
}
export function deleteStudent(id) {
  const i = students.findIndex((s) => s.studentId === id)
  if (i === -1) return { code: 1, msg: '学生不存在' }
  // 联动释放该生占用的床位（若在住），并重算对应房间占用
  Object.keys(bedStudents).forEach((roomId) => {
    const bedIdx = bedStudents[roomId]
    const bedNo = Object.keys(bedIdx).find((b) => bedIdx[b] === id)
    if (bedNo) freeBed(Number(roomId), Number(bedNo), id)
  })
  students.splice(i, 1)
  return ok(null)
}

// ============ 班级 ============
export function getClasses(params = {}) {
  let list = classes.filter((c) => {
    if (params.grade && String(c.grade) !== String(params.grade)) return false
    if (params.college && c.college !== params.college) return false
    if (params.name && !c.name.includes(params.name)) return false
    return true
  })
  return ok(page(list, params))
}
export function createClass(d) {
  if (classes.some((c) => c.name === d.name)) return { code: 1, msg: '班级名称已存在' }
  classes.push({ ...d, id: Date.now() })
  return ok(null)
}
export function updateClass(id, d) {
  const i = classes.findIndex((c) => c.id === Number(id))
  if (i === -1) return { code: 1, msg: '班级不存在' }
  const old = classes[i]
  // 班级改名时校验重名，并级联更新该班学生归属（class-学生以 name 为逻辑关联键，student.className 为可更新字段）
  if (d.name && d.name !== old.name) {
    if (classes.some((c) => c.id !== Number(id) && c.name === d.name)) return { code: 1, msg: '班级名称已存在' }
    students.forEach((s) => { if (s.className === old.name) s.className = d.name })
  }
  classes[i] = { ...old, ...d, id: Number(id) }
  return ok(null)
}
export function deleteClass(id) {
  const i = classes.findIndex((c) => c.id === Number(id))
  if (i === -1) return { code: 1, msg: '班级不存在' }
  if (students.some((s) => s.className === classes[i].name)) return { code: 1, msg: '该班级下仍有学生，无法删除' }
  classes.splice(i, 1)
  return ok(null)
}

// ============ 学院（字典 CRUD，对齐报修类型/退宿原因引用校验约定） ============
export function getColleges() {
  return ok(colleges.map((x) => ({ ...x })))
}
export function createCollege(d) {
  const name = (d.name || '').trim()
  if (!name) return { code: 1, msg: '请输入学院名称' }
  if (colleges.some((x) => x.name === name)) return { code: 1, msg: '该学院已存在' }
  colleges.push({ id: Math.max(0, ...colleges.map((x) => x.id)) + 1, name })
  return ok(null)
}
export function updateCollege(id, d) {
  const t = colleges.find((x) => x.id === Number(id))
  if (!t) return { code: 1, msg: '学院不存在' }
  const name = (d.name || '').trim()
  if (!name) return { code: 1, msg: '请输入学院名称' }
  if (colleges.some((x) => x.id !== Number(id) && x.name === name)) return { code: 1, msg: '该学院已存在' }
  // 改名级联更新该学院下的班级与学生（class/student 以 college 字符串为关联键）
  if (name !== t.name) {
    classes.forEach((c) => { if (c.college === t.name) c.college = name })
    students.forEach((s) => { if (s.college === t.name) s.college = name })
  }
  t.name = name
  return ok(null)
}
export function deleteCollege(id) {
  const idx = colleges.findIndex((x) => x.id === Number(id))
  if (idx === -1) return { code: 1, msg: '学院不存在' }
  const name = colleges[idx].name
  if (classes.some((c) => c.college === name) || students.some((s) => s.college === name)) {
    return { code: 1, msg: '该学院下仍有班级或学生，无法删除' }
  }
  colleges.splice(idx, 1)
  return ok(null)
}

// ============ 楼栋 ============
export function getBuildings(params = {}) {
  let list = buildings.filter((b) => {
    if (params.buildingName && !b.buildingName.includes(params.buildingName)) return false
    if (params.manager && !b.manager.includes(params.manager)) return false
    return true
  })
  return ok(page(list, params))
}
export function createBuilding(d) {
  if (buildings.some((b) => b.buildingName === d.buildingName)) return { code: 1, msg: '楼栋名已存在' }
  buildings.push({ ...d, id: Date.now() })
  return ok(null)
}
export function updateBuilding(id, d) {
  const i = buildings.findIndex((b) => b.id === Number(id))
  if (i === -1) return { code: 1, msg: '楼栋不存在' }
  buildings[i] = { ...buildings[i], ...d, id: Number(id) }
  return ok(null)
}
export function deleteBuilding(id) {
  const i = buildings.findIndex((b) => b.id === Number(id))
  if (i === -1) return { code: 1, msg: '楼栋不存在' }
  buildings.splice(i, 1)
  return ok(null)
}

// ============ 房间 ============
export function getRooms(params = {}) {
  let list = rooms.map((r) => ({ ...r, buildingName: cloneNameByRoom(r) })).filter((r) => {
    if (params.buildingId && r.buildingId !== Number(params.buildingId)) return false
    if (params.roomNo && !r.roomNo.includes(params.roomNo)) return false
    if (params.status && r.status !== params.status) return false
    if (params.roomType && r.roomType !== params.roomType) return false
    return true
  })
  return ok(page(list, params))
}
const roomFloorOf = (roomNo) => Number(String(roomNo || '').charAt(0)) || 1 // 由房号首位推导楼层，如 102→1
export function createRoom(d) {
  const b = buildings.find((x) => x.id === Number(d.buildingId))
  if (rooms.some((r) => r.buildingId === Number(d.buildingId) && r.roomNo === d.roomNo)) return { code: 1, msg: '该楼栋房间号已存在' }
  rooms.push({
    id: Date.now(),
    buildingId: Number(d.buildingId),
    floor: Number(d.floor || roomFloorOf(d.roomNo)),
    roomNo: d.roomNo,
    capacity: Number(d.capacity),
    roomType: roomTypeOf(d.capacity),
    status: '空闲',
    occupiedCount: 0,
    buildingName: b ? b.buildingName : ''
  })
  return ok(null)
}
export function updateRoom(id, d) {
  const i = rooms.findIndex((r) => r.id === Number(id))
  if (i === -1) return { code: 1, msg: '房间不存在' }
  rooms[i] = {
    ...rooms[i],
    buildingId: Number(d.buildingId),
    floor: Number(d.floor || roomFloorOf(d.roomNo)),
    roomNo: d.roomNo,
    capacity: Number(d.capacity),
    roomType: roomTypeOf(d.capacity),
    status: roomStatusOf(rooms[i].occupiedCount, Number(d.capacity))
  }
  return ok(null)
}
export function deleteRoom(id) {
  const i = rooms.findIndex((r) => r.id === Number(id))
  if (i === -1) return { code: 1, msg: '房间不存在' }
  rooms.splice(i, 1)
  return ok(null)
}
export function roomOptions() {
  return ok({ buildings: buildings.map((b) => ({ id: b.id, buildingName: b.buildingName })), types: ROOM_TYPES })
}

// ============ 床位 ============
// 床位占用以「在住」入住记录为唯一权威源：在 checkin.js 初始化后调用
// initBedsFromRecords() 重建快照，保证 床位/房间占用/入住记录 三者一致，
// 避免删除在住学生时联动释放因快照缺失而失效。
let bedStudents = {}
const bedStudentsSeed = {
  2: { 1: '2023010101', 2: '2023010102', 3: '2023010105', 4: '2023010401' }, // 102室 4 床（与原型 room-detail 在住一致）
  3: { 1: '2023010301' }, // 103室 1 床
  5: { 1: '2023010201' } // 201室 1 床
}
// 用权威 seed 初始化床位快照，并同步各房间占用数/状态（对齐 checkInRecords 在住记录）
bedStudents = JSON.parse(JSON.stringify(bedStudentsSeed))
Object.keys(bedStudents).forEach((rid) => recomputeRoomOccupancy(rid))
function buildBeds(roomId) {
  const room = rooms.find((r) => r.id === Number(roomId))
  if (!room) return []
  const occupied = bedStudents[roomId] || {}
  return Array.from({ length: room.capacity }, (_, idx) => {
    const bedNo = idx + 1
    const sid = occupied[bedNo]
    const st = sid ? students.find((s) => s.studentId === sid) : null
    return {
      bedId: `${roomId}-${bedNo}`,
      bedNo,
      status: st ? '占用' : '空闲',
      studentId: st ? st.studentId : null,
      studentName: st ? st.name : null
    }
  })
}
export function getBeds(roomId) {
  return ok(buildBeds(roomId))
}

// ============ 住宿操作联动（入住 / 退宿 Service 在 mock 侧的等价物） ============
function recomputeRoomOccupancy(roomId) {
  const r = rooms.find((x) => x.id === Number(roomId))
  if (!r) return
  const bedIdx = bedStudents[roomId] || {}
  r.occupiedCount = Object.values(bedIdx).filter(Boolean).length
  r.status = roomStatusOf(r.occupiedCount, r.capacity)
}

/** 更新学生住宿状态（冗余字段，与 check_in 联动） */
export function updateStudentHousing(studentId, housingStatus) {
  const s = students.find((x) => x.studentId === studentId)
  if (s) s.housingStatus = housingStatus
}

/** 占用床位：把 studentId 安排到 roomId 的 bedNo（若该生已在别床则先释放） */
export function occupyBed(roomId, bedNo, studentId) {
  const bedIdx = bedStudents[roomId] || (bedStudents[roomId] = {})
  const prev = Object.keys(bedIdx).find((k) => bedIdx[k] === studentId)
  if (prev) delete bedIdx[prev]
  bedIdx[bedNo] = studentId
  recomputeRoomOccupancy(roomId)
}

/** 释放床位：studentId 从 roomId 的 bedNo 迁出 */
export function freeBed(roomId, bedNo, studentId) {
  const bedIdx = bedStudents[roomId] || {}
  if (bedIdx[bedNo] === studentId) delete bedIdx[bedNo]
  recomputeRoomOccupancy(roomId)
}