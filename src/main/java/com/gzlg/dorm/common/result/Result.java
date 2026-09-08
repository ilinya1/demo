package com.gzlg.dorm.common.result;

import java.io.Serializable;

/**
 * 统一响应体。
 * 契约对齐前端 request.js：{ code, msg, data }，code=0 成功，非 0 失败（前端弹 msg）。
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 业务码：0 成功 / 非 0 失败 */
    private int code;
    /** 提示信息（前端直接展示） */
    private String msg;
    /** 业务数据 */
    private T data;

    public Result() {
    }

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        return fail(resultCode.getCode(), resultCode.getMessage());
    }

    public static <T> Result<T> fail(ResultCode resultCode, String msg) {
        return fail(resultCode.getCode(), msg);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}