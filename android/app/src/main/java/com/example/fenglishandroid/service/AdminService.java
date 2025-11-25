package com.example.fenglishandroid.service;

import com.example.fenglishandroid.model.UserListResponse;
import com.example.fenglishandroid.model.BaseResponse;
import com.example.fenglishandroid.model.request.User;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminService {
    // 查找所有/部分用户（模糊搜索）
    @GET("api/admin/list")
    Call<UserListResponse> getUserList(
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("size") int size
    );

    // 查找指定用户
    @GET("api/admin/{userId}")
    Call<BaseResponse> getUserById(@Path("userId") String userId);

    // 删除指定用户
    @DELETE("api/admin/delete/{userId}")
    Call<BaseResponse> deleteUser(@Path("userId") String userId);
}