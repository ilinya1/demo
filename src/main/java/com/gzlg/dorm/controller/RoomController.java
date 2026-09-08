package com.gzlg.dorm.controller;

import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.common.result.Result;
import com.gzlg.dorm.dto.RoomReq;
import com.gzlg.dorm.service.RoomService;
import com.gzlg.dorm.vo.BedVO;
import com.gzlg.dorm.vo.RoomVO;
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
 * 房间管理接口。
 */
@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /** 下拉选项需置于 {id}/beds 之前由 Spring 按最佳匹配解析 */
    @GetMapping("/options")
    public Result<Map<String, Object>> options() {
        return Result.ok(roomService.options());
    }

    @GetMapping
    public Result<PageResult<RoomVO>> page(@RequestParam(required = false) Long buildingId,
                                           @RequestParam(required = false) String roomNo,
                                           @RequestParam(required = false) String status,
                                           @RequestParam(required = false) String roomType,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(roomService.page(buildingId, roomNo, status, roomType, page, pageSize));
    }

    @PostMapping
    public Result<Void> create(@RequestBody @Valid RoomReq req) {
        roomService.create(req);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid RoomReq req) {
        roomService.update(id, req);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return Result.ok();
    }

    @GetMapping("/{id}/beds")
    public Result<List<BedVO>> beds(@PathVariable Long id) {
        return Result.ok(roomService.beds(id));
    }
}