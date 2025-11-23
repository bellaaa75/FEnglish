package com.example.fenglishandroid.ui.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.Result;
import com.example.fenglishandroid.model.VocabularyBookDetailResp;
import com.example.fenglishandroid.model.WordSimpleResp;
import com.example.fenglishandroid.service.RetrofitClient;
import com.example.fenglishandroid.service.VocabularyBookService;
import com.example.fenglishandroid.service.WordService;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VocabularyBookDetailActivity extends AppCompatActivity {
    private TextView tvBookName, tvPublishTime, tvWordCount;
    private RecyclerView rvWords;
    private Button btnAddWord;
    private String bookId;
    private WordListAdapter wordAdapter;
    private List<WordSimpleResp> wordList = new ArrayList<>();
    private VocabularyBookService bookService;
    private WordService wordService;

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

        // 初始化服务
        bookService = RetrofitClient.getVocabularyBookService();
        wordService = RetrofitClient.getWordService();

        // 初始化控件
        initViews();

        // 加载单词书详情
        loadBookDetail();

        // 加载单词列表
        loadWordList();
    }

    private void initViews() {
        tvBookName = findViewById(R.id.tv_book_name);
        tvPublishTime = findViewById(R.id.tv_publish_time);
        tvWordCount = findViewById(R.id.tv_word_count);
        rvWords = findViewById(R.id.rv_words);
        btnAddWord = findViewById(R.id.btn_add_word);
        ImageView ivBack = findViewById(R.id.iv_back);

        // 初始化适配器
        wordAdapter = new WordListAdapter(wordList, wordId -> removeWordFromBook(wordId));
        rvWords.setLayoutManager(new LinearLayoutManager(this));
        rvWords.setAdapter(wordAdapter);

        // 添加单词按钮点击事件
        btnAddWord.setOnClickListener(v -> showAddWordDialog());
        ivBack.setOnClickListener(v -> finish());
    }

    // 加载单词书详情
    private void loadBookDetail() {
        bookService.getBookDetail(bookId).enqueue(new Callback<Result<VocabularyBookDetailResp>>() {
            @Override
            public void onResponse(Call<Result<VocabularyBookDetailResp>> call, Response<Result<VocabularyBookDetailResp>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    VocabularyBookDetailResp book = response.body().getData();
                    if (book != null) {
                        tvBookName.setText(book.getBookName());
                        tvPublishTime.setText(book.getPublishTime());
                        tvWordCount.setText(String.valueOf(book.getWordCount()));
                    }
                } else {
                    Toast.makeText(VocabularyBookDetailActivity.this, "获取详情失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<VocabularyBookDetailResp>> call, Throwable t) {
                Toast.makeText(VocabularyBookDetailActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 加载单词列表
    private void loadWordList() {
        bookService.getWordsInBook(bookId).enqueue(new Callback<Result<List<WordSimpleResp>>>() {
            @Override
            public void onResponse(Call<Result<List<WordSimpleResp>>> call, Response<Result<List<WordSimpleResp>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    wordList.clear();
                    wordList.addAll(response.body().getData());
                    wordAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(VocabularyBookDetailActivity.this, "获取单词列表失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<List<WordSimpleResp>>> call, Throwable t) {
                Toast.makeText(VocabularyBookDetailActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 从单词书中移除单词
    private void removeWordFromBook(String wordId) {
        new AlertDialog.Builder(this)
                .setTitle("确认移除")
                .setMessage("确定要将此单词从单词书中移除吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    bookService.removeWordFromBook(bookId, wordId).enqueue(new Callback<Result<Void>>() {
                        @Override
                        public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Toast.makeText(VocabularyBookDetailActivity.this, "移除成功", Toast.LENGTH_SHORT).show();
                                loadWordList(); // 重新加载单词列表
                                loadBookDetail(); // 更新单词数量
                            } else {
                                Toast.makeText(VocabularyBookDetailActivity.this, "移除失败", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Result<Void>> call, Throwable t) {
                            Toast.makeText(VocabularyBookDetailActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 显示添加单词对话框
    private void showAddWordDialog() {
        EditText etWordId = new EditText(this);
        etWordId.setHint("请输入单词ID");

        new AlertDialog.Builder(this)
                .setTitle("添加单词到单词书")
                .setView(etWordId)
                .setPositiveButton("确定", (dialog, which) -> {
                    String wordId = etWordId.getText().toString().trim();
                    if (wordId.isEmpty()) {
                        Toast.makeText(VocabularyBookDetailActivity.this, "单词ID不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addWordToBook(wordId);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 向单词书添加单词
    private void addWordToBook(String wordId) {
        VocabularyBookService.WordAddReq req = new VocabularyBookService.WordAddReq();
        req.setWordId(wordId);

        bookService.addWordToBook(bookId, req).enqueue(new Callback<Result<Void>>() {
            @Override
            public void onResponse(Call<Result<Void>> call, Response<Result<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(VocabularyBookDetailActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    loadWordList(); // 重新加载单词列表
                    loadBookDetail(); // 更新单词数量
                } else {
                    String message = "添加失败";
                    if (response.body() != null) {
                        message = response.body().getMessage();
                    }
                    Toast.makeText(VocabularyBookDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<Void>> call, Throwable t) {
                Toast.makeText(VocabularyBookDetailActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 单词列表适配器
    private class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder> {
        private List<WordSimpleResp> words;
        private OnWordDeleteListener listener;

        public WordListAdapter(List<WordSimpleResp> words, OnWordDeleteListener listener) {
            this.words = words;
            this.listener = listener;
        }

        @Override
        public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_word_in_book, parent, false);
            return new WordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(WordViewHolder holder, int position) {
            WordSimpleResp word = words.get(position);
            holder.tvWordName.setText(word.getWordName());
            holder.tvWordExplain.setText(word.getWordExplain());
            holder.btnDelete.setOnClickListener(v -> listener.onDelete(word.getWordId()));
        }

        @Override
        public int getItemCount() {
            return words.size();
        }

        class WordViewHolder extends RecyclerView.ViewHolder {
            TextView tvWordName, tvWordExplain;
            Button btnDelete;

            public WordViewHolder(View itemView) {
                super(itemView);
                tvWordName = itemView.findViewById(R.id.tv_word_name);
                tvWordExplain = itemView.findViewById(R.id.tv_word_explain);
                btnDelete = itemView.findViewById(R.id.btn_delete);
            }
        }
    }

    // 单词删除监听器
    interface OnWordDeleteListener {
        void onDelete(String wordId);
    }
}