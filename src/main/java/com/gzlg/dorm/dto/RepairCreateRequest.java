package com.gzlg.dorm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 学生提交报修请求（roomId 取自学生当前宿舍）。
 */
@Data
public class RepairCreateRequest {

    @NotBlank(message = "请先登录")
    private String studentId;

    @NotNull(message = "宿舍信息不存在")
    private Long roomId;

    @NotNull(message = "请选择报修物品")
    private Long typeId;

    @NotBlank(message = "请描述问题情况")
    private String description;

    private String contactPhone;

    private List<String> images;
}