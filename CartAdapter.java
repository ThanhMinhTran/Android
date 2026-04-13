package com.example.oderfoodapp.recyclerViewAdapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.R;
import com.example.oderfoodapp.TrangChung;
import com.example.oderfoodapp.database.AppDatabase;
import com.example.oderfoodapp.fragment.CartFrag;
import com.example.oderfoodapp.fragment.ThongTinMonFrag;
import com.example.oderfoodapp.object.Cart;
import com.example.oderfoodapp.object.Food;
import com.example.oderfoodapp.object.Food1;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnCartClickListener {
        void onCartClick(int cartID);
    }

    private List<Cart> cartItemList;
    private List<Food> foodList;
    private final OnCartClickListener onCartClickListener;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private AppDatabase appDatabase;
    private Context context;
    private TrangChung activity;
    private CartFrag cartFrag;

    public CartAdapter(Context context, TrangChung activity, OnCartClickListener listener, CartFrag cartFrag) {
        this.context = context;
        this.activity = activity; // Lưu lại tham chiếu của TrangChung
        this.onCartClickListener = listener;
        this.cartItemList = new ArrayList<>(); // Khởi tạo danh sách trống
        this.foodList = new ArrayList<>();
        this.appDatabase = AppDatabase.getInstance(context);
        this.cartFrag = cartFrag;
    }

    public void setCartData(List<Cart> cartList, List<Food> foodList) {
        this.cartItemList = cartList != null ? cartList : new ArrayList<>();
        this.foodList = foodList != null ? foodList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cartItem = cartItemList.get(position);
        Food food = foodList.get(position); // Lấy Food tương ứng
        holder.txtFoodName.setText(food.getName());
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.txtPrice.setText(format.format(cartItem.getCartPrice()) + " VND");
        holder.txtQuantity.setText(String.valueOf(cartItem.getFoodQuantity()));
        Uri imageUri = Uri.parse(food.getImage());
        holder.imgFood.setImageURI(imageUri);



        // Xử lý tăng/giảm số lượng
        holder.btnIncrease.setOnClickListener(v -> updateQuantity(cartItem, holder, true));
        holder.btnDecrease.setOnClickListener(v -> updateQuantity(cartItem, holder, false));

        holder.btnDelete.setOnClickListener(v -> {
            executorService.execute(() -> {
                try {
                    // Xóa dữ liệu trong cơ sở dữ liệu
                    appDatabase.cartDAO().delete(cartItem);

                    // Loại bỏ món khỏi danh sách trong bộ nhớ
                    cartItemList.remove(cartItem);

                    // Cập nhật giao diện trên main thread
                    ((FragmentActivity) context).runOnUiThread(this::notifyDataSetChanged);
                    if (cartFrag != null) {
                        cartFrag.updateTotalAmount();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });


        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("foodID", cartItem.getFoodID()); // Truyền foodID
            ThongTinMonFrag thongTinMonFrag = new ThongTinMonFrag();
            thongTinMonFrag.setArguments(bundle);

            activity.replaceFrag(thongTinMonFrag, "Thông tin món");
        });


    }

    @Override
    public int getItemCount() {
        if (cartItemList == null || foodList == null) {
            return 0; // Trả về 0 nếu danh sách là null
        }
        return cartItemList.size();
    }


    // Khai báo lớp ViewHolder bên trong Adapter
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView txtFoodName, txtQuantity, txtPrice;
        ImageView imgFood;
        ImageButton btnDecrease, btnIncrease;
        Button btnDelete;
        private TextView txtTotalAmount, txtTongTien, txtPhiVanChuyen, txtGiamGia;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFoodName = itemView.findViewById(R.id.tvProductName);
            txtQuantity = itemView.findViewById(R.id.tvQuantity);
            txtPrice = itemView.findViewById(R.id.tvProductPrice);
            imgFood = itemView.findViewById(R.id.imgProduct);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDelete = itemView.findViewById(R.id.btnDeleteCart);
            txtTotalAmount = itemView.findViewById(R.id.txtTotalAmount);
            txtTongTien = itemView.findViewById(R.id.txtTongTienValue);
        }
    }
    private void updateQuantity(Cart cartItem, CartViewHolder holder, boolean increase) {
        executorService.execute(() -> {
            try {
                int currentQuantity = cartItem.getFoodQuantity();
                int newQuantity = currentQuantity + (increase ? 1 : -1);
                Food food = appDatabase.foodDAO().getFoodByID(cartItem.getFoodID());

                if (newQuantity <= 0) return;

                if (newQuantity <= food.getQuantity()) {
                    cartItem.setFoodQuantity(newQuantity);
                    float newTotalPrice = food.getPrice() * newQuantity;
                    cartItem.setCartPrice(newTotalPrice);
                    appDatabase.cartDAO().update(cartItem);

                    ((FragmentActivity) context).runOnUiThread(() -> {
                        holder.txtQuantity.setText(String.valueOf(newQuantity));
                        holder.txtPrice.setText(String.format("%,.0f VND", cartItem.getCartPrice()));
                        if (cartFrag != null) {
                            cartFrag.updateTotalAmount();
                        }
                    });
                } else {
                    // Thông báo nếu số lượng vượt quá tồn kho
                    ((FragmentActivity) context).runOnUiThread(() -> {
                        android.widget.Toast.makeText(context, "Số lượng không đủ", android.widget.Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}


