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

import java.util.ArrayList;
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
        // 关键：确保使用 Activity 范围的同一个 ViewModel 实例
        vm = new ViewModelProvider(requireActivity()).get(CollectViewModel.class);
        Log.d("CollectWordFragment", "📋 ViewModel实例: " + vm.toString());
        Log.d("CollectWordFragment", "📋 ViewModel hashCode: " + vm.hashCode());

        // 检查 LiveData 是否为空
        if (vm.getWordSections() == null) {
            Log.e("CollectWordFragment", "❌ wordSections LiveData 为 null");
        } else {
            Log.d("CollectWordFragment", "✅ wordSections LiveData 正常");
        }

//        vm.setCollectionStatusListener((wordId, isCollected) -> {
//            // 当有单词收藏状态变化时，立即刷新列表
//            Log.d("CollectWordFragment", "收藏状态变化，刷新列表");
//            vm.loadWordCollects(0, 20);
//        });
        // ④ 初始化多类型 Adapter
        adapter = new WordSectionAdapter(this);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycler.setAdapter(adapter);

        // 关键修复：确保使用正确的 ViewModel 实例
        // 观察单词分段数据变化 - 添加更详细的日志
        vm.getWordSections().observe(getViewLifecycleOwner(), sections -> {
            Log.d("CollectWordFragment", "🔥 收到数据变化通知，段数：" + (sections == null ? "null" : sections.size()));
            if (sections != null) {
                int totalWords = sections.stream().mapToInt(s -> s.words != null ? s.words.size() : 0).sum();
                Log.d("CollectWordFragment", "✅ 更新列表，总单词数：" + totalWords);
                adapter.setSections(sections);

                // 检查是否有数据但UI没更新
                if (totalWords > 0 && binding.recycler.getChildCount() == 0) {
                    Log.w("CollectWordFragment", "⚠️ 有数据但RecyclerView为空，强制刷新");
                    adapter.notifyDataSetChanged();
                }
            } else {
                Log.d("CollectWordFragment", "收到空数据，清空列表");
                adapter.setSections(new ArrayList<>());
            }
        });

        vm.getWordError().observe(getViewLifecycleOwner(),
                msg -> {
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    Log.e("CollectWordFragment", "错误信息：" + msg);
                });

        // 监听收藏操作结果
        vm.getCollectResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Log.d("CollectWordFragment", "🎯 收藏操作成功，手动触发刷新");
                // 添加延迟确保数据同步
                binding.recycler.postDelayed(() -> {
                    vm.loadWordCollects(0, 20);
                }, 300);
            }
        });

        // 设置收藏状态监听器 - 确保使用同一个ViewModel实例
        vm.setCollectionStatusListener((wordId, isCollected) -> {
            Log.d("CollectWordFragment", "🔄 收藏状态变化，wordId=" + wordId + ", isCollected=" + isCollected);
            // 立即刷新列表
            binding.recycler.postDelayed(() -> {
                vm.loadWordCollects(0, 20);
            }, 200);
        });

        // 初始加载
        Log.d("CollectWordFragment", "🚀 开始加载初始数据");
        vm.loadWordCollects(0, 20);
    }

    // 在这里添加 onResume 方法
    @Override
    public void onResume() {
        super.onResume();
        Log.d("CollectWordFragment", "📱 Fragment恢复显示，强制刷新数据");
        // 每次显示时都强制刷新数据
        if (vm != null) {
            vm.loadWordCollects(0, 20);
        }
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