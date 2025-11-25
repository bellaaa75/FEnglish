package com.example.fenglishandroid.service;

import com.example.fenglishandroid.model.BaseResponse;
import com.example.fenglishandroid.model.request.AdminRegisterRequest;
import com.example.fenglishandroid.model.request.ChangePasswordRequest;
import com.example.fenglishandroid.model.request.DeleteAccountRequest;
import com.example.fenglishandroid.model.request.LoginRequest;
import com.example.fenglishandroid.model.request.OrdinaryUserRegisterRequest;
import com.example.fenglishandroid.model.request.UpdateUserRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface UserApiService {
    // 普通用户注册
    @POST("api/user/register")
    Call<BaseResponse> registerOrdinary(@Body OrdinaryUserRegisterRequest request);

    // 管理员注册
    @POST("api/admin/register")
    Call<BaseResponse> registerAdmin(@Body AdminRegisterRequest request);

    // 普通用户登录
    @POST("api/user/login")
    Call<BaseResponse> loginOrdinary(@Body LoginRequest request);

    // 管理员登录
    @POST("api/admin/login")
    Call<BaseResponse> loginAdmin(@Body LoginRequest request);
    // 获取用户信息
    @GET("api/user/info")
    Call<BaseResponse> getUserInfo();

    // 更新用户信息
    @PUT("api/user/info")
    Call<BaseResponse> updateUser(@Body UpdateUserRequest request);

    // 修改密码
    @PUT("api/user/password")
    Call<BaseResponse> changePassword(@Body ChangePasswordRequest request);

    // 注销账号
    @HTTP(method = "DELETE", path = "api/user/account", hasBody = true)
    Call<BaseResponse> deleteAccount(@Body DeleteAccountRequest request);

}