package com.example.oderfoodapp.recyclerViewAdapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.R;
import com.example.oderfoodapp.TrangChung;
import com.example.oderfoodapp.database.AppDatabase;
import com.example.oderfoodapp.fragment.CartFrag;
import com.example.oderfoodapp.fragment.TrangChu;
import com.example.oderfoodapp.object.Cart;
import com.example.oderfoodapp.object.Food;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomePopularFoodAdapter extends RecyclerView.Adapter<HomePopularFoodAdapter.PopularFoodViewHolder> {

    public interface OnFoodClickListener {
        void onFoodClick(String food);
    }

    Context context;
    private List<Food> popularFoodList;
    private final OnFoodClickListener onFoodClickListener;
    AppDatabase appDatabase;
    private TrangChung activity;
    String username;

    public HomePopularFoodAdapter(OnFoodClickListener listener, AppDatabase appDatabase, TrangChung activity, Context context, String username) {
        this.onFoodClickListener = listener;
        this.context = context;
        this.appDatabase = appDatabase;
        this.activity = activity;
        this.username = username;
    }
    public void setFoodData(List<Food> list) {
        this.popularFoodList = list;
        notifyDataSetChanged();
    }

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @NonNull
    @Override
    public PopularFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trang_chu_popular_food_item, parent, false);
        return new PopularFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularFoodViewHolder holder, int position) {
        Food popularFood = popularFoodList.get(position);

        // Gán dữ liệu cho từng thành phần trong item
        holder.foodTitle.setText(popularFood.getName());
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.foodPrice.setText(format.format(popularFood.getPrice()));

        // Gán ảnh từ tên resource (nếu có)
        Uri imageUri = Uri.parse(popularFood.getImage());
        holder.foodImg.setImageURI(imageUri);

        // Xử lý sự kiện khi nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            if (onFoodClickListener != null) {
                onFoodClickListener.onFoodClick(popularFood.getFoodID());
            }
        });
        holder.addBtn.setOnClickListener(v -> {
            executorService.execute(() -> {
                try {
                    // Kiểm tra xem món đã có trong giỏ hay chưa
                    Cart existingCart = appDatabase.cartDAO().getCartByUserAndFood(username, popularFood.getFoodID());
                    if (existingCart != null) {
                        // Nếu đã tồn tại, tăng số lượng
                        int updatedQuantity = existingCart.getFoodQuantity() + 1;
                        Food food = appDatabase.foodDAO().getFoodByID(existingCart.getFoodID());
                        existingCart.setFoodQuantity(updatedQuantity);
                        float newTotalPrice = food.getPrice() * updatedQuantity;
                        existingCart.setCartPrice(newTotalPrice);
                        appDatabase.cartDAO().update(existingCart);
                        ((FragmentActivity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Tăng số lượng món trong giỏ hàng!", Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        // Nếu chưa tồn tại, thêm mới
                        float foodPrice = appDatabase.foodDAO().getFoodByID(popularFood.getFoodID()).getPrice();
                        Cart newCart = new Cart(0, username, popularFood.getFoodID(), 1, foodPrice);
                        appDatabase.cartDAO().insert(newCart);
                        ((FragmentActivity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Thêm vào giỏ thành công!", Toast.LENGTH_SHORT).show()
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ((FragmentActivity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Lỗi khi thêm vào giỏ!", Toast.LENGTH_SHORT).show()
                    );
                }
                // Điều hướng sang CartFrag
                ((FragmentActivity) context).runOnUiThread(() -> {
                    CartFrag cartFrag = new CartFrag();
                    activity.replaceFrag(cartFrag, "Giỏ hàng");
                });
            });
        });
    }

    @Override
    public int getItemCount() {
        return (popularFoodList != null) ? popularFoodList.size() : 0;
    }

    public static class PopularFoodViewHolder extends RecyclerView.ViewHolder {

        TextView foodTitle, foodPrice;
        ImageView foodImg;
        Button addBtn;
        LinearLayout layoutPopularFood;

        public PopularFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodTitle = itemView.findViewById(R.id.foodTitle);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodImg = itemView.findViewById(R.id.foodImg);
            addBtn = itemView.findViewById(R.id.addBtn);
            layoutPopularFood = itemView.findViewById(R.id.layoutPopularFood);
        }
    }
}

