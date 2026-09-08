package com.gzlg.dorm.controller;

import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.common.result.Result;
import com.gzlg.dorm.dto.RepairCreateRequest;
import com.gzlg.dorm.dto.RepairHandleRequest;
import com.gzlg.dorm.service.RepairService;
import com.gzlg.dorm.vo.RepairVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 报修接口（学生提交/列表/详情/处理）。
 */
@RestController
public class RepairController {

    private final RepairService repairService;

    public RepairController(RepairService repairService) {
        this.repairService = repairService;
    }

    @GetMapping("/daily/repairs")
    public Result<PageResult<RepairVO>> page(@RequestParam(required = false) String orderNo,
                                             @RequestParam(required = false) Long buildingId,
                                             @RequestParam(required = false) String studentId,
                                             @RequestParam(required = false) String status,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(repairService.page(orderNo, buildingId, studentId, status, page, pageSize));
    }

    @PostMapping("/daily/repairs")
    public Result<Void> create(@RequestBody @Valid RepairCreateRequest req) {
        repairService.create(req);
        return Result.ok();
    }

    @GetMapping("/daily/repair/{id}")
    public Result<RepairVO> detail(@PathVariable Long id) {
        return Result.ok(repairService.detail(id));
    }

    @PutMapping("/daily/repair/{id}")
    public Result<Void> handle(@PathVariable Long id, @RequestBody @Valid RepairHandleRequest req) {
        repairService.handle(id, req);
        return Result.ok();
    }
}