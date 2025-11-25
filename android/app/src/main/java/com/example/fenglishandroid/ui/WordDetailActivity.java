package com.example.fenglishandroid.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.WordSimpleResp;
import com.example.fenglishandroid.service.RetrofitClient;
import com.example.fenglishandroid.service.WordService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WordDetailActivity extends AppCompatActivity {
    // 仅保留必要控件，删除 tv_detail_mastery
    private ImageView iv_detail_back;
    private TextView tv_detail_word, tv_detail_part_of_speech;
    private TextView tv_detail_third_person, tv_detail_present_participle, tv_detail_past_participle, tv_detail_past_tense;
    private TextView tv_detail_explain_title, tv_detail_explain;
    private TextView tv_detail_example_title, tv_detail_example;

    private String wordId;
    private WordService wordService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        wordService = RetrofitClient.getWordService();
        initViews(); // 初始化调整后的控件
        wordId = getIntent().getStringExtra("wordId");
        if (wordId == null || wordId.isEmpty()) {
            Toast.makeText(this, "单词ID不能为空", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadWordDetail();
        iv_detail_back.setOnClickListener(v -> finish());
    }

    // 初始化控件（删除 tv_detail_mastery 相关）
    private void initViews() {
        iv_detail_back = findViewById(R.id.iv_detail_back);
        tv_detail_word = findViewById(R.id.tv_detail_word);
        tv_detail_part_of_speech = findViewById(R.id.tv_detail_part_of_speech);
        tv_detail_third_person = findViewById(R.id.tv_detail_third_person);
        tv_detail_present_participle = findViewById(R.id.tv_detail_present_participle);
        tv_detail_past_participle = findViewById(R.id.tv_detail_past_participle);
        tv_detail_past_tense = findViewById(R.id.tv_detail_past_tense);
        tv_detail_explain_title = findViewById(R.id.tv_detail_explain_title);
        tv_detail_explain = findViewById(R.id.tv_detail_explain);
        tv_detail_example_title = findViewById(R.id.tv_detail_example_title);
        tv_detail_example = findViewById(R.id.tv_detail_example);
    }

    private void loadWordDetail() {
        Call<WordSimpleResp> call = wordService.getWordDetail(wordId);
        call.enqueue(new Callback<WordSimpleResp>() {
            @Override
            public void onResponse(Call<WordSimpleResp> call, Response<WordSimpleResp> response) {
                if (response.code() == 404) {
                    Toast.makeText(WordDetailActivity.this, "该单词不存在", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    WordSimpleResp word = response.body();
                    updateUI(word); // 调整UI更新逻辑
                } else {
                    Toast.makeText(WordDetailActivity.this, "加载详情失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WordSimpleResp> call, Throwable t) {
                Toast.makeText(WordDetailActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // UI更新：仅单词加粗，例句去掉“例句：”开头
    private void updateUI(WordSimpleResp word) {
        // 单词名称（仅这里加粗，布局已设置 textStyle="bold"）
        tv_detail_word.setText(word.getWordName());
        tv_detail_part_of_speech.setText(word.getPartOfSpeech() != null ? word.getPartOfSpeech() : "");

        // 单词变形（正常黑色，不加粗）
        tv_detail_third_person.setText("第三人称现在式 " + (word.getThirdPersonSingular() != null ? word.getThirdPersonSingular() : "无"));
        tv_detail_present_participle.setText("现在分词 " + (word.getPresentParticiple() != null ? word.getPresentParticiple() : "无"));
        tv_detail_past_participle.setText("过去分词 " + (word.getPastParticiple() != null ? word.getPastParticiple() : "无"));
        tv_detail_past_tense.setText("过去式 " + (word.getPastTense() != null ? word.getPastTense() : "无"));

        // 单词释义（正常黑色，不加粗）
        tv_detail_explain.setText(word.getWordExplain() != null ? word.getWordExplain() : "无释义");

        // 例句：去掉“例句：”开头，直接显示原文
        tv_detail_example.setText(word.getExampleSentence() != null ? word.getExampleSentence() : "无例句");
    }
}