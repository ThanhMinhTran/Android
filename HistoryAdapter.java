package com.example.oderfoodapp.recyclerViewAdapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide; // Thư viện Glide để tải hình ảnh
import com.example.oderfoodapp.R;
import com.example.oderfoodapp.TrangChung;
import com.example.oderfoodapp.fragment.HistoryChiTietFrag;
import com.example.oderfoodapp.object.History;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<History> historyList;
    TrangChung mainActivity;

    public HistoryAdapter(Context context) {
        this.context = context;
        mainActivity = (TrangChung) context;
    }

    public void setHistoryData(List<History> historyList) {
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item,parent,false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = historyList.get(position);
        // Xử lý tiền tệ
        Locale locale = new Locale("vi","VN");
        Currency currency = Currency.getInstance(locale);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        numberFormat.setCurrency(currency);
        String strTongTien = numberFormat.format(history.getTongtien());
        strTongTien = strTongTien.substring(0,strTongTien.length()-2);
        strTongTien += " VND";

        // Xử lý ngày giờ
        holder.transactionDate.setText("Ngày đặt hàng: "+history.getTransactionDate());
        holder.totalAmount.setText("Tổng tiền hàng: "+strTongTien);
        holder.address.setText("Địa chỉ: "+history.getAddress());
        holder.linearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                HistoryChiTietFrag oder = new HistoryChiTietFrag();
                Bundle bundle = new Bundle();
                bundle.putInt("transactionID",history.getTransactionID());
                oder.setArguments(bundle);
                mainActivity.replaceFrag(oder,"Chi tiết đơn hàng");
            }
        });

    }


    @Override
    public int getItemCount() {
        return historyList == null ? 0 : historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;
        TextView transactionDate,totalAmount,address;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.itemHistory);
            transactionDate = itemView.findViewById(R.id.transactionDate);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            address = itemView.findViewById(R.id.address);
        }
    }
}
