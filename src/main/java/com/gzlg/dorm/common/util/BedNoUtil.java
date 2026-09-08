package com.gzlg.dorm.common.util;

/**
 * 床位号展示工具。
 * 库中 dorm_bed.bed_no 存储为 "N号床"（容量建床 / 入住写入统一加 "号床" 后缀），
 * 前端契约统一将床位号视为纯数字并自行拼后缀，故对外接口返回值统一剥掉 "号床"。
 */
public final class BedNoUtil {

    private BedNoUtil() {
    }

    private static final String SUFFIX = "号床";

    /** 去掉尾部 "号床" 后缀；无后缀或为空则原样返回。 */
    public static String strip(String bedNo) {
        if (bedNo == null) {
            return null;
        }
        return bedNo.endsWith(SUFFIX) ? bedNo.substring(0, bedNo.length() - SUFFIX.length()) : bedNo;
    }
}