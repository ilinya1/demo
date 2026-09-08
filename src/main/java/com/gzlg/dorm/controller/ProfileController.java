package com.gzlg.dorm.controller;

import com.gzlg.dorm.common.result.Result;
import com.gzlg.dorm.service.ProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 个人中心接口。
 */
@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public Result<Map<String, Object>> get(@RequestParam String role, @RequestParam String username) {
        return Result.ok(profileService.getProfile(role, username));
    }

    @PutMapping
    public Result<Void> update(@RequestParam String role, @RequestParam String username,
                               @RequestBody Map<String, Object> data) {
        profileService.updateProfile(role, username, data);
        return Result.ok();
    }
}