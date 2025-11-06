package org.example.fenglish.common;

import org.example.fenglish.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* 1. 自定义业务异常 最高优先级 */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException e) {
        log.warn("BusinessException: {}", e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /* 2. 参数校验异常 */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ApiResponse<Void> handleValid(Exception e) {
        String msg = "参数错误";
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            msg = ex.getBindingResult().getFieldError().getDefaultMessage();
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            msg = ex.getBindingResult().getFieldError().getDefaultMessage();
        }
        log.warn("参数校验失败: {}", msg);
        return ApiResponse.error(400, msg);
    }

    /* 3. 数据库主键/唯一索引冲突 */
    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResponse<Void> handleDuplicateKey(DuplicateKeyException e) {
        log.warn("数据库主键冲突: {}", e.getMessage());
        return ApiResponse.error(400, "数据已存在，请勿重复操作");
    }

    /* 4. 其它数据库或运行时异常（可继续细化） */
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<Void> handleRuntime(RuntimeException e) {
        log.error("运行时异常: ", e);
        return ApiResponse.error(500, "服务器内部错误");
    }

    /* 5. 最终兜底 */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleAll(Exception e) {
        log.error("未知异常: ", e);
        return ApiResponse.error(500, "服务器内部错误");
    }
}