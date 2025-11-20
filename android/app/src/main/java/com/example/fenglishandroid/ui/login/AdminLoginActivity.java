package com.example.fenglishandroid.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.request.LoginRequest;
import com.example.fenglishandroid.ui.register.AdminRegisterActivity;
import com.example.fenglishandroid.viewModel.UserViewModel;

public class AdminLoginActivity extends AppCompatActivity {
    private EditText etIdentifier, etPassword;
    private Button btnLogin;
    private TextView tvAdminRegister, tvBackToOrdinary;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        initViews();
        initViewModel();
        setupClickListeners();
    }

    private void initViews() {
        etIdentifier = findViewById(R.id.et_identifier);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvAdminRegister = findViewById(R.id.tv_admin_register);
        tvBackToOrdinary = findViewById(R.id.tv_back_to_ordinary);
    }

    private void initViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getLoginLiveData().observe(this, response -> {
            if (response.isSuccess()) {
                Toast.makeText(this, "管理员登录成功！", Toast.LENGTH_SHORT).show();
                saveUserInfo(response.getToken(), response.getUserId(), "admin");
                navigateToAdminMain();
            } else {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        userViewModel.getErrorLiveData().observe(this, errorMsg -> {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> doLogin());

        tvAdminRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminRegisterActivity.class);
            startActivity(intent);
        });

        tvBackToOrdinary.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void doLogin() {
        String identifier = etIdentifier.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (identifier.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest request = new LoginRequest();
        request.setIdentifier(identifier);
        request.setPassword(password);

        userViewModel.loginAdmin(request);
    }

    private void saveUserInfo(String token, String userId, String userType) {
        getSharedPreferences("user_info", MODE_PRIVATE)
                .edit()
                .putString("token", token)
                .putString("user_id", userId)
                .putString("user_type", userType)
                .apply();
    }

    private void navigateToAdminMain() {
        // 跳转到管理员主界面
        // Intent intent = new Intent(this, AdminMainActivity.class);
        // startActivity(intent);
        finish();
    }
}