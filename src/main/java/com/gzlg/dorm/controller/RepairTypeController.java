package com.gzlg.dorm.controller;

import com.gzlg.dorm.common.result.Result;
import com.gzlg.dorm.dto.RepairTypeRequest;
import com.gzlg.dorm.entity.RepairType;
import com.gzlg.dorm.service.RepairTypeService;
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
 * 报修类型字典接口。
 */
@RestController
@RequestMapping("/daily/repair-types")
public class RepairTypeController {

    private final RepairTypeService repairTypeService;

    public RepairTypeController(RepairTypeService repairTypeService) {
        this.repairTypeService = repairTypeService;
    }

    @GetMapping
    public Result<List<RepairType>> list() {
        return Result.ok(repairTypeService.list());
    }

    @PostMapping
    public Result<Void> create(@RequestBody @Valid RepairTypeRequest req) {
        repairTypeService.create(req);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid RepairTypeRequest req) {
        repairTypeService.update(id, req);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        repairTypeService.delete(id);
        return Result.ok();
    }
}