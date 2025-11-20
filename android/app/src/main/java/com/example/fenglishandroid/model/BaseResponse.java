package com.example.fenglishandroid.model;
import java.util.HashMap;
import java.util.Map;
public class BaseResponse {
    private boolean success;
    private String message;
    private String userId;
    private String token;
    private String userType;
    private Map<String, Object> userInfo = new HashMap<>();

    // getter和setter
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
}
