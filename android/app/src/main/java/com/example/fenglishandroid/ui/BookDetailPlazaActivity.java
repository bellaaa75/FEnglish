package com.example.fenglishandroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.fenglishandroid.model.WordSimpleResp;
import com.example.fenglishandroid.model.request.PageResult;
import com.example.fenglishandroid.service.RetrofitClient;
import com.example.fenglishandroid.service.WordService;
import com.example.fenglishandroid.viewModel.WordViewModel;

import java.util.ArrayList;
import java.util.List;

public class BookDetailPlazaActivity extends AppCompatActivity {
    private static final String TAG = "BookDetailPlaza";
    private ProgressBar mLoadingProgress;
    // 控件
    private Button btnBack;
    private TextView tvBookTitle, tvBookName, tvBookInfo;
    private RecyclerView rvWordList;
    private ImageView ivPrev, ivNext;
    private TextView tvPage1, tvPage2, tvPage3, tvPage4;
    private ProgressBar progressBar;

    // 数据和适配器
    private List<WordSimpleResp> wordList = new ArrayList<>();
    private WordPlazaAdapter wordAdapter;
    private String bookId;
    private String bookName;

    // 分页参数
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;
    private int totalWords = 0;

    // 分页控件集合
    private List<TextView> pageTvList = new ArrayList<>();
    private static final int PAGE_BUTTON_COUNT = 4;

