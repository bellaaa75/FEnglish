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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    // 加载单词书详情
    private void loadBookDetail() {
        showLoading();
        viewModel.getBookDetail(bookId).observe(this, new Observer<VocabularyBookDetailResp>() {
            @Override
            public void onChanged(VocabularyBookDetailResp book) {
                hideLoading();
                if (book != null) {
                    tvBookName.setText(book.getBookName());
                    tvPublishTime.setText(book.getPublishTime());

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

        EditText etSearch = dialogView.findViewById(R.id.et_search);
        Button btnSearch = dialogView.findViewById(R.id.btn_search);
        RecyclerView rvCandidateWords = dialogView.findViewById(R.id.rv_candidate_words);
        Button btnConfirmAdd = dialogView.findViewById(R.id.btn_confirm_add);

        List<WordSimpleResp> candidateWords = new ArrayList<>();
        CandidateWordAdapter candidateAdapter = new CandidateWordAdapter(candidateWords);
        rvCandidateWords.setLayoutManager(new LinearLayoutManager(this));
        rvCandidateWords.setAdapter(candidateAdapter);

        AlertDialog dialog = builder.create();
        dialog.show();

        // 加载可选单词
        loadCandidateWords("", candidateWords, candidateAdapter);

        // 搜索按钮
        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            loadCandidateWords(keyword, candidateWords, candidateAdapter);
        });

        // 确认添加
        btnConfirmAdd.setOnClickListener(v -> {
            Set<String> selectedWordIds = candidateAdapter.getSelectedWordIds();
            if (selectedWordIds.isEmpty()) {
                Toast.makeText(this, "请选择要添加的单词", Toast.LENGTH_SHORT).show();
                return;
            }

            addWordsToBook(selectedWordIds);
            dialog.dismiss();
        });
    }

    // 加载候选单词
    private void loadCandidateWords(String keyword, List<WordSimpleResp> candidateWords, CandidateWordAdapter adapter) {
        showLoading();
        wordService.getWordList(1, 50, keyword).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        Type type = new TypeToken<Result<PageResult<WordSimpleResp>>>(){}.getType();
                        Result<PageResult<WordSimpleResp>> result = gson.fromJson(json, type);

                        if (result != null && result.isSuccess() && result.getData() != null) {
                            candidateWords.clear();
                            candidateWords.addAll(result.getData().getList());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(VocabularyBookDetailActivity.this, "获取单词列表失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "解析单词列表失败", e);
                        Toast.makeText(VocabularyBookDetailActivity.this, "解析数据失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VocabularyBookDetailActivity.this, "获取单词列表失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideLoading();
                Toast.makeText(VocabularyBookDetailActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 批量添加单词
    private void addWordsToBook(Set<String> wordIds) {
        int[] successCount = {0};
        int total = wordIds.size();
        showLoading();

        for (String wordId : wordIds) {
            VocabularyBookService.WordAddReq req = new VocabularyBookService.WordAddReq();
            req.setWordId(wordId);

            bookService.addWordToBook(bookId, req).enqueue(new Callback<Result<Void>>() {
                @Override
                public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        successCount[0]++;
                    }
                    checkAddCompletion(successCount[0], total);
                }

                @Override
                public void onFailure(Call<Result<Void>> call, Throwable t) {
                    checkAddCompletion(successCount[0], total);
                }
            });
        }
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
    private class CandidateWordAdapter extends RecyclerView.Adapter<CandidateWordAdapter.CandidateWordViewHolder> {
        private List<WordSimpleResp> candidateWords;
        private Map<String, Boolean> selectedStatus = new HashMap<>();

        public CandidateWordAdapter(List<WordSimpleResp> candidateWords) {
            this.candidateWords = candidateWords;
        }

        @Override
        public CandidateWordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_candidate_word, parent, false);
            return new CandidateWordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CandidateWordViewHolder holder, int position) {
            WordSimpleResp word = candidateWords.get(position);
            holder.tvWordName.setText(word.getWordName());
            holder.tvWordExplain.setText(word.getWordExplain());

            Boolean isSelected = selectedStatus.get(word.getWordId());
            holder.cbSelect.setChecked(isSelected != null && isSelected);

            holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) ->
                    selectedStatus.put(word.getWordId(), isChecked));
        }

        @Override
        public int getItemCount() {
            return candidateWords.size();
        }

        public Set<String> getSelectedWordIds() {
            Set<String> selectedIds = new HashSet<>();
            for (Map.Entry<String, Boolean> entry : selectedStatus.entrySet()) {
                if (entry.getValue()) {
                    selectedIds.add(entry.getKey());
                }
            }
            return selectedIds;
        }

        class CandidateWordViewHolder extends RecyclerView.ViewHolder {
            TextView tvWordName, tvWordExplain;
            CheckBox cbSelect;

            public CandidateWordViewHolder(View itemView) {
                super(itemView);
                tvWordName = itemView.findViewById(R.id.tv_word_name);
                tvWordExplain = itemView.findViewById(R.id.tv_word_explain);
                cbSelect = itemView.findViewById(R.id.cb_select);
            }
        }
    }
}