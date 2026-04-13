package com.example.oderfoodapp.recyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.R;
import com.example.oderfoodapp.object.FoodRatingDetails;

import java.util.List;

public class FoodRatingDetailsAdapter extends RecyclerView.Adapter<FoodRatingDetailsAdapter.FoodDetailsViewHolder> {

    private Context context;
    private List<FoodRatingDetails> foodRatingDetailsList;

    // Constructor cho Adapter
    public FoodRatingDetailsAdapter(Context context, List<FoodRatingDetails> foodRatingDetailsList) {
        this.context = context;
        this.foodRatingDetailsList = foodRatingDetailsList;
    }

    @NonNull
    @Override
    public FoodDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.food_rating_layout, parent, false);
        return new FoodDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodDetailsViewHolder holder, int position) {
        FoodRatingDetails foodRatingDetails = foodRatingDetailsList.get(position);

        // Load hình ảnh đại diện
        int imageResource = holder.itemView.getContext().getResources().getIdentifier(foodRatingDetails.getImgAvatar(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imgAvatar.setImageResource(imageResource);

        // Set tên khách hàng
        holder.customerName.setText(foodRatingDetails.getCustomerName());

        // Set đánh giá sao
        holder.ratingBarReview.setRating(foodRatingDetails.getRatingBarReview());

        // Set mô tả đánh giá
        holder.customerReview.setText(foodRatingDetails.getCustomerReview());
    }

    @Override
    public int getItemCount() {
        return foodRatingDetailsList.size();
    }

    // ViewHolder để giữ các view bên trong từng item của RecyclerView
    public static class FoodDetailsViewHolder extends RecyclerView.ViewHolder {

        ImageView imgAvatar;
        TextView customerName, customerReview;
        RatingBar ratingBarReview;

        public FoodDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            customerName = itemView.findViewById(R.id.customerName);
            ratingBarReview = itemView.findViewById(R.id.ratingBarReview);
            customerReview = itemView.findViewById(R.id.customerReview);
        }
    }
}