    // 数据和网络相关
    private WordViewModel wordViewModel;
    private WordService wordService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail_plaza);

        // 获取传递的参数
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        bookName = intent.getStringExtra("bookName");

        if (bookId == null || bookId.isEmpty() || bookName == null) {
            Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 初始化ViewModel和服务
        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        wordService = RetrofitClient.getWordService();

        initViews();
        initPageTvList();
        initAdapter();
        initListener();
        observeData(); // 观察数据变化
        fetchWordList(); // 加载单词列表
        updateBookInfo(); // 更新单词书信息
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvBookTitle = findViewById(R.id.tv_book_title);
        tvBookName = findViewById(R.id.tv_book_name);
        tvBookInfo = findViewById(R.id.tv_book_info);
        rvWordList = findViewById(R.id.rv_word_list);
        tvPage1 = findViewById(R.id.tv_page_1);
        tvPage2 = findViewById(R.id.tv_page_2);
        tvPage3 = findViewById(R.id.tv_page_3);
        tvPage4 = findViewById(R.id.tv_page_4);
        ivPrev = findViewById(R.id.iv_prev);
        ivNext = findViewById(R.id.iv_next);
        progressBar = findViewById(R.id.progress_bar); // 加载进度条
        mLoadingProgress = findViewById(R.id.loading_progress);
    }

    private void initPageTvList() {
        pageTvList.clear();
        pageTvList.add(tvPage1);
        pageTvList.add(tvPage2);
        pageTvList.add(tvPage3);
        pageTvList.add(tvPage4);
    }

    private void initAdapter() {
        wordAdapter = new WordPlazaAdapter(wordList, new WordPlazaAdapter.OnButtonClickListener() {
            @Override
            public void onLearnClick(WordSimpleResp word) {

            }
            @Override
            public void onCollectClick(WordSimpleResp word) {

            }
        });
        rvWordList.setLayoutManager(new LinearLayoutManager(this));
        rvWordList.setAdapter(wordAdapter);
    }

    private void initListener() {
        // 返回按钮
        btnBack.setOnClickListener(v -> finish());

        // 上一页
        ivPrev.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                fetchWordList();
            } else {
                Toast.makeText(this, "已是第一页", Toast.LENGTH_SHORT).show();
            }
        });

        // 下一页
        ivNext.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                fetchWordList();
            } else {
                Toast.makeText(this, "已是最后一页", Toast.LENGTH_SHORT).show();
            }
        });

        // 分页数字点击
        for (int i = 0; i < pageTvList.size(); i++) {
            int finalI = i;
            pageTvList.get(i).setOnClickListener(v -> {
                String pageStr = pageTvList.get(finalI).getText().toString().trim();
                if (pageStr.isEmpty()) return;
                int targetPage = Integer.parseInt(pageStr);
                if (targetPage >= 1 && targetPage <= totalPages && targetPage != currentPage) {
                    currentPage = targetPage;
                    fetchWordList();
                }
            });
        }
    }

    // 观察ViewModel数据变化
    private void observeData() {
        wordViewModel.getWordPageResult().observe(this, new Observer<Result<PageResult<WordSimpleResp>>>() {
            @Override
            public void onChanged(Result<PageResult<WordSimpleResp>> result) {
                hideLoading();
                Log.d(TAG, "观察到的结果: " + (result != null));

                if (result != null && result.isSuccess() && result.getData() != null) {
                    PageResult<WordSimpleResp> pageResult = result.getData();

                    // 添加空值检查
                    if (pageResult.getList() == null) {
                        Log.e(TAG, "单词列表为null，使用空列表");
                        wordList.clear();
                    } else {
                        wordList.clear();
                        wordList.addAll(pageResult.getList());
                    }

                    totalPages = pageResult.getPages();
                    totalWords = Math.toIntExact(pageResult.getTotal());
                    wordAdapter.notifyDataSetChanged();
                    updatePageUI();

                    // 更新单词书信息
                    tvBookInfo.setText("单词总数：" + totalWords);
                } else {
                    String errorMsg = result != null ? result.getMessage() : "加载失败";
                    Toast.makeText(BookDetailPlazaActivity.this, "加载失败：" + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        wordViewModel.getErrorLiveData().observe(this, errorMsg -> {
            hideLoading();
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
        });
    }

    // 更新单词书信息
    private void updateBookInfo() {
        tvBookTitle.setText(bookName + " - 单词列表");
        tvBookName.setText(bookName);
        tvBookInfo.setText("加载中...");
    }

    // 加载单词列表
    private void fetchWordList() {
        showLoading();
        wordViewModel.getWordsByBookId(bookId, currentPage, pageSize);
    }

    // 更新分页UI
    private void updatePageUI() {
        resetPageTvStyle();
        int[] displayPages = calculateDisplayPages();

        for (int i = 0; i < pageTvList.size(); i++) {
            int pageNum = displayPages[i];
            if (pageNum > 0 && pageNum <= totalPages) {
                pageTvList.get(i).setText(String.valueOf(pageNum));
                pageTvList.get(i).setVisibility(View.VISIBLE);

                // 高亮当前页
                if (pageNum == currentPage) {
                    pageTvList.get(i).setTextColor(getResources().getColor(R.color.gray_dark));
                }
            } else {
                pageTvList.get(i).setVisibility(View.GONE);
            }
        }

        // 更新分页按钮状态
        ivPrev.setEnabled(currentPage > 1);
        ivNext.setEnabled(currentPage < totalPages);
    }

    // 重置分页按钮样式
    private void resetPageTvStyle() {
        for (TextView tv : pageTvList) {
            tv.setTextColor(getResources().getColor(R.color.black));
        }
    }

    // 计算要显示的页码
    private int[] calculateDisplayPages() {
        int[] pages = new int[PAGE_BUTTON_COUNT];
        if (totalPages <= PAGE_BUTTON_COUNT) {
            for (int i = 0; i < totalPages; i++) {
                pages[i] = i + 1;
            }
            for (int i = totalPages; i < PAGE_BUTTON_COUNT; i++) {
                pages[i] = 0;
            }
        } else {
            if (currentPage <= 2) {
                pages[0] = 1;
                pages[1] = 2;
                pages[2] = 3;
                pages[3] = 4;
            } else if (currentPage >= totalPages - 1) {
                pages[0] = totalPages - 3;
                pages[1] = totalPages - 2;
                pages[2] = totalPages - 1;
                pages[3] = totalPages;
            } else {
                pages[0] = currentPage - 2;
                pages[1] = currentPage - 1;
                pages[2] = currentPage;
                pages[3] = currentPage + 1;
            }
        }
        return pages;
    }

    // 显示加载状态
    private void showLoading() {
        if (mLoadingProgress != null) {
            mLoadingProgress.setVisibility(View.VISIBLE);
        } else {
            Log.e("BookDetail", "ProgressBar未初始化！");
        }
    }

    // 隐藏加载状态
    private void hideLoading() {
        if (mLoadingProgress != null) {
            mLoadingProgress.setVisibility(View.GONE);
        }
    }

    // 单词适配器
    public static class WordPlazaAdapter extends RecyclerView.Adapter<WordPlazaAdapter.ViewHolder> {
        private List<WordSimpleResp> mWordList;
        private OnButtonClickListener mListener;

        public WordPlazaAdapter(List<WordSimpleResp> wordList, OnButtonClickListener listener) {
            mWordList = wordList;
            mListener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_word_plaza, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            WordSimpleResp word = mWordList.get(position);
            holder.tvWordName.setText(word.getWordName());

            // 处理词性显示
            if (word.getPartOfSpeech() != null && !word.getPartOfSpeech().isEmpty()) {
                holder.tvPartOfSpeech.setText("[" + word.getPartOfSpeech() + "]");
            } else {
                holder.tvPartOfSpeech.setText("");
            }

            holder.tvWordExplain.setText(word.getWordExplain());

            // 学习按钮点击
            holder.tvLearn.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onLearnClick(word);
                }
            });

            // 收藏按钮点击
            holder.tvCollect.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onCollectClick(word);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mWordList == null ? 0 : mWordList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvWordName;
            TextView tvPartOfSpeech;
            TextView tvWordExplain;
            TextView tvLearn;
            TextView tvCollect;

            public ViewHolder(View view) {
                super(view);
                tvWordName = view.findViewById(R.id.tv_word_name);
                tvPartOfSpeech = view.findViewById(R.id.tv_part_of_speech);
                tvWordExplain = view.findViewById(R.id.tv_word_explain);
                tvLearn = view.findViewById(R.id.tv_learn);
                tvCollect = view.findViewById(R.id.tv_collect);
            }
        }

        // 按钮点击事件接口
        public interface OnButtonClickListener {
            void onLearnClick(WordSimpleResp word);
            void onCollectClick(WordSimpleResp word);
        }
    }
}