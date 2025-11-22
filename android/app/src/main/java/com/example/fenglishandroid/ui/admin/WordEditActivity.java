package com.example.fenglishandroid.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fenglishandroid.R;
import com.example.fenglishandroid.service.RetrofitClient;
import com.example.fenglishandroid.service.WordService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WordEditActivity extends AppCompatActivity {
    // 控件
    private ImageView ivBack;
    private EditText etWordName, etPartOfSpeech, etThirdPerson, etPresentParticiple;
    private EditText etPastParticiple, etPastTense, etWordExplain, etExampleSentence;
    private Button btnConfirm;

    // 传递的单词数据
    private String wordId;
    private Map<String, Object> wordData;

    // 服务接口
    private WordService wordService;
    private Gson gson = new Gson();
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_edit);

        // 初始化服务
        wordService = RetrofitClient.getWordService();

        // 获取传递的单词数据
        Intent intent = getIntent();
        wordId = intent.getStringExtra("wordId");
        String wordDataStr = intent.getStringExtra("wordData");
        wordData = gson.fromJson(wordDataStr, new TypeToken<Map<String, Object>>() {}.getType());

        initView();
        fillWordData(); // 填充原有单词信息
        initListener();
    }

    private void initView() {
        ivBack = findViewById(R.id.iv_back);
        etWordName = findViewById(R.id.et_word_name);
        etPartOfSpeech = findViewById(R.id.et_part_of_speech);
        etThirdPerson = findViewById(R.id.et_third_person);
        etPresentParticiple = findViewById(R.id.et_present_participle);
        etPastParticiple = findViewById(R.id.et_past_participle);
        etPastTense = findViewById(R.id.et_past_tense);
        etWordExplain = findViewById(R.id.et_word_explain);
        etExampleSentence = findViewById(R.id.et_example_sentence);
        btnConfirm = findViewById(R.id.btn_confirm);
    }

    // 填充原有单词信息到输入框
    private void fillWordData() {
        if (wordData == null) return;

        // 单词名称
        etWordName.setText(wordData.get("wordName") != null ? wordData.get("wordName").toString() : "");
        // 词性
        etPartOfSpeech.setText(wordData.get("partOfSpeech") != null ? wordData.get("partOfSpeech").toString() : "");
        // 第三人称现在式
        etThirdPerson.setText(wordData.get("thirdPersonSingular") != null ? wordData.get("thirdPersonSingular").toString() : "");
        // 现在分词
        etPresentParticiple.setText(wordData.get("presentParticiple") != null ? wordData.get("presentParticiple").toString() : "");
        // 过去分词
        etPastParticiple.setText(wordData.get("pastParticiple") != null ? wordData.get("pastParticiple").toString() : "");
        // 过去式
        etPastTense.setText(wordData.get("pastTense") != null ? wordData.get("pastTense").toString() : "");
        // 单词释义
        etWordExplain.setText(wordData.get("wordExplain") != null ? wordData.get("wordExplain").toString() : "");
        // 例句
        etExampleSentence.setText(wordData.get("exampleSentence") != null ? wordData.get("exampleSentence").toString() : "");
    }

    private void initListener() {
        // 返回按钮
        ivBack.setOnClickListener(v -> finish()); // 关闭当前页面，返回单词列表

        // 确认按钮（提交修改）
        btnConfirm.setOnClickListener(v -> submitEdit());
    }

    // 提交编辑内容到后端
    private void submitEdit() {
        // 1. 收集输入数据
        Map<String, Object> editData = new HashMap<>();
        editData.put("wordName", etWordName.getText().toString().trim());
        editData.put("partOfSpeech", etPartOfSpeech.getText().toString().trim());
        editData.put("thirdPersonSingular", etThirdPerson.getText().toString().trim());
        editData.put("presentParticiple", etPresentParticiple.getText().toString().trim());
        editData.put("pastParticiple", etPastParticiple.getText().toString().trim());
        editData.put("pastTense", etPastTense.getText().toString().trim());
        editData.put("wordExplain", etWordExplain.getText().toString().trim());
        editData.put("exampleSentence", etExampleSentence.getText().toString().trim());

        // 2. 转换为JSON请求体
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                gson.toJson(editData)
        );

        // 3. 调用后端PUT接口
        Call<ResponseBody> call = wordService.updateWord(wordId, requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        Log.d("WordEditDebug", "修改响应：" + json);

                        Map<String, Object> result = gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
                        boolean success = (boolean) result.get("success");
                        String message = result.get("message") != null ? result.get("message").toString() : "操作失败";

                        // 显示结果弹窗
                        showToast(message);
                        if (success) {
                            // 修改成功，返回单词列表并刷新
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent); // 标记修改成功
                            finish();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        showToast("解析失败");
                    }
                } else {
                    showToast("修改失败，状态码：" + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("网络错误：" + t.getMessage());
                Log.e("WordEditDebug", "修改失败：" + t.getMessage(), t);
            }
        });
    }

    // 自定义Toast（避免堆积）
    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
