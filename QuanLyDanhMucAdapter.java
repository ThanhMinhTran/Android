package com.example.oderfoodapp.recyclerViewAdapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.QuanLyDanhMuc;
import com.example.oderfoodapp.R;
import com.example.oderfoodapp.object.Category;
import com.example.oderfoodapp.object.Food;

import java.util.List;

public class QuanLyDanhMucAdapter extends RecyclerView.Adapter<QuanLyDanhMucAdapter.QuanLyDanhMucViewHolder> {

    // interface
    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    private List<Category> mListCategory;
    private final QuanLyDanhMucAdapter.OnItemClickListener onItemClickListener;

    public QuanLyDanhMucAdapter(QuanLyDanhMucAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public void setData(List<Category> list) {
        this.mListCategory = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuanLyDanhMucViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quanlydanhmuc_recyclerview_item, parent, false);
        return new QuanLyDanhMucAdapter.QuanLyDanhMucViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuanLyDanhMucViewHolder holder, int position) {
        Category category = mListCategory.get(position);
        if (category == null) return;

        holder.txtCategoryId.setText(category.getCategoryID());
        holder.txtCategoryName.setText(category.getName());

        Uri imageUri = Uri.parse(category.getImage());
        holder.imgCategory.setImageURI(imageUri);

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(category));
    }

    @Override
    public int getItemCount() {
        return (mListCategory != null) ? mListCategory.size() : 0;
    }

    public static class QuanLyDanhMucViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtCategoryId, txtCategoryName;
        private final ImageView imgCategory;

        public QuanLyDanhMucViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategoryId = itemView.findViewById(R.id.txtCategoryID);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
            imgCategory = itemView.findViewById(R.id.imgCategory);
        }
    }
}
