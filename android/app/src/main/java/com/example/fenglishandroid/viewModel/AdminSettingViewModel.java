package com.example.fenglishandroid.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.fenglishandroid.model.UserListResponse;
import com.example.fenglishandroid.model.BaseResponse;
import com.example.fenglishandroid.model.request.User;
import com.example.fenglishandroid.repository.AdminRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminSettingViewModel extends AndroidViewModel {

    private final AdminRepository adminRepository;

    // LiveData
    public final MutableLiveData<List<User>> userList = new MutableLiveData<>();
    public final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public final MutableLiveData<User> selectedUser = new MutableLiveData<>();

    // 分页相关
    public final MutableLiveData<Integer> currentPage = new MutableLiveData<>();
    public final MutableLiveData<Integer> totalPages = new MutableLiveData<>();
    public final MutableLiveData<Long> totalCount = new MutableLiveData<>();

    private String currentKeyword = "";
    private final int pageSize = 10;

    public AdminSettingViewModel(@NonNull Application application) {
        super(application);
        adminRepository = new AdminRepository(application);

        // 初始化数据
        userList.setValue(new ArrayList<>());
        isLoading.setValue(false);
        currentPage.setValue(0);
        totalPages.setValue(0);
        totalCount.setValue(0L);
    }

    // 搜索用户（重置到第一页）
    public void searchUsers(String keyword) {
        currentKeyword = keyword != null ? keyword : "";
        currentPage.setValue(0);
        loadUsers(true);
    }

    // 加载上一页
    public void loadPreviousPage() {
        int current = currentPage.getValue() != null ? currentPage.getValue() : 0;
        if (current > 0) {
            currentPage.setValue(current - 1);
            loadUsers(false);
        }
    }

    // 加载下一页
    public void loadNextPage() {
        int current = currentPage.getValue() != null ? currentPage.getValue() : 0;
        int total = totalPages.getValue() != null ? totalPages.getValue() : 0;
        if (current < total - 1) {
            currentPage.setValue(current + 1);
            loadUsers(false);
        }
    }

    // 新增：跳转到指定页
    public void goToPage(int pageNumber) {
        int total = totalPages.getValue() != null ? totalPages.getValue() : 0;

        // 验证页码有效性
        if (pageNumber < 1 || pageNumber > total) {
            return;
        }

        // 转换为从0开始的页码
        int targetPage = pageNumber - 1;
        int current = currentPage.getValue() != null ? currentPage.getValue() : 0;

        // 如果目标页与当前页相同，则不重复加载
        if (targetPage != current) {
            currentPage.setValue(targetPage);
            loadUsers(false);
        }
    }

    // 刷新用户列表（保持当前页）
    public void refreshUsers() {
        loadUsers(false);
    }

    // 加载用户列表
    private void loadUsers(boolean isNewSearch) {
        isLoading.setValue(true);

        int page = currentPage.getValue() != null ? currentPage.getValue() : 0;

        adminRepository.getUserList(currentKeyword, page, pageSize, new AdminRepository.OnUserListListener() {
            @Override
            public void onSuccess(UserListResponse response) {
                isLoading.setValue(false);
                if (response.isSuccess() && response.getUsers() != null) {
                    // 更新分页信息
                    totalPages.setValue(response.getTotalPages());
                    totalCount.setValue(response.getTotalCount());

                    if (isNewSearch || page == 0) {
                        // 新搜索或第一页，直接设置
                        userList.setValue(response.getUsers());
                    } else {
                        // 翻页，更新现有列表
                        userList.setValue(response.getUsers());
                    }

                    String message = "找到 " + response.getTotalCount() + " 个用户";
                    if (response.getTotalPages() > 1) {
                        message += "，共 " + response.getTotalPages() + " 页";
                    }
                    toastMessage.setValue(message);
                } else {
                    toastMessage.setValue("未找到用户");
                    userList.setValue(new ArrayList<>());
                    totalPages.setValue(0);
                    totalCount.setValue(0L);
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                isLoading.setValue(false);
                toastMessage.setValue(errorMsg);
            }
        });
    }

    // 获取用户详情
    public void getUserDetail(String userId) {
        isLoading.setValue(true);

        adminRepository.getUserById(userId, new AdminRepository.OnUserDetailListener() {
            @Override
            public void onSuccess(BaseResponse response) {
                isLoading.setValue(false);
                if (response.isSuccess() && response.getUserInfo() != null) {
                    selectedUser.setValue(parseUserInfoFromResponse(response));
                } else {
                    toastMessage.setValue(response.getMessage());
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                isLoading.setValue(false);
                toastMessage.setValue(errorMsg);
            }
        });
    }

    // 删除用户
    public void deleteUser(String userId) {
        isLoading.setValue(true);

        adminRepository.deleteUser(userId, new AdminRepository.OnDeleteUserListener() {
            @Override
            public void onSuccess(BaseResponse response) {
                isLoading.setValue(false);
                if (response.isSuccess()) {
                    toastMessage.setValue("用户删除成功");
                    // 删除成功后刷新列表
                    refreshUsers();
                } else {
                    toastMessage.setValue("删除失败: " + response.getMessage());
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                isLoading.setValue(false);
                toastMessage.setValue("删除失败: " + errorMsg);
            }
        });
    }

    // 从 BaseResponse 解析 UserInfo
    private User parseUserInfoFromResponse(BaseResponse response) {
        if (response == null || response.getUserInfo() == null) {
            return null;
        }

        Map<String, Object> userInfoMap = response.getUserInfo();
        User userInfo = new User();

        if (userInfoMap.get("userId") != null) {
            userInfo.setUserId(userInfoMap.get("userId").toString());
        }
        if (userInfoMap.get("userName") != null) {
            userInfo.setUserName(userInfoMap.get("userName").toString());
        }
        if (userInfoMap.get("userType") != null) {
            userInfo.setUserType(userInfoMap.get("userType").toString());
        }
        if (userInfoMap.get("phoneNumber") != null) {
            userInfo.setPhoneNumber(userInfoMap.get("phoneNumber").toString());
        }
        if (userInfoMap.get("userMailbox") != null) {
            userInfo.setUserMailbox(userInfoMap.get("userMailbox").toString());
        }
        if (userInfoMap.get("gender") != null) {
            userInfo.setGender(userInfoMap.get("gender").toString());
        }
        if (userInfoMap.get("registerTime") != null) {
            userInfo.setRegisterTime(userInfoMap.get("registerTime").toString());
        }

        return userInfo;
    }

    // LiveData Getters
    public MutableLiveData<List<User>> getUserList() { return userList; }
    public MutableLiveData<String> getToastMessage() { return toastMessage; }
    public MutableLiveData<Boolean> getIsLoading() { return isLoading; }
    public MutableLiveData<User> getSelectedUser() { return selectedUser; }
    public MutableLiveData<Integer> getCurrentPage() { return currentPage; }
    public MutableLiveData<Integer> getTotalPages() { return totalPages; }
    public MutableLiveData<Long> getTotalCount() { return totalCount; }
}