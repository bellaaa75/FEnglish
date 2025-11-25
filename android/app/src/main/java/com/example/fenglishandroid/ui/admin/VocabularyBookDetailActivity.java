package com.example.fenglishandroid.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.Result;
import com.example.fenglishandroid.model.VocabularyBookDetailResp;
import com.example.fenglishandroid.model.WordListResult;
import com.example.fenglishandroid.model.WordSimpleResp;
import com.example.fenglishandroid.model.request.PageResult;
import com.example.fenglishandroid.service.RetrofitClient;
import com.example.fenglishandroid.service.VocabularyBookService;
import com.example.fenglishandroid.service.WordService;
import com.example.fenglishandroid.viewModel.VocabularyBookViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VocabularyBookDetailActivity extends AppCompatActivity {
    private static final String TAG = "VocabularyBookDetail";
    private TextView tvBookName, tvPublishTime, tvWordCount, tvPageInfo;
    private RecyclerView rvWords;
    private Button btnAddWord, btnPrevPage, btnNextPage;
    private ProgressBar progressBar;
    private String bookId;
    private WordInBookAdapter wordAdapter;
    private List<WordSimpleResp> allWords = new ArrayList<>();
    private List<WordSimpleResp> currentPageWords = new ArrayList<>();
    private VocabularyBookService bookService;
    private WordService wordService;
    private Gson gson = new Gson();
    private VocabularyBookViewModel viewModel;

    // 分页参数
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_book_detail);

        // 获取传递的bookId
        bookId = getIntent().getStringExtra("bookId");
        if (bookId == null || bookId.isEmpty()) {
            Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 初始化ViewModel和服务
        viewModel = new ViewModelProvider(this).get(VocabularyBookViewModel.class);
        bookService = RetrofitClient.getVocabularyBookService();
        wordService = RetrofitClient.getWordService();

        // 初始化控件
        initViews();

        // 加载单词书详情
        loadBookDetail();
    }

    // 自定义Toast工具方法（避免多次点击导致的Toast堆积）
    private Toast mToast;
    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void initViews() {
        tvBookName = findViewById(R.id.tv_book_name);
        tvPublishTime = findViewById(R.id.tv_publish_time);
        tvWordCount = findViewById(R.id.tv_word_count);
        tvPageInfo = findViewById(R.id.tv_page_info);
        rvWords = findViewById(R.id.rv_words);
        btnAddWord = findViewById(R.id.btn_add_word);
        btnPrevPage = findViewById(R.id.btn_prev_page);
        btnNextPage = findViewById(R.id.btn_next_page);
        progressBar = findViewById(R.id.progress_bar);
        ImageView ivBack = findViewById(R.id.iv_back);

        // 初始化适配器
        wordAdapter = new WordInBookAdapter(currentPageWords,
                word -> {}, // 空实现，如需单词点击事件可添加
                this::removeWordFromBook);
        rvWords.setLayoutManager(new LinearLayoutManager(this));
        rvWords.setAdapter(wordAdapter);

        // 事件监听
        btnAddWord.setOnClickListener(v -> showAddWordDialog());
        ivBack.setOnClickListener(v -> finish());
        btnPrevPage.setOnClickListener(v -> goToPrevPage());
        btnNextPage.setOnClickListener(v -> goToNextPage());
    }

    // 格式化发布时间（复用已有逻辑）
    private String formatPublishTime(String time) {
        if (time == null || time.isEmpty()) return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat targetSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return targetSdf.format(sdf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
            return time; // 解析失败时返回原始字符串
        }
    }

    // 加载单词书详情
    private void loadBookDetail() {
        showLoading();
        viewModel.getBookDetail(bookId).observe(this, new Observer<VocabularyBookDetailResp>() {
            @Override
            public void onChanged(VocabularyBookDetailResp book) {
                hideLoading();
                if (book != null) {
                    tvBookName.setText(book.getBookName());
                    tvPublishTime.setText(formatPublishTime(book.getPublishTime()));

                    // 处理单词列表
                    allWords.clear();
                    if (book.getWordList() != null) {
                        allWords.addAll(book.getWordList());
                    }
                    tvWordCount.setText(String.valueOf(allWords.size()));

                    // 初始化分页
                    calculateTotalPages();
                    currentPage = 1;
                    loadCurrentPageWords();
                    updatePageInfo();
                } else {
                    Toast.makeText(VocabularyBookDetailActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 计算总页数
    private void calculateTotalPages() {
        if (allWords.isEmpty()) {
            totalPages = 1;
            return;
        }
        totalPages = (allWords.size() + pageSize - 1) / pageSize;
    }

    // 加载当前页单词
    private void loadCurrentPageWords() {
        currentPageWords.clear();
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, allWords.size());

        if (start < allWords.size()) {
            currentPageWords.addAll(allWords.subList(start, end));
        }
        wordAdapter.notifyDataSetChanged();
    }

    // 更新分页信息
    private void updatePageInfo() {
        tvPageInfo.setText(String.format("第 %d/%d 页  共 %d 个单词",
                currentPage, totalPages, allWords.size()));

        // 控制按钮状态
        btnPrevPage.setEnabled(currentPage > 1);
        btnNextPage.setEnabled(currentPage < totalPages);
    }

    // 上一页
    private void goToPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            loadCurrentPageWords();
            updatePageInfo();
        }
    }

    // 下一页
    private void goToNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadCurrentPageWords();
            updatePageInfo();
        }
    }

    // 从单词书中移除单词
    private void removeWordFromBook(String wordId) {
        new AlertDialog.Builder(this)
                .setTitle("确认移除")
                .setMessage("确定要将此单词从单词书中移除吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    showLoading();
                    bookService.removeWordFromBook(bookId, wordId).enqueue(new Callback<Result<Void>>() {
                        @Override
                        public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                            hideLoading();
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Toast.makeText(VocabularyBookDetailActivity.this, "移除成功", Toast.LENGTH_SHORT).show();
                                loadBookDetail(); // 重新加载详情
                            } else {
                                Toast.makeText(VocabularyBookDetailActivity.this, "移除失败", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Result<Void>> call, Throwable t) {
                            hideLoading();
                            Toast.makeText(VocabularyBookDetailActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 显示添加单词对话框
    private void showAddWordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_words_to_book, null);
        builder.setView(dialogView);

        RecyclerView rvCandidateWords = dialogView.findViewById(R.id.rv_candidate_words);
        Button btnConfirmAdd = dialogView.findViewById(R.id.btn_confirm_add);
        // 新增分页控件
        Button btnPrevPage = dialogView.findViewById(R.id.btn_prev_page);
        Button btnNextPage = dialogView.findViewById(R.id.btn_next_page);

        // 候选单词数据与适配器
        List<WordSimpleResp> candidateWords = new ArrayList<>();
        CandidateWordAdapter candidateAdapter = new CandidateWordAdapter(candidateWords);
        rvCandidateWords.setLayoutManager(new LinearLayoutManager(this));
        rvCandidateWords.setAdapter(candidateAdapter);

        // 分页参数（仿照AdminWordFragment）
        final int[] currentDialogPage = {1};
        int dialogPageSize = 20;
        AtomicInteger totalDialogPages = new AtomicInteger(1);

        AlertDialog dialog = builder.create();
        dialog.show();

        // 初始加载第一页
        loadCandidateWords("", currentDialogPage[0], dialogPageSize, candidateWords, candidateAdapter, (totalPages) -> {
            totalDialogPages.set(totalPages);
            updateDialogPageButtons(btnPrevPage, btnNextPage, currentDialogPage[0], totalDialogPages.get());
        });

        // 上一页逻辑
        btnPrevPage.setOnClickListener(v -> {
            if (currentDialogPage[0] > 1) {
                currentDialogPage[0]--;
                loadCandidateWords("", currentDialogPage[0], dialogPageSize, candidateWords, candidateAdapter, (totalPages) -> {
                    totalDialogPages.set(totalPages);
                    updateDialogPageButtons(btnPrevPage, btnNextPage, currentDialogPage[0], totalDialogPages.get());
                });
            }
        });

        // 下一页逻辑
        btnNextPage.setOnClickListener(v -> {
            if (currentDialogPage[0] < totalDialogPages.get()) {
                currentDialogPage[0]++;
                loadCandidateWords("", currentDialogPage[0], dialogPageSize, candidateWords, candidateAdapter, (totalPages) -> {
                    totalDialogPages.set(totalPages);
                    updateDialogPageButtons(btnPrevPage, btnNextPage, currentDialogPage[0], totalDialogPages.get());
                });
            }
        });

        // 确认添加逻辑
        btnConfirmAdd.setOnClickListener(v -> {
            Set<String> selectedWordIds = candidateAdapter.getSelectedWordIds();
            Log.d(TAG, "确认添加，选中的单词数量: " + selectedWordIds.size());
            if (selectedWordIds.isEmpty()) {
                showToast("请选择要添加的单词");
                return;
            }
            // 打印选中的单词ID
            for (String wordId : selectedWordIds) {
                Log.d(TAG, "选中的单词ID: " + wordId);
            }
            addWordsToBook(selectedWordIds);
            dialog.dismiss();
        });
    }

    // 加载候选单词
    private void loadCandidateWords(String keyword, int pageNum, int pageSize,
                                    List<WordSimpleResp> candidateWords,
                                    CandidateWordAdapter adapter,
                                    OnTotalPagesListener listener) {
        showLoading();
        wordService.getWordList(pageNum, pageSize, keyword).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        Log.d(TAG, "候选单词响应: " + json);

                        // 关键修改：使用WordListResult解析（匹配后端结构）
                        Type type = new TypeToken<WordListResult>(){}.getType();
                        WordListResult result = gson.fromJson(json, type);

                        if (result == null) {
                            showToast("数据格式错误");
                            return;
                        }

                        if (result.isSuccess()) {
                            // 直接从data获取单词列表（数组）
                            List<WordSimpleResp> words = result.getData();
                            candidateWords.clear();
                            if (words != null) {
                                candidateWords.addAll(words);
                            }
                            adapter.notifyDataSetChanged();
                            // 从result获取总页数
                            listener.onTotalPages(result.getTotalPages());
                        } else {
                            String errorMsg = result.getMessage() != null ? result.getMessage() : "获取失败";
                            showToast(errorMsg);
                            listener.onTotalPages(0);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "解析失败", e);
                        showToast("解析失败");
                        listener.onTotalPages(0);
                    }
                } else {
                    showToast("请求失败，状态码: " + response.code());
                    listener.onTotalPages(0);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideLoading();
                Log.e(TAG, "网络错误", t);
                showToast("网络错误: " + t.getMessage());
                listener.onTotalPages(0);
            }
        });
    }

    // 辅助接口：更新总页数
    private interface OnTotalPagesListener {
        void onTotalPages(int totalPages);
    }

    // 更新分页按钮状态
    private void updateDialogPageButtons(Button btnPrev, Button btnNext, int currentPage, int totalPages) {
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    // 批量添加单词
    private void addWordsToBook(Set<String> selectedWordIds) {
        if (selectedWordIds.isEmpty()) return;

        showLoading();

        // 统计成功和失败的数量
        int total = selectedWordIds.size();
        final int[] successCount = {0};
        final int[] failedCount = {0};
        List<String> failedWordIds = new ArrayList<>();

        // 使用计数信号量确保所有请求完成后再处理结果
        final CountDownLatch latch = new CountDownLatch(total);

        for (String wordId : selectedWordIds) {
            // 构造请求参数（使用WordAddReq替代Map）
            VocabularyBookService.WordAddReq payload = new VocabularyBookService.WordAddReq();
            payload.setWordId(wordId);

            // 调用单个添加接口
            Call<Result<Void>> call = bookService.addWordToBook(bookId, payload);
            call.enqueue(new Callback<Result<Void>>() {
                // 回调实现保持不变...
                @Override
                public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                    latch.countDown();
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        successCount[0]++;
                    } else {
                        failedCount[0]++;
                        failedWordIds.add(wordId);
                    }

                    if (latch.getCount() == 0) {
                        handleAddResult(successCount[0], failedCount[0], failedWordIds);
                    }
                }

                @Override
                public void onFailure(Call<Result<Void>> call, Throwable t) {
                    latch.countDown();
                    failedCount[0]++;
                    failedWordIds.add(wordId);
                    Log.e(TAG, "添加单词失败: " + t.getMessage());

                    if (latch.getCount() == 0) {
                        handleAddResult(successCount[0], failedCount[0], failedWordIds);
                    }
                }
            });
        }
    }

    // 处理添加结果
    private void handleAddResult(int successCount, int failedCount, List<String> failedWordIds) {
        hideLoading();

        // 显示结果提示
        String message;
        if (failedCount == 0) {
            message = "全部添加成功，共添加 " + successCount + " 个单词";
        } else if (successCount == 0) {
            message = "添加失败，共 " + failedCount + " 个单词未能添加";
        } else {
            message = "部分添加成功：" + successCount + " 个成功，" + failedCount + " 个失败（可能已存在）";
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // 重新加载单词书详情，刷新列表
        loadBookDetail();
    }

    // 检查添加是否完成
    private void checkAddCompletion(int success, int total) {
        if (success + (total - success) == total) {
            hideLoading();
            Toast.makeText(this,
                    "添加完成，成功" + success + "个，失败" + (total - success) + "个",
                    Toast.LENGTH_SHORT).show();
            loadBookDetail();
        }
    }

    // 显示加载状态
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    // 隐藏加载状态
    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    // 单词列表适配器
    public static class WordInBookAdapter extends RecyclerView.Adapter<WordInBookAdapter.ViewHolder> {

        private List<WordSimpleResp> mWordList;
        private OnItemClickListener mItemListener;
        private OnDeleteClickListener mDeleteListener;

        public interface OnItemClickListener {
            void onItemClick(WordSimpleResp word);
        }

        public interface OnDeleteClickListener {
            void onDeleteClick(String wordId);
        }

        public WordInBookAdapter(List<WordSimpleResp> wordList,
                                 OnItemClickListener itemListener,
                                 OnDeleteClickListener deleteListener) {
            mWordList = wordList;
            mItemListener = itemListener;
            mDeleteListener = deleteListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_word_in_book, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            WordSimpleResp word = mWordList.get(position);
            holder.tvWordName.setText(word.getWordName());

            if (word.getPartOfSpeech() != null && !word.getPartOfSpeech().isEmpty()) {
                holder.tvPartOfSpeech.setText("[" + word.getPartOfSpeech() + "]");
            } else {
                holder.tvPartOfSpeech.setText("");
            }

            holder.tvWordExplain.setText(word.getWordExplain());

            holder.itemView.setOnClickListener(v -> {
                if (mItemListener != null) {
                    mItemListener.onItemClick(word);
                }
            });

            holder.btnDelete.setOnClickListener(v -> {
                if (mDeleteListener != null) {
                    mDeleteListener.onDeleteClick(word.getWordId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mWordList == null ? 0 : mWordList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvWordName;
            TextView tvPartOfSpeech;
            TextView tvWordExplain;
            TextView btnDelete;

            public ViewHolder(View itemView) {
                super(itemView);
                tvWordName = itemView.findViewById(R.id.tv_word_name);
                tvPartOfSpeech = itemView.findViewById(R.id.tv_part_of_speech);
                tvWordExplain = itemView.findViewById(R.id.tv_word_explain);
                btnDelete = itemView.findViewById(R.id.tv_delete);
            }
        }
    }

    // 候选单词适配器
    public class CandidateWordAdapter extends RecyclerView.Adapter<CandidateWordAdapter.ViewHolder> {
        private static final String TAG = "CandidateWordAdapter";
        private List<WordSimpleResp> mWordList;
        private Set<String> mSelectedWordIds = new HashSet<>();

        public CandidateWordAdapter(List<WordSimpleResp> wordList) {
            mWordList = wordList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_candidate_word, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            WordSimpleResp word = mWordList.get(position);
            holder.tvWord.setText(word.getWordName());
            holder.tvExplain.setText(word.getWordExplain());

            // 设置勾选状态
            holder.cbSelect.setChecked(mSelectedWordIds.contains(word.getWordId()));

            // 为CheckBox单独设置点击事件（关键修改）
            holder.cbSelect.setOnClickListener(v -> {
                boolean isChecked = holder.cbSelect.isChecked();
                if (isChecked) {
                    mSelectedWordIds.add(word.getWordId());
                    Log.d(TAG, "勾选单词: " + word.getWordName() + " (id: " + word.getWordId() + ")");
                } else {
                    mSelectedWordIds.remove(word.getWordId());
                    Log.d(TAG, "取消勾选单词: " + word.getWordName() + " (id: " + word.getWordId() + ")");
                }
                notifyItemChanged(position);
            });

            // 勾选事件处理
            holder.itemView.setOnClickListener(v -> {
                boolean isChecked = !holder.cbSelect.isChecked();
                holder.cbSelect.setChecked(isChecked);
                if (isChecked) {
                    mSelectedWordIds.add(word.getWordId());
                    Log.d(TAG, "点击条目勾选: " + word.getWordName() + " (id: " + word.getWordId() + ")");
                } else {
                    mSelectedWordIds.remove(word.getWordId());
                    Log.d(TAG, "点击条目取消勾选: " + word.getWordName() + " (id: " + word.getWordId() + ")");
                }
                notifyItemChanged(position);
            });
        }

        @Override
        public int getItemCount() {
            return mWordList.size();
        }

        public Set<String> getSelectedWordIds() {
            return mSelectedWordIds;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox cbSelect;
            TextView tvWord;
            TextView tvExplain;

            public ViewHolder(View view) {
                super(view);
                cbSelect = view.findViewById(R.id.cb_select);
                tvWord = view.findViewById(R.id.tv_word);
                tvExplain = view.findViewById(R.id.tv_explain);
            }
        }
    }
}