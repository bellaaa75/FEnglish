package com.example.fenglishandroid.repository;

import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

import com.example.fenglishandroid.model.BaseResponse;
import com.example.fenglishandroid.model.request.AdminRegisterRequest;
import com.example.fenglishandroid.model.request.ChangePasswordRequest;
import com.example.fenglishandroid.model.request.DeleteAccountRequest;
import com.example.fenglishandroid.model.request.LoginRequest;
import com.example.fenglishandroid.model.request.OrdinaryUserRegisterRequest;
import com.example.fenglishandroid.model.request.UpdateUserRequest;
import com.example.fenglishandroid.service.RetrofitClient;
import com.example.fenglishandroid.service.UserApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private UserApiService apiService;
    private Context context;

    public UserRepository(Context context) {
        this.context = context.getApplicationContext(); // 保存context
        RetrofitClient.init(this.context); // 初始化RetrofitClient
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
                // 确保请求没有被取消
                if (call.isCanceled()) {
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("UserRepository", "服务器响应 - success: " + response.body().isSuccess());
                    if(response.body().isSuccess()){
                        saveToken(response.body().getToken());
                        saveUserId(response.body().getUserId());
                        listener.onSuccess(response.body());
                    }else{
                        listener.onFailure(response.body().getMessage());
                    }
                } else {
                    listener.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    listener.onFailure("网络错误：" + t.getMessage());
                }
            }
        });
    }

    // 管理员登录
    public void loginAdmin(LoginRequest request, final OnResultListener listener) {
        apiService.loginAdmin(request).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().isSuccess()) {
                        saveToken(response.body().getToken());
                        saveUserId(response.body().getUserId());
                        listener.onSuccess(response.body());
                    }else{
                        listener.onFailure(response.body().getMessage());
                    }
                } else {
                    listener.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                listener.onFailure("网络错误：" + t.getMessage());
            }
        });
    }
    // 获取用户信息
    public void getUserInfo(final OnResultListener listener) {
        apiService.getUserInfo().enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    if (response.code() == 401) {
                        // Token过期或无效
                        clearToken();
                        listener.onFailure("登录已过期，请重新登录");
                    } else {
                        listener.onFailure(response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                listener.onFailure("网络错误：" + t.getMessage());
            }
        });
    }

    // 更新用户信息
    public void updateUser(UpdateUserRequest request, final OnResultListener listener) {
        apiService.updateUser(request).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    if (response.code() == 401) {
                        clearToken();
                        listener.onFailure("登录已过期，请重新登录");
                    } else {
                        listener.onFailure("更新用户信息失败：" + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                listener.onFailure("网络错误：" + t.getMessage());
            }
        });
    }

    // 修改密码
    public void changePassword(ChangePasswordRequest request, final OnResultListener listener) {
        apiService.changePassword(request).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    if (response.code() == 401) {
                        clearToken();
                        listener.onFailure("登录已过期，请重新登录");
                    } else {
                        listener.onFailure(response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                listener.onFailure("网络错误：" + t.getMessage());
            }
        });
    }

    // 注销账号
    public void deleteAccount(DeleteAccountRequest request, final OnResultListener listener) {
        apiService.deleteAccount(request).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 注销成功，清除本地数据
                    if (response.body().isSuccess()) {
                        clearUserData();
                    }
                    listener.onSuccess(response.body());
                } else {
                    if (response.code() == 401) {
                        clearToken();
                        listener.onFailure("登录已过期，请重新登录");
                    } else {
                        listener.onFailure(response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                listener.onFailure("网络错误：" + t.getMessage());
            }
        });
    }

    // 退出登录
    public void logout() {
        clearUserData();
    }

    // 保存Token到SharedPreferences
    private void saveToken(String token) {
        SharedPreferences sp = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        sp.edit().putString("token", token).apply();
    }

    // 保存用户ID到SharedPreferences
    private void saveUserId(String userId) {
        SharedPreferences sp = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        sp.edit().putString("userId", userId).apply();
    }

    // 清除Token
    private void clearToken() {
        SharedPreferences sp = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        sp.edit().remove("token").apply();
    }

    // 清除所有用户数据
    private void clearUserData() {
        SharedPreferences sp = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        sp.edit()
                .remove("token")
                .remove("userId")
                .apply();
    }

    // 获取当前Token
    public String getCurrentToken() {
        SharedPreferences sp = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        return sp.getString("token", null);
    }

    // 获取当前用户ID
    public String getCurrentUserId() {
        SharedPreferences sp = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        return sp.getString("userId", null);
    }

    // 检查是否已登录
    public boolean isLoggedIn() {
        return getCurrentToken() != null && !getCurrentToken().isEmpty();
    }

    // 回调接口
    public interface OnResultListener {
        void onSuccess(BaseResponse response);
        void onFailure(String errorMsg);
    }
}