package com.gzlg.dorm.controller;

import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.common.result.Result;
import com.gzlg.dorm.dto.ClazzReq;
import com.gzlg.dorm.service.ClazzService;
import com.gzlg.dorm.vo.ClazzVO;
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
 * 班级管理接口。
 */
@RestController
@RequestMapping("/classes")
public class ClazzController {

    private final ClazzService clazzService;

    public ClazzController(ClazzService clazzService) {
        this.clazzService = clazzService;
    }

    @GetMapping
    public Result<PageResult<ClazzVO>> page(@RequestParam(required = false) String name,
                                            @RequestParam(required = false) String college,
                                            @RequestParam(required = false) String grade,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(clazzService.page(name, college, grade, page, pageSize));
    }

    @PostMapping
    public Result<Void> create(@RequestBody @Valid ClazzReq req) {
        clazzService.create(req);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid ClazzReq req) {
        clazzService.update(id, req);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        clazzService.delete(id);
        return Result.ok();
    }
}