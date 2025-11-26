package com.example.fenglishandroid.fragment.admin;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fenglishandroid.R;
import com.example.fenglishandroid.adapter.UserAdapter;
import com.example.fenglishandroid.databinding.FragAdminSettingBinding;
import com.example.fenglishandroid.model.request.User;
import com.example.fenglishandroid.viewModel.AdminSettingViewModel;

import java.util.List;

public class AdminSettingFragment extends Fragment {

    private FragAdminSettingBinding binding;
    private AdminSettingViewModel viewModel;
    private UserAdapter userAdapter;

    // 视图引用
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private RecyclerView rvUserList;
    private LinearLayout paginationLayout;
    private ImageButton btnPrev;
    private ImageButton btnNext;
    private LinearLayout pageNumberContainer;
    private TextView tvFooter;
    private EditText etSearch;
    private Button btnSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragAdminSettingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("AdminSettingFragment", "Fragment 视图已创建");

        viewModel = new ViewModelProvider(this).get(AdminSettingViewModel.class);

        initViews();
        setupObservers(); // 添加这个方法调用
        setupClickListeners();

        // 初始加载用户列表
        viewModel.refreshUsers();
    }

    private void initViews() {
        // 初始化视图引用
        progressBar = binding.progressBar;
        tvEmpty = binding.tvEmpty;
        rvUserList = binding.rvUserList;
        paginationLayout = binding.paginationLayout;
        btnPrev = binding.btnPrev;
        btnNext = binding.btnNext;
        pageNumberContainer = binding.pageNumberContainer;
        tvFooter = binding.tvFooter;
        etSearch = binding.etSearch;
        btnSearch = binding.btnSearch;

        // 初始化 RecyclerView
        userAdapter = new UserAdapter(null, new UserAdapter.OnUserActionListener() {
            @Override
            public void onViewUserDetail(User user) {
                showUserDetailDialog(user);
            }

            @Override
            public void onDeleteUser(User user) {
                showDeleteUserDialog(user);
            }
        });

        rvUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUserList.setAdapter(userAdapter);
    }

    // 添加缺失的 setupObservers 方法
    private void setupObservers() {
        // 观察用户列表
        viewModel.getUserList().observe(getViewLifecycleOwner(), users -> {
            Log.d("AdminSettingFragment", "用户列表更新，数量: " + (users != null ? users.size() : 0));
            userAdapter.updateData(users);
            updateEmptyState(users);
        });

        // 观察分页信息变化
        viewModel.getCurrentPage().observe(getViewLifecycleOwner(), page -> {
            Log.d("AdminSettingFragment", "当前页码: " + page);
            updatePaginationUI();
        });

        viewModel.getTotalPages().observe(getViewLifecycleOwner(), totalPages -> {
            Log.d("AdminSettingFragment", "总页数: " + totalPages);
            updatePaginationUI();
        });

        viewModel.getTotalCount().observe(getViewLifecycleOwner(), totalCount -> {
            Log.d("AdminSettingFragment", "总用户数: " + totalCount);
            updateFooter(totalCount);
        });

        // 观察Toast消息
        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Log.d("AdminSettingFragment", "显示Toast: " + message);
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // 观察加载状态
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d("AdminSettingFragment", "加载状态: " + isLoading);
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void updateEmptyState(List<User> users) {
        if (users == null || users.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvUserList.setVisibility(View.GONE);
            paginationLayout.setVisibility(View.GONE);
            tvFooter.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvUserList.setVisibility(View.VISIBLE);
        }
    }

    private void updatePaginationUI() {
        Integer currentPage = viewModel.getCurrentPage().getValue();
        Integer totalPages = viewModel.getTotalPages().getValue();

        if (currentPage != null && totalPages != null && totalPages > 1) {
            // 更新箭头按钮状态
            boolean hasPrevPage = currentPage > 0;
            boolean hasNextPage = currentPage < totalPages - 1;

            btnPrev.setEnabled(hasPrevPage);
            btnNext.setEnabled(hasNextPage);

            // 使用颜色变化来表示可用/禁用状态
            if (hasPrevPage) {
                btnPrev.setColorFilter(Color.parseColor("#2196F3")); // 启用时蓝色
            } else {
                btnPrev.setColorFilter(Color.parseColor("#CCCCCC")); // 禁用时灰色
            }

            if (hasNextPage) {
                btnNext.setColorFilter(Color.parseColor("#2196F3")); // 启用时蓝色
            } else {
                btnNext.setColorFilter(Color.parseColor("#CCCCCC")); // 禁用时灰色
            }

            // 生成页码按钮
            generatePageNumbers(currentPage, totalPages);

            // 显示分页控件
            paginationLayout.setVisibility(View.VISIBLE);
        } else {
            paginationLayout.setVisibility(View.GONE);
        }
    }

    // 生成页码按钮
    private void generatePageNumbers(int currentPage, int totalPages) {
        pageNumberContainer.removeAllViews();  // 清除旧的页码按钮

        // 计算显示的页码范围（最多显示5个页码）
        int startPage = Math.max(0, currentPage - 2);
        int endPage = Math.min(totalPages - 1, currentPage + 2);

        // 如果左边有空缺，向右调整
        if (startPage > 0 && endPage - startPage < 4) {
            endPage = Math.min(totalPages - 1, startPage + 4);
        }
        // 如果右边有空缺，向左调整
        if (endPage < totalPages - 1 && endPage - startPage < 4) {
            startPage = Math.max(0, endPage - 4);
        }

        // 添加页码按钮
        for (int i = startPage; i <= endPage; i++) {
            Button pageButton = createPageButton(i, i == currentPage);
            pageNumberContainer.addView(pageButton);

            // 添加间距（除了最后一个按钮）
            if (i < endPage) {
                View space = new View(getContext());
                space.setLayoutParams(new LinearLayout.LayoutParams(8, 1));
                pageNumberContainer.addView(space);
            }
        }
    }

    // 创建页码按钮
    private Button createPageButton(int pageIndex, boolean isCurrent) {
        Button button = new Button(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(36), dpToPx(36)
        );
        params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
        button.setLayoutParams(params);

        button.setText(String.valueOf(pageIndex + 1));
        button.setTextSize(14);

        if (isCurrent) {
            button.setBackgroundResource(R.drawable.btn_circle_solid);
            button.setTextColor(Color.WHITE);
        } else {
            button.setBackgroundResource(R.drawable.btn_circle_page);
            button.setTextColor(Color.parseColor("#666666"));
        }

        button.setStateListAnimator(null);

        final int targetPage = pageIndex;
        button.setOnClickListener(v -> {
            Log.d("AdminSettingFragment", "点击页码: " + (targetPage + 1));
            viewModel.goToPage(targetPage + 1);
        });

        return button;
    }

    // dp转px
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void updateFooter(Long totalCount) {
        if (totalCount != null && totalCount > 0) {
            tvFooter.setText("共找到 " + totalCount + " 个用户");
            //tvFooter.setVisibility(View.VISIBLE);
        } else {
            tvFooter.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        // 搜索按钮
        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            Log.d("AdminSettingFragment", "搜索关键词: " + keyword);
            viewModel.searchUsers(keyword);
        });

        // 搜索框的搜索动作
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = etSearch.getText().toString().trim();
            viewModel.searchUsers(keyword);
            return true;
        });

        // 上一页按钮
        btnPrev.setOnClickListener(v -> {
            Log.d("AdminSettingFragment", "点击上一页");
            viewModel.loadPreviousPage();
        });

        // 下一页按钮
        btnNext.setOnClickListener(v -> {
            Log.d("AdminSettingFragment", "点击下一页");
            viewModel.loadNextPage();
        });
    }

    private void showUserDetailDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("用户详情");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_user_detail, null);

        TextView tvUserId = dialogView.findViewById(R.id.tv_user_id);
        TextView tvUserName = dialogView.findViewById(R.id.tv_user_name);
        TextView tvUserType = dialogView.findViewById(R.id.tv_user_type);
        TextView tvPhone = dialogView.findViewById(R.id.tv_phone);
        TextView tvEmail = dialogView.findViewById(R.id.tv_email);
        TextView tvGender = dialogView.findViewById(R.id.tv_gender);
        TextView tvRegisterTime = dialogView.findViewById(R.id.tv_register_time);

        tvUserId.setText(user.getUserId());
        tvUserName.setText(user.getUserName());
        tvUserType.setText("普通用户");
        tvPhone.setText(TextUtils.isEmpty(user.getPhoneNumber()) ? "未设置" : user.getPhoneNumber());
        tvEmail.setText(TextUtils.isEmpty(user.getUserMailbox()) ? "未设置" : user.getUserMailbox());
        tvGender.setText(TextUtils.isEmpty(user.getGender()) ? "未设置" : user.getGender());
        tvRegisterTime.setText(formatRegisterTime(user.getRegisterTime()));

        builder.setView(dialogView);
        builder.setPositiveButton("关闭", null);
        builder.show();
    }

    private void showDeleteUserDialog(User user) {
        new AlertDialog.Builder(getContext())
                .setTitle("删除用户")
                .setMessage("确定要删除用户 " + user.getUserName() + " (" + user.getUserId() + ") 吗？\n\n此操作不可恢复！")
                .setPositiveButton("删除", (dialog, which) -> {
                    viewModel.deleteUser(user.getUserId());
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private String formatRegisterTime(String registerTime) {
        if (TextUtils.isEmpty(registerTime)) {
            return "未知时间";
        }
        try {
            return registerTime.replace("T", " ");
        } catch (Exception e) {
            return registerTime;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("AdminSettingFragment", "Fragment视图销毁");
        binding = null;
    }
}