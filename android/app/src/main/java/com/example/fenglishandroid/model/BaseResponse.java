package com.example.fenglishandroid.model;
import java.util.HashMap;
import java.util.Map;
public class BaseResponse<T> {
    private boolean success;
    private String message;
    private String userId;
    private int code;
    private String token;
    private String userType;
    private Map<String, Object> userInfo = new HashMap<>();

    /* 新增：真正的业务数据 */
    private T data;

    // getter和setter
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    public Map<String, Object> getUserInfo() { return userInfo; }
    public void setUserInfo(Map<String, Object> userInfo) { this.userInfo = userInfo; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
