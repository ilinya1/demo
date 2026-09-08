package com.gzlg.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzlg.dorm.common.exception.BizException;
import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.dto.ClazzReq;
import com.gzlg.dorm.entity.Clazz;
import com.gzlg.dorm.entity.Student;
import com.gzlg.dorm.mapper.ClazzMapper;
import com.gzlg.dorm.mapper.StudentMapper;
import com.gzlg.dorm.service.ClazzService;
import com.gzlg.dorm.vo.ClazzVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 班级服务实现：重名校验、删除时班内有学生则拦截。
 * 注：后端学生用 classId 关联，班级改名无需级联学生（与前端 mock 的 className 快照不同）。
 */
@Service
public class ClazzServiceImpl implements ClazzService {

    private final ClazzMapper clazzMapper;
    private final StudentMapper studentMapper;

    public ClazzServiceImpl(ClazzMapper clazzMapper, StudentMapper studentMapper) {
        this.clazzMapper = clazzMapper;
        this.studentMapper = studentMapper;
    }

    @Override
    public PageResult<ClazzVO> page(String name, String college, String grade, int page, int pageSize) {
        Page<Clazz> p = new Page<>(page, pageSize);
        clazzMapper.selectPage(p, Wrappers.<Clazz>lambdaQuery()
                .like(name != null && !name.isBlank(), Clazz::getClassName, name)
                .eq(college != null && !college.isBlank(), Clazz::getCollege, college)
                .eq(grade != null && !grade.isBlank(), Clazz::getGrade, grade)
                .orderByAsc(Clazz::getId));
        List<ClazzVO> list = p.getRecords().stream().map(c -> {
            ClazzVO vo = new ClazzVO();
            vo.setId(c.getId());
            vo.setName(c.getClassName());
            vo.setCollege(c.getCollege());
            vo.setMajor(c.getMajor());
            vo.setGrade(c.getGrade());
            vo.setHeadTeacher(c.getHeadTeacher());
            vo.setStudentCount(studentMapper.selectCount(Wrappers.<Student>lambdaQuery()
                    .eq(Student::getClassId, c.getId())));
            vo.setBoardingCount(studentMapper.selectCount(Wrappers.<Student>lambdaQuery()
                    .eq(Student::getClassId, c.getId())
                    .eq(Student::getHousingStatus, "在住")));
            return vo;
        }).toList();
        return PageResult.of(list, p.getTotal());
    }

    @Override
    public void create(ClazzReq req) {
        String name = req.getName().trim();
        if (clazzMapper.selectCount(Wrappers.<Clazz>lambdaQuery().eq(Clazz::getClassName, name)) > 0) {
            throw new BizException("班级名称已存在");
        }
        Clazz c = new Clazz();
        c.setClassName(name);
        c.setCollege(req.getCollege());
        c.setMajor(req.getMajor());
        c.setGrade(req.getGrade());
        c.setHeadTeacher(req.getHeadTeacher());
        clazzMapper.insert(c);
    }

    @Override
    public void update(Long id, ClazzReq req) {
        Clazz exist = clazzMapper.selectById(id);
        if (exist == null) {
            throw new BizException("班级不存在");
        }
        String name = req.getName().trim();
        if (!name.equals(exist.getClassName())
                && clazzMapper.selectCount(Wrappers.<Clazz>lambdaQuery()
                        .eq(Clazz::getClassName, name).ne(Clazz::getId, id)) > 0) {
            throw new BizException("班级名称已存在");
        }
        exist.setClassName(name);
        exist.setCollege(req.getCollege());
        exist.setMajor(req.getMajor());
        exist.setGrade(req.getGrade());
        exist.setHeadTeacher(req.getHeadTeacher());
        clazzMapper.updateById(exist);
    }

    @Override
    public void delete(Long id) {
        Clazz exist = clazzMapper.selectById(id);
        if (exist == null) {
            throw new BizException("班级不存在");
        }
        if (studentMapper.selectCount(Wrappers.<Student>lambdaQuery().eq(Student::getClassId, id)) > 0) {
            throw new BizException("该班级下仍有学生，无法删除");
        }
        clazzMapper.deleteById(id);
    }
}