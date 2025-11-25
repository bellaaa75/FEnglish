package com.example.fenglishandroid.adapter;

import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fenglishandroid.R;
import com.example.fenglishandroid.model.CollectWordDTO;
import com.example.fenglishandroid.model.WordSection;
import java.util.*;

public class WordSectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_WORD   = 1;

    private final List<Object> flatList = new ArrayList<>(); // 只存两种对象：String（日期）+ CollectWordDTO
    private final Callback cb;



    public interface Callback {
        void onUnCollect(CollectWordDTO w);
        void onWordClick(CollectWordDTO w); // 新增单词点击回调
    }

    public WordSectionAdapter(Callback cb) { this.cb = cb; }

    /** 把 WordSection 列表拍平成 [date,word,word,date,word...] */
//    public void setSections(List<WordSection> sections) {
//        flatList.clear();
//        for (WordSection sec : sections) {
//            flatList.add(sec.date);      // 先放日期
//            flatList.addAll(sec.words);  // 再放当天全部单词
//        }
//        notifyDataSetChanged();
//    }

    /** 清空并设置新数据 */
    public void setSections(List<WordSection> sections) {
        flatList.clear();
        if (sections != null) {
            for (WordSection sec : sections) {
                flatList.add(sec.date);      // 先放日期
                if (sec.words != null) {
                    flatList.addAll(sec.words);  // 再放当天全部单词
                }
            }
        }
        notifyDataSetChanged();
        Log.d("WordSectionAdapter", "数据已更新，总项数: " + flatList.size());
    }


    @Override
    public int getItemViewType(int position) {
        return flatList.get(position) instanceof String ? TYPE_HEADER : TYPE_WORD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View v = inf.inflate(R.layout.item_word_section_header, parent, false);
            return new HeaderHolder(v);
        } else {
            View v = inf.inflate(R.layout.item_word_real, parent, false);
            return new WordHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            String date = (String) flatList.get(position);
            ((HeaderHolder) holder).tvDate.setText(date);
        } else {
            CollectWordDTO w = (CollectWordDTO) flatList.get(position);
            WordHolder h = (WordHolder) holder;
            h.tvWord.setText(w.getWordName());
            h.tvMean.setText(w.getWordExplain());
            h.btnCancel.setOnClickListener(v -> cb.onUnCollect(w));


            // 添加单词名称点击监听
            h.tvWord.setOnClickListener(v -> {
                if (cb != null) {
                    cb.onWordClick(w); // 需要新增这个回调方法
                }
            });
        }
    }

    @Override
    public int getItemCount() { return flatList.size(); }

    /* --------------- ViewHolder --------------- */
    static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        HeaderHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_section_date);
        }
    }

    static class WordHolder extends RecyclerView.ViewHolder {
        TextView tvWord, tvMean;
        Button btnCancel;
        View wordItemView; // 添加单词项整体视图引用

        WordHolder(View itemView) {
            super(itemView);
            tvWord    = itemView.findViewById(R.id.item_word);
            tvMean    = itemView.findViewById(R.id.item_mean);
            btnCancel = itemView.findViewById(R.id.item_cancel);
            wordItemView = itemView.findViewById(R.id.word_item_layout); // 假设有这个布局，如果没有需要创建
        }
    }

}