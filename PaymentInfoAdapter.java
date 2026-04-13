package com.example.oderfoodapp.recyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.R;

import java.util.List;

public class PaymentInfoAdapter extends RecyclerView.Adapter<PaymentInfoAdapter.ViewHolder> {

    private List<String> itemList; // Danh sách dữ liệu hiển thị
    private Context context;

    public PaymentInfoAdapter(Context context, List<String> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    // Tạo một ViewHolder đại diện cho từng phần tử trong RecyclerView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_info_layout, parent, false);
        return new ViewHolder(view);
    }

    // Gán dữ liệu cho từng phần tử
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = itemList.get(position);

        // Thiết lập dữ liệu cho các TextView và EditText
        holder.tvHint.setText(item); // Gán tiêu đề cho từng phần tử, ví dụ: "Phương thức thanh toán", "Địa chỉ"...
        holder.etPaymentInfoInput.setText(""); // Đặt giá trị trống cho mỗi EditText (có thể thay đổi theo nhu cầu)
        holder.etPaymentInfoInput.setHint(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder đại diện cho một item trong RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHint;
        EditText etPaymentInfoInput;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHint = itemView.findViewById(R.id.tvHint);
            etPaymentInfoInput = itemView.findViewById(R.id.etPaymentInfoInput);
        }
    }
}

