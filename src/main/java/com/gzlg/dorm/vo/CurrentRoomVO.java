package com.gzlg.dorm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生端「我的宿舍」：当前在住信息 + 室友；未入住 dorm 为 null。
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrentRoomVO {

    private StudentMini student;
    private DormInfo dorm;
    private List<Roommate> roommates;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StudentMini {
        private String studentId;
        private String name;
        private String gender;
        private String className;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DormInfo {
        private Long buildingId;
        private String buildingName;
        private String roomNo;
        private String bedNo;
        private Long roomId;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime checkInTime;
    }

    @Data
    public static class Roommate {
        private String studentId;
        private String name;
        private String bedNo;
    }
}