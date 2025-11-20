package com.example.fenglishandroid.application;

import android.app.Application;

import com.example.fenglishandroid.service.RetrofitClient;

public class MyApp extends Application {

    private static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // 初始化网络服务
        RetrofitClient.init(this);

        // 可以在这里初始化其他全局组件
        // DatabaseManager.init(this);
        // PushService.init(this);
    }

    public static MyApp getInstance() {
        return instance;
    }
}