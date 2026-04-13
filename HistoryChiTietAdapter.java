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
import com.example.oderfoodapp.object.Food;
import com.example.oderfoodapp.object.HistoryDetail;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HistoryChiTietAdapter extends RecyclerView.Adapter<HistoryChiTietAdapter.DetailFoodHolder> {

    List<HistoryDetail> listHistoryDetail;
    List<Food> listFood;
    Context context;
    Food food;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public HistoryChiTietAdapter(Context context, List<HistoryDetail> list) {
        listHistoryDetail = list;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return listHistoryDetail.size();
    }

    @Override
    public void onBindViewHolder(@NonNull DetailFoodHolder holder, int position) {
        HistoryDetail detail = listHistoryDetail.get(position);

        Future<Food> future = (Future<Food>) executorService.submit(new Runnable() {
            @Override
            public void run() {
                food = AppDatabase.getInstance(context).foodDAO().getFoodByID(detail.getFoodID());
            }
        });
        try {
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.foodname.setText(food.getName());
        holder.quantity.setText("Số lượng " + detail.getQuantity());

    }

    @NonNull
    @Override
    public DetailFoodHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thanh_toan_item, parent, false);
        return new DetailFoodHolder(view);
    }

    public static class DetailFoodHolder extends RecyclerView.ViewHolder {
        TextView foodname, quantity;

        public DetailFoodHolder(@NonNull View itemView) {
            super(itemView);
            foodname = itemView.findViewById(R.id.foodname);
            quantity = itemView.findViewById(R.id.foodquantity);
        }
    }
}
