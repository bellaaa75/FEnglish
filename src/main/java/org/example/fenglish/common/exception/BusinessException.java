package org.example.fenglish.common.exception;

import lombok.Getter;
import org.example.fenglish.common.ErrorCodes;
/**
 * 自定义异常类
 * 业务异常，可指定错误码
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
    /*---- 直接传枚举 ----*/
    public BusinessException(ErrorCodes errorCodes) {
        this(errorCodes.getCode(), errorCodes.getMsg());
    }
    public BusinessException(String message) {
        this(400, message);
    }
}