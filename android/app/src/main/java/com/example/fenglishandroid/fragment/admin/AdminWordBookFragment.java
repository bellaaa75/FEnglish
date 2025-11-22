package com.example.fenglishandroid.fragment.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.request.PageResult;
import com.example.fenglishandroid.model.Result;
import com.example.fenglishandroid.model.VocabularyBookSimpleResp;
import com.example.fenglishandroid.service.RetrofitClient;
import com.example.fenglishandroid.service.VocabularyBookService;
import com.example.fenglishandroid.ui.admin.AddBookActivity;
import com.example.fenglishandroid.ui.admin.EditBookActivity;
import com.google.gson.Gson;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminWordBookFragment extends Fragment {
    // 控件
    private SearchView searchView;
    private Button btnAdd;
    private RecyclerView recyclerView;
    private TextView tvPage1, tvPage2, tvPage3, tvPage4;
    private View ivPrev, ivNext;
    private Button btnSearch, btnReset;


    // 数据和适配器
    private List<VocabularyBookSimpleResp> bookList = new ArrayList<>();
    private BookAdapter bookAdapter;
    private Gson gson = new Gson();

    // 分页参数
    private int currentPage = 1;
    private int pageSize = 5;
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
        View view = inflater.inflate(R.layout.frag_admin_wordbook, container, false);

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
        searchView = view.findViewById(R.id.search_view);
        btnAdd = view.findViewById(R.id.btn_add);
        recyclerView = view.findViewById(R.id.recycler_view);
        tvPage1 = view.findViewById(R.id.tv_page_1);
        tvPage2 = view.findViewById(R.id.tv_page_2);
        tvPage3 = view.findViewById(R.id.tv_page_3);
        tvPage4 = view.findViewById(R.id.tv_page_4);
        ivPrev = view.findViewById(R.id.iv_prev);
        ivNext = view.findViewById(R.id.iv_next);
        btnSearch = view.findViewById(R.id.btn_search);
        btnReset = view.findViewById(R.id.btn_reset);
    }

    private void initPageTvList() {
        pageTvList.clear();
        pageTvList.add(tvPage1);
        pageTvList.add(tvPage2);
        pageTvList.add(tvPage3);
        pageTvList.add(tvPage4);
    }

    private void initAdapter() {
        bookAdapter = new BookAdapter(bookList, new OnItemActionListener() {
            @Override
            public void onEditClick(VocabularyBookSimpleResp book) {
                // 跳转到编辑页面
                Intent intent = new Intent(getContext(), EditBookActivity.class);
                intent.putExtra("bookId", book.getBookId());
                startActivityForResult(intent, 100);
            }

            @Override
            public void onDeleteClick(String bookId) {
                // 显示删除确认对话框
                new androidx.appcompat.app.AlertDialog.Builder(getContext())
                        .setTitle("删除确认")
                        .setMessage("确定要删除这本单词书吗？")
                        .setPositiveButton("确定", (dialog, which) -> deleteBook(bookId))
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bookAdapter);
    }

    private void initListener() {
        // 搜索功能
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                keyword = query.trim();
                currentPage = 1;
                fetchBookList();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    keyword = "";
                    currentPage = 1;
                    fetchBookList();
                }
                return false;
            }
        });
        btnSearch.setOnClickListener(v -> {
            keyword = searchView.getQuery().toString().trim();
            currentPage = 1;
            fetchBookList();
        });

        // 重置按钮点击
        btnReset.setOnClickListener(v -> {
            searchView.setQuery("", false);  // 清空搜索框
            keyword = "";
            currentPage = 1;
            fetchBookList();
        });

        // 新增单词书
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddBookActivity.class);
            startActivityForResult(intent, 100);
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
        Log.d("BookDebug", "请求参数：page=" + currentPage + ", size=" + pageSize + ", keyword=" + keyword);
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
                        Toast.makeText(getContext(), "共" + pageResult.getTotal() + "本单词书", Toast.LENGTH_SHORT).show();
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
                Log.e("BookDebug", "网络失败：" + t.getMessage(), t);
            }
        });
    }

    // 删除单词书
    private void deleteBook(String bookId) {
        Call<Result<Void>> call = bookService.deleteBook(bookId);
        call.enqueue(new Callback<Result<Void>>() {
            @Override
            public void onResponse(@NonNull Call<Result<Void>> call, @NonNull Response<Result<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        fetchBookList(); // 刷新列表
                    } else {
                        Toast.makeText(getContext(), "删除失败：" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleErrorResponse(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result<Void>> call, @NonNull Throwable t) {
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
                if (pageNum == currentPage) {
                    pageTvList.get(i).setBackgroundResource(R.drawable.shape_page_selected);
                    pageTvList.get(i).setTextColor(getResources().getColor(R.color.white));
                }
            } else {
                pageTvList.get(i).setVisibility(View.GONE);
            }
        }

        updatePrevNextButtonStatus();
    }

    // 计算显示的分页页码
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

    // 重置分页按钮样式
    private void resetPageTvStyle() {
        for (TextView tv : pageTvList) {
            tv.setBackgroundResource(0);
            tv.setTextColor(getResources().getColor(R.color.black));
            tv.setVisibility(View.VISIBLE);
        }
    }

    // 更新上一页/下一页按钮状态
    private void updatePrevNextButtonStatus() {
        ivPrev.setAlpha(currentPage == 1 ? 0.5f : 1.0f);
        ivPrev.setClickable(currentPage != 1);
        ivNext.setAlpha(currentPage == totalPages ? 0.5f : 1.0f);
        ivNext.setClickable(currentPage != totalPages);
    }

    // 页面返回刷新
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == android.app.Activity.RESULT_OK) {
            fetchBookList(); // 新增/编辑成功后刷新列表
        }
    }

    // 内部适配器类（合并原VocabularyBookAdapter）
    private class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
        private List<VocabularyBookSimpleResp> data;
        private OnItemActionListener listener;

        public BookAdapter(List<VocabularyBookSimpleResp> data, OnItemActionListener listener) {
            this.data = data;
            this.listener = listener;
        }

        @NonNull
        @Override
        public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_vocabulary_book, parent, false);
            return new BookViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
            VocabularyBookSimpleResp book = data.get(position);
            if (book == null) return;

            // 绑定数据
            holder.tvBookId.setText(book.getBookId());
            holder.tvBookName.setText(book.getBookName());
            holder.tvPublishTime.setText(formatPublishTime(book.getPublishTime()));
            holder.tvWordCount.setText(String.valueOf(book.getWordCount()));

            // 编辑按钮点击
            holder.btnEdit.setOnClickListener(v -> listener.onEditClick(book));

            // 删除按钮点击
            holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(book.getBookId()));
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        // 格式化发布时间
        private String formatPublishTime(String time) {
            if (time == null || time.isEmpty()) return "";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat targetSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                return targetSdf.format(sdf.parse(time));
            } catch (ParseException e) {
                e.printStackTrace();
                return time;
            }
        }

        // ViewHolder
        class BookViewHolder extends RecyclerView.ViewHolder {
            TextView tvBookId;
            TextView tvBookName;
            TextView tvPublishTime;
            TextView tvWordCount;
            View btnEdit;
            View btnDelete;

            public BookViewHolder(@NonNull View itemView) {
                super(itemView);
                tvBookId = itemView.findViewById(R.id.tv_book_id);
                tvBookName = itemView.findViewById(R.id.tv_book_name);
                tvPublishTime = itemView.findViewById(R.id.tv_publish_time);
                tvWordCount = itemView.findViewById(R.id.tv_word_count);
                btnEdit = itemView.findViewById(R.id.btn_edit);
                btnDelete = itemView.findViewById(R.id.btn_delete);
            }
        }
    }

    // 内部点击事件接口
    public interface OnItemActionListener {
        void onEditClick(VocabularyBookSimpleResp book);
        void onDeleteClick(String bookId);
    }
}