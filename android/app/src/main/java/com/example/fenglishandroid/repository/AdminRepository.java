package com.example.fenglishandroid.repository;

import android.content.Context;
import android.util.Log;

import com.example.fenglishandroid.model.UserListResponse;
import com.example.fenglishandroid.model.BaseResponse;
import com.example.fenglishandroid.service.AdminService;
import com.example.fenglishandroid.service.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRepository {
    private static final String TAG = "AdminRepository";
    private AdminService adminService;
    private Context context;

    public AdminRepository(Context context) {
        this.context = context.getApplicationContext();
        RetrofitClient.init(this.context);
        this.adminService = RetrofitClient.getAdminService();
    }

    // 获取用户列表
    public void getUserList(String keyword, int page, int size, final OnUserListListener listener) {
        Log.d(TAG, "获取用户列表 - 关键词: " + keyword + ", 页码: " + page + ", 大小: " + size);

        adminService.getUserList(keyword, page, size).enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserListResponse body = response.body();
                    Log.d(TAG, "获取用户列表成功 - 总数: " + body.getTotalCount() + ", 用户数: " +
                            (body.getUsers() != null ? body.getUsers().size() : 0));
                    listener.onSuccess(body);
                } else {
                    String errorMsg = "获取用户列表失败: " + response.message();
                    Log.e(TAG, errorMsg);
                    listener.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                String errorMsg = "网络错误: " + t.getMessage();
                Log.e(TAG, errorMsg);
                listener.onFailure(errorMsg);
            }
        });
    }

    // 获取指定用户信息
    public void getUserById(String userId, final OnUserDetailListener listener) {
        Log.d(TAG, "获取用户详情 - 用户ID: " + userId);

        adminService.getUserById(userId).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse body = response.body();
                    Log.d(TAG, "获取用户详情成功");
                    listener.onSuccess(body);
                } else {
                    String errorMsg = "获取用户详情失败: " + response.message();
                    Log.e(TAG, errorMsg);
                    listener.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                String errorMsg = "网络错误: " + t.getMessage();
                Log.e(TAG, errorMsg);
                listener.onFailure(errorMsg);
            }
        });
    }

    // 删除用户
    public void deleteUser(String userId, final OnDeleteUserListener listener) {
        Log.d(TAG, "删除用户 - 用户ID: " + userId);

        adminService.deleteUser(userId).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse body = response.body();
                    Log.d(TAG, "删除用户成功: " + body.getMessage());
                    listener.onSuccess(body);
                } else {
                    String errorMsg = "删除用户失败: " + response.message();
                    Log.e(TAG, errorMsg);
                    listener.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                String errorMsg = "网络错误: " + t.getMessage();
                Log.e(TAG, errorMsg);
                listener.onFailure(errorMsg);
            }
        });
    }

    // 回调接口
    public interface OnUserListListener {
        void onSuccess(UserListResponse response);
        void onFailure(String errorMsg);
    }

    public interface OnUserDetailListener {
        void onSuccess(BaseResponse response);
        void onFailure(String errorMsg);
    }

    public interface OnDeleteUserListener {
        void onSuccess(BaseResponse response);
        void onFailure(String errorMsg);
    }
}