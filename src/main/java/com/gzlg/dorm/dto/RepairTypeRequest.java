package com.gzlg.dorm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 报修类型新增/编辑请求。
 */
@Data
public class RepairTypeRequest {

    @NotNull(message = "请输入类型名称")
    private String name;

    @NotNull(message = "请输入排序")
    private Integer sort;
}