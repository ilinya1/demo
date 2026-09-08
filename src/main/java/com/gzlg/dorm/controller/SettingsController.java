package com.gzlg.dorm.controller;

import com.gzlg.dorm.common.result.Result;
import com.gzlg.dorm.service.SettingsService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 系统设置接口：系统参数 + 退宿原因字典。
 */
@RestController
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping("/settings/params")
    public Result<List<Map<String, Object>>> params() {
        return Result.ok(settingsService.getParams());
    }

    @PutMapping("/settings/params")
    public Result<Void> updateParams(@RequestBody Map<String, Object> body) {
        Object list = body.get("list");
        List<Map<String, Object>> items;
        if (list instanceof List<?> raw) {
            items = raw.stream()
                    .filter(Map.class::isInstance)
                    .map(item -> (Map<String, Object>) item)
                    .toList();
        } else {
            items = List.of();
        }
        settingsService.updateParams(items);
        return Result.ok();
    }

    @PostMapping("/settings/params/reset")
    public Result<Void> resetParams() {
        settingsService.resetParams();
        return Result.ok();
    }

    // ---- 退宿原因字典 ----
    @GetMapping("/daily/checkout-reasons")
    public Result<List<Map<String, Object>>> reasons() {
        return Result.ok(settingsService.getCheckoutReasons());
    }

    @PostMapping("/daily/checkout-reasons")
    public Result<Void> createReason(@RequestBody Map<String, Object> body) {
        settingsService.createCheckoutReason(
                asString(body.get("name")), asInteger(body.get("sort")));
        return Result.ok();
    }

    @PutMapping("/daily/checkout-reasons/{id}")
    public Result<Void> updateReason(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        settingsService.updateCheckoutReason(id, asString(body.get("name")), asInteger(body.get("sort")));
        return Result.ok();
    }

    @DeleteMapping("/daily/checkout-reasons/{id}")
    public Result<Void> deleteReason(@PathVariable Long id) {
        settingsService.deleteCheckoutReason(id);
        return Result.ok();
    }

    private String asString(Object o) {
        return o == null ? null : o.toString();
    }

    private Integer asInteger(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Number num) {
            return num.intValue();
        }
        try {
            return Integer.parseInt(o.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}