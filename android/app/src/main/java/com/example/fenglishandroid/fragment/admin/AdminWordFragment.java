package com.example.fenglishandroid.fragment.admin;

import android.os.Bundle;
import android.util.Log;
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
import com.example.fenglishandroid.service.RetrofitClient;
import com.example.fenglishandroid.service.WordService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminWordFragment extends Fragment {
    // 控件
    private EditText etSearch;
    private Button btnSearch, btnReset;
    private RecyclerView rvWordList;
    private ImageView ivPrev, ivNext;
    private TextView tvPage1, tvPage2, tvPage3, tvPage4;

    // 数据和适配器
    private List<Map<String, Object>> wordList = new ArrayList<>();
    private WordAdapter wordAdapter;
    private Gson gson = new Gson();

    // 分页参数
    private int currentPage = 1;
    private int pageSize = 10; // 每页默认10个单词
    private int totalPages = 1; // 总页数（从后端获取）
    private String keyword = "";

    // 分页控件集合（方便统一处理）
    private List<TextView> pageTvList = new ArrayList<>();
    private static final int PAGE_BUTTON_COUNT = 4; // 分页按钮数量（固定4个）

    // 公共WordService接口
    private WordService wordService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_admin_word, container, false);

        // 初始化WordService
        wordService = RetrofitClient.getWordService();

        initView(view);
        initPageTvList(); // 初始化分页控件集合
        initAdapter();
        initListener();
        fetchWordList(); // 加载数据
        return view;
    }

    private void initView(View view) {
        etSearch = view.findViewById(R.id.et_search);
        btnSearch = view.findViewById(R.id.btn_search);
        btnReset = view.findViewById(R.id.btn_reset);
        rvWordList = view.findViewById(R.id.rv_word_list);
        ivPrev = view.findViewById(R.id.iv_prev);
        ivNext = view.findViewById(R.id.iv_next);
        tvPage1 = view.findViewById(R.id.tv_page_1);
        tvPage2 = view.findViewById(R.id.tv_page_2);
        tvPage3 = view.findViewById(R.id.tv_page_3);
        tvPage4 = view.findViewById(R.id.tv_page_4);
    }

    // 初始化分页控件集合（方便循环处理）
    private void initPageTvList() {
        pageTvList.clear();
        pageTvList.add(tvPage1);
        pageTvList.add(tvPage2);
        pageTvList.add(tvPage3);
        pageTvList.add(tvPage4);
    }

    // RecyclerView适配器
    private void initAdapter() {
        wordAdapter = new WordAdapter(wordList, new OnItemClickListener() {
            @Override
            public void onEditClick(Map<String, Object> word) {
                Toast.makeText(getContext(), "编辑单词：" + word.get("wordName"), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int wordID) {
                deleteWord(wordID); // 调用删除接口
            }
        });
        rvWordList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvWordList.setAdapter(wordAdapter);
    }

    private void initListener() {
        // 搜索
        btnSearch.setOnClickListener(v -> {
            keyword = etSearch.getText().toString().trim();
            currentPage = 1; // 搜索后重置为第一页
            fetchWordList();
        });

        // 重置
        btnReset.setOnClickListener(v -> {
            etSearch.setText("");
            keyword = "";
            currentPage = 1; // 重置后回到第一页
            fetchWordList();
        });

        // 上一页
        ivPrev.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                updatePageUI(); // 更新分页样式
                fetchWordList(); // 加载数据
            } else {
                Toast.makeText(getContext(), "已是第一页", Toast.LENGTH_SHORT).show();
            }
        });

        // 下一页
        ivNext.setOnClickListener(v -> {
            if (currentPage < totalPages) { // 只有当前页 < 总页数时才能翻页
                currentPage++;
                updatePageUI(); // 更新分页样式
                fetchWordList(); // 加载数据
            } else {
                Toast.makeText(getContext(), "已是最后一页", Toast.LENGTH_SHORT).show();
            }
        });

        // 分页数字点击（循环绑定，避免重复代码）
        for (int i = 0; i < pageTvList.size(); i++) {
            int finalI = i;
            pageTvList.get(i).setOnClickListener(v -> {
                // 获取当前点击的页码（转为int）
                String pageStr = pageTvList.get(finalI).getText().toString().trim();
                if (pageStr.isEmpty()) return;
                int targetPage = Integer.parseInt(pageStr);
                // 只有目标页在有效范围内且不等于当前页时才切换
                if (targetPage >= 1 && targetPage <= totalPages && targetPage != currentPage) {
                    currentPage = targetPage;
                    updatePageUI();
                    fetchWordList();
                }
            });
        }
    }

    // 核心优化：更新分页UI（当前页居中，不足4页全展示）
    private void updatePageUI() {
        // 1. 重置所有分页按钮样式
        resetPageTvStyle();

        // 2. 根据总页数和当前页，计算要显示的4个页码
        int[] displayPages = calculateDisplayPages();

        // 3. 给分页按钮设置页码和点击事件
        for (int i = 0; i < pageTvList.size(); i++) {
            int pageNum = displayPages[i];
            if (pageNum > 0 && pageNum <= totalPages) { // 只显示有效页码
                pageTvList.get(i).setText(String.valueOf(pageNum));
                pageTvList.get(i).setVisibility(View.VISIBLE); // 显示按钮

                // 高亮当前页
                if (pageNum == currentPage) {
                    pageTvList.get(i).setBackgroundResource(R.drawable.shape_page_selected);
                    pageTvList.get(i).setTextColor(getResources().getColor(R.color.white));
                }
            } else {
                pageTvList.get(i).setVisibility(View.GONE); // 隐藏无效页码按钮
            }
        }

        // 4. 控制上一页/下一页按钮状态（可选：添加禁用样式）
        updatePrevNextButtonStatus();
    }

    // 计算要显示的4个页码（核心逻辑）
    private int[] calculateDisplayPages() {
        int[] pages = new int[PAGE_BUTTON_COUNT];
        if (totalPages <= PAGE_BUTTON_COUNT) {
            // 情况1：总页数 ≤ 4，显示所有页码（1,2,3,...totalPages）
            for (int i = 0; i < totalPages; i++) {
                pages[i] = i + 1;
            }
            // 剩余位置填充0（后续隐藏）
            for (int i = totalPages; i < PAGE_BUTTON_COUNT; i++) {
                pages[i] = 0;
            }
        } else {
            // 情况2：总页数 > 4，当前页居中（尽量让当前页在中间位置）
            if (currentPage <= 2) {
                // 前2页：显示1,2,3,4（当前页在1或2时，左侧无更多页）
                pages[0] = 1;
                pages[1] = 2;
                pages[2] = 3;
                pages[3] = 4;
            } else if (currentPage >= totalPages - 1) {
                // 后2页：显示 totalPages-3, totalPages-2, totalPages-1, totalPages（当前页在最后2页时，右侧无更多页）
                pages[0] = totalPages - 3;
                pages[1] = totalPages - 2;
                pages[2] = totalPages - 1;
                pages[3] = totalPages;
            } else {
                // 中间页：显示 currentPage-2, currentPage-1, currentPage, currentPage+1（当前页居中）
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

    // 更新上一页/下一页按钮状态（可选：添加禁用效果，更友好）
    private void updatePrevNextButtonStatus() {
        // 上一页：当前页=1时禁用
        ivPrev.setAlpha(currentPage == 1 ? 0.5f : 1.0f);
        ivPrev.setClickable(currentPage != 1);

        // 下一页：当前页=总页数时禁用
        ivNext.setAlpha(currentPage == totalPages ? 0.5f : 1.0f);
        ivNext.setClickable(currentPage != totalPages);
    }

    // 核心：调用后端分页接口（GET请求+Query参数）
    private void fetchWordList() {
        Log.d("WordDebug", "请求参数：pageNum=" + currentPage + ", pageSize=" + pageSize + ", keyword=" + keyword);

        Call<ResponseBody> call = wordService.getWordList(currentPage, pageSize, keyword);
        Log.d("WordDebug", "请求URL：" + call.request().url().toString());
        // Log.d("WordDebug", "携带的Token：" + call.request().header("Authorization"));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        Log.d("WordDebug", "响应数据：" + json);

                        Map<String, Object> result = gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
                        boolean success = (boolean) result.get("success");

                        if (success) {
                            List<Map<String, Object>> newWordList = gson.fromJson(
                                    gson.toJson(result.get("data")),
                                    new TypeToken<List<Map<String, Object>>>() {}.getType()
                            );
                            // 处理总页数（避免Double转int报错）
                            totalPages = result.get("totalPages") instanceof Double ?
                                    ((Double) result.get("totalPages")).intValue() :
                                    Integer.parseInt(result.get("totalPages").toString());
                            int total = result.get("total") instanceof Double ?
                                    ((Double) result.get("total")).intValue() :
                                    Integer.parseInt(result.get("total").toString());

                            // 更新UI
                            wordList.clear();
                            wordList.addAll(newWordList);
                            wordAdapter.notifyDataSetChanged();
                            updatePageUI(); // 加载数据后更新分页样式
                            Toast.makeText(getContext(), "共" + total + "个单词", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = result.get("message") != null ? result.get("message").toString() : "未知错误";
                            Toast.makeText(getContext(), "加载失败：" + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "解析失败", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "数据处理失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(getContext(), "Token无效/未登录，请重新登录", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(getContext(), "接口路径错误（404）", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 500) {
                        Toast.makeText(getContext(), "后端接口报错（500）", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "请求失败：" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("WordDebug", "网络失败：" + t.getMessage(), t);
            }
        });
    }

    // 删除单词接口
    private void deleteWord(int wordID) {
        Call<ResponseBody> call = wordService.deleteWord(wordID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        Map<String, Object> result = gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
                        boolean success = (boolean) result.get("success");

                        if (success) {
                            Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                            fetchWordList(); // 刷新列表
                        } else {
                            String message = result.get("message") != null ? result.get("message").toString() : "未知错误";
                            Toast.makeText(getContext(), "删除失败：" + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "解析失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(getContext(), "Token无效/未登录，请重新登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "删除失败，状态码：" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 内部点击事件接口
    interface OnItemClickListener {
        void onEditClick(Map<String, Object> word);
        void onDeleteClick(int wordID);
    }

    // 内部RecyclerView适配器
    class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {
        private List<Map<String, Object>> data;
        private OnItemClickListener listener;

        public WordAdapter(List<Map<String, Object>> data, OnItemClickListener listener) {
            this.data = data;
            this.listener = listener;
        }

        @NonNull
        @Override
        public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_word, parent, false);
            return new WordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
            Map<String, Object> word = data.get(position);
            // 1. 绑定单词名称
            holder.tvWordName.setText(word.get("wordName") != null ? word.get("wordName").toString() : "");

            // 2. 绑定词性（原灰色区域，显示 partOfSpeech 字段）
            String partOfSpeech = word.get("partOfSpeech") != null ? word.get("partOfSpeech").toString() : "未知词性";
            holder.tvPartOfSpeech.setText(partOfSpeech); // 变量名与XML ID对应

            // 3. 绑定单词释义
            holder.tvWordExplain.setText(word.get("wordExplain") != null ? word.get("wordExplain").toString() : "");

            // 操作事件
            holder.tvEdit.setOnClickListener(v -> listener.onEditClick(word));
            holder.tvDelete.setOnClickListener(v -> {
                // 处理wordID类型转换
                int wordID = 0;
                Object idObj = word.get("wordID");
                if (idObj instanceof Double) {
                    wordID = ((Double) idObj).intValue();
                } else if (idObj instanceof Integer) {
                    wordID = (Integer) idObj;
                }
                if (wordID != 0) {
                    listener.onDeleteClick(wordID);
                } else {
                    Toast.makeText(getContext(), "单词ID无效", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        // 修正 ViewHolder：变量名与XML控件ID完全一致
        class WordViewHolder extends RecyclerView.ViewHolder {
            TextView tvWordName;
            TextView tvPartOfSpeech; // 对应XML中的 id: tv_part_of_speech（驼峰命名）
            TextView tvWordExplain;
            TextView tvEdit;
            TextView tvDelete;

            public WordViewHolder(@NonNull View itemView) {
                super(itemView);
                tvWordName = itemView.findViewById(R.id.tv_word_name);
                tvPartOfSpeech = itemView.findViewById(R.id.tv_part_of_speech); // 绑定正确ID
                tvWordExplain = itemView.findViewById(R.id.tv_word_explain);
                tvEdit = itemView.findViewById(R.id.tv_edit);
                tvDelete = itemView.findViewById(R.id.tv_delete);
            }
        }
    }
}