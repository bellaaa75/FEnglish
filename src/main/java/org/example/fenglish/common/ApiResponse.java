package org.example.fenglish.common;

import lombok.Data;

/**
 * 统一返回实体
 * @param <T> 业务数据类型
 */
@Data
public class ApiResponse<T> {
    private int code;//服务端返回的错误/成功码
    private String message;//服务端返回的错误/成功信息
    private T data;//服务端返回的数据

    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /*---------- 快速工厂方法 ----------*/
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /*---------- 枚举快捷工厂 ----------*/
    public static <T> ApiResponse<T> error(ErrorCodes errorCodes) {
        return new ApiResponse<>(errorCodes.getCode(), errorCodes.getMsg(), null);
    }

    public static <T> ApiResponse<T> success(ErrorCodes errorCodes, T data) {
        return new ApiResponse<>(errorCodes.getCode(), errorCodes.getMsg(), data);
    }
}