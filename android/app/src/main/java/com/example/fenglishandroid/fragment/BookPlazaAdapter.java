package com.example.fenglishandroid.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.VocabularyBookSimpleResp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import android.graphics.Color;

public class BookPlazaAdapter extends RecyclerView.Adapter<BookPlazaAdapter.ViewHolder> {
    private List<VocabularyBookSimpleResp> mBookList;
    private LearningPlazaFragment.OnItemClickListener mListener;
    private Set<String> collectedIds; // 添加 collectedIds 成员变量

    private String formatPublishTime(String time) {
        if (time == null || time.isEmpty()) {
            return "";
        }
        try {
            // 定义输入格式（后端返回的格式，包含T）
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            // 定义目标输出格式（年月日 时分）
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            // 解析输入时间并转换为目标格式
            Date date = inputFormat.parse(time);
            return date != null ? outputFormat.format(date) : time;
        } catch (ParseException e) {
            // 解析失败时返回原始字符串（避免崩溃）
            e.printStackTrace();
            return time;
        }
    }
    // 修改构造函数，接收 collectedIds
    public BookPlazaAdapter(List<VocabularyBookSimpleResp> bookList, LearningPlazaFragment.OnItemClickListener listener, Set<String> collectedIds) {
        mBookList = bookList;
        mListener = listener;
        this.collectedIds = collectedIds; // 初始化 collectedIds
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book_plaza, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VocabularyBookSimpleResp book = mBookList.get(position);
        holder.tvBookId.setText("ID: " + book.getBookId());
        holder.tvBookName.setText(book.getBookName());
        holder.tvPublishTime.setText(formatPublishTime(book.getPublishTime()));
        holder.tvWordCount.setText(String.valueOf(book.getWordCount()));

        // 使用 collectedIds 判断收藏状态
        boolean already = collectedIds.contains(book.getBookId());
        holder.btnCollect.setEnabled(!already);
        holder.btnCollect.setText(already ? "已收藏" : "收藏");
        holder.btnCollect.setBackgroundColor(already ? Color.GRAY : Color.parseColor("#6750A3"));

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(book);
            }
        });

        holder.btnCollect.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCollectClick(book);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookId;
        TextView tvBookName;
        TextView tvPublishTime;
        TextView tvWordCount;
        Button btnCollect;

        public ViewHolder(View view) {
            super(view);
            tvBookId = view.findViewById(R.id.tv_book_id);
            tvBookName = view.findViewById(R.id.tv_book_name);
            tvPublishTime = view.findViewById(R.id.tv_publish_time);
            tvWordCount = view.findViewById(R.id.tv_word_count);
            btnCollect = view.findViewById(R.id.btn_collect);
        }
    }
}