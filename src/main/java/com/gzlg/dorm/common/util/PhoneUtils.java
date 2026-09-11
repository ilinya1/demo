package com.gzlg.dorm.common.util;

import com.gzlg.dorm.common.exception.BizException;

import java.util.regex.Pattern;

/**
 * 电话规范化工具：统一手机号校验与存储格式。
 * 规则：仅中国的 11 位手机号 {@code 1[3-9]\d{9}}；入库前去除空格/横线/括号/加号等间隔符，规范为纯数字。
 */
public final class PhoneUtils {

    private static final Pattern MOBILE = Pattern.compile("^1[3-9]\\d{9}$");

    private PhoneUtils() {
    }

    /**
     * 规范化：去除空格、横线、括号、加号、点号等间隔符，返回纯数字串；入参为空则返回 null。
     */
    public static String normalize(String phone) {
        if (phone == null) {
            return null;
        }
        return phone.replaceAll("[\\s\\-()（）+.]", "");
    }

    /**
     * 校验并返回规范化后的号码。为空或非手机号则抛出业务异常。
     *
     * @param phone     待校验电话
     * @param fieldName 字段中文名，用于错误提示，如「联系电话」
     */
    public static String requireMobile(String phone, String fieldName) {
        String p = normalize(phone);
        if (p == null || p.isEmpty()) {
            throw new BizException(fieldName + "不能为空");
        }
        if (!MOBILE.matcher(p).matches()) {
            throw new BizException(fieldName + "格式不正确，请输入 11 位手机号");
        }
        return p;
    }
}