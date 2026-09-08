package com.gzlg.dorm.controller;

import com.gzlg.dorm.common.result.Result;
import com.gzlg.dorm.service.StatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 统计报表与仪表盘聚合接口。
 */
@RestController
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    /** 入住率统计：卡片 + 分楼栋 + 近6月趋势 */
    @GetMapping("/stats/occupancy")
    public Result<Map<String, Object>> occupancy() {
        return Result.ok(statsService.occupancy());
    }

    /** 卫生统计：卡片 + 近4周 + 结果分布 */
    @GetMapping("/stats/hygiene")
    public Result<Map<String, Object>> hygiene() {
        return Result.ok(statsService.hygiene());
    }

    /** 报修统计：卡片 + 类型分布 + 近6月趋势 */
    @GetMapping("/stats/repair")
    public Result<Map<String, Object>> repair() {
        return Result.ok(statsService.repair());
    }

    /** 仪表盘：总体概览 */
    @GetMapping("/dashboard/stats")
    public Result<Map<String, Object>> dashboardStats() {
        return Result.ok(statsService.dashboardStats());
    }

    /** 仪表盘：分楼栋占用率 */
    @GetMapping("/dashboard/building-occupancy")
    public Result<List<Map<String, Object>>> buildingOccupancy() {
        return Result.ok(statsService.buildingOccupancy());
    }

    /** 仪表盘：近4周卫生趋势 */
    @GetMapping("/dashboard/hygiene-trend")
    public Result<List<Map<String, Object>>> hygieneTrend() {
        return Result.ok(statsService.hygieneTrend());
    }

    /** 仪表盘：工作台聚合（待办/积压/告警/动态） */
    @GetMapping("/dashboard/workbench")
    public Result<Map<String, Object>> workbench() {
        return Result.ok(statsService.workbench());
    }
}