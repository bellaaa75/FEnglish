package com.example.fenglishandroid.service;

import com.example.fenglishandroid.model.BaseResponse;
import com.example.fenglishandroid.model.request.AdminRegisterRequest;
import com.example.fenglishandroid.model.request.LoginRequest;
import com.example.fenglishandroid.model.request.OrdinaryUserRegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

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
}