package com.example.fenglishandroid.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
                Toast.makeText(this, "管理员注册成功！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AdminLoginActivity.class);
                startActivity(intent);
                finish();
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
}