package com.example.fenglishandroid.adapter;

import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.CollectBookDTO;

import java.text.SimpleDateFormat;
import java.util.*;

public class CollectBookAdapter extends RecyclerView.Adapter<CollectBookAdapter.Holder> {

    private final List<CollectBookDTO> data = new ArrayList<>();
    private final Callback cb;
    /* ✅ 旧 API 21 兼容的格式化器 */
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    /* ✅ 新增：仅用于分组标题的“日”格式化 */
    private final SimpleDateFormat fmtDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public interface Callback {
        void onUnCollect(CollectBookDTO b);
    }

    public CollectBookAdapter(Callback cb) { this.cb = cb; }

    public void setData(List<CollectBookDTO> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup p, int viewType) {
        View v = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_collect_book, p, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        CollectBookDTO b = data.get(position);
        h.tvName.setText(b.getBookName());


        // 日期组标题可见性
        if (b.isShowDateHeader()) {
            h.tvTimeHeader.setVisibility(View.VISIBLE);
            h.tvTimeHeader.setText(fmtDay.format(b.getCollectTime()));
        } else {
            h.tvTimeHeader.setVisibility(View.GONE);
        }

        h.btnCancel.setOnClickListener(v -> cb.onUnCollect(b));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvTimeHeader;
        Button btnCancel;

        Holder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.item_book_name);

            tvTimeHeader = itemView.findViewById(R.id.item_book_time_header);
            btnCancel = itemView.findViewById(R.id.item_cancel);
        }
    }
}