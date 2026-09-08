package com.gzlg.dorm.common.result;

/**
 * 业务返回码。
 * 契约约定：code=0 为成功；非 0 为业务错误（前端 request 拦截器以 code!==0 判定失败并弹 msg）。
 */
public enum ResultCode {

    SUCCESS(0, "success"),
    PARAM_ERROR(400, "参数校验失败"),
    UNAUTHORIZED(401, "未登录或登录已失效"),
    FORBIDDEN(403, "无访问权限"),
    NOT_FOUND(404, "资源不存在"),
    ERROR(500, "服务器内部错误");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}