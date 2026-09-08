package com.gzlg.dorm.common.exception;

import com.gzlg.dorm.common.result.Result;
import com.gzlg.dorm.common.result.ResultCode;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理：统一转为 Result{code,msg}（HTTP 200，前端以 code!==0 判定失败并弹 msg）。
 * 避免把堆栈直接抛给前端，业务异常/参数异常给出友好提示，未知异常记日志并返回通用错误。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 业务异常 */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<Result<Void>> handleBizException(BizException e) {
        return ok(Result.fail(e.getCode(), e.getMessage()));
    }

    /** 请求体 Json 解析失败 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleNotReadable(HttpMessageNotReadableException e) {
        return ok(Result.fail(ResultCode.PARAM_ERROR, "请求体格式错误"));
    }

    /** @RequestBody + @Valid 校验失败 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getDefaultMessage() == null ? "参数校验失败" : fe.getDefaultMessage())
                .orElse("参数校验失败");
        return ok(Result.fail(ResultCode.PARAM_ERROR, msg));
    }

    /** 表单绑定校验失败 */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBind(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getDefaultMessage() == null ? "参数校验失败" : fe.getDefaultMessage())
                .orElse("参数校验失败");
        return ok(Result.fail(ResultCode.PARAM_ERROR, msg));
    }

    /** 方法参数约束校验失败（@Validated 于方法参数） */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraint(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().isEmpty()
                ? "参数校验失败"
                : e.getConstraintViolations().iterator().next().getMessage();
        return ok(Result.fail(ResultCode.PARAM_ERROR, msg));
    }

    /** 路径/请求参数类型不匹配 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ok(Result.fail(ResultCode.PARAM_ERROR, "参数类型不正确：" + e.getName()));
    }

    /** 资源不存在（404 语义，仍返回 HTTP 200 + code=404） */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNotFound(NoResourceFoundException e) {
        return ok(Result.fail(ResultCode.NOT_FOUND, "请求的资源不存在"));
    }

    /** 兜底：未知异常 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("服务器内部错误", e);
        return ok(Result.fail(ResultCode.ERROR, "服务器开小差了，请稍后重试"));
    }

    /** 统一包一层 HTTP 200，code 承载业务结果（与前端 request 拦截器契直） */
    private static ResponseEntity<Result<Void>> ok(Result<Void> result) {
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}