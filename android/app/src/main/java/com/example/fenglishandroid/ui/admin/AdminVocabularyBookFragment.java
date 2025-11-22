package com.example.fenglishandroid.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.VocabularyBookSimpleResp;
import com.example.fenglishandroid.ui.admin.adapter.VocabularyBookAdapter;
import com.example.fenglishandroid.viewModel.VocabularyBookViewModel;

import java.util.List;

public class AdminVocabularyBookFragment extends Fragment {
    private VocabularyBookViewModel viewModel;
    private RecyclerView recyclerView;
    private VocabularyBookAdapter adapter;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_admin_wordbook, container, false);

        // 初始化控件
        recyclerView = view.findViewById(R.id.recycler_view);
        searchView = view.findViewById(R.id.search_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VocabularyBookAdapter();
        recyclerView.setAdapter(adapter);

        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(VocabularyBookViewModel.class);

        // 观察数据变化
        viewModel.getBookListLiveData().observe(getViewLifecycleOwner(), new Observer<List<VocabularyBookSimpleResp>>() {
            @Override
            public void onChanged(List<VocabularyBookSimpleResp> books) {
                adapter.setData(books);
            }
        });

        // 加载数据
        viewModel.loadBookList(1, 10);

        // 搜索功能
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 执行搜索
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }
}