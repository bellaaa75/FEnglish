package com.example.fenglishandroid.ui.register;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.request.AdminRegisterRequest;
import com.example.fenglishandroid.ui.login.AdminLoginActivity;
import com.example.fenglishandroid.viewModel.UserViewModel;

public class AdminRegisterActivity extends AppCompatActivity {
    private EditText etUserPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvBackToAdminLogin;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        initViews();
        initViewModel();
        setupClickListeners();
    }

    private void initViews() {
        etUserPassword = findViewById(R.id.et_user_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvBackToAdminLogin = findViewById(R.id.tv_back_to_admin_login);
    }

    private void initViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getRegisterLiveData().observe(this, response -> {
            if (response.isSuccess()) {
                // 显示用户ID弹窗
                showUserIdDialog(response.getUserId());
                /*Toast.makeText(this, "管理员注册成功！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AdminLoginActivity.class);
                startActivity(intent);
                finish();*/
            } else {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        userViewModel.getErrorLiveData().observe(this, errorMsg -> {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> doRegister());

        tvBackToAdminLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminLoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void doRegister() {
        String userPassword = etUserPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (userPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userPassword.equals(confirmPassword)) {
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        AdminRegisterRequest request = new AdminRegisterRequest();
        request.setUserPassword(userPassword);
        request.setConfirmPassword(confirmPassword);

        userViewModel.registerAdmin(request);
    }

    private void showUserIdDialog(String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("管理员注册成功");

        // 自定义布局
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_user_id, null);
        TextView tvUserId = dialogView.findViewById(R.id.tv_user_id);
        TextView tvWarning = dialogView.findViewById(R.id.tv_warning);

        // 设置用户ID和警告信息
        tvUserId.setText(userId);
        tvWarning.setText("请妥善保存此用户ID，这是您登录的唯一凭证！");

        builder.setView(dialogView);
        builder.setPositiveButton("我已保存", (dialog, which) -> {
            // 跳转到管理员登录页面
            Intent intent = new Intent(this, AdminLoginActivity.class);
            startActivity(intent);
            finish();
        });

        // 设置弹窗不可取消（避免用户误触）
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();

        // 可选：自定义按钮样式
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (positiveButton != null) {
            positiveButton.setTextColor(getResources().getColor(R.color.primary_color, getTheme()));
        }
    }

}