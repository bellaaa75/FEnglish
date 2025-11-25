package com.example.fenglishandroid.fragment.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fenglishandroid.R;
import com.example.fenglishandroid.databinding.FragAdminAccountBinding;
import com.example.fenglishandroid.model.request.ChangePasswordRequest;
import com.example.fenglishandroid.model.request.DeleteAccountRequest;
import com.example.fenglishandroid.ui.login.AdminLoginActivity;
import com.example.fenglishandroid.viewModel.AdminAccountViewModel;

public class AdminAccountFragment extends Fragment {

    private FragAdminAccountBinding binding;
    private AdminAccountViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragAdminAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 添加生命周期日志
        Log.d("AdminAccountFragment", "Fragment 视图已创建并显示");
        Log.d("AdminAccountFragment", "根视图宽度：" + view.getWidth() + "，高度：" + view.getHeight());

        // 打印控件信息
        Log.d("AdminAccountFragment", "根视图类型：" + view.getClass().getName());
        Log.d("AdminAccountFragment", "修改密码控件是否存在：" + (binding.layoutChangePassword != null));
        Log.d("AdminAccountFragment", "注销账号控件是否存在：" + (binding.layoutDeleteAccount != null));
        Log.d("AdminAccountFragment", "退出登录控件是否存在：" + (binding.layoutLogout != null));

        viewModel = new ViewModelProvider(this).get(AdminAccountViewModel.class);

        // 设置数据绑定
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        setupObservers();
        setupClickListeners();
    }

    private void setupObservers() {
        // 观察用户ID数据
        viewModel.getUserId().observe(getViewLifecycleOwner(), userId -> {
            if (userId != null) {
                Log.d("AdminAccountFragment", "用户ID更新: " + userId);
            }
        });

        // 观察Toast消息
        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Log.d("AdminAccountFragment", "显示Toast消息: " + message);
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // 观察导航到登录页面的请求
        viewModel.getNavigateToLogin().observe(getViewLifecycleOwner(), shouldNavigate -> {
            if (shouldNavigate != null && shouldNavigate) {
                Log.d("AdminAccountFragment", "导航到登录页面");
                navigateToLogin();
            }
        });

        // 观察加载状态
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d("AdminAccountFragment", "加载状态: " + isLoading);
        });
    }

    private void setupClickListeners() {
        // 修改密码
        binding.layoutChangePassword.setOnClickListener(v -> {
            Log.d("AdminAccountFragment", "点击修改密码");
            showChangePasswordDialog();
        });

        // 注销账号
        binding.layoutDeleteAccount.setOnClickListener(v -> {
            Log.d("AdminAccountFragment", "点击注销账号");
            showDeleteAccountDialog();
        });

        // 退出登录
        binding.layoutLogout.setOnClickListener(v -> {
            Log.d("AdminAccountFragment", "点击退出登录");
            showLogoutDialog();
        });
    }

    private void showChangePasswordDialog() {
        Log.d("AdminAccountFragment", "显示修改密码对话框");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("修改密码");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        // 设置确认按钮点击事件
        Button btnConfirmChange = dialogView.findViewById(R.id.btn_confirm_change);
        btnConfirmChange.setOnClickListener(v -> {
            Log.d("AdminAccountFragment", "点击确认修改密码");

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

            Log.d("AdminAccountFragment", "调用ViewModel修改密码");
            viewModel.changePassword(request);
            dialog.dismiss();
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }

    private void showDeleteAccountDialog() {
        Log.d("AdminAccountFragment", "显示注销账号对话框");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("注销账号");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_account, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button confirmButton = dialogView.findViewById(R.id.btn_confirm_delete);
        confirmButton.setOnClickListener(v -> {
            Log.d("AdminAccountFragment", "点击确认注销账号");

            EditText etPassword = dialogView.findViewById(R.id.et_password);
            String password = etPassword.getText().toString().trim();

            if (password.isEmpty()) {
                Toast.makeText(getContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }

            DeleteAccountRequest request = new DeleteAccountRequest();
            request.setPassword(password);

            Log.d("AdminAccountFragment", "调用ViewModel注销账号");
            viewModel.deleteAccount(request);
            dialog.dismiss();
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }

    private void showLogoutDialog() {
        Log.d("AdminAccountFragment", "显示退出登录对话框");

        new AlertDialog.Builder(getContext())
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    Log.d("AdminAccountFragment", "确认退出登录");
                    viewModel.logout();
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    Log.d("AdminAccountFragment", "取消退出登录");
                    dialog.dismiss();
                })
                .show();
    }

    private void navigateToLogin() {
        if (getActivity() == null) {
            Log.w("AdminAccountFragment", "Activity为null，无法跳转");
            return;
        }

        try {
            Log.d("AdminAccountFragment", "执行跳转到管理员登录页面");
            Intent intent = new Intent(getActivity(), AdminLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        } catch (Exception e) {
            Log.e("AdminAccountFragment", "跳转异常", e);
            Toast.makeText(getContext(), "跳转失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("AdminAccountFragment", "Fragment视图销毁");
        binding = null;
    }
}