package com.example.oderfoodapp.recyclerViewAdapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.R;
import com.example.oderfoodapp.object.FavoriteFood;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FavoriteFoodAdapter extends RecyclerView.Adapter<FavoriteFoodAdapter.FavoriteFoodViewHolder> {

    public interface OnFoodClickListener {
        void onFoodClick(String food);
    }

    private Context context;
    private List<FavoriteFood> favoriteFoodList;
    private final FavoriteFoodAdapter.OnFoodClickListener onFoodClickListener;

    public FavoriteFoodAdapter(Context context, OnFoodClickListener listener) {
        this.context = context;
        this.onFoodClickListener = listener;
    }

    public void setFavoriteData(List<FavoriteFood> list) {
        this.favoriteFoodList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorite_food_item, parent, false);
        return new FavoriteFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteFoodViewHolder holder, int position) {
        FavoriteFood favoriteFood = favoriteFoodList.get(position);
        holder.txtFoodName.setText(favoriteFood.getFood_name());

        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.txtFoodPrice.setText(format.format(favoriteFood.getPrice()));

        Uri imageUri = Uri.parse(favoriteFood.getImage());
        holder.imgFoodImage.setImageURI(imageUri);

        // Nếu bạn có ảnh từ URL hoặc file, bạn có thể dùng thư viện như Glide hoặc Picasso để load ảnh.
        // Glide.with(context).load(favoriteFood.getImage()).into(holder.imgFoodImage);
        holder.itemView.setOnClickListener(v -> {
            if (onFoodClickListener != null) {
                onFoodClickListener.onFoodClick(favoriteFood.getFoodID());
            }
        });
    }

    @Override
    public int getItemCount() {
        return (favoriteFoodList != null) ? favoriteFoodList.size() : 0;
    }

    public static class FavoriteFoodViewHolder extends RecyclerView.ViewHolder {

        TextView txtFoodName, txtFoodPrice;
        ImageView imgFoodImage;

        public FavoriteFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFoodName = itemView.findViewById(R.id.txtFoodName);
            txtFoodPrice = itemView.findViewById(R.id.txtFoodPrice);
            imgFoodImage = itemView.findViewById(R.id.imgFoodImage);
        }
    }
}
