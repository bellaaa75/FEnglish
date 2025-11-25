package com.example.fenglishandroid.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.fenglishandroid.model.BaseResponse;
import com.example.fenglishandroid.model.request.ChangePasswordRequest;
import com.example.fenglishandroid.model.request.DeleteAccountRequest;
import com.example.fenglishandroid.repository.UserRepository;

public class AdminAccountViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navigateToLogin = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public AdminAccountViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);

        // 初始化时获取当前用户ID
        String currentUserId = userRepository.getCurrentUserId();
        userId.setValue(currentUserId);
    }

    // 修改密码
    public void changePassword(ChangePasswordRequest request) {
        if (!userRepository.isLoggedIn()) {
            toastMessage.setValue("请先登录");
            navigateToLogin.setValue(true);
            return;
        }

        isLoading.setValue(true);
        userRepository.changePassword(request, new UserRepository.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse response) {
                isLoading.setValue(false);
                if (response.isSuccess()) {
                    toastMessage.setValue("密码修改成功");
                } else {
                    toastMessage.setValue(response.getMessage());
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                isLoading.setValue(false);
                toastMessage.setValue(errorMsg);
                if (errorMsg.contains("登录已过期")) {
                    navigateToLogin.setValue(true);
                }
            }
        });
    }

    // 注销账号
    public void deleteAccount(DeleteAccountRequest request) {
        if (!userRepository.isLoggedIn()) {
            toastMessage.setValue("请先登录");
            navigateToLogin.setValue(true);
            return;
        }

        isLoading.setValue(true);
        userRepository.deleteAccount(request, new UserRepository.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse response) {
                isLoading.setValue(false);
                if (response.isSuccess()) {
                    toastMessage.setValue("账号注销成功");
                    navigateToLogin.setValue(true);
                } else {
                    toastMessage.setValue(response.getMessage());
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                isLoading.setValue(false);
                toastMessage.setValue(errorMsg);
                if (errorMsg.contains("登录已过期")) {
                    navigateToLogin.setValue(true);
                }
            }
        });
    }

    // 退出登录
    // 退出登录 - 修正后的版本
    public void logout() {
        if (!userRepository.isLoggedIn()) {
            toastMessage.setValue("您尚未登录");
            return;
        }

        isLoading.setValue(true);

        try {
            // 直接调用同步的退出登录方法
            userRepository.logout();

            isLoading.setValue(false);
            toastMessage.setValue("已退出登录");
            navigateToLogin.setValue(true);

        } catch (Exception e) {
            isLoading.setValue(false);
            toastMessage.setValue("退出登录时发生错误");
            // 即使出错也跳转到登录页
            navigateToLogin.setValue(true);
        }
    }

    // LiveData Getters
    public MutableLiveData<String> getUserId() { return userId; }
    public MutableLiveData<String> getToastMessage() { return toastMessage; }
    public MutableLiveData<Boolean> getNavigateToLogin() { return navigateToLogin; }
    public MutableLiveData<Boolean> getIsLoading() { return isLoading; }
}
