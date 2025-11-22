package com.example.fenglishandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fenglishandroid.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // 使用一个简单的启动布局

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d(TAG, "MainActivity created, isTaskRoot: " + isTaskRoot());

        // 延迟跳转到登录界面
        navigateToLogin();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 检查是否是任务根Activity被销毁（即应用完全退出）
        if (isTaskRoot()) {
            clearToken();
            Log.d(TAG, "应用完全退出，Token已清除");
        } else {
            Log.d(TAG, "MainActivity销毁，但不是应用退出");
        }
    }

    private void navigateToLogin() {
        // 延迟跳转，显示启动画面
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // 关闭MainActivity
        }, 1000); // 1秒延迟
    }

    private void clearToken() {
        try {
            SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
            boolean hasToken = sp.contains("token");
            boolean hasUserId = sp.contains("user_id");

            if (hasToken || hasUserId) {
                sp.edit()
                        .remove("token")
                        .remove("user_id")
                        .apply();
                Log.d(TAG, "Token清除成功，原有token: " + hasToken + ", 原有user_id: " + hasUserId);
            } else {
                Log.d(TAG, "无需清除Token，本地无用户数据");
            }
        } catch (Exception e) {
            Log.e(TAG, "清除Token时发生错误", e);
        }
    }
}