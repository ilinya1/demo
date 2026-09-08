package com.gzlg.dorm.common.exception;

import com.gzlg.dorm.common.result.ResultCode;

/**
 * 业务异常。由 GlobalExceptionHandler 统一捕获并转为 Result{code, msg}（HTTP 200，
 * 前端以 code!==0 判定失败并展示 msg）。
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int code;

    public BizException(String message) {
        super(message);
        this.code = ResultCode.PARAM_ERROR.getCode();
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BizException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    public int getCode() {
        return code;
    }
}