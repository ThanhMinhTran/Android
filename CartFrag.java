package com.example.oderfoodapp.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.R;
import com.example.oderfoodapp.TrangChung;
import com.example.oderfoodapp.database.AppDatabase;
import com.example.oderfoodapp.object.Cart;
import com.example.oderfoodapp.object.Food;
import com.example.oderfoodapp.object.History;
import com.example.oderfoodapp.recyclerViewAdapter.CartAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartFrag extends Fragment implements CartAdapter.OnCartClickListener{

    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private List<Cart> cartItemList;
    private Button btnThanhToan;
    private AppDatabase appDatabase;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private SharedPreferences sharedPreferences;
    private String username;
    private float tongTienGioHang;
    private String foodID;
    private TextView txtTotalAmount, txtTongTien, txtPhiVanChuyen;
    private static final int SHIPPING_FEE = 30000; // Phí vận chuyển mặc định
    private int discount = 0; // Giảm giá ban đầu là 0

    private ProgressBar progressBar;

    private TrangChung mainActivity;

    public CartFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Nhận foodID từ Bundle
        if (getArguments() != null) {
            foodID = getArguments().getString("foodID");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_frag, container, false);

        // Ép kiểu getActivity() sang TrangChung
        if (getActivity() instanceof TrangChung) {
            mainActivity = (TrangChung) getActivity();
        } else {
            Toast.makeText(getContext(), "Fragment phải được gắn vào TrangChung", Toast.LENGTH_SHORT).show();
            return view; // Ngăn chặn lỗi nếu không thể ép kiểu
        }


        // Khởi tạo AppDatabase
        appDatabase = AppDatabase.getInstance(getContext());

        // Khởi tạo RecyclerView và Adapter
        recyclerViewCart = view.findViewById(R.id.listFoodOrder);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCart.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        txtTongTien = view.findViewById(R.id.txtTongTienValue);
        txtPhiVanChuyen = view.findViewById(R.id.txtPhiVanChuyenValue);
        txtTotalAmount = view.findViewById(R.id.txtTotalAmount);
        btnThanhToan = view.findViewById(R.id.btnThanhToan);

        progressBar = view.findViewById(R.id.progressBar);


        cartAdapter = new CartAdapter(getContext(), mainActivity, cartID -> {
            // Tạo một instance của ThongTinMonFrag
            ThongTinMonFrag thongTinMonFrag = new ThongTinMonFrag();

            // Truyền foodId cho ThongTinMonFrag thông qua Bundle
            Bundle args = new Bundle();
            args.putString("foodID", foodID);
            thongTinMonFrag.setArguments(args);

            // Thay thế Fragment hiện tại bằng ThongTinMonFrag
            mainActivity.replaceFrag(thongTinMonFrag, "Thông tin món ăn");
        }, this);
        recyclerViewCart.setAdapter(cartAdapter);

        // Tải dữ liệu giỏ hàng
        loadCartData();

        // Nút thanh toán
        btnThanhToan.setOnClickListener(v -> {
            if (cartItemList == null || cartItemList.isEmpty()) {
                Toast.makeText(getContext(), "Giỏ hàng trống, không thể thanh toán!", Toast.LENGTH_SHORT).show();
            } else {
                ConfirmPayment confirmPayment = new ConfirmPayment(cartItemList);
                mainActivity.replaceFrag(confirmPayment,"Thanh toán");
            }
        });


        return view;
    }
    /*
    // hiện box lựa chọn phương thức thanh toán
    private void showPaymentDialog() {
        String[] paymentMethods = {"Thanh toán bằng tiền mặt", "Thanh toán bằng thẻ", "Ví điện tử"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chọn phương thức thanh toán");

        builder.setSingleChoiceItems(paymentMethods, -1, (dialog, which) -> {
            Toast.makeText(getContext(), "Bạn chọn: " + paymentMethods[which], Toast.LENGTH_SHORT).show();
        });

        builder.setPositiveButton("Thanh toán", (dialog, which) -> {
            Toast.makeText(getContext(), "Đang thực hiện thanh toán...", Toast.LENGTH_SHORT).show();
            executorService.execute(() -> {
                saveTransactionHistory();
                for (Cart cart : cartItemList) {
                    appDatabase.cartDAO().delete(cart);
                }
                cartItemList.clear();
                getActivity().runOnUiThread(() -> {
                    cartAdapter.notifyDataSetChanged();
                    updateTotalAmount();
                    Toast.makeText(getContext(), "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                });
            });
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    // phương thức để lưu món trong giỏ vào lịch sử
    private void saveTransactionHistory() {
        sharedPreferences = getActivity().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        List<Cart> cartList = appDatabase.cartDAO().getAllCartsByUserId(username);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String transactionDate = sdf.format(new Date());

        executorService.execute(() -> {
            for (Cart cart : cartList) {
                // Tạo đối tượng History từ dữ liệu trong giỏ hàng
                History history = new History(0, username, cart.getFoodID(), cart.getFoodQuantity(), transactionDate);
                // Lưu từng mục vào bảng History
                appDatabase.historyDAO().insert(history);
            }

        });
    }*/

    public void loadCartData() {
        sharedPreferences = getActivity().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        if (username == null || username.isEmpty()) {
            getActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), "Không tìm thấy username. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show()
            );
            return;
        }

        executorService.execute(() -> {
            List<Cart> cartList = appDatabase.cartDAO().getAllCartsByUserId(username);
            List<Food> foodList = new ArrayList<>();
            if (cartList == null) {
                cartList = new ArrayList<>();
            }
            for (Cart cart : cartList) {
                Food food = appDatabase.foodDAO().getFoodByID(cart.getFoodID());
                if (food != null) {
                    foodList.add(food);
                }
            }

            cartItemList = cartList; // Cập nhật danh sách giỏ hàng
            getActivity().runOnUiThread(() -> {
                cartAdapter.setCartData(cartItemList, foodList);
                updateTotalAmount(); // Cập nhật tổng tiền
            });
        });
    }

    public void updateTotalAmount() {
        executorService.execute(() -> {
            float totalCartPrice = 0;
            if (cartItemList != null) {
                for (Cart cart : cartItemList) {
                    totalCartPrice += cart.getCartPrice();
                }
            }
            final float finalCartPrice = totalCartPrice;
            final int totalAmount = Math.round(finalCartPrice + SHIPPING_FEE - discount);

            getActivity().runOnUiThread(() -> {
                txtTongTien.setText(String.format("%,.0f VND", finalCartPrice));
                txtPhiVanChuyen.setText(String.format("%,d VND", SHIPPING_FEE));
                txtTotalAmount.setText(String.format("%,d VND", totalAmount));
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartData(); // Tải lại dữ liệu mỗi khi Fragment được hiển thị
    }


    @Override
    public void onCartClick(int cartID) {
        // Lấy foodID từ danh sách giỏ hàng
        String foodID = null;
        for (Cart cart : cartItemList) {
            if (cart.getCartID() == cartID) {
                foodID = cart.getFoodID();
                break;
            }
        }

        if (foodID != null) {
            ThongTinMonFrag thongTinMonFrag = new ThongTinMonFrag();
            Bundle args = new Bundle();
            args.putString("foodID", foodID);
            thongTinMonFrag.setArguments(args);

            // Thay thế Fragment hiện tại
            mainActivity.replaceFrag(thongTinMonFrag, "Thông tin món ăn");
        } else {
            Toast.makeText(getContext(), "Không tìm thấy món ăn!", Toast.LENGTH_SHORT).show();
        }
    }

    public void applyDiscount(int voucherDiscount) {
        discount = voucherDiscount;
        updateTotalAmount();
    }
}