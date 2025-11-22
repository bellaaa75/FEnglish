package com.example.fenglishandroid.fragment.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fenglishandroid.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class AdminWordFragment extends Fragment {
    // 控件
    private EditText etSearch;
    private Button btnSearch, btnReset;
    private RecyclerView rvWordList;
    private ImageView ivPrev, ivNext;
    private TextView tvPage1, tvPage2, tvPage3, tvPage4;

    // 数据和适配器
    private List<Map<String, Object>> wordList = new ArrayList<>();
    private WordAdapter wordAdapter;
    private Gson gson = new Gson();

    // 分页参数
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;
    private String keyword = "";

    // Retrofit Service（内部接口，不新增文件）
    private WordService wordService;

    // 关键：和LoginActivity一致的SharedPreferences名称（仓库中是"MyApp"）
    private static final String SP_NAME = "MyApp";
    private static final String SP_KEY_TOKEN = "token";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_admin_word, container, false);

        // 关键修改：添加Token拦截器，从正确的SharedPreferences读取Token
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        // 1. 从LoginActivity存储Token的SharedPreferences中读取（名称：MyApp，键：token）
                        SharedPreferences sp = getContext().getSharedPreferences(SP_NAME, getContext().MODE_PRIVATE);
                        String token = sp.getString(SP_KEY_TOKEN, "");
                        Log.d("WordDebug", "读取到的Token：" + (token.isEmpty() ? "空" : token.substring(0, 20) + "...")); // 隐藏完整Token，保护隐私

                        // 2. 构建新请求，添加Authorization头（Bearer Token格式）
                        Request originalRequest = chain.request();
                        Request newRequest = originalRequest.newBuilder()
                                .addHeader("Authorization", "Bearer " + token) // 后端要求的Token格式
                                .build();

                        // 3. 继续执行请求
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        // 初始化Retrofit（使用10.0.2.2，适配模拟器，团队共用）
        wordService = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // 团队共用，模拟器自动映射电脑localhost
                .client(client) // 加入带Token的客户端
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WordService.class);

        initView(view);
        initAdapter();
        initListener();
        fetchWordList(); // 加载数据
        return view;
    }

    private void initView(View view) {
        etSearch = view.findViewById(R.id.et_search);
        btnSearch = view.findViewById(R.id.btn_search);
        btnReset = view.findViewById(R.id.btn_reset);
        rvWordList = view.findViewById(R.id.rv_word_list);
        ivPrev = view.findViewById(R.id.iv_prev);
        ivNext = view.findViewById(R.id.iv_next);
        tvPage1 = view.findViewById(R.id.tv_page_1);
        tvPage2 = view.findViewById(R.id.tv_page_2);
        tvPage3 = view.findViewById(R.id.tv_page_3);
        tvPage4 = view.findViewById(R.id.tv_page_4);
    }

    // RecyclerView适配器（内部类，无新增目录）
    private void initAdapter() {
        wordAdapter = new WordAdapter(wordList, new OnItemClickListener() {
            @Override
            public void onEditClick(Map<String, Object> word) {
                Toast.makeText(getContext(), "编辑单词：" + word.get("wordName"), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int wordID) {
                deleteWord(wordID); // 调用删除接口
            }
        });
        rvWordList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvWordList.setAdapter(wordAdapter);
    }

    private void initListener() {
        // 搜索
        btnSearch.setOnClickListener(v -> {
            keyword = etSearch.getText().toString().trim();
            currentPage = 1;
            fetchWordList();
        });

        // 重置
        btnReset.setOnClickListener(v -> {
            etSearch.setText("");
            keyword = "";
            currentPage = 1;
            fetchWordList();
        });

        // 分页点击
        ivPrev.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                updatePageUI();
                fetchWordList();
            } else {
                Toast.makeText(getContext(), "已是第一页", Toast.LENGTH_SHORT).show();
            }
        });

        ivNext.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                updatePageUI();
                fetchWordList();
            } else {
                Toast.makeText(getContext(), "已是最后一页", Toast.LENGTH_SHORT).show();
            }
        });

        tvPage1.setOnClickListener(v -> switchPage(1));
        tvPage2.setOnClickListener(v -> switchPage(2));
        tvPage3.setOnClickListener(v -> switchPage(3));
        tvPage4.setOnClickListener(v -> switchPage(4));
    }

    // 切换页码
    private void switchPage(int targetPage) {
        if (targetPage < 1 || targetPage > totalPages) return;
        currentPage = targetPage;
        updatePageUI();
        fetchWordList();
    }

    // 更新分页样式
    private void updatePageUI() {
        // 重置样式
        tvPage1.setBackgroundResource(0);
        tvPage1.setTextColor(getResources().getColor(R.color.black));
        tvPage2.setBackgroundResource(0);
        tvPage2.setTextColor(getResources().getColor(R.color.black));
        tvPage3.setBackgroundResource(0);
        tvPage3.setTextColor(getResources().getColor(R.color.black));
        tvPage4.setBackgroundResource(0);
        tvPage4.setTextColor(getResources().getColor(R.color.black));

        // 选中当前页
        switch (currentPage) {
            case 1:
                tvPage1.setBackgroundResource(R.drawable.shape_page_selected);
                tvPage1.setTextColor(getResources().getColor(R.color.white));
                break;
            case 2:
                tvPage2.setBackgroundResource(R.drawable.shape_page_selected);
                tvPage2.setTextColor(getResources().getColor(R.color.white));
                break;
            case 3:
                tvPage3.setBackgroundResource(R.drawable.shape_page_selected);
                tvPage3.setTextColor(getResources().getColor(R.color.white));
                break;
            case 4:
                tvPage4.setBackgroundResource(R.drawable.shape_page_selected);
                tvPage4.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    // 核心：调用后端分页接口（携带Token）
    private void fetchWordList() {
        // 1. 打印请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("pageNum", currentPage);
        params.put("pageSize", pageSize);
        if (!keyword.isEmpty()) {
            params.put("keyword", keyword);
        }
        Log.d("WordDebug", "请求参数：" + params.toString());

        // 2. 转换参数为JSON（和登录请求一致）
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                gson.toJson(params)
        );

        // 3. 发起POST请求（自动携带Token）
        Call<ResponseBody> call = wordService.getWordList(requestBody);
        Log.d("WordDebug", "请求URL：" + call.request().url().toString());
        Log.d("WordDebug", "请求头：" + call.request().headers().toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                // 打印响应状态码和响应头
                Log.d("WordDebug", "响应状态码：" + response.code());
                Log.d("WordDebug", "响应头：" + response.headers().toString());

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        Log.d("WordDebug", "响应数据：" + json); // 打印后端返回的完整JSON

                        Map<String, Object> result = gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
                        boolean success = (boolean) result.get("success");

                        if (success) {
                            List<Map<String, Object>> newWordList = gson.fromJson(
                                    gson.toJson(result.get("data")),
                                    new TypeToken<List<Map<String, Object>>>() {}.getType()
                            );
                            totalPages = ((Double) result.get("totalPages")).intValue();
                            int total = ((Double) result.get("total")).intValue();

                            // 更新UI
                            wordList.clear();
                            wordList.addAll(newWordList);
                            wordAdapter.notifyDataSetChanged();
                            updatePageUI();
                            Toast.makeText(getContext(), "共" + total + "个单词", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = result.get("message") != null ? result.get("message").toString() : "未知错误";
                            Toast.makeText(getContext(), "加载失败：" + message, Toast.LENGTH_SHORT).show();
                            Log.e("WordDebug", "加载失败：" + message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("WordDebug", "解析失败：" + e.getMessage());
                        Toast.makeText(getContext(), "解析失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 处理401（Token无效/过期/未携带）
                    if (response.code() == 401) {
                        Toast.makeText(getContext(), "Token无效/未登录，请重新登录", Toast.LENGTH_SHORT).show();
                        Log.e("WordDebug", "401未授权：Token可能为空或无效");
                    } else if (response.code() == 404) {
                        Toast.makeText(getContext(), "接口路径错误（404）", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 500) {
                        Toast.makeText(getContext(), "后端接口报错（500）", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "请求失败：" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("WordDebug", "网络失败：" + t.getMessage(), t);
            }
        });
    }

    // 调用后端删除接口（携带Token）
    private void deleteWord(int wordID) {
        wordService.deleteWord(wordID).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        Log.d("WordDebug", "删除响应数据：" + json);
                        Map<String, Object> result = gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
                        boolean success = (boolean) result.get("success");

                        if (success) {
                            Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                            fetchWordList(); // 刷新列表
                        } else {
                            String message = result.get("message") != null ? result.get("message").toString() : "未知错误";
                            Toast.makeText(getContext(), "删除失败：" + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "解析失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(getContext(), "Token无效/未登录，请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "删除失败，状态码：" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("WordDebug", "删除网络失败：" + t.getMessage(), t);
            }
        });
    }

    // 内部点击事件接口
    interface OnItemClickListener {
        void onEditClick(Map<String, Object> word);
        void onDeleteClick(int wordID);
    }

    // 内部RecyclerView适配器
    class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {
        private List<Map<String, Object>> data;
        private OnItemClickListener listener;

        public WordAdapter(List<Map<String, Object>> data, OnItemClickListener listener) {
            this.data = data;
            this.listener = listener;
        }

        @NonNull
        @Override
        public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_word, parent, false);
            return new WordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
            Map<String, Object> word = data.get(position);
            // 绑定数据（后端返回的字段）
            holder.tvWordName.setText(word.get("wordName") != null ? word.get("wordName").toString() : "");
            holder.tvPartOfSpeech.setText(word.get("partOfSpeech") != null ? word.get("partOfSpeech").toString() : "");
            holder.tvWordExplain.setText(word.get("wordExplain") != null ? word.get("wordExplain").toString() : "");

            // 操作事件
            holder.tvEdit.setOnClickListener(v -> listener.onEditClick(word));
            holder.tvDelete.setOnClickListener(v -> {
                int wordID = word.get("wordID") != null ? ((Double) word.get("wordID")).intValue() : 0;
                if (wordID != 0) {
                    listener.onDeleteClick(wordID);
                } else {
                    Toast.makeText(getContext(), "单词ID无效", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        class WordViewHolder extends RecyclerView.ViewHolder {
            TextView tvWordName, tvPartOfSpeech, tvWordExplain, tvEdit, tvDelete;

            public WordViewHolder(@NonNull View itemView) {
                super(itemView);
                tvWordName = itemView.findViewById(R.id.tv_word_name);
                tvPartOfSpeech = itemView.findViewById(R.id.tv_part_of_speech);
                tvWordExplain = itemView.findViewById(R.id.tv_word_explain);
                tvEdit = itemView.findViewById(R.id.tv_edit);
                tvDelete = itemView.findViewById(R.id.tv_delete);
            }
        }
    }

    // 内部Retrofit接口（相对路径）
    interface WordService {
        @POST("api/words")
        Call<ResponseBody> getWordList(@Body RequestBody requestBody);

        @DELETE("api/words/{wordID}")
        Call<ResponseBody> deleteWord(@Path("wordID") int wordID);
    }
}