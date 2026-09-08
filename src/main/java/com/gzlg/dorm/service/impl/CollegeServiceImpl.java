package com.gzlg.dorm.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gzlg.dorm.common.exception.BizException;
import com.gzlg.dorm.dto.CollegeReq;
import com.gzlg.dorm.entity.Clazz;
import com.gzlg.dorm.entity.Student;
import com.gzlg.dorm.entity.TCollege;
import com.gzlg.dorm.mapper.ClazzMapper;
import com.gzlg.dorm.mapper.StudentMapper;
import com.gzlg.dorm.mapper.TCollegeMapper;
import com.gzlg.dorm.service.CollegeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学院字典实现：重名校验、改名级联 class.college/student.college、删除前引用拦截。
 */
@Service
public class CollegeServiceImpl implements CollegeService {

    private final TCollegeMapper collegeMapper;
    private final ClazzMapper clazzMapper;
    private final StudentMapper studentMapper;

    public CollegeServiceImpl(TCollegeMapper collegeMapper, ClazzMapper clazzMapper, StudentMapper studentMapper) {
        this.collegeMapper = collegeMapper;
        this.clazzMapper = clazzMapper;
        this.studentMapper = studentMapper;
    }

    @Override
    public List<TCollege> list() {
        return collegeMapper.selectList(Wrappers.<TCollege>lambdaQuery()
                .orderByAsc(TCollege::getSort).orderByAsc(TCollege::getId));
    }

    @Override
    public void create(CollegeReq req) {
        String name = req.getName().trim();
        if (collegeMapper.selectCount(Wrappers.<TCollege>lambdaQuery().eq(TCollege::getName, name)) > 0) {
            throw new BizException("该学院已存在");
        }
        TCollege c = new TCollege();
        c.setName(name);
        c.setSort(req.getSort() == null ? 0 : req.getSort());
        collegeMapper.insert(c);
    }

    @Override
    public void update(Long id, CollegeReq req) {
        TCollege exist = collegeMapper.selectById(id);
        if (exist == null) {
            throw new BizException("学院不存在");
        }
        String name = req.getName().trim();
        if (!name.equals(exist.getName())
                && collegeMapper.selectCount(Wrappers.<TCollege>lambdaQuery()
                        .eq(TCollege::getName, name).ne(TCollege::getId, id)) > 0) {
            throw new BizException("该学院已存在");
        }
        String old = exist.getName();
        exist.setName(name);
        exist.setSort(req.getSort() == null ? exist.getSort() : req.getSort());
        collegeMapper.updateById(exist);
        if (!name.equals(old)) {
            clazzMapper.update(null, new LambdaUpdateWrapper<Clazz>()
                    .eq(Clazz::getCollege, old).set(Clazz::getCollege, name));
            studentMapper.update(null, new LambdaUpdateWrapper<Student>()
                    .eq(Student::getCollege, old).set(Student::getCollege, name));
        }
    }

    @Override
    public void delete(Long id) {
        TCollege exist = collegeMapper.selectById(id);
        if (exist == null) {
            throw new BizException("学院不存在");
        }
        String name = exist.getName();
        if (clazzMapper.selectCount(Wrappers.<Clazz>lambdaQuery().eq(Clazz::getCollege, name)) > 0
                || studentMapper.selectCount(Wrappers.<Student>lambdaQuery().eq(Student::getCollege, name)) > 0) {
            throw new BizException("该学院下仍有班级或学生，无法删除");
        }
        collegeMapper.deleteById(id);
    }
}