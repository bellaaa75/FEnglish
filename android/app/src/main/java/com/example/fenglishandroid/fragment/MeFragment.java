package com.example.fenglishandroid.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fenglishandroid.R;
import com.example.fenglishandroid.databinding.FragmentMeBinding;
import com.example.fenglishandroid.model.request.User;
import com.example.fenglishandroid.model.request.ChangePasswordRequest;
import com.example.fenglishandroid.model.request.DeleteAccountRequest;
import com.example.fenglishandroid.model.request.UpdateUserRequest;
import com.example.fenglishandroid.ui.login.LoginActivity;
import com.example.fenglishandroid.ui.record.LearningTrackActivity;
import com.example.fenglishandroid.viewModel.UserProfileViewModel;

import java.util.Map;

public class MeFragment extends Fragment {

    private FragmentMeBinding binding;
    private UserProfileViewModel userProfileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("MeFragmentLifecycle", "Fragment 视图已创建并显示");
        Log.d("MeFragmentLifecycle", "根视图宽度：" + view.getWidth() + "，高度：" + view.getHeight()); // 应输出非0值

        // 打印根视图信息
        Log.d("MeFragmentDebug", "根视图类型：" + view.getClass().getName()); // 应输出 ScrollView
        Log.d("MeFragmentDebug", "学习轨迹控件是否存在：" + (binding.layoutLearningTrack != null)); // 应输出 true
        Log.d("MeFragmentDebug", "编辑按钮是否存在：" + (binding.btnEditProfile != null)); // 应输出 true

        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);

        // 设置数据绑定
        binding.setViewModel(userProfileViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        setupObservers();
        setupClickListeners();
    }

    private void setupObservers() {
        // 观察用户数据 - 数据绑定会自动更新UI，所以可以移除手动更新
        userProfileViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.setUser(user);
            }
        });

        // 观察加载状态 - 数据绑定会自动处理
        // userProfileViewModel.getIsLoading() 已经在布局中绑定了

        // 观察Toast消息
        userProfileViewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // 观察导航到登录页面的请求
        userProfileViewModel.getNavigateToLogin().observe(getViewLifecycleOwner(), shouldNavigate -> {
            if (shouldNavigate != null && shouldNavigate) {
                navigateToLogin();
            }
        });
    }

    private void setupClickListeners() {
        // 编辑个人信息 - 直接通过binding访问
        binding.btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        // 学习轨迹
        binding.layoutLearningTrack.setOnClickListener(v -> {
            Log.d("LearningTrackDebug", "点击了学习轨迹按钮");

            SharedPreferences sp = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);

            // 调试：打印所有存储的键值对
            Map<String, ?> allEntries = sp.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d("SharedPreferencesDebug", "Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }

            String userId = sp.getString("userId", "");
            Log.d("LearningTrackDebug", "从SharedPreferences获取的userId: '" + userId + "'");

            if (userId.isEmpty()) {
                Toast.makeText(getContext(), "用户信息获取失败，请重新登录", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getContext(), LearningTrackActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        // 修改密码
        binding.layoutChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        // 注销账号
        binding.layoutDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());

        // 退出登录
        binding.layoutLogout.setOnClickListener(v -> showLogoutDialog());
    }


    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
        setupEditDialogViews(dialogView);

        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        // 设置保存按钮点击事件
        setupSaveButton(dialogView, dialog);
    }

    private void setupEditDialogViews(View dialogView) {
        User currentUser = userProfileViewModel.getUserLiveData().getValue();
        if (currentUser == null) return;

        EditText etUsername = dialogView.findViewById(R.id.et_username);
        TextView etGender = dialogView.findViewById(R.id.et_gender);
        EditText etPhone = dialogView.findViewById(R.id.et_phone);
        EditText etEmail = dialogView.findViewById(R.id.et_email);

        etUsername.setText(currentUser.getUserName());
        etGender.setText(currentUser.getGender());
        etPhone.setText(currentUser.getPhoneNumber());
        etEmail.setText(currentUser.getUserMailbox());

        // 性别选择
        etGender.setOnClickListener(v -> showGenderSelectionDialog(etGender));
    }

    private void setupSaveButton(View dialogView, AlertDialog dialog) {
        Button btnSave = dialogView.findViewById(R.id.btn_save_profile);
        btnSave.setOnClickListener(v -> {
            saveUserInfo(dialogView);
            dialog.dismiss(); // 保存后关闭对话框
        });
    }

    private void showGenderSelectionDialog(TextView genderView) {
        String[] genders = {"男", "女", "保密"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("选择性别")
                .setItems(genders, (dialog, which) -> genderView.setText(genders[which]));
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void saveUserInfo(View dialogView) {
        EditText etUsername = dialogView.findViewById(R.id.et_username);
        TextView etGender = dialogView.findViewById(R.id.et_gender);
        EditText etPhone = dialogView.findViewById(R.id.et_phone);
        EditText etEmail = dialogView.findViewById(R.id.et_email);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setUserName(etUsername.getText().toString());
        request.setGender(etGender.getText().toString());
        request.setPhoneNumber(etPhone.getText().toString());
        request.setUserMailbox(etEmail.getText().toString());

        userProfileViewModel.updateUser(request);
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);

        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        // 设置确认按钮点击事件
        Button btnConfirmChange = dialogView.findViewById(R.id.btn_confirm_change);
        btnConfirmChange.setOnClickListener(v -> {
            EditText etOldPassword = dialogView.findViewById(R.id.et_old_password);
            EditText etNewPassword = dialogView.findViewById(R.id.et_new_password);
            EditText etConfirmPassword = dialogView.findViewById(R.id.et_confirm_password);

            String oldPassword = etOldPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(getContext(), "请填写所有密码字段", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(getContext(), "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() < 6) {
                Toast.makeText(getContext(), "密码长度不能少于6位", Toast.LENGTH_SHORT).show();
                return;
            }

            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setUsePasswordVerification(true);
            request.setOldPassword(oldPassword);
            request.setNewPassword(newPassword);
            request.setConfirmPassword(confirmPassword);

            userProfileViewModel.changePassword(request);
            dialog.dismiss();
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }

    private void showDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // 1. 加载自定义布局
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_account, null);
        builder.setView(dialogView);

        // 2. 创建对话框并立即显示
        AlertDialog dialog = builder.create();
        dialog.show();

        // 3. 从已显示的对话框中找到确认按钮，并设置点击事件
        Button confirmButton = dialogView.findViewById(R.id.btn_confirm_delete);
        confirmButton.setOnClickListener(v -> {
            // 在按钮的点击事件中处理逻辑
            EditText etPassword = dialogView.findViewById(R.id.et_password);
            String password = etPassword.getText().toString().trim();

            if (password.isEmpty()) {
                Toast.makeText(getContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }

            DeleteAccountRequest request = new DeleteAccountRequest();
            request.setPassword(password);

            // 调用 ViewModel 的方法执行注销
            userProfileViewModel.deleteAccount(request);

            // 4. 直接使用已创建的 dialog 对象关闭对话框
            dialog.dismiss();
        });

        // 可选：设置对话框的其他属性
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定", (dialog, which) -> userProfileViewModel.logout())
                .setNegativeButton("取消", null)
                .show();
    }

    private void navigateToLogin() {
        if (getActivity() == null) return;

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        // 关键：清除当前任务栈中所有Activity，只保留登录页
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // 关闭当前Fragment所在的Activity（通常是首页）
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}