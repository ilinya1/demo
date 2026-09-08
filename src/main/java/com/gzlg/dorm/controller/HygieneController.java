package com.gzlg.dorm.controller;

import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.common.result.Result;
import com.gzlg.dorm.dto.HygieneRequest;
import com.gzlg.dorm.service.HygieneService;
import com.gzlg.dorm.vo.HygieneVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 卫生检查接口。
 */
@RestController
@RequestMapping("/daily/hygiene")
public class HygieneController {

    private final HygieneService hygieneService;

    public HygieneController(HygieneService hygieneService) {
        this.hygieneService = hygieneService;
    }

    @GetMapping
    public Result<PageResult<HygieneVO>> page(@RequestParam(required = false) String checkDate,
                                              @RequestParam(required = false) Long buildingId,
                                              @RequestParam(required = false) Long roomId,
                                              @RequestParam(required = false) String result,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(hygieneService.page(checkDate, buildingId, roomId, result, page, pageSize));
    }

    @PostMapping
    public Result<Void> create(@RequestBody @Valid HygieneRequest req) {
        hygieneService.create(req);
        return Result.ok();
    }
}