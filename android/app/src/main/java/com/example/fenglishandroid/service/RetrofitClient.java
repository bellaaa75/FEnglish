package com.example.fenglishandroid.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/"; // 替换为实际后端地址
    private static Retrofit retrofit;

    private static Context mContext;

    // 初始化时传入上下文，用于获取SharedPreferences中的Token和UserId
    public static void init(Context context) {
        mContext = context.getApplicationContext();
        Log.d("RetrofitClient", "RetrofitClient initialized with context");
    }

    public static Retrofit getInstance() {
        if (retrofit == null) {
            if (mContext == null) {
                throw new IllegalStateException("RetrofitClient must be initialized with init() first");
            }
            // 添加日志拦截器
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 创建OkHttpClient
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Log.d("RetrofitClient", "Making request to: " + chain.request().url());
                        String token = getSavedToken();
                        String userId = getSavedUserId(); // 获取保存的 userId
                        Request original = chain.request();

                        Request.Builder builder = original.newBuilder();
                        if (token != null && !token.isEmpty()) {
                            builder.header("Authorization", "Bearer " + token);
                            Log.d("RetrofitClient", "Added Authorization header");
                        }
                        if (userId != null && !userId.isEmpty()) {
                            builder.header("userId", userId); // 添加 userId 请求头
                            Log.d("RetrofitClient", "Added userId header: " + userId);
                        }
                        return chain.proceed(builder.build());
                    })
                    .addInterceptor(logging) // 添加日志
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)  // 设置自定义的OkHttpClient
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Log.d("RetrofitClient", "Retrofit instance created with base URL: " + BASE_URL);
        }
        return retrofit;
    }

    // 获取保存的Token
    private static String getSavedToken() {
        if (mContext == null) {
            return null;
        }
        SharedPreferences sp = mContext.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        return sp.getString("token", null);
    }

    // 获取保存的UserId
    private static String getSavedUserId() {
        if (mContext == null) {
            return null;
        }
        SharedPreferences sp = mContext.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        return sp.getString("userId", null); // 确保保存了 userId
    }

    public static UserApiService getUserApi() {
        return getInstance().create(UserApiService.class);
    }

    public static VocabularyBookService getVocabularyBookService() {
        return getInstance().create(VocabularyBookService.class);
    }

    public static WordService getWordService() {
        return getInstance().create(WordService.class);
    }

    public static StudyRecordService getStudyRecordService() {
        return getInstance().create(StudyRecordService.class);
    }

    public static CollectApiService getCollectApi() {
        return getInstance().create(CollectApiService.class);
    }

    public static AdminService getAdminService() {
        return getInstance().create(AdminService.class);
    }
}