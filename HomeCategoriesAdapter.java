package com.example.oderfoodapp.recyclerViewAdapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.R;
import com.example.oderfoodapp.object.Category;

import java.util.List;

public class HomeCategoriesAdapter extends RecyclerView.Adapter<HomeCategoriesAdapter.CategoriesViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(String categoryID);
    }

    private List<Category> categoryList;
    private final OnCategoryClickListener onCategoryClickListener;
    private int selectedPosition = -1;

    // Constructor cho Adapter
    public HomeCategoriesAdapter(OnCategoryClickListener listener) {
        this.onCategoryClickListener = listener;
    }



    public void setCategoryData(List<Category> list) {
        this.categoryList = list;
        notifyDataSetChanged();
    }



    // Tạo ViewHolder cho item
    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trang_chu_categories_item, parent, false);
        return new CategoriesViewHolder(view);
    }

    // Gán dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
        Category category = categoryList.get(position);
        if(category == null) return;

        holder.titleCategories.setText(category.getName());

        Uri imageUri = Uri.parse(category.getImage());
        holder.imgCategories.setImageURI(imageUri);

        // Sử dụng Glide để tải ảnh
        /*Glide.with(holder.itemView.getContext())
                .load(Uri.parse(category.getImage()))
                .placeholder(R.drawable.placeholder_image) // Thêm ảnh placeholder nếu cần
                .into(holder.imgCategories); */

        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.cate_background3); // Màu vàng khi chọn
        } else {
            holder.itemView.setBackgroundResource(R.drawable.cate_background4); // Màu nền mặc định
        }
        /*switch (position) {
            case 0: {
                holder.layoutCategories.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.cate_background1));
                break;
            }
            case 1: {
                holder.layoutCategories.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.cate_background2));
                break;
            }
            case 2: {
                holder.layoutCategories.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.cate_background3));
                break;
            }
            case 3: {
                holder.layoutCategories.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.cate_background4));
                break;
            }
            case 4: {
                holder.layoutCategories.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.cate_background5));
                break;
            }
            default: {
                holder.layoutCategories.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.cate_background1));
                break;
            }
        }
*/
        holder.itemView.setOnClickListener(view -> {
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition == RecyclerView.NO_POSITION) return; // Kiểm tra xem vị trí có hợp lệ không

            if (onCategoryClickListener != null) {
                int prevSelectedPosition = selectedPosition;
                selectedPosition = clickedPosition;
                onCategoryClickListener.onCategoryClick(category.getCategoryID());

                // Cập nhật lại màu cho item đã chọn và item cũ
                notifyItemChanged(prevSelectedPosition); // Cập nhật item trước đó
                notifyItemChanged(selectedPosition); // Cập nhật item hiện tại
            }
        });
    }

    // Trả về tổng số item trong danh sách
    @Override
    public int getItemCount() {
        return (categoryList != null) ? categoryList.size() : 0;
    }

    // Lớp ViewHolder cho các item trong RecyclerView
    public static class CategoriesViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCategories;
        TextView titleCategories;
        LinearLayout layoutCategories;

        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategories = itemView.findViewById(R.id.imgCategories);
            titleCategories = itemView.findViewById(R.id.titleCategories);
            layoutCategories = itemView.findViewById(R.id.layoutCategories);
        }
    }

}
