package org.example.fenglish.vo.response;

// 手动生成getter/setter，不依赖Lombok
public class Result<T> {
    // 状态码：200成功，400参数错误，404资源不存在，500服务器错误
    private int code;
    // 提示消息（如“操作成功”“单词书不存在”）
    private String message;
    // 响应数据（泛型T，支持任意类型：单个对象、列表、空）
    private T data;

    // 1. 无参构造器（手动生成，必须保留，否则JSON序列化失败）
    public Result() {
    }

    // 2. 全参构造器（手动生成，用于静态方法内部创建对象）
    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 3. 成功（无数据）：显式绑定泛型为Void，无需参数
    public static Result<Void> success() {
        // 调用全参构造器：code=200，message=操作成功，data=null（无数据）
        return new Result<>(200, "操作成功", null);
    }

    // 4. 成功（有数据）：泛型T由调用方传入的data类型决定
    public static <T> Result<T> success(T data) {
        // 调用全参构造器：code=200，message=操作成功，data=传入的具体数据
        return new Result<>(200, "操作成功", data);
    }

    // 5. 失败（无数据）：显式绑定泛型为Void，需传入“错误码”和“错误消息”
    public static Result<Void> fail(int code, String message) {
        // 调用全参构造器：code=错误码，message=错误消息，data=null
        return new Result<>(code, message, null);
    }

    // 6. 手动生成所有字段的getter/setter（之前已生成，确保无遗漏）
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}