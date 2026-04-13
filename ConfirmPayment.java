package com.example.oderfoodapp.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.R;
import com.example.oderfoodapp.TrangChung;
import com.example.oderfoodapp.database.AppDatabase;
import com.example.oderfoodapp.object.Cart;
import com.example.oderfoodapp.object.History;
import com.example.oderfoodapp.object.HistoryDetail;
import com.example.oderfoodapp.object.User;
import com.example.oderfoodapp.object.Voucher;
import com.example.oderfoodapp.object.VoucherDetail;
import com.example.oderfoodapp.recyclerViewAdapter.PaymentAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConfirmPayment extends Fragment {

    PaymentAdapter paymentAdapter;
    List<Cart> listCart = new ArrayList<>();
    RecyclerView recyclerView;
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    Button btnHuy, btnPay;
    TextView showpttt, showdiscount, tongtien;
    TextInputEditText fullname, phonenumber, address;
    TextInputLayout lfullname, lphonenumber, laddress;

    float totalCart = 0;
    float applyDiscount = 0;
    String wayToPay = "";
    User user;
    TrangChung mainactivity;

    public ConfirmPayment(List<Cart> listCartt) {
        this.listCart = listCartt;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.thanh_toan_frag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnHuy = view.findViewById(R.id.btnhuy);
        btnPay = view.findViewById(R.id.btnpay);
        showdiscount = view.findViewById(R.id.btndiscount);
        showpttt = view.findViewById(R.id.btnpttt);
        tongtien = view.findViewById(R.id.tongtien);
        fullname = view.findViewById(R.id.fullname);
        phonenumber = view.findViewById(R.id.phonenumber);
        address = view.findViewById(R.id.address);
        lfullname = view.findViewById(R.id.lfullname);
        lphonenumber = view.findViewById(R.id.lphonenumber);
        laddress = view.findViewById(R.id.laddress);

        if (getActivity() instanceof TrangChung) {
            mainactivity = (TrangChung) getActivity();
        } else {
            return;
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        // Lấy thông tin người dùng
        Future<User> futureUser = executorService.submit(() -> AppDatabase.getInstance(getContext()).userDAO().checkUser(username));
        try {
            user = futureUser.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fullname.setText(user.getFullname());
        address.setText(user.getAddress());
        phonenumber.setText(user.getPhone());

        paymentAdapter = new PaymentAdapter(getActivity(), listCart);
        recyclerView = view.findViewById(R.id.listMon);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(paymentAdapter);

        float shipFee = 30000;
        for (Cart cart : listCart) {
            totalCart += cart.getCartPrice();
        }
        totalCart += shipFee;
        applyDiscount = totalCart;
        updateTotal(applyDiscount);

        showpttt.setOnClickListener(v -> showPaymentDialog());
        showdiscount.setOnClickListener(v -> showDiscountDialog(username));
        btnHuy.setOnClickListener(v -> mainactivity.replaceFrag(new CartFrag(), "Giỏ hàng"));
        btnPay.setOnClickListener(v -> handlePayment(username));
    }

    private void showPaymentDialog() {
        final String[] methods = {"Tiền mặt", "Chuyển khoản ngân hàng", "Momo", "ZaloPay"};
        final int[] selected = {-1};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chọn phương thức thanh toán");
        builder.setSingleChoiceItems(methods, -1, (dialog, which) -> selected[0] = which);
        builder.setPositiveButton("Chọn", (dialog, which) -> {
            if (selected[0] != -1) {
                wayToPay = methods[selected[0]];
                showpttt.setText(wayToPay);
                showpttt.setTextColor(Color.BLACK);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showDiscountDialog(String username) {
        executorService.execute(() -> {
            List<VoucherDetail> voucherDetails = AppDatabase.getInstance(getContext())
                    .voucherDetaildao().getVoucherOfCustomer(username);
            List<Voucher> validVouchers = new ArrayList<>();

            for (VoucherDetail vd : voucherDetails) {
                Voucher voucher = AppDatabase.getInstance(getContext())
                        .DaoVoucher().getVoucherByID(vd.getVoucherid());

                if (voucher != null && totalCart >= voucher.getMin()) {
                    validVouchers.add(voucher);
                }
            }

            getActivity().runOnUiThread(() -> {
                if (validVouchers.isEmpty()) {
                    Toast.makeText(getContext(), "Không có mã giảm giá phù hợp", Toast.LENGTH_LONG).show();
                    return;
                }

                String[] voucherTexts = new String[validVouchers.size()];
                for (int i = 0; i < validVouchers.size(); i++) {
                    voucherTexts[i] = validVouchers.get(i).getVouchercode() + " - " + validVouchers.get(i).getMota();
                }

                final int[] selected = {-1};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Chọn mã giảm giá");
                builder.setSingleChoiceItems(voucherTexts, -1, (dialog, which) -> selected[0] = which);

                builder.setPositiveButton("Chọn", (dialog, which) -> {
                    if (selected[0] != -1) {
                        Voucher chosen = validVouchers.get(selected[0]);
                        if (chosen.getTypevoucher().equals("freeship")) {
                            applyDiscount = totalCart - 30000;
                        } else {
                            applyDiscount = totalCart - (totalCart * chosen.getDiscount_percent() / 100);
                        }
                        updateTotal(applyDiscount);
                        showdiscount.setText(chosen.getVouchercode());
                        showdiscount.setTextColor(Color.BLACK);
                    }
                });
                builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
                builder.show();
            });
        });
    }

    private void handlePayment(String username) {
        boolean check = true;

        if (fullname.getText().toString().isEmpty()) {
            lfullname.setError("Require");
            check = false;
        } else lfullname.setError(null);

        if (phonenumber.getText().toString().isEmpty()) {
            lphonenumber.setError("Require");
            check = false;
        } else lphonenumber.setError(null);

        if (address.getText().toString().isEmpty()) {
            laddress.setError("Require");
            check = false;
        } else laddress.setError(null);

        if (wayToPay.isEmpty()) {
            Toast.makeText(getContext(), "Chưa chọn phương thức thanh toán", Toast.LENGTH_LONG).show();
            check = false;
        }

        if (!check) return;

        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        String voucherCode = showdiscount.getText().toString();
        String note = ((TextInputEditText) getView().findViewById(R.id.note)).getText().toString();

        // Lấy dữ liệu từ EditText
        String fullNameInfo = fullname.getText().toString();
        String phoneNumberInfo = phonenumber.getText().toString();
        String addressInfo = address.getText().toString();

        History history = new History(username, fullNameInfo, date, applyDiscount, wayToPay, phoneNumberInfo, addressInfo, voucherCode, note);

        executorService.execute(() -> {
            try {
                long lastID = AppDatabase.getInstance(getContext()).historyDAO().insert(history);
                for (Cart cart : listCart) {
                    HistoryDetail detail = new HistoryDetail((int) lastID, cart.getFoodID(), cart.getFoodQuantity());
                    AppDatabase.getInstance(getContext()).daoDetailHistory().insertDetailHistory(detail);
                }

                getActivity().runOnUiThread(() -> new AlertDialog.Builder(getContext())
                        .setTitle("Thanh toán thành công!")
                        .setIcon(R.drawable.baseline_done_24)
                        .setPositiveButton("Về trang chủ", (dialog, which) ->
                                mainactivity.replaceFrag(new TrangChu(), "Trang chủ"))
                        .setNegativeButton("Chi tiết đơn hàng", (dialog, which) -> {
                            HistoryChiTietFrag frag = new HistoryChiTietFrag();
                            Bundle b = new Bundle();
                            b.putInt("transactionID", (int) lastID);
                            frag.setArguments(b);
                            mainactivity.replaceFrag(frag, "Chi tiết đơn hàng");
                        })
                        .show());
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> new AlertDialog.Builder(getContext())
                        .setTitle("Thanh toán thất bại!")
                        .setIcon(R.drawable.baseline_error_24)
                        .show());
            }
        });
    }

    private void updateTotal(float amount) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        format.setCurrency(Currency.getInstance(locale));
        String str = format.format(amount);
        tongtien.setText(str.replace("₫", " VND"));
    }
}
