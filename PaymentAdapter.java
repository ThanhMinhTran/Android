package com.example.oderfoodapp.recyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.R;
import com.example.oderfoodapp.database.AppDatabase;
import com.example.oderfoodapp.object.Cart;
import com.example.oderfoodapp.object.Food;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentHolder> {
    List<Cart> listCartItem = new ArrayList<>();
    Context context;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public PaymentAdapter(Context context, List<Cart> list) {
        listCartItem = list;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentHolder holder, int position) {
        Cart cart = listCartItem.get(position);
        final Food[] food = {new Food()};
        Future<Food> future = (Future<Food>) executorService.submit(new Runnable() {
            @Override
            public void run() {
                food[0] = AppDatabase.getInstance(context).foodDAO().getFoodByID(cart.getFoodID());

            }
        });
        try {
            future.get(); // nó sẽ chặn luống chạy của ứng dụng cho đến khi thằng code trong future hoàn thành
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.foodname.setText(food[0].getName());
        holder.foodquantity.setText("Số lượng " + cart.getFoodQuantity());
    }

    @Override
    public int getItemCount() {
        return listCartItem.size();
    }

    @NonNull
    @Override
    public PaymentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thanh_toan_item, parent, false);
        return new PaymentHolder(view);
    }

    public static class PaymentHolder extends RecyclerView.ViewHolder {
        TextView foodname;
        TextView foodquantity;

        public PaymentHolder(@NonNull View itemView) {
            super(itemView);
            foodname = itemView.findViewById(R.id.foodname);
            foodquantity = itemView.findViewById(R.id.foodquantity);
        }

    }
}
