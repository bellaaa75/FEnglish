package com.example.fenglishandroid.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fenglishandroid.model.BaseResponse;
import com.example.fenglishandroid.model.request.User;
import com.example.fenglishandroid.model.request.ChangePasswordRequest;
import com.example.fenglishandroid.model.request.DeleteAccountRequest;
import com.example.fenglishandroid.model.request.UpdateUserRequest;
import com.example.fenglishandroid.repository.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserProfileViewModel extends AndroidViewModel {
    private static final String TAG = "UserProfileViewModel";
    private UserRepository repository;

    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> navigateToLogin = new MutableLiveData<>();
    private MutableLiveData<BaseResponse> updateUserLiveData = new MutableLiveData<>();
    private MutableLiveData<BaseResponse> changePasswordLiveData = new MutableLiveData<>();
    private MutableLiveData<BaseResponse> deleteAccountLiveData = new MutableLiveData<>();

    public UserProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);

        // 初始化加载状态
        isLoading.setValue(false);

        // 检查是否已登录，如果已登录则加载用户信息
        if (repository.isLoggedIn()) {
            fetchUserInfo();
        } else {
            // 如果没有登录，加载模拟数据（仅用于演示）
            loadMockUserData();
        }
    }

    private void loadMockUserData() {
        User mockUser = new User(
                "123456",
                "张三",
                "女",
                "12345678900",
                "123456@qq.com",
                "2024年11月"
        );
        userLiveData.setValue(mockUser);
    }

    // 获取用户信息（从服务器）
    public void fetchUserInfo() {
        if (!repository.isLoggedIn()) {
            toastMessage.setValue("请先登录");
            navigateToLogin.setValue(true);
            return;
        }

        isLoading.setValue(true);
        repository.getUserInfo(new UserRepository.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse response) {
                isLoading.setValue(false);
                if (response.isSuccess() && response.getUserInfo() != null) {
                    // 解析用户信息
                    User user = parseUserFromResponse(response);
                    userLiveData.setValue(user);
                    //toastMessage.setValue("用户信息加载成功");
                } else {
                    toastMessage.setValue(response.getMessage());
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                isLoading.setValue(false);
                toastMessage.setValue(errorMsg);
                // 如果是因为token过期，跳转到登录页面
                if (errorMsg.contains("登录已过期")) {
                    navigateToLogin.setValue(true);
                }
            }
        });
    }

    // 更新用户信息
    public void updateUser(UpdateUserRequest request) {
        if (!repository.isLoggedIn()) {
            toastMessage.setValue("请先登录");
            navigateToLogin.setValue(true);
            return;
        }

        //有效性
        if (!isValidUpdateRequest(request)) {
            toastMessage.setValue("请填写有效的用户信息");
            return;
        }

        isLoading.setValue(true);
        repository.updateUser(request, new UserRepository.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse response) {
                isLoading.setValue(false);
                updateUserLiveData.setValue(response);
                if (response.isSuccess()) {
                    toastMessage.setValue("个人信息更新成功");
                    // 重新获取用户信息
                    fetchUserInfo();
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

    // 修改密码
    public void changePassword(ChangePasswordRequest request) {
        if (!repository.isLoggedIn()) {
            toastMessage.setValue("请先登录");
            navigateToLogin.setValue(true);
            return;
        }

        // 前端验证
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            toastMessage.setValue("两次输入的密码不一致");
            return;
        }

        if (request.getNewPassword().length() < 6) {
            toastMessage.setValue("密码长度不能少于6位");
            return;
        }

        isLoading.setValue(true);
        repository.changePassword(request, new UserRepository.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse response) {
                isLoading.setValue(false);
                changePasswordLiveData.setValue(response);
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
        if (!repository.isLoggedIn()) {
            toastMessage.setValue("请先登录");
            navigateToLogin.setValue(true);
            return;
        }

        isLoading.setValue(true);
        repository.deleteAccount(request, new UserRepository.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse response) {
                isLoading.setValue(false);
                deleteAccountLiveData.setValue(response);
                if (response.isSuccess()) {
                    toastMessage.setValue("账号已注销");
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
    public void logout() {
        if (!repository.isLoggedIn()) {
            // 如果本来就没登录，直接跳转到登录页面
            navigateToLogin.setValue(true);
            return;
        }

        isLoading.setValue(true);
        repository.logout();
        navigateToLogin.postValue(true);
    }

    private User parseUserFromResponse(BaseResponse response) {
        // 从BaseResponse中解析User对象
        // 这里需要根据实际的API响应结构来解析
        return new User(
                (String) response.getUserInfo().get("userId"),
                (String) response.getUserInfo().get("userName"),
                (String) response.getUserInfo().get("gender"),
                (String) response.getUserInfo().get("phoneNumber"),
                (String) response.getUserInfo().get("userMailbox"),
                formatRegisterTime((String) response.getUserInfo().get("registerTime"))
        );
    }
    private boolean isValidUpdateRequest(UpdateUserRequest request) {
        // 验证用户名
        if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
            return false;
        }

        // 验证邮箱格式（如果提供了邮箱）
        if (request.getUserMailbox() != null && !request.getUserMailbox().isEmpty()) {
            if (!isValidEmail(request.getUserMailbox())) {
                return false;
            }
        }

        // 验证手机号格式（如果提供了手机号）
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            if (!isValidPhoneNumber(request.getPhoneNumber())) {
                return false;
            }
        }

        return true;
    }

    // 5. 邮箱格式验证
    private boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailPattern);
    }

    // 6. 手机号格式验证
    private boolean isValidPhoneNumber(String phone) {
        // 简单的手机号验证，可根据需求调整
        String phonePattern = "^1[3-9]\\d{9}$";
        return phone != null && phone.matches(phonePattern);
    }

    private String formatRegisterTime(String originalTime) {
        if (originalTime == null || originalTime.isEmpty()) {
            return "未知时间";
        }

        try {
            // 方案1: 如果是时间戳格式
            if (originalTime.matches("\\d+")) {
                long timestamp = Long.parseLong(originalTime);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
                return sdf.format(new Date(timestamp));
            }

            // 方案2: 如果是 ISO 8601 格式 (2024-11-25T10:30:00)
            if (originalTime.contains("T")) {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
                Date date = inputFormat.parse(originalTime);
                return outputFormat.format(date);
            }

            // 方案3: 如果是其他格式，直接返回或简单处理
            return originalTime;

        } catch (Exception e) {
            Log.e("UserProfileViewModel", "时间格式化失败: " + originalTime, e);
            return originalTime; // 格式化失败返回原值
        }
    }
    // Getter方法 - 返回 LiveData 而不是 MutableLiveData
    public LiveData<User> getUserLiveData() { return userLiveData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getToastMessage() { return toastMessage; }
    public LiveData<Boolean> getNavigateToLogin() { return navigateToLogin; }
    public LiveData<BaseResponse> getUpdateUserLiveData() { return updateUserLiveData; }
    public LiveData<BaseResponse> getChangePasswordLiveData() { return changePasswordLiveData; }
    public LiveData<BaseResponse> getDeleteAccountLiveData() { return deleteAccountLiveData; }
}