package com.gzlg.dorm.controller;

import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.common.result.Result;
import com.gzlg.dorm.dto.BuildingReq;
import com.gzlg.dorm.entity.DormBuilding;
import com.gzlg.dorm.service.BuildingService;
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

import java.util.List;
import java.util.Map;

/**
 * 楼栋管理接口。
 */
@RestController
@RequestMapping("/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping("/options")
    public Result<List<Map<String, Object>>> options() {
        return Result.ok(buildingService.options());
    }

    @GetMapping
    public Result<PageResult<DormBuilding>> page(@RequestParam(required = false) String buildingName,
                                                 @RequestParam(required = false) String manager,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(buildingService.page(buildingName, manager, page, pageSize));
    }

    @PostMapping
    public Result<Void> create(@RequestBody @Valid BuildingReq req) {
        buildingService.create(req);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid BuildingReq req) {
        buildingService.update(id, req);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        buildingService.delete(id);
        return Result.ok();
    }
}