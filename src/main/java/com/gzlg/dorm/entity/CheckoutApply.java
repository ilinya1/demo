package com.gzlg.dorm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 退宿申请。撤销约定：待审核可撤销=直接删除记录，不新增枚举；已通过/已驳回不可撤销。
 */
@Data
@TableName("checkout_apply")
public class CheckoutApply {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 业务唯一 */
    private String applyNo;

    /** 逻辑FK -> student.student_id */
    private String studentId;

    private String reason;

    private LocalDate planDate;

    private String description;

    /** 待审核/已通过/已驳回 */
    private String status;

    private String rejectReason;

    private LocalDateTime createTime;

    private LocalDateTime auditTime;
}