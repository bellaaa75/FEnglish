package com.example.fenglishandroid.ui;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.BaseResponse;
import com.example.fenglishandroid.model.LearningStateResp;
import com.example.fenglishandroid.model.WordListResult;
import com.example.fenglishandroid.model.WordSimpleResp;
import com.example.fenglishandroid.model.request.StudyRecordRequest;
import com.example.fenglishandroid.service.RetrofitClient;
import com.example.fenglishandroid.service.WordService;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class WordQuizActivity extends AppCompatActivity {
    private static final String TAG = "WordQuizActivity";

    // 控件（LinearLayout布局适配）
    private ImageView iv_quiz_back;
    private TextView tv_quiz_word, tv_quiz_option1, tv_quiz_option2, tv_quiz_option3,tv_quiz_mastery;
    private Button btn_quiz_view_explain;

    // 数据
    private String currentWordId;
    private String currentUserId;
    private WordSimpleResp currentWord;
    private List<String> options = new ArrayList<>();
    private WordService wordService;
    private boolean isMastered = false;
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

        // 新增：1. 获取当前登录用户ID（从SharedPreferences获取，不修改原有存储逻辑）
        currentUserId = getCurrentUserId();
        // 新增：校验用户登录状态
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(this, "用户未登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadCurrentWordDetail();
        loadDistractorOptions();
        bindEvents();

        queryAndHandleLearningState();
    }

    private void initViews() {
        iv_quiz_back = findViewById(R.id.iv_quiz_back);
        tv_quiz_word = findViewById(R.id.tv_quiz_word);
        tv_quiz_option1 = findViewById(R.id.tv_quiz_option1);
        tv_quiz_option2 = findViewById(R.id.tv_quiz_option2);
        tv_quiz_option3 = findViewById(R.id.tv_quiz_option3);
        btn_quiz_view_explain = findViewById(R.id.btn_quiz_view_explain);
        tv_quiz_mastery = findViewById(R.id.tv_quiz_mastery);
    }

    private void bindEvents() {
        iv_quiz_back.setOnClickListener(v -> finish());
        tv_quiz_option1.setOnClickListener(v -> { if (!isOptionClicked) checkOption(tv_quiz_option1); });
        tv_quiz_option2.setOnClickListener(v -> { if (!isOptionClicked) checkOption(tv_quiz_option2); });
        tv_quiz_option3.setOnClickListener(v -> { if (!isOptionClicked) checkOption(tv_quiz_option3); });
        btn_quiz_view_explain.setOnClickListener(v -> { if (!isOptionClicked) jumpToDetail(); });
        tv_quiz_mastery.setOnClickListener(v -> {
            Log.d(TAG, "勾选框被点击，isOptionClicked=" + isOptionClicked);
            if (!isOptionClicked) { // 未选择选项时可操作
                isMastered = !isMastered;
                tv_quiz_mastery.setText(isMastered ? "√ 熟练掌握" : "□ 熟练掌握");
                String targetState = isMastered ? "熟练掌握" : "已学";
                syncMasteryToBackend(targetState);
            } else {
                Toast.makeText(WordQuizActivity.this, "已选择选项，无法修改熟练状态", Toast.LENGTH_SHORT).show();
            }
        });
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
        int randomPage = random.nextInt(10) + 1; // 随机页码（1-10，可根据实际调整）
        Call<ResponseBody> call = wordService.getWordList(randomPage, 10, null);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        WordListResult result = gson.fromJson(json, WordListResult.class);
                        if (result != null && result.isSuccess() && result.getData() != null) {
                            List<WordSimpleResp> allWords = result.getData();
                            List<WordSimpleResp> distractors = new ArrayList<>(); // 存储最终干扰项
                            Set<String> usedWordIds = new HashSet<>(); // 去重：已选单词的wordId（避免同词重复）
                            Set<String> usedExplains = new HashSet<>(); // 去重：已选解释（避免内容重复）

                            // 步骤1：先添加正确答案的解释到去重集合（避免干扰项与正确答案重复）
                            if (currentWord != null && currentWord.getWordExplain() != null) {
                                usedExplains.add(currentWord.getWordExplain().trim());
                                usedWordIds.add(currentWord.getWordId()); // 用wordId确保完全排除当前单词
                            }

                            // 步骤2：从单词列表中筛选干扰项（三重条件：非当前词+未选过+解释不同）
                            for (WordSimpleResp word : allWords) {
                                // 终止条件：选够2个干扰项即可
                                if (distractors.size() >= 2) break;

                                // 过滤条件1：排除当前正确答案（按wordId，绝对唯一）
                                if (word.getWordId().equals(currentWord.getWordId())) continue;
                                // 过滤条件2：排除已选过的单词（按wordId，避免重复选同一个词）
                                if (usedWordIds.contains(word.getWordId())) continue;
                                // 过滤条件3：排除解释相同/相似的单词（按wordExplain，避免内容重复）
                                String wordExplain = word.getWordExplain() != null ? word.getWordExplain().trim() : "";
                                if (wordExplain.isEmpty() || usedExplains.contains(wordExplain)) continue;

                                // 符合条件：添加到干扰项列表，并标记为已使用
                                distractors.add(word);
                                usedWordIds.add(word.getWordId());
                                usedExplains.add(wordExplain);
                            }

                            // 步骤3：如果筛选后不足2个干扰项（极端情况），补充随机项（避免选项缺失）
                            while (distractors.size() < 2) {
                                WordSimpleResp randomWord = allWords.get(random.nextInt(allWords.size()));
                                String wordExplain = randomWord.getWordExplain() != null ? randomWord.getWordExplain().trim() : "";
                                // 即使补充，也确保不与正确答案重复
                                if (!randomWord.getWordId().equals(currentWord.getWordId()) && !wordExplain.isEmpty()) {
                                    distractors.add(randomWord);
                                }
                            }

                            // 步骤4：组合所有选项（正确答案+2个干扰项），并打乱顺序（提升体验）
                            List<String> allOptions = new ArrayList<>();
                            allOptions.add(currentWord.getWordExplain()); // 正确答案
                            allOptions.add(distractors.get(0).getWordExplain()); // 干扰项1
                            allOptions.add(distractors.get(1).getWordExplain()); // 干扰项2

                            // 打乱选项顺序（避免正确答案永远在第一个）
                            Collections.shuffle(allOptions);

                            // 步骤5：设置选项文本
                            tv_quiz_option1.setText(allOptions.get(0));
                            tv_quiz_option2.setText(allOptions.get(1));
                            tv_quiz_option3.setText(allOptions.get(2));

                        }
                    } catch (IOException e) {
                        Log.e(TAG, "解析干扰项失败：" + e.getMessage(), e);
                        Toast.makeText(WordQuizActivity.this, "加载选项失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "加载干扰项网络错误：" + t.getMessage(), t);
                Toast.makeText(WordQuizActivity.this, "加载选项失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkOption(TextView optionView) {
        isOptionClicked = true;
        String selectedExplain = optionView.getText().toString();
        String correctExplain = currentWord.getWordExplain() != null ? currentWord.getWordExplain() : "无释义";

        if (selectedExplain.equals(correctExplain)) {
            optionView.setBackgroundResource(R.drawable.bg_option_correct);
            new Handler().postDelayed(this::jumpToDetail, 1000);
        } else {
            optionView.setBackgroundResource(R.drawable.bg_option_wrong);
            new Handler().postDelayed(this::jumpToDetail, 1000);
            //jumpToDetail();
        }
    }

    private void jumpToDetail() {
        Intent intent = new Intent(this, WordDetailActivity.class);
        intent.putExtra("wordId", currentWordId);
        startActivity(intent);
        finish();
    }


    // ====================== 新增：用户ID相关方法（独立实现，不修改原有代码）======================
    /**
     * 从SharedPreferences获取当前登录用户ID（假设登录时已存储，不修改原有登录逻辑）
     * 若你的项目用其他方式存储用户ID（如ViewModel/全局变量），仅修改此方法即可
     */
    private String getCurrentUserId() {
        SharedPreferences sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        return sp.getString("userId", null); // 未登录返回null
    }

    /**
     * 保存用户ID到SharedPreferences（登录成功时调用，仅提供方法，不修改原有登录逻辑）
     */
    public void saveCurrentUserId(String userId) {
        SharedPreferences sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        sp.edit().putString("userId", userId).apply();
    }

    // ====================== 新增：学习状态核心逻辑（独立实现，不影响原有代码）======================
    /**
     * 步骤1：查询用户对当前单词的学习状态
     */
    /**
     * 核心逻辑：修改判断规则（适配后端返回特点）
     * 后端规则：
     * - 接口调用成功（HTTP 200）→ message="操作成功"，success=true
     * - 实际查询到状态 → data=LearningStateResp对象
     * - 实际未查询到状态 → data=null 或 data=false
     */
    private void queryAndHandleLearningState() {
        Call<BaseResponse<LearningStateResp>> call = wordService.getLearningState(currentUserId, currentWordId);
        call.enqueue(new Callback<BaseResponse<LearningStateResp>>() {
            @Override
            public void onResponse(Call<BaseResponse<LearningStateResp>> call, Response<BaseResponse<LearningStateResp>> response) {
                // 1. 只要HTTP响应成功（200），就认为接口调用成功（不管后端data是什么）
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<LearningStateResp> resp = response.body();
                    LearningStateResp state = resp.getData();

                    // 2. 判断核心：data是否为有效状态（非null且非false）
                    if (state != null) {
                        // 分支1：查询到状态 → 按状态处理
                        String learnState = state.getLearnState();
                        Log.d(TAG, "查询到学习状态：" + learnState);

                        // 新增：根据后端状态设置勾选框
                        if ("熟练掌握".equals(learnState)) {
                            isMastered = true; // 标记为已勾选
                            tv_quiz_mastery.setText("√ 熟练掌握");
                        } else {
                            isMastered = false; // 未勾选（未学/已学）
                            tv_quiz_mastery.setText("□ 熟练掌握");
                        }

                        if ("未学".equals(learnState)) {
                            // 未学 → 修改为已学
                            updateLearningState("已学");
                            addStudyRecord();
                        } else if ("已学".equals(learnState) || "熟练掌握".equals(learnState)) {
                            // 已学/熟练掌握 → 不操作（自定义提示，不用后端message）
                            Log.d(TAG, "学习状态无需修改：" + learnState);
                            // 可选：取消提示，避免干扰用户
                            // Toast.makeText(WordQuizActivity.this, "当前状态：" + learnState, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(WordQuizActivity.this, "学习状态异常：" + learnState, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // 分支2：未查询到状态（data=null或false）→ 创建状态
                        Log.d(TAG, "未查询到学习状态，创建新记录（已学）");
                        isMastered = false;
                        tv_quiz_mastery.setText("□ 熟练掌握");
                        createLearningState();
                        addStudyRecord();
                        // 自定义提示，不用后端的"操作成功"
                        Toast.makeText(WordQuizActivity.this, "首次学习，自动标记为已学", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 3. HTTP响应失败（非200）→ 接口调用失败
                    isMastered = false;
                    tv_quiz_mastery.setText("□ 熟练掌握");
                    Toast.makeText(WordQuizActivity.this, "查询学习状态失败（接口异常）", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<LearningStateResp>> call, Throwable t) {
                // 4. 网络错误 → 单独提示
                isMastered = false;
                tv_quiz_mastery.setText("□ 熟练掌握");
                Log.e(TAG, "查询学习状态网络错误：" + t.getMessage());
                Toast.makeText(WordQuizActivity.this, "网络错误，无法查询学习状态", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 步骤2：创建学习状态（未查询到时调用）
     */
    /**
     * 创建学习状态（修改提示逻辑）
     */
    private void createLearningState() {
        Call<BaseResponse<Boolean>> call = wordService.createLearningState(currentUserId, currentWordId);
        call.enqueue(new Callback<BaseResponse<Boolean>>() {
            @Override
            public void onResponse(Call<BaseResponse<Boolean>> call, Response<BaseResponse<Boolean>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Boolean> resp = response.body();
                    // 后端用data=true表示创建成功，false表示失败
                    if (Boolean.TRUE.equals(resp.getData())) {
                        Log.d(TAG, "创建学习状态成功（已学）");
                        updateLearningState("已学");
                    } else {
                        // 自定义失败提示，不用后端message
                        Toast.makeText(WordQuizActivity.this, "创建学习状态失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WordQuizActivity.this, "创建学习状态接口异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Boolean>> call, Throwable t) {
                Log.e(TAG, "创建学习状态网络错误：" + t.getMessage());
                Toast.makeText(WordQuizActivity.this, "网络错误，无法创建学习状态", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 修改学习状态（修改提示逻辑）
     */
    private void updateLearningState(String targetState) {
        Call<BaseResponse<Boolean>> call = wordService.updateLearningState(currentUserId, currentWordId, targetState);
        call.enqueue(new Callback<BaseResponse<Boolean>>() {
            @Override
            public void onResponse(Call<BaseResponse<Boolean>> call, Response<BaseResponse<Boolean>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Boolean> resp = response.body();
                    // 后端用data=true表示修改成功，false表示失败
                    if (Boolean.TRUE.equals(resp.getData())) {
                        Log.d(TAG, "学习状态修改成功：未学 → " + targetState);
                        // 可选：提示用户状态变化
                        // Toast.makeText(WordQuizActivity.this, "学习状态已更新为" + targetState, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WordQuizActivity.this, "修改学习状态失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WordQuizActivity.this, "修改学习状态接口异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Boolean>> call, Throwable t) {
                Log.e(TAG, "修改学习状态网络错误：" + t.getMessage());
                Toast.makeText(WordQuizActivity.this, "网络错误，无法修改学习状态", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getCurrentStudyTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return sdf.format(new Date());
    }

    /**
     * 调用接口：增加学习记录
     */
    private void addStudyRecord() {
        // 1. 构建请求体参数
        String studyTime = getCurrentStudyTime();
        StudyRecordRequest request = new StudyRecordRequest(studyTime, currentUserId, currentWordId);

        // 2. 调用接口
        Call<BaseResponse<Boolean>> call = wordService.addStudyRecord(request);
        call.enqueue(new Callback<BaseResponse<Boolean>>() {
            @Override
            public void onResponse(Call<BaseResponse<Boolean>> call, Response<BaseResponse<Boolean>> response) {
                // 适配后端返回特点：HTTP 200 即认为接口调用完成（不管data是true/false）
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "增加学习记录接口调用成功");
                    // 后端错误（用户不存在、单词不存在等）会返回data=false，这里仅提示，不影响主流程
                    if (Boolean.FALSE.equals(response.body().getData())) {
                        Log.w(TAG, "增加学习记录失败（可能用户/单词不存在）");
                        Toast.makeText(WordQuizActivity.this, "学习记录添加失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "增加学习记录接口异常");
                    Toast.makeText(WordQuizActivity.this, "学习记录添加失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Boolean>> call, Throwable t) {
                Log.e(TAG, "增加学习记录网络错误：" + t.getMessage());
                Toast.makeText(WordQuizActivity.this, "网络错误，学习记录添加失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 专用方法：同步勾选状态到后端（勾选→熟练掌握，取消→已学）
     */
    private void syncMasteryToBackend(String targetState) {
        Call<BaseResponse<Boolean>> call = wordService.updateLearningState(currentUserId, currentWordId, targetState);
        call.enqueue(new Callback<BaseResponse<Boolean>>() {
            @Override
            public void onResponse(Call<BaseResponse<Boolean>> call, Response<BaseResponse<Boolean>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Boolean> resp = response.body();
                    if (Boolean.TRUE.equals(resp.getData())) {
                        Log.d(TAG, "学习状态同步成功：" + targetState);
                        // 可选：提示用户状态变化
                        Toast.makeText(WordQuizActivity.this, "已更新为" + targetState, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "学习状态同步失败");
                        // 回滚UI状态（避免用户看到错误状态）
                        isMastered = !isMastered;
                        tv_quiz_mastery.setText(isMastered ? "√ 熟练掌握" : "□ 熟练掌握");
                        Toast.makeText(WordQuizActivity.this, "状态更新失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "同步状态接口异常");
                    // 回滚UI状态
                    isMastered = !isMastered;
                    tv_quiz_mastery.setText(isMastered ? "√ 熟练掌握" : "□ 熟练掌握");
                    Toast.makeText(WordQuizActivity.this, "接口异常，更新失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Boolean>> call, Throwable t) {
                Log.e(TAG, "同步状态网络错误：" + t.getMessage());
                // 回滚UI状态
                isMastered = !isMastered;
                tv_quiz_mastery.setText(isMastered ? "√ 熟练掌握" : "□ 熟练掌握");
                Toast.makeText(WordQuizActivity.this, "网络错误，更新失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}