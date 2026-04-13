package com.example.oderfoodapp.recyclerViewAdapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.R;
import com.example.oderfoodapp.object.User;

import java.util.List;

public class QuanLyUserAdapter extends RecyclerView.Adapter<QuanLyUserAdapter.QuanLyUserViewHolder>{
    // interface
    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    private List<User> mListUser;
    private final OnItemClickListener onItemClickListener;

    public QuanLyUserAdapter(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setData(List<User> list) {
        this.mListUser = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuanLyUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quanlyuser_recyclerview_item, parent, false);
        return new QuanLyUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuanLyUserViewHolder holder, int position) {
        User user = mListUser.get(position);
        if (user == null) return;

        holder.txtUsername.setText(user.getUsername());
        holder.txtEmail.setText(user.getEmail());
        holder.txtPhone.setText(user.getPhone());

        // Kiểm tra avatar trước khi tạo URI
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Uri imageUri = Uri.parse(user.getAvatar());
            holder.imgAvatar.setImageURI(imageUri);
        } else {
            // Đặt ảnh mặc định nếu avatar bị null hoặc trống
            holder.imgAvatar.setImageResource(R.drawable.profile); // Đảm bảo có ảnh mặc định trong resources
        }

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(user));
    }

    @Override
    public int getItemCount() {
        return (mListUser != null) ? mListUser.size() : 0;
    }

    public static class QuanLyUserViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtUsername, txtEmail, txtPhone;
        private final ImageView imgAvatar;

        public QuanLyUserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}
