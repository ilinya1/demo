package com.gzlg.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzlg.dorm.common.exception.BizException;
import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.dto.StudentReq;
import com.gzlg.dorm.entity.Clazz;
import com.gzlg.dorm.entity.Student;
import com.gzlg.dorm.mapper.ClazzMapper;
import com.gzlg.dorm.mapper.StudentMapper;
import com.gzlg.dorm.service.StudentService;
import com.gzlg.dorm.vo.StudentVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学生服务实现：学号唯一、班级(className→classId)校验；在住学生禁止删除。
 */
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;
    private final ClazzMapper clazzMapper;

    public StudentServiceImpl(StudentMapper studentMapper, ClazzMapper clazzMapper) {
        this.studentMapper = studentMapper;
        this.clazzMapper = clazzMapper;
    }

    @Override
    public PageResult<StudentVO> page(String studentId, String name, String college,
                                      String academicStatus, String className, int page, int pageSize) {
        Long classId = null;
        if (className != null && !className.isBlank()) {
            Clazz c = clazzMapper.selectOne(Wrappers.<Clazz>lambdaQuery().eq(Clazz::getClassName, className));
            classId = c == null ? -1L : c.getId(); // 查不到则返回空
        }
        Page<Student> p = new Page<>(page, pageSize);
        studentMapper.selectPage(p, Wrappers.<Student>lambdaQuery()
                .like(studentId != null && !studentId.isBlank(), Student::getStudentId, studentId)
                .like(name != null && !name.isBlank(), Student::getName, name)
                .eq(college != null && !college.isBlank(), Student::getCollege, college)
                .eq(academicStatus != null && !academicStatus.isBlank(), Student::getAcademicStatus, academicStatus)
                .eq(classId != null, Student::getClassId, classId)
                .orderByAsc(Student::getStudentId));
        return PageResult.of(toVOList(p.getRecords()), p.getTotal());
    }

    @Override
    public StudentVO getByStudentId(String studentId) {
        Student s = studentMapper.selectById(studentId);
        if (s == null) {
            throw new BizException("学生不存在");
        }
        return toVO(s);
    }

    @Override
    public void create(StudentReq req) {
        if (studentMapper.selectById(req.getStudentId()) != null) {
            throw new BizException("学号已存在");
        }
        Long classId = resolveClassId(req.getClassName());
        Student s = new Student();
        s.setStudentId(req.getStudentId());
        apply(s, req, classId);
        studentMapper.insert(s);
    }

    @Override
    public void update(String studentId, StudentReq req) {
        Student exist = studentMapper.selectById(studentId);
        if (exist == null) {
            throw new BizException("学生不存在");
        }
        Long classId = resolveClassId(req.getClassName());
        apply(exist, req, classId);
        studentMapper.updateById(exist);
    }

    @Override
    public void delete(String studentId) {
        Student exist = studentMapper.selectById(studentId);
        if (exist == null) {
            throw new BizException("学生不存在");
        }
        if ("在住".equals(exist.getHousingStatus())) {
            throw new BizException("该学生当前在住，无法删除，请先办理退宿");
        }
        studentMapper.deleteById(studentId);
    }

    private Long resolveClassId(String className) {
        if (className == null || className.isBlank()) {
            throw new BizException("请选择班级");
        }
        Clazz c = clazzMapper.selectOne(Wrappers.<Clazz>lambdaQuery().eq(Clazz::getClassName, className));
        if (c == null) {
            throw new BizException("所选班级不存在");
        }
        return c.getId();
    }

    private void apply(Student s, StudentReq req, Long classId) {
        s.setName(req.getName());
        s.setGender(req.getGender());
        s.setCollege(req.getCollege());
        s.setMajor(req.getMajor());
        s.setClassId(classId);
        s.setContactPhone(req.getContactPhone());
        s.setEmergencyContact(req.getEmergencyContact());
        s.setEmergencyPhone(req.getEmergencyPhone());
        s.setAcademicStatus(req.getAcademicStatus());
        s.setHousingStatus(req.getHousingStatus());
    }

    private List<StudentVO> toVOList(List<Student> students) {
        Map<Long, String> idName = clazzMapper.selectList(null).stream()
                .collect(Collectors.toMap(Clazz::getId, Clazz::getClassName));
        return students.stream().map(s -> toVO(s, idName)).toList();
    }

    private StudentVO toVO(Student s) {
        return toVO(s, clazzMapper.selectList(null).stream()
                .collect(Collectors.toMap(Clazz::getId, Clazz::getClassName)));
    }

    private StudentVO toVO(Student s, Map<Long, String> classNameById) {
        StudentVO vo = new StudentVO();
        vo.setStudentId(s.getStudentId());
        vo.setName(s.getName());
        vo.setGender(s.getGender());
        vo.setCollege(s.getCollege());
        vo.setMajor(s.getMajor());
        vo.setClassId(s.getClassId());
        vo.setClassName(classNameById.get(s.getClassId()));
        vo.setContactPhone(s.getContactPhone());
        vo.setEmergencyContact(s.getEmergencyContact());
        vo.setEmergencyPhone(s.getEmergencyPhone());
        vo.setAcademicStatus(s.getAcademicStatus());
        vo.setHousingStatus(s.getHousingStatus());
        return vo;
    }
}