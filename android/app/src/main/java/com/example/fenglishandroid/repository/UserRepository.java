package com.example.fenglishandroid.repository;

import com.example.fenglishandroid.model.BaseResponse;
import com.example.fenglishandroid.model.request.AdminRegisterRequest;
import com.example.fenglishandroid.model.request.LoginRequest;
import com.example.fenglishandroid.model.request.OrdinaryUserRegisterRequest;
import com.example.fenglishandroid.service.RetrofitClient;
import com.example.fenglishandroid.service.UserApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private UserApiService apiService;

    public UserRepository() {
        this.apiService = RetrofitClient.getUserApi();
    }

    // 普通用户注册
    public void registerOrdinary(OrdinaryUserRegisterRequest request, final OnResultListener listener) {
        apiService.registerOrdinary(request).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onFailure("注册失败：" + response.message());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                listener.onFailure("网络错误：" + t.getMessage());
            }
        });
    }

    // 管理员注册
    public void registerAdmin(AdminRegisterRequest request, final OnResultListener listener) {
        apiService.registerAdmin(request).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onFailure("注册失败：" + response.message());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                listener.onFailure("网络错误：" + t.getMessage());
            }
        });
    }

    // 普通用户登录
    public void loginOrdinary(LoginRequest request, final OnResultListener listener) {
        apiService.loginOrdinary(request).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onFailure("登录失败：" + response.message());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                listener.onFailure("网络错误：" + t.getMessage());
            }
        });
    }

    // 管理员登录
    public void loginAdmin(LoginRequest request, final OnResultListener listener) {
        apiService.loginAdmin(request).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onFailure("登录失败：" + response.message());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                listener.onFailure("网络错误：" + t.getMessage());
            }
        });
    }

    // 回调接口
    public interface OnResultListener {
        void onSuccess(BaseResponse response);
        void onFailure(String errorMsg);
    }
}