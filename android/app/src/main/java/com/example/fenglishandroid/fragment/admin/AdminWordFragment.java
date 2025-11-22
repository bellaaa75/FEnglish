package com.example.fenglishandroid.fragment.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    private List<Map<String, Object>> wordList = new ArrayList<>(); // 当前页显示的数据
    private List<Map<String, Object>> fullSearchResult = new ArrayList<>(); // 搜索模式下的完整结果（用于前端分页）
    private WordAdapter wordAdapter;
    private Gson gson = new Gson();

    // 分页参数
    private int currentPage = 1;
    private int pageSize = 10; // 每页默认10个单词
    private int totalPages = 1; // 总页数（从后端获取/前端计算）
    private String keyword = "";

    // 分页控件集合（方便统一处理）
    private List<TextView> pageTvList = new ArrayList<>();
    private static final int PAGE_BUTTON_COUNT = 4; // 分页按钮数量（固定4个）

    // 公共WordService接口
    private WordService wordService;

    // 全局Toast对象（避免堆积）
    private Toast mToast;

    // 标记当前是否为搜索状态（true：搜索结果，false：全部单词）
    private boolean isSearchMode = false;

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
        fetchWordList(); // 加载数据（默认显示全部单词）
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
                showToast("编辑单词：" + word.get("wordName"));
            }

            @Override
            public void onDeleteClick(String wordId, String wordName) {
                // 点击删除时先显示确认弹窗
                showDeleteConfirmDialog(wordId, wordName);
            }
        });
        rvWordList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvWordList.setAdapter(wordAdapter);
    }

    private void initListener() {
        // 搜索按钮
        btnSearch.setOnClickListener(v -> {
            keyword = etSearch.getText().toString().trim();
            if (keyword.isEmpty()) {
                showToast("请输入搜索关键词");
                return;
            }
            isSearchMode = true; // 进入搜索模式
            currentPage = 1; // 搜索后重置为第一页
            fetchSearchResult(); // 调用搜索接口（获取完整结果）
        });

        // 重置按钮
        btnReset.setOnClickListener(v -> {
            etSearch.setText(""); // 清空输入框
            keyword = ""; // 清空关键词
            isSearchMode = false; // 退出搜索模式
            fullSearchResult.clear(); // 清空完整搜索结果
            currentPage = 1; // 重置为第一页
            fetchWordList(); // 加载全部单词
        });

        // 上一页
        ivPrev.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                if (isSearchMode) {
                    loadCurrentPageSearchResult(); // 搜索模式：前端截取当前页数据
                } else {
                    fetchWordList(); // 非搜索模式：调用后端分页接口
                }
                updatePageUI(); // 更新分页样式
            } else {
                showToast("已是第一页");
            }
        });

        // 下一页
        ivNext.setOnClickListener(v -> {
            if (currentPage < totalPages) { // 只有当前页 < 总页数时才能翻页
                currentPage++;
                if (isSearchMode) {
                    loadCurrentPageSearchResult(); // 搜索模式：前端截取当前页数据
                } else {
                    fetchWordList(); // 非搜索模式：调用后端分页接口
                }
                updatePageUI(); // 更新分页样式
            } else {
                showToast("已是最后一页");
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
                    if (isSearchMode) {
                        loadCurrentPageSearchResult(); // 搜索模式：前端截取当前页数据
                    } else {
                        fetchWordList(); // 非搜索模式：调用后端分页接口
                    }
                    updatePageUI();
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

    // 加载全部单词（原有逻辑，无改动）
    private void fetchWordList() {
        Log.d("WordDebug", "请求参数：pageNum=" + currentPage + ", pageSize=" + pageSize + ", keyword=" + keyword);

        Call<ResponseBody> call = wordService.getWordList(currentPage, pageSize, keyword);
        Log.d("WordDebug", "请求URL：" + call.request().url().toString());

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
                            showToast("共" + total + "个单词");
                        } else {
                            String message = result.get("message") != null ? result.get("message").toString() : "未知错误";
                            showToast("加载失败：" + message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        showToast("解析失败");
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("数据处理失败");
                    }
                } else {
                    if (response.code() == 401) {
                        showToast("Token无效/未登录，请重新登录");
                    } else if (response.code() == 404) {
                        showToast("接口路径错误（404）");
                    } else if (response.code() == 500) {
                        showToast("后端接口报错（500）");
                    } else {
                        showToast("请求失败：" + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                showToast("网络错误：" + t.getMessage());
                Log.e("WordDebug", "网络失败：" + t.getMessage(), t);
            }
        });
    }

    // 新增：获取完整搜索结果（调用后端接口）
    private void fetchSearchResult() {
        Log.d("WordDebug", "搜索关键词：" + keyword);

        // 调用后端模糊搜索接口：GET /api/words/name/fuzzy/{wordName}（无分页参数，获取全部结果）
        Call<ResponseBody> call = wordService.searchWordByFuzzyName(keyword);
        Log.d("WordDebug", "搜索请求URL：" + call.request().url().toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        Log.d("WordDebug", "搜索响应数据（完整结果）：" + json);

                        // 后端返回直接是单词列表（成功返回列表，失败返回空列表）
                        fullSearchResult = gson.fromJson(
                                json,
                                new TypeToken<List<Map<String, Object>>>() {}.getType()
                        );

                        int total = fullSearchResult.size();
                        // 前端计算总页数（向上取整）
                        totalPages = (total + pageSize - 1) / pageSize;

                        // 加载第一页搜索结果
                        loadCurrentPageSearchResult();

                        // 显示提示
                        if (total == 0) {
                            showToast("未找到包含关键词\"" + keyword + "\"的单词");
                        } else {
                            showToast("找到" + total + "个匹配单词，共" + totalPages + "页");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        showToast("搜索结果解析失败");
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("搜索数据处理失败");
                    }
                } else {
                    showToast("搜索失败，状态码：" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                showToast("搜索网络错误：" + t.getMessage());
                Log.e("WordDebug", "搜索网络失败：" + t.getMessage(), t);
            }
        });
    }

    // 新增：搜索模式下，加载当前页数据（前端手动分页）
    private void loadCurrentPageSearchResult() {
        List<Map<String, Object>> currentPageData = new ArrayList<>();
        int total = fullSearchResult.size();

        // 计算当前页的起始索引和结束索引
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total); // 避免索引越界

        // 截取当前页数据
        if (startIndex < total) {
            currentPageData = fullSearchResult.subList(startIndex, endIndex);
        }

        // 更新UI
        wordList.clear();
        wordList.addAll(currentPageData);
        wordAdapter.notifyDataSetChanged();
        updatePageUI(); // 更新分页样式
    }

    // 删除单词接口（修改：搜索模式下删除后重新截取当前页数据）
    private void deleteWord(String wordId) {
        Log.d("WordDebug", "删除单词：wordId=" + wordId); // 打印wordId，确认格式正确
        Call<ResponseBody> call = wordService.deleteWord(wordId); // 传递字符串wordId
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        Log.d("WordDebug", "删除响应数据：" + json);
                        Map<String, Object> result = gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
                        boolean success = (boolean) result.get("success");
                        String message = result.get("message") != null ? result.get("message").toString() : "操作失败";

                        if (success) {
                            showToast("删除成功");
                            // 根据当前模式刷新数据
                            if (isSearchMode) {
                                // 搜索模式：从完整结果中移除删除的单词，重新加载当前页
                                removeDeletedWordFromSearchResult(wordId);
                                loadCurrentPageSearchResult();
                            } else {
                                fetchWordList();
                            }
                        } else {
                            showToast("删除失败：" + message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        showToast("解析失败");
                    }
                } else {
                    if (response.code() == 401) {
                        showToast("Token无效/未登录，请重新登录");
                    } else if (response.code() == 404) {
                        showToast("单词不存在（404）");
                    } else {
                        showToast("删除失败，状态码：" + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                showToast("网络错误：" + t.getMessage());
                Log.e("WordDebug", "删除网络失败：" + t.getMessage(), t);
            }
        });
    }

    // 新增：从完整搜索结果中移除已删除的单词
    private void removeDeletedWordFromSearchResult(String deletedWordId) {
        List<Map<String, Object>> newResult = new ArrayList<>();
        for (Map<String, Object> word : fullSearchResult) {
            String wordId = word.get("wordId") != null ? word.get("wordId").toString() : "";
            if (!wordId.equals(deletedWordId)) {
                newResult.add(word);
            }
        }
        fullSearchResult = newResult;
        // 重新计算总页数
        totalPages = (fullSearchResult.size() + pageSize - 1) / pageSize;
        // 如果当前页超过总页数，回退到最后一页
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }
    }

    // 显示删除确认弹窗（原有逻辑，无改动）
    private void showDeleteConfirmDialog(String wordId, String wordName) {
        new AlertDialog.Builder(getContext())
                .setTitle("确认删除")
                .setMessage("你确定要删除单词 \"" + wordName + "\" 吗？删除后不可恢复！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击确定，执行删除操作
                        deleteWord(wordId);
                        dialog.dismiss(); // 关闭弹窗
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击取消，不执行操作，关闭弹窗
                        dialog.dismiss();
                    }
                })
                .setCancelable(false) // 点击弹窗外部不关闭
                .show();
    }

    // 内部点击事件接口（原有逻辑，无改动）
    interface OnItemClickListener {
        void onEditClick(Map<String, Object> word);
        void onDeleteClick(String wordId, String wordName);
    }

    // 内部RecyclerView适配器（原有逻辑，无改动）
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
            String wordName = word.get("wordName") != null ? word.get("wordName").toString() : "未知单词";
            holder.tvWordName.setText(wordName);

            // 2. 绑定词性（原灰色区域，显示 partOfSpeech 字段）
            String partOfSpeech = word.get("partOfSpeech") != null ? word.get("partOfSpeech").toString() : "未知词性";
            holder.tvPartOfSpeech.setText(partOfSpeech); // 变量名与XML ID对应

            // 3. 绑定单词释义
            holder.tvWordExplain.setText(word.get("wordExplain") != null ? word.get("wordExplain").toString() : "");

            // 操作事件
            holder.tvEdit.setOnClickListener(v -> listener.onEditClick(word));
            holder.tvDelete.setOnClickListener(v -> {
                // 获取字符串类型的wordId
                String wordId = null;
                Object idObj = word.get("wordId");
                if (idObj != null) {
                    wordId = idObj.toString();
                }

                if (wordId != null && !wordId.isEmpty()) {
                    // 传递wordId和wordName到弹窗
                    listener.onDeleteClick(wordId, wordName);
                } else {
                    showToast("单词ID无效");
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

    // 自定义Toast方法（原有逻辑，无改动）
    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        mToast.show();
    }
}