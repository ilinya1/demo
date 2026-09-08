package com.gzlg.dorm.vo;

import lombok.Data;

/**
 * 班级列表项（含班内学生数/住宿数，供前端左侧班级面板展示）。
 */
@Data
public class ClazzVO {

    private Long id;
    private String name;
    private String college;
    private String major;
    private String grade;
    private String headTeacher;
    private Long studentCount;
    private Long boardingCount;
}