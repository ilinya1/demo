package com.gzlg.dorm.controller;

import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.common.result.Result;
import com.gzlg.dorm.dto.StudentReq;
import com.gzlg.dorm.service.StudentService;
import com.gzlg.dorm.vo.StudentVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学生管理接口。
 */
@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public Result<PageResult<StudentVO>> page(@RequestParam(required = false) String studentId,
                                              @RequestParam(required = false) String name,
                                              @RequestParam(required = false) String college,
                                              @RequestParam(required = false) String academicStatus,
                                              @RequestParam(required = false) String className,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(studentService.page(studentId, name, college, academicStatus, className, page, pageSize));
    }

    @PostMapping
    public Result<Void> create(@RequestBody @Valid StudentReq req) {
        studentService.create(req);
        return Result.ok();
    }

    @PutMapping("/{studentId}")
    public Result<Void> update(@PathVariable String studentId, @RequestBody @Valid StudentReq req) {
        studentService.update(studentId, req);
        return Result.ok();
    }

    @DeleteMapping("/{studentId}")
    public Result<Void> delete(@PathVariable String studentId) {
        studentService.delete(studentId);
        return Result.ok();
    }

    /** 学生单条（入住登记带出学生信息用） */
    @GetMapping("/{studentId}")
    public Result<StudentVO> get(@PathVariable String studentId) {
        return Result.ok(studentService.getByStudentId(studentId));
    }
}