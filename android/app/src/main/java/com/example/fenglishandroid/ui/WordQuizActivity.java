package com.example.fenglishandroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.WordListResult;
import com.example.fenglishandroid.model.WordSimpleResp;
import com.example.fenglishandroid.service.RetrofitClient;
import com.example.fenglishandroid.service.WordService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WordQuizActivity extends AppCompatActivity {
    // 控件（LinearLayout布局适配）
    private ImageView iv_quiz_back;
    private TextView tv_quiz_word, tv_quiz_option1, tv_quiz_option2, tv_quiz_option3;
    private Button btn_quiz_view_explain;

    // 数据
    private String currentWordId;
    private WordSimpleResp currentWord;
    private List<String> options = new ArrayList<>();
    private WordService wordService;
    private Random random = new Random();
    private boolean isOptionClicked = false;
    private final Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_quiz); // 对应修改后的LinearLayout布局
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // 修复：用你的 RetrofitClient 实际方法 getWordService()
        wordService = RetrofitClient.getWordService();
        initViews();
        currentWordId = getIntent().getStringExtra("wordId");
        if (currentWordId == null || currentWordId.isEmpty()) {
            Toast.makeText(this, "单词ID不能为空", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadCurrentWordDetail();
        loadDistractorOptions();
        bindEvents();
    }

    private void initViews() {
        iv_quiz_back = findViewById(R.id.iv_quiz_back);
        tv_quiz_word = findViewById(R.id.tv_quiz_word);
        tv_quiz_option1 = findViewById(R.id.tv_quiz_option1);
        tv_quiz_option2 = findViewById(R.id.tv_quiz_option2);
        tv_quiz_option3 = findViewById(R.id.tv_quiz_option3);
        btn_quiz_view_explain = findViewById(R.id.btn_quiz_view_explain);
    }

    private void bindEvents() {
        iv_quiz_back.setOnClickListener(v -> finish());
        tv_quiz_option1.setOnClickListener(v -> { if (!isOptionClicked) checkOption(tv_quiz_option1); });
        tv_quiz_option2.setOnClickListener(v -> { if (!isOptionClicked) checkOption(tv_quiz_option2); });
        tv_quiz_option3.setOnClickListener(v -> { if (!isOptionClicked) checkOption(tv_quiz_option3); });
        btn_quiz_view_explain.setOnClickListener(v -> { if (!isOptionClicked) jumpToDetail(); });
    }

    // 加载当前单词详情（调用 WordService 的 getWordDetail 方法）
    private void loadCurrentWordDetail() {
        // 修复：确保 WordService 中存在 getWordDetail 方法（下方附 WordService 完整代码）
        Call<WordSimpleResp> call = wordService.getWordDetail(currentWordId);
        call.enqueue(new Callback<WordSimpleResp>() {
            @Override
            public void onResponse(Call<WordSimpleResp> call, Response<WordSimpleResp> response) {
                if (response.code() == 404) {
                    Toast.makeText(WordQuizActivity.this, "该单词不存在", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    currentWord = response.body();
                    tv_quiz_word.setText(currentWord.getWordName());
                    String correctExplain = currentWord.getWordExplain() != null ? currentWord.getWordExplain() : "无释义";
                    options.add(correctExplain);
                } else {
                    Toast.makeText(WordQuizActivity.this, "加载单词失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WordSimpleResp> call, Throwable t) {
                Toast.makeText(WordQuizActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 加载干扰项
    private void loadDistractorOptions() {
        int randomPage = random.nextInt(10) + 1;
        // 修复1：补充第三个参数 keyword（可选，传 null）
        Call<ResponseBody> call = wordService.getWordList(randomPage, 10, null);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // 修复2：将 ResponseBody 解析为 WordListResult
                        String json = response.body().string();
                        WordListResult result = gson.fromJson(json, WordListResult.class);

                        if (result != null && result.isSuccess() && result.getData() != null) {
                            List<WordSimpleResp> wordList = result.getData();
                            if (wordList == null || wordList.isEmpty()) return;

                            // 筛选2个干扰项（逻辑不变）
                            int count = 0;
                            for (WordSimpleResp word : wordList) {
                                if (count >= 2) break;
                                if (!word.getWordId().equals(currentWordId)
                                        && word.getWordExplain() != null
                                        && !word.getWordExplain().equals(currentWord.getWordExplain())) {
                                    options.add(word.getWordExplain());
                                    count++;
                                }
                            }

                            // 打乱选项并填充UI（逻辑不变）
                            Collections.shuffle(options);
                            tv_quiz_option1.setText(options.get(0));
                            tv_quiz_option2.setText(options.get(1));
                            tv_quiz_option3.setText(options.get(2));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(WordQuizActivity.this, "解析数据失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(WordQuizActivity.this, "干扰项加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkOption(TextView optionView) {
        isOptionClicked = true;
        String selectedExplain = optionView.getText().toString();
        String correctExplain = currentWord.getWordExplain() != null ? currentWord.getWordExplain() : "无释义";

        if (selectedExplain.equals(correctExplain)) {
            optionView.setBackgroundResource(R.drawable.bg_option_correct);
            new Handler().postDelayed(this::jumpToDetail, 2000);
        } else {
            optionView.setBackgroundResource(R.drawable.bg_option_wrong);
            jumpToDetail();
        }
    }

    private void jumpToDetail() {
        Intent intent = new Intent(this, WordDetailActivity.class);
        intent.putExtra("wordId", currentWordId);
        startActivity(intent);
        finish();
    }
}