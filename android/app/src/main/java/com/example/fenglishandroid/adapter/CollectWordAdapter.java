package com.example.fenglishandroid.adapter;

import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.CollectWordDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CollectWordAdapter extends RecyclerView.Adapter<CollectWordAdapter.Holder> {
    private final List<CollectWordDTO> data = new ArrayList<>();
    private final Callback cb;

    public interface Callback {
        void onUnCollect(CollectWordDTO w);
    }

    public CollectWordAdapter(Callback cb) { this.cb = cb; }

    public void setData(List<CollectWordDTO> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup p, int viewType) {
        View v = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_collect_word, p, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        CollectWordDTO w = data.get(position);
        h.tvWord.setText(w.getWordName());
        h.tvMean.setText(w.getWordExplain());

        // 根据 isShowDateHeader 动态显示日期标题
        if (w.isShowDateHeader()) {
            h.tvTimeHeader.setVisibility(View.VISIBLE);
            h.tvTimeHeader.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(w.getCollectTime()));
        } else {
            h.tvTimeHeader.setVisibility(View.GONE);
        }

        h.btnCancel.setOnClickListener(v -> cb.onUnCollect(w));
    }



    @Override
    public int getItemCount() { return data.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tvWord, tvMean, tvTimeHeader;
        Button btnCancel;

        Holder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.item_word);
            tvMean = itemView.findViewById(R.id.item_mean);
            tvTimeHeader = itemView.findViewById(R.id.item_word_time_header); // 添加日期组标题
            btnCancel = itemView.findViewById(R.id.item_cancel);
        }
    }
}