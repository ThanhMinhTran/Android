package com.example.oderfoodapp.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.R;
import com.example.oderfoodapp.TrangChung;
import com.example.oderfoodapp.database.AppDatabase;
import com.example.oderfoodapp.object.History;
import com.example.oderfoodapp.object.HistoryDetail;
import com.example.oderfoodapp.recyclerViewAdapter.HistoryChiTietAdapter;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryChiTietFrag extends Fragment {

    private History history;
    private List<HistoryDetail> historyDetailList;

    private TextView totalAmount, transactionDate, fullname, phonenumber, address, wayToPay;
    private TextView voucherCodeText, noteText; // ✅ Thêm dòng này

    private RecyclerView listMon;
    private Button btnBack;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private TrangChung mainActivity;
    private SharedPreferences sharedPreferences;
    private String username;
    private int transactionID;

    private HistoryChiTietAdapter historyChiTietAdapter;

    public HistoryChiTietFrag() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (TrangChung) getActivity();
        sharedPreferences = getActivity().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        if (getArguments() != null) {
            transactionID = getArguments().getInt("transactionID");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listMon = view.findViewById(R.id.listMon);
        btnBack = view.findViewById(R.id.btnBack);
        totalAmount = view.findViewById(R.id.totalAmount);
        transactionDate = view.findViewById(R.id.transactionDate);
        fullname = view.findViewById(R.id.fullname);
        phonenumber = view.findViewById(R.id.phonenumber);
        address = view.findViewById(R.id.address);
        wayToPay = view.findViewById(R.id.wayToPay);

        // ✅ Gắn ID các TextView mới
        voucherCodeText = view.findViewById(R.id.voucherCode);
        noteText = view.findViewById(R.id.note);

        executorService.execute(() -> {
            history = AppDatabase.getInstance(getContext()).historyDAO().getAHistoryByID(transactionID, username);
            historyDetailList = AppDatabase.getInstance(getContext()).daoDetailHistory().getListHistory(transactionID);

            if (getActivity() == null) return;

            getActivity().runOnUiThread(() -> {
                Locale locale = new Locale("vi", "VN");
                Currency currency = Currency.getInstance(locale);
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
                numberFormat.setCurrency(currency);

                String strTongTien = numberFormat.format(history.getTongtien());
                strTongTien = strTongTien.replace("₫", " VND");

                fullname.setText(history.getFullname());
                address.setText(history.getAddress());
                phonenumber.setText(history.getPhonenumber());
                wayToPay.setText(history.getPttt());
                transactionDate.setText("Ngày đặt hàng  " + history.getTransactionDate());
                totalAmount.setText(strTongTien);

                // ✅ Set dữ liệu mới thêm
                voucherCodeText.setText("Mã giảm giá: " + (history.getVoucherCode() == null ? "Không có" : history.getVoucherCode()));
                noteText.setText("Ghi chú: " + (history.getNote() == null ? "Không có" : history.getNote()));

                historyChiTietAdapter = new HistoryChiTietAdapter(mainActivity, historyDetailList);
                listMon.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false));
                listMon.setAdapter(historyChiTietAdapter);
            });
        });

        btnBack.setOnClickListener(view1 -> {
            HistoryFrag hf = new HistoryFrag();
            mainActivity.replaceFrag(hf, "Lịch sử mua hàng");
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.history_chitiet_frag, container, false);
    }
}
