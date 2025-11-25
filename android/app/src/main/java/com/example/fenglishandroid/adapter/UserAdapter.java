package com.example.fenglishandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fenglishandroid.databinding.ItemUserBinding;
import com.example.fenglishandroid.model.request.User;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserActionListener listener;

    public UserAdapter(List<User> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    public void updateData(List<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserBinding binding;

        UserViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(User user) {
            binding.setUser(user);
            binding.executePendingBindings();

            // 设置查看详情点击事件
            binding.btnView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewUserDetail(user);
                }
            });

            // 设置删除点击事件
            binding.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteUser(user);
                }
            });

            // 整个item点击也可以查看详情
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewUserDetail(user);
                }
            });
        }
    }

    public interface OnUserActionListener {
        void onViewUserDetail(User user);
        void onDeleteUser(User user);
    }
}