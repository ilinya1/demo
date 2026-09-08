package com.gzlg.dorm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 入住/退宿记录（核心审计表）。关联均 NOT NULL；5 个名称快照入住时写入、退宿不覆盖。
 */
@Data
@TableName("check_in")
public class CheckIn {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 逻辑FK -> student.student_id */
    private String studentId;

    /** 逻辑FK -> dorm_building.id */
    private Long buildingId;

    /** 逻辑FK -> dorm_room.id */
    private Long roomId;

    /** 逻辑FK -> dorm_bed.id */
    private Long bedId;

    /** 快照：学生姓名 */
    private String studentName;

    /** 快照：班级名称 */
    private String className;

    /** 快照：楼栋名称 */
    private String buildingName;

    /** 快照：房间号 */
    private String roomNo;

    /** 快照：床位号 */
    private String bedNo;

    private LocalDateTime checkInTime;

    /** 退宿时间（未退宿为 NULL） */
    private LocalDateTime checkOutTime;

    /** 退宿来源：apply / direct */
    private String source;

    /** 在住/已退宿 */
    private String status;

    private String remark;

    private LocalDateTime createdAt;
}