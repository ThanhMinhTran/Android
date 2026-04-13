package com.example.oderfoodapp.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.Locale;

import com.example.oderfoodapp.R;
import com.example.oderfoodapp.TrangChung;
import com.example.oderfoodapp.database.AppDatabase;
import com.example.oderfoodapp.object.Cart;
import com.example.oderfoodapp.object.FavoriteFood;
import com.example.oderfoodapp.object.Food;
import com.example.oderfoodapp.object.FoodRatingDetails;
import com.example.oderfoodapp.recyclerViewAdapter.FoodRatingDetailsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class ThongTinMonFrag extends Fragment {

    private RecyclerView listRating;
    private FoodRatingDetailsAdapter foodRatingDetailsAdapter;
    private List<FoodRatingDetails> foodRatingDetailsList;
    private AppDatabase appDatabase;
    private String foodID;
    SharedPreferences sharedPreferences;
    String username;
    RatingBar ratingBar;
    ImageButton favoriteHeartFood;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    public ThongTinMonFrag() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkFavoriteStatus(); // Gọi khi giao diện được hiển thị
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
        View view = inflater.inflate(R.layout.thong_tin_mon_frag, container, false);

        // Khởi tạo AppDatabase
        appDatabase = AppDatabase.getInstance(getContext());

        // Khởi tạo RecyclerView
        listRating = view.findViewById(R.id.listRating);
        listRating.setHasFixedSize(true);
        listRating.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tạo danh sách dữ liệu mẫu
        foodRatingDetailsList = new ArrayList<>();
        foodRatingDetailsList.add(new FoodRatingDetails("images6", "Trần Minh Thành", 4, "Đồ ăn rất ngon."));
        foodRatingDetailsList.add(new FoodRatingDetails("my_avatar", "Hoàng Minh Duy", 5, "Pizza ngon vừa miệng bố sung dinh dưỡng cho một ngày làm việc mệt mỏi."));
        foodRatingDetailsList.add(new FoodRatingDetails("images4", "Nguyễn Thế Vũ", 3, "Khoai tây hơi mềm."));

        // Tạo Adapter và liên kết với RecyclerView
        foodRatingDetailsAdapter = new FoodRatingDetailsAdapter(getContext(), foodRatingDetailsList);
        listRating.setAdapter(foodRatingDetailsAdapter);

        // lấy username phiên hiện tại
        sharedPreferences = getActivity().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        // nút thêm vào giỏ
        Button btnThemVaoGio = view.findViewById(R.id.btnThemVaoGio);
        btnThemVaoGio.setOnClickListener(v -> {
            executorService.execute(() -> {
                try {
                    // Kiểm tra xem món đã có trong giỏ hay chưa
                    Cart existingCart = appDatabase.cartDAO().getCartByUserAndFood(username, foodID);
                    if (existingCart != null) {
                        // Nếu đã tồn tại, tăng số
                        Food food = appDatabase.foodDAO().getFoodByID(existingCart.getFoodID());
                        int updatedQuantity = existingCart.getFoodQuantity() + 1;
                        existingCart.setFoodQuantity(updatedQuantity);
                        float newTotalPrice = food.getPrice() * updatedQuantity;
                        existingCart.setCartPrice(newTotalPrice);
                        appDatabase.cartDAO().update(existingCart);
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Tăng số lượng món trong giỏ hàng!", Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        // Nếu chưa tồn tại, thêm mới
                        float foodPrice = appDatabase.foodDAO().getFoodByID(foodID).getPrice();
                        Cart newCart = new Cart(0, username, foodID, 1, foodPrice);
                        appDatabase.cartDAO().insert(newCart);
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Thêm vào giỏ thành công!", Toast.LENGTH_SHORT).show()
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Lỗi khi thêm vào giỏ!", Toast.LENGTH_SHORT).show()
                    );
                }
                // Điều hướng sang CartFrag
                getActivity().runOnUiThread(() -> {
                    CartFrag cartFrag = new CartFrag();
                    ((TrangChung) getActivity()).replaceFrag(cartFrag, "Giỏ hàng");
                });
            });
        });
        loadFoodDetails(foodID, view);

        ratingBar = view.findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating(rating);
            }
        });

        checkFavoriteStatus();
        favoriteHeartFood = view.findViewById(R.id.favoriteHeartFood);
        favoriteHeartFood.setOnClickListener(v -> toggleFavoriteStatus());

        return view;
    }

    private void checkFavoriteStatus() {
        executorService.execute(() -> {
            try {
                // Lấy thông tin món ăn hiện tại
                Food currentFood = appDatabase.foodDAO().getFoodByID(foodID);
                if (currentFood != null) {
                    getActivity().runOnUiThread(() -> {
                        if (currentFood.isFavorite()) {
                            favoriteHeartFood.setImageResource(R.drawable.favorite_heart_full);
                        } else {
                            favoriteHeartFood.setImageResource(R.drawable.favorite_heart);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Lỗi khi kiểm tra trạng thái yêu thích!", Toast.LENGTH_SHORT).show());
            }
        });
    }


    private void toggleFavoriteStatus() {
        executorService.execute(() -> {
            try {
                // Lấy thông tin món ăn hiện tại
                Food currentFood = appDatabase.foodDAO().getFoodByID(foodID);
                if (currentFood == null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Món ăn không tồn tại!", Toast.LENGTH_SHORT).show());
                    return;
                }

                if (currentFood.isFavorite()) {
                    // Nếu món đã yêu thích, xóa khỏi FavoriteFood và cập nhật trạng thái
                    appDatabase.favoriteDAO().deleteByUserAndFood(username, foodID);
                    currentFood.setFavorite(false); // Cập nhật trạng thái isFavorite
                    appDatabase.foodDAO().update(currentFood);

                    getActivity().runOnUiThread(() -> {
                        favoriteHeartFood.setImageResource(R.drawable.favorite_heart);
                        Toast.makeText(getContext(), "Đã xóa khỏi danh sách yêu thích!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Nếu món chưa yêu thích, thêm vào FavoriteFood và cập nhật trạng thái
                    FavoriteFood newFavorite = new FavoriteFood(0, username, foodID, currentFood.getImage(), currentFood.getPrice(), currentFood.getName());
                    appDatabase.favoriteDAO().insert(newFavorite);
                    currentFood.setFavorite(true); // Cập nhật trạng thái isFavorite
                    appDatabase.foodDAO().update(currentFood);

                    getActivity().runOnUiThread(() -> {
                        favoriteHeartFood.setImageResource(R.drawable.favorite_heart_full);
                        Toast.makeText(getContext(), "Đã thêm vào danh sách yêu thích!", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show());
            }
        });
    }


    // load thông tin món ra ThongTinMonFrag
    private void loadFoodDetails(String foodID, View view) {
        // Tạo một ExecutorService để thực hiện truy vấn trên background thread
        executorService.execute(() -> {
            // Truy vấn dữ liệu món ăn từ cơ sở dữ liệu trong luồng nền
            Food food = appDatabase.foodDAO().getFoodByID(foodID);

            // Sau khi có kết quả từ cơ sở dữ liệu, cập nhật giao diện người dùng (UI) trên main thread
            if (food != null) {
                // Cập nhật UI trên main thread
                getActivity().runOnUiThread(() -> {
                    TextView foodName = view.findViewById(R.id.txtThongTinFoodName);
                    foodName.setText(food.getName());

                    TextView foodPrice = view.findViewById(R.id.txtThongTinFoodPrice);
                    NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
                    foodPrice.setText(format.format(food.getPrice()) + " VND");

                    TextView foodDescription = view.findViewById(R.id.txtMoTaMon); // Thêm TextView cho mô tả
                    foodDescription.setText(food.getDescription());

                    // Cập nhật hình ảnh món ăn (giả sử bạn có URI hình ảnh)
                    ImageView foodImage = view.findViewById(R.id.imgThongTinFood); // Thêm ImageView trong layout
                    Uri foodImageUri = Uri.parse(food.getImage());
                    foodImage.setImageURI(foodImageUri);
                });
            }
        });
    }
}