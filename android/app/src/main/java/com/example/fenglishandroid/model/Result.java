package com.example.fenglishandroid.model;

import com.google.gson.annotations.SerializedName;

public class Result<T> {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;
    private boolean success;

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

    public boolean isSuccess() {
        // 假设后端约定 200 为成功
        return code == 200;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
}