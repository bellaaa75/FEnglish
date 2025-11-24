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
import com.example.fenglishandroid.adapter.WordSectionAdapter;   // ① 换新 Adapter
import com.example.fenglishandroid.databinding.FragmentCollectWordBinding;
import com.example.fenglishandroid.model.CollectWordDTO;
import com.example.fenglishandroid.model.WordSection;           // ② 新数据模型
import com.example.fenglishandroid.viewModel.CollectViewModel;

import java.util.List;

public class CollectWordFragment extends Fragment implements WordSectionAdapter.Callback {

    private FragmentCollectWordBinding binding;
    private CollectViewModel vm;
    private WordSectionAdapter adapter;                          // ③ 新 Adapter

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCollectWordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm = new ViewModelProvider(requireActivity()).get(CollectViewModel.class); // 改为使用 Activity 范围的 ViewModel

        vm.setCollectionStatusListener((wordId, isCollected) -> {
            // 当有单词收藏状态变化时，立即刷新列表
            Log.d("CollectWordFragment", "收藏状态变化，刷新列表");
            vm.loadWordCollects(0, 20);
        });
        // ④ 初始化多类型 Adapter
        adapter = new WordSectionAdapter(this);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycler.setAdapter(adapter);

        // ⑤ 观察 WordSection 列表（ViewModel 已换成 wordSectionLive）
        vm.getWordSections().observe(getViewLifecycleOwner(), sections -> {
            Log.d("CollectWordFragment", "收到段数：" + (sections == null ? "null" : sections.size()));
            if (sections != null) adapter.setSections(sections);
        });

        vm.getWordError().observe(getViewLifecycleOwner(),
                msg -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());

        // ✅ ✅ ✅ 添加这一行：监听收藏操作成功事件
        vm.getCollectResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Log.d("CollectWordFragment", "收藏操作成功，刷新列表");
                vm.loadWordCollects(0, 20);
            }
        });

        // ⑥ 拉取第一页
        vm.loadWordCollects(0, 20);
    }

    /* ⑦ 接口回调：取消收藏 */
    @Override
    public void onUnCollect(CollectWordDTO w) {
        vm.unCollectWord(w.getTargetId());   // 交给 ViewModel 处理网络 & 重新拉列表
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}