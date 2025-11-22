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

public class WordAddActivity extends AppCompatActivity {
    // 控件（与编辑页面完全一致）
    private ImageView ivBack;
    private EditText etWordName, etPartOfSpeech, etThirdPerson, etPresentParticiple;
    private EditText etPastParticiple, etPastTense, etWordExplain, etExampleSentence;
    private Button btnConfirm;

    // 服务接口
    private WordService wordService;
    private Gson gson = new Gson();
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_edit); // 复用编辑页面布局

        // 初始化服务
        wordService = RetrofitClient.getWordService();

        initView();
        initListener();
    }

    private void initView() {
        // 绑定控件（与编辑页面一致）
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

        // 修改按钮文字为“添加”（可选）
        btnConfirm.setText("添加");
    }

    private void initListener() {
        // 返回按钮：关闭页面，返回列表
        ivBack.setOnClickListener(v -> finish());

        // 确认添加按钮：提交新增单词
        btnConfirm.setOnClickListener(v -> submitAddWord());
    }

    // 提交新增单词到后端
    private void submitAddWord() {
        // 1. 校验必填字段（wordName和wordExplain非空）
        String wordName = etWordName.getText().toString().trim();
        String wordExplain = etWordExplain.getText().toString().trim();
        if (wordName.isEmpty()) {
            showToast("单词名称不能为空");
            return;
        }
        if (wordExplain.isEmpty()) {
            showToast("单词释义不能为空");
            return;
        }

        // 2. 收集所有输入数据（可选字段为空则不传递或传递空字符串）
        Map<String, Object> addData = new HashMap<>();
        addData.put("wordName", wordName);
        addData.put("partOfSpeech", etPartOfSpeech.getText().toString().trim());
        addData.put("thirdPersonSingular", etThirdPerson.getText().toString().trim());
        addData.put("presentParticiple", etPresentParticiple.getText().toString().trim());
        addData.put("pastParticiple", etPastParticiple.getText().toString().trim());
        addData.put("pastTense", etPastTense.getText().toString().trim());
        addData.put("wordExplain", wordExplain);
        addData.put("exampleSentence", etExampleSentence.getText().toString().trim());

        // 3. 转换为JSON请求体
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                gson.toJson(addData)
        );

        // 4. 调用后端POST接口（新增单词）
        Call<ResponseBody> call = wordService.addWord(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        Log.d("WordAddDebug", "新增响应：" + json);

                        Map<String, Object> result = gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
                        boolean success = (boolean) result.get("success");
                        String message = result.get("message") != null ? result.get("message").toString() : "添加失败";

                        // 显示结果提示
                        showToast(message);
                        if (success) {
                            // 新增成功，返回列表并刷新
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        showToast("解析失败");
                    }
                } else {
                    showToast("添加失败，状态码：" + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("网络错误：" + t.getMessage());
                Log.e("WordAddDebug", "新增失败：" + t.getMessage(), t);
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