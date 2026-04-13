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
import com.example.oderfoodapp.object.Food;

import java.util.List;

public class QuanLyMonAdapter extends RecyclerView.Adapter<QuanLyMonAdapter.QuanLyMonViewHolder> {

    // interface
    public interface OnItemClickListener {
        void onItemClick(Food food);
    }

    private List<Food> mListFood;
    private final OnItemClickListener onItemClickListener;

    public QuanLyMonAdapter(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setData(List<Food> list) {
        this.mListFood = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuanLyMonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quanlymon_recyclerview_item, parent, false);
        return new QuanLyMonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuanLyMonViewHolder holder, int position) {
        Food food = mListFood.get(position);
        if (food == null) return;

        holder.txtFoodId.setText(food.getFoodID());
        holder.txtFoodName.setText(food.getName());
        holder.txtQuantity.setText(String.valueOf(food.getQuantity()));
        holder.txtPrice.setText(String.valueOf(food.getPrice()));

        Uri imageUri = Uri.parse(food.getImage());
        holder.imgFood.setImageURI(imageUri);

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(food));


    }

    @Override
    public int getItemCount() {
        return (mListFood != null) ? mListFood.size() : 0;
    }

    public static class QuanLyMonViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtFoodId, txtFoodName, txtQuantity, txtPrice;
        private final ImageView imgFood;

        public QuanLyMonViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFoodId = itemView.findViewById(R.id.txtFoodID);
            txtFoodName = itemView.findViewById(R.id.txtFoodName);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            imgFood = itemView.findViewById(R.id.imgFood);
        }

    }
}
