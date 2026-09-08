package com.gzlg.dorm.vo;

import lombok.Data;

/**
 * 入住登记可选房间（有剩余床位）。
 */
@Data
public class CheckinRoomVO {

    private Long id;
    private String roomNo;
    private Integer freeBeds;
}