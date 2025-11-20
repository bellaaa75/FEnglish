package com.example.fenglishandroid.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.fenglishandroid.model.BaseResponse;
import com.example.fenglishandroid.model.request.AdminRegisterRequest;
import com.example.fenglishandroid.model.request.LoginRequest;
import com.example.fenglishandroid.model.request.OrdinaryUserRegisterRequest;
import com.example.fenglishandroid.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private static final String TAG = "UserViewModel";
    private UserRepository repository;
    private MutableLiveData<BaseResponse> registerLiveData = new MutableLiveData<>();
    private MutableLiveData<BaseResponse> loginLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository();
    }

    // 普通用户注册
    public void registerOrdinary(OrdinaryUserRegisterRequest request) {
        repository.registerOrdinary(request, new UserRepository.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse response) {
                Log.d("Register", "注册成功: " + response.toString());
                registerLiveData.postValue(response);
            }
            @Override
            public void onFailure(String errorMsg) {
                Log.d("Register", "注册失败: " + errorMsg);
                errorLiveData.postValue(errorMsg);
            }
        });
    }

    // 管理员注册
    public void registerAdmin(AdminRegisterRequest request) {
        // 前端先校验两次密码一致
        if (!request.getUserPassword().equals(request.getConfirmPassword())) {
            errorLiveData.postValue("两次密码不一致");
            return;
        }
        repository.registerAdmin(request, new UserRepository.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse response) {
                registerLiveData.postValue(response);
            }

            @Override
            public void onFailure(String errorMsg) {
                errorLiveData.postValue(errorMsg);
            }
        });
    }

    // 普通用户登录
    public void loginOrdinary(LoginRequest request) {
        repository.loginOrdinary(request, new UserRepository.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse response) {
                Log.d("Login", "登录成功: " + response.toString());
                loginLiveData.postValue(response);
            }

            @Override
            public void onFailure(String errorMsg) {
                Log.e("Login", "登录失败: " + errorMsg);
                errorLiveData.postValue(errorMsg);
            }
        });
    }

    // 管理员登录
    public void loginAdmin(LoginRequest request) {
        repository.loginAdmin(request, new UserRepository.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse response) {
                loginLiveData.postValue(response);
            }

            @Override
            public void onFailure(String errorMsg) {
                errorLiveData.postValue(errorMsg);
            }
        });
    }

    // Getter方法供View观察
    public MutableLiveData<BaseResponse> getRegisterLiveData() { return registerLiveData; }
    public MutableLiveData<BaseResponse> getLoginLiveData() { return loginLiveData; }
    public MutableLiveData<String> getErrorLiveData() { return errorLiveData; }
}