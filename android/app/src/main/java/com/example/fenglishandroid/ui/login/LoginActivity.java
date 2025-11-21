package com.example.fenglishandroid.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.request.LoginRequest;
import com.example.fenglishandroid.ui.index.IndexActivity;
import com.example.fenglishandroid.ui.register.RegisterActivity;
import com.example.fenglishandroid.viewModel.UserViewModel;

public class LoginActivity extends AppCompatActivity {
    private EditText etIdentifier, etPassword;
    private Button btnLogin;
    private TextView tvGoRegister, tvAdminLogin;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initViewModel();
        setupClickListeners();

        // 检查是否已登录，如果已登录直接跳转到主界面
        checkLoginStatus();
    }

    private void initViews() {
        etIdentifier = findViewById(R.id.et_identifier);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvGoRegister = findViewById(R.id.tv_go_register);
        tvAdminLogin = findViewById(R.id.tv_admin_login);
    }

    private void initViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // 观察登录结果
        userViewModel.getLoginLiveData().observe(this, response -> {
            btnLogin.setEnabled(true);
            if (isDestroyed() || isFinishing()) {
                return; // Activity正在销毁，不处理回调
            }
            if (response != null && response.isSuccess()) {
                if (response.getToken() == null || response.getUserId() == null) {
                    Toast.makeText(this, "登录信息异常", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();
                // 跳转到主界面
                navigateToMain();
            } else {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // 观察错误信息
        userViewModel.getErrorLiveData().observe(this, errorMsg -> {
            btnLogin.setEnabled(true);
            if (isDestroyed() || isFinishing()) {
                return; // Activity正在销毁，不处理回调
            }
            if (errorMsg != null)
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupClickListeners() {
        // 登录按钮
        btnLogin.setOnClickListener(v -> doLogin());

        // 跳转到注册界面
        tvGoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 跳转到管理员登录界面
        tvAdminLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, AdminLoginActivity.class);
            startActivity(intent);
            finish(); // 关闭当前界面
        });
    }

    private void doLogin() {
        btnLogin.setEnabled(false);

        try {
            String identifier = etIdentifier.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (identifier.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                btnLogin.setEnabled(true);
                return;
            }

            LoginRequest request = new LoginRequest();
            request.setIdentifier(identifier);
            request.setPassword(password);

            // 调用普通用户登录
            userViewModel.loginOrdinary(request);
        } catch (Exception e) {
            btnLogin.setEnabled(true);
            Log.e("LoginActivity", "登录异常", e);
        }
    }

    // 新增：检查登录状态
    private void checkLoginStatus() {
        if (userViewModel.isLoggedIn()) {
            // 如果已经登录，直接跳转到主界面
            Log.d("LoginActivity", "用户已登录，自动跳转到主界面");
            navigateToMain();
        }
    }

    private void navigateToMain() {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        try {
            // 模拟一些初始化工作（如果有的话）
            Intent intent = new Intent(LoginActivity.this, IndexActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Log.e("LoginActivity", "跳转异常", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理观察者（ViewModelProvider 会自动处理，这里只是保险）
        if (userViewModel != null) {
            userViewModel.getLoginLiveData().removeObservers(this);
            userViewModel.getErrorLiveData().removeObservers(this);
        }
    }
}