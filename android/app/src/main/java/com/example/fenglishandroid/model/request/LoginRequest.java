package com.example.fenglishandroid.model.request;

public class LoginRequest {
    private String identifier; // 普通用户：手机号/邮箱；管理员：用户ID
    private String password;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
