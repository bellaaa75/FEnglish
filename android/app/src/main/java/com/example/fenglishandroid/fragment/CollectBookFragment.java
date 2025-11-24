package com.example.fenglishandroid.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fenglishandroid.R;
import com.example.fenglishandroid.adapter.CollectBookAdapter;
import com.example.fenglishandroid.databinding.FragmentCollectBookBinding; // ✅ 改用已有 Binding
import com.example.fenglishandroid.model.CollectBookDTO;
import com.example.fenglishandroid.viewModel.CollectViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;


public class CollectBookFragment extends Fragment implements CollectBookAdapter.Callback {

    // ✅ 复用 FragmentCollectBookBinding（方案 B）
    private FragmentCollectBookBinding binding;
    private CollectViewModel sharedCollectViewModel;
    private CollectBookAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCollectBookBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedCollectViewModel = new ViewModelProvider(requireActivity()).get(CollectViewModel.class);

        adapter = new CollectBookAdapter(this);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycler.setAdapter(adapter);

        sharedCollectViewModel.setCollectionStatusListener((bookId, isCollected) -> {
            // 刷新列表显示
            sharedCollectViewModel.loadBookCollects(0, 10);
        });

        sharedCollectViewModel.getBookCollects().observe(getViewLifecycleOwner(), books ->  {
            Log.d("CollectBookFragment", "收到数据条数：" + (books == null ? "null" : books.size()));
            if (books == null) return;

            // 1. 按 collectTime 倒序
            Collections.sort(books, (a, b) -> Long.compare(b.getCollectTime().getTime(), a.getCollectTime().getTime()));

            // 2. 打"是否显示日期"标记
            Set<String> shownDate = new HashSet<>();
            SimpleDateFormat dayFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dayFmt.setTimeZone(TimeZone.getTimeZone("UTC"));

            for (CollectBookDTO b : books) {
                String day = dayFmt.format(b.getCollectTime());
                b.setShowDateHeader(!shownDate.contains(day));
                if (b.isShowDateHeader()) shownDate.add(day);
            }

            adapter.setData(books);
        });
        sharedCollectViewModel.getBookError().observe(getViewLifecycleOwner(),
                msg -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());
        // 拉取第一页收藏书单
        sharedCollectViewModel.loadBookCollects(0, 20);



    }

    @Override
    public void onUnCollect(CollectBookDTO b) {
        sharedCollectViewModel.unCollectBook(b.getTargetId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}