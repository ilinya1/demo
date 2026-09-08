package com.gzlg.dorm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 卫生检查记录视图（含楼栋/房间回填与 JSON 解析后的列表）。
 */
@Data
public class HygieneVO {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkDate;

    private String checker;

    private Long buildingId;

    private Long roomId;

    private String buildingName;

    private String roomNo;

    private Integer score;

    /** 优秀/合格/不合格 */
    private String result;

    private List<String> deductItems;

    private List<String> photos;

    private String comment;
}