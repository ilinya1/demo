package com.gzlg.dorm.vo;

import lombok.Data;

/**
 * 床位分布项（房间详情/床位弹窗：bedId 用 roomId-bedNo 组合串，studentName 由在住记录回填）。
 */
@Data
public class BedVO {

    private String bedId;
    private String bedNo;
    private String status;
    private String studentId;
    private String studentName;
}