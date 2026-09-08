package com.gzlg.dorm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报修单视图（含学生/楼栋/房间/类型回填与图片列表解析）。
 */
@Data
public class RepairVO {

    private Long id;

    private String orderNo;

    private String studentId;

    private String studentName;

    private Long buildingId;

    private Long roomId;

    private String buildingName;

    private String roomNo;

    private Long typeId;

    private String typeName;

    private String description;

    private String contactPhone;

    private List<String> images;

    /** 待处理/处理中/已完成 */
    private String status;

    private String handlerName;

    private String handlerPhone;

    private String handleDesc;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handleTime;
}