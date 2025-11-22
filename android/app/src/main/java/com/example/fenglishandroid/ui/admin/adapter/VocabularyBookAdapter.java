package com.example.fenglishandroid.ui.admin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.VocabularyBookSimpleResp;
import java.util.List;

public class VocabularyBookAdapter extends RecyclerView.Adapter<VocabularyBookAdapter.ViewHolder> {

    // 存储单词书数据的列表
    private List<VocabularyBookSimpleResp> bookList;
    // 点击事件回调（处理编辑、删除等操作）
    private OnItemActionListener listener;

    // 构造方法：初始化数据
    public VocabularyBookAdapter() {
    }

    // 设置数据并刷新列表
    public void setData(List<VocabularyBookSimpleResp> data) {
        this.bookList = data;
        notifyDataSetChanged(); // 通知列表刷新
    }

    // 创建ViewHolder（加载列表项布局）
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载列表项布局（需自定义，如 item_vocabulary_book.xml）
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vocabulary_book, parent, false);
        return new ViewHolder(view);
    }

    // 绑定数据到ViewHolder（将数据显示到UI控件）
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VocabularyBookSimpleResp book = bookList.get(position);
        if (book == null) return;

        // 绑定字段到控件（示例）
        holder.tvBookId.setText(book.getBookId());
        holder.tvBookName.setText(book.getBookName());
        holder.tvPublishTime.setText(book.getPublishTime()); // 需格式化日期
        holder.tvWordCount.setText(String.valueOf(book.getWordCount()));

        // 编辑按钮点击事件
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(book);
            }
        });

        // 删除按钮点击事件
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(book.getBookId());
            }
        });
    }

    // 数据数量
    @Override
    public int getItemCount() {
        return bookList == null ? 0 : bookList.size();
    }

    // ViewHolder：缓存列表项的控件
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookId;
        TextView tvBookName;
        TextView tvPublishTime;
        TextView tvWordCount;
        View btnEdit;
        View btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 初始化控件（与 item_vocabulary_book.xml 中的id对应）
            tvBookId = itemView.findViewById(R.id.tv_book_id);
            tvBookName = itemView.findViewById(R.id.tv_book_name);
            tvPublishTime = itemView.findViewById(R.id.tv_publish_time);
            tvWordCount = itemView.findViewById(R.id.tv_word_count);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    // 定义交互事件接口（供Fragment实现）
    public interface OnItemActionListener {
        void onEditClick(VocabularyBookSimpleResp book); // 编辑单词书
        void onDeleteClick(String bookId); // 删除单词书
    }

    // 设置事件监听器
    public void setOnItemActionListener(OnItemActionListener listener) {
        this.listener = listener;
    }
}