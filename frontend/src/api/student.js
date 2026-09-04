import request from './request'

/** 学生列表（分页 + 搜索）：GET /api/students?page&pageSize&studentId&name&college&academicStatus -> {list,total} */
export function getStudents(params) {
  return request({ url: '/students', method: 'get', params })
}

/** 新增学生：POST /api/students -> data=null */
export function addStudent(data) {
  return request({ url: '/students', method: 'post', data })
}

/** 更新学生：PUT /api/students/{studentId} -> data=null */
export function updateStudent(studentId, data) {
  return request({ url: `/students/${encodeURIComponent(studentId)}`, method: 'put', data })
}

/** 删除学生：DELETE /api/students/{studentId} -> data=null */
export function deleteStudent(studentId) {
  return request({ url: `/students/${encodeURIComponent(studentId)}`, method: 'delete' })
}

export default { getStudents, addStudent, updateStudent, deleteStudent }