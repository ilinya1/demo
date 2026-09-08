package com.gzlg.dorm.controller;

import com.gzlg.dorm.common.result.Result;
import com.gzlg.dorm.dto.CollegeReq;
import com.gzlg.dorm.entity.TCollege;
import com.gzlg.dorm.service.CollegeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学院管理接口（GET 返回数组对齐前端契约）。
 */
@RestController
@RequestMapping("/colleges")
public class CollegeController {

    private final CollegeService collegeService;

    public CollegeController(CollegeService collegeService) {
        this.collegeService = collegeService;
    }

    @GetMapping
    public Result<List<TCollege>> list() {
        return Result.ok(collegeService.list());
    }

    @PostMapping
    public Result<Void> create(@RequestBody @Valid CollegeReq req) {
        collegeService.create(req);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid CollegeReq req) {
        collegeService.update(id, req);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        collegeService.delete(id);
        return Result.ok();
    }
}