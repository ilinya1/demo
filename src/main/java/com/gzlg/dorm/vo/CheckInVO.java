package com.gzlg.dorm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 入住记录项。
 */
@Data
public class CheckInVO {

    private String studentId;
    private String studentName;
    private String buildingName;
    private String roomNo;
    private String bedNo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkOutTime;
    private String source;
    private String status;
    private String remark;
}