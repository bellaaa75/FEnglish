package com.example.fenglishandroid.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.fenglishandroid.model.Result;
import com.example.fenglishandroid.model.VocabularyBookSimpleResp;
import com.example.fenglishandroid.model.request.PageResult;
import com.example.fenglishandroid.service.RetrofitClient;
import com.example.fenglishandroid.service.VocabularyBookService;
import com.example.fenglishandroid.ui.BookDetailPlazaActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LearningPlazaFragment extends Fragment {
    // 控件
    private EditText etSearch;
    private Button btnSearch, btnReset;
    private RecyclerView rvBookList;
    private ImageView ivPrev, ivNext;
    private TextView tvPage1, tvPage2, tvPage3, tvPage4;

    // 数据和适配器
    private List<VocabularyBookSimpleResp> bookList = new ArrayList<>();
    private BookPlazaAdapter bookAdapter;

    // 分页参数
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;
    private String keyword = "";

    // 分页控件集合
    private List<TextView> pageTvList = new ArrayList<>();
    private static final int PAGE_BUTTON_COUNT = 4;

    // 网络服务
    private VocabularyBookService bookService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_learning_plaza, container, false);

        // 初始化网络服务
        bookService = RetrofitClient.getVocabularyBookService();

        initView(view);
        initPageTvList();
        initAdapter();
        initListener();
        fetchBookList(); // 加载单词书列表
        return view;
    }

    private void initView(View view) {
        etSearch = view.findViewById(R.id.et_search);
        btnSearch = view.findViewById(R.id.btn_search);
        btnReset = view.findViewById(R.id.btn_reset);
        rvBookList = view.findViewById(R.id.rv_book_list);
        tvPage1 = view.findViewById(R.id.tv_page_1);
        tvPage2 = view.findViewById(R.id.tv_page_2);
        tvPage3 = view.findViewById(R.id.tv_page_3);
        tvPage4 = view.findViewById(R.id.tv_page_4);
        ivPrev = view.findViewById(R.id.iv_prev);
        ivNext = view.findViewById(R.id.iv_next);
    }

    private void initPageTvList() {
        pageTvList.clear();
        pageTvList.add(tvPage1);
        pageTvList.add(tvPage2);
        pageTvList.add(tvPage3);
        pageTvList.add(tvPage4);
    }

    private void initAdapter() {
        bookAdapter = new BookPlazaAdapter(bookList, new OnItemClickListener() {
            @Override
            public void onItemClick(VocabularyBookSimpleResp book) {
                // 跳转到单词书详情页面
                Intent intent = new Intent(getContext(), BookDetailPlazaActivity.class);
                intent.putExtra("bookId", book.getBookId());
                intent.putExtra("bookName", book.getBookName());
                startActivity(intent);
            }

            @Override
            public void onCollectClick(VocabularyBookSimpleResp book) {
                // 仅显示按钮，暂不实现功能
                Toast.makeText(getContext(), "收藏功能待实现", Toast.LENGTH_SHORT).show();
            }
        });
        rvBookList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBookList.setAdapter(bookAdapter);
    }

    private void initListener() {
        // 搜索按钮
        btnSearch.setOnClickListener(v -> {
            keyword = etSearch.getText().toString().trim();
            currentPage = 1;
            fetchBookList();
        });

        // 重置按钮
        btnReset.setOnClickListener(v -> {
            etSearch.setText("");
            keyword = "";
            currentPage = 1;
            fetchBookList();
        });

        // 上一页
        ivPrev.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                updatePageUI();
                fetchBookList();
            } else {
                Toast.makeText(getContext(), "已是第一页", Toast.LENGTH_SHORT).show();
            }
        });

        // 下一页
        ivNext.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                updatePageUI();
                fetchBookList();
            } else {
                Toast.makeText(getContext(), "已是最后一页", Toast.LENGTH_SHORT).show();
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
                    updatePageUI();
                    fetchBookList();
                }
            });
        }
    }

    // 加载单词书列表
    private void fetchBookList() {
        Call<Result<PageResult<VocabularyBookSimpleResp>>> call = bookService.searchBooks(keyword, currentPage, pageSize);

        call.enqueue(new Callback<Result<PageResult<VocabularyBookSimpleResp>>>() {
            @Override
            public void onResponse(@NonNull Call<Result<PageResult<VocabularyBookSimpleResp>>> call, @NonNull Response<Result<PageResult<VocabularyBookSimpleResp>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<PageResult<VocabularyBookSimpleResp>> result = response.body();
                    if (result.isSuccess()) {
                        PageResult<VocabularyBookSimpleResp> pageResult = result.getData();
                        bookList.clear();
                        bookList.addAll(pageResult.getList());
                        totalPages = pageResult.getPages();
                        bookAdapter.notifyDataSetChanged();
                        updatePageUI();
                    } else {
                        Toast.makeText(getContext(), "加载失败：" + result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleErrorResponse(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<PageResult<VocabularyBookSimpleResp>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 处理错误响应码
    private void handleErrorResponse(int code) {
        if (code == 401) {
            Toast.makeText(getContext(), "Token无效/未登录，请重新登录", Toast.LENGTH_SHORT).show();
        } else if (code == 404) {
            Toast.makeText(getContext(), "接口路径错误（404）", Toast.LENGTH_SHORT).show();
        } else if (code == 500) {
            Toast.makeText(getContext(), "后端接口报错（500）", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "请求失败：" + code, Toast.LENGTH_SHORT).show();
        }
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
                    pageTvList.get(i).setBackgroundResource(R.drawable.shape_page_selected);
                    pageTvList.get(i).setTextColor(getResources().getColor(R.color.white));
                }
            } else {
                pageTvList.get(i).setVisibility(View.GONE);
            }
        }
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

    // 单词书适配器
    public class BookPlazaAdapter extends RecyclerView.Adapter<BookPlazaAdapter.ViewHolder> {
        private List<VocabularyBookSimpleResp> mBookList;
        private LearningPlazaFragment.OnItemClickListener mListener;

        public BookPlazaAdapter(List<VocabularyBookSimpleResp> bookList, LearningPlazaFragment.OnItemClickListener listener) {
            mBookList = bookList;
            mListener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_book_plaza, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            VocabularyBookSimpleResp book = mBookList.get(position);
            holder.tvBookId.setText("ID: " + book.getBookId());
            holder.tvBookName.setText(book.getBookName());
            holder.tvPublishTime.setText(book.getPublishTime());
            holder.tvWordCount.setText(String.valueOf(book.getWordCount()));

            // 点击整个项进入详情
            holder.itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(book);
                }
            });

            // 收藏按钮点击
            holder.btnCollect.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onCollectClick(book);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mBookList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvBookId;
            TextView tvBookName;
            TextView tvPublishTime;
            TextView tvWordCount;
            Button btnCollect;

            public ViewHolder(View view) {
                super(view);
                tvBookId = view.findViewById(R.id.tv_book_id);
                tvBookName = view.findViewById(R.id.tv_book_name);
                tvPublishTime = view.findViewById(R.id.tv_publish_time);
                tvWordCount = view.findViewById(R.id.tv_word_count);
                btnCollect = view.findViewById(R.id.btn_collect);
            }
        }
    }

    // 点击事件接口
    public interface OnItemClickListener {
        void onItemClick(VocabularyBookSimpleResp book);
        void onCollectClick(VocabularyBookSimpleResp book);
    }
}