package com.example.oderfoodapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.R;
import com.example.oderfoodapp.TrangChung;
import com.example.oderfoodapp.database.AppDatabase;
import com.example.oderfoodapp.object.Category;
import com.example.oderfoodapp.object.Food;
import com.example.oderfoodapp.recyclerViewAdapter.HomeCategoriesAdapter;
import com.example.oderfoodapp.recyclerViewAdapter.HomePopularFoodAdapter;
import com.example.oderfoodapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TrangChu extends Fragment implements HomeCategoriesAdapter.OnCategoryClickListener, HomePopularFoodAdapter.OnFoodClickListener{

    private RecyclerView rcvCategory, rcvPopularFood;
    private HomeCategoriesAdapter categoryAdapter;
    private AutoCompleteTextView autoCompleteTextView;
    private HomePopularFoodAdapter popularFoodAdapter;
    private AppDatabase appDatabase;
    SharedPreferences sharedPreferences;
    String username;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private TrangChung mainActivity;
    List<String> listFoodName = new ArrayList<>();// Lấy ra tên món ăn cho phần tìm kiếm
    List<Food> listFood = new ArrayList<>();
    ImageView btnSearch;

    public TrangChu() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TrangChung) {
            mainActivity = (TrangChung) context; // Truyền TrangChung để dùng replaceFrag
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trang_chu_frag, container, false);

        // Khởi tạo AppDatabase và DAO
        appDatabase = AppDatabase.getInstance(getContext());
        sharedPreferences = getActivity().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        // Thiết lập RecyclerView cho danh mục
        rcvCategory = view.findViewById(R.id.listCategories);
        rcvCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new HomeCategoriesAdapter(this);
        rcvCategory.setAdapter(categoryAdapter);

        // Thiết lập RecyclerView cho món ăn
        rcvPopularFood = view.findViewById(R.id.listPopularFood);
        //rcvPopularFood.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // Sử dụng GridLayoutManager với 2 cột
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rcvPopularFood.setLayoutManager(linearLayoutManager);

        // Thiết lập dữ liệu cho autocompleteTextView
        autoCompleteTextView = view.findViewById(R.id.textSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        Future<Food> future = (Future<Food>) executorService.submit(new Runnable() {
            @Override
            public void run() {
                listFood = appDatabase.foodDAO().getAllFoods();
            }
        });
        try {
            future.get();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // Lấy ra tên tất cả các món ăn
        for(Food x : listFood){
            listFoodName.add(x.getName());
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(mainActivity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,listFoodName);
        autoCompleteTextView.setAdapter(arrayAdapter);
        // Xử lý khi click vào icon search
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchFoodByKey(autoCompleteTextView.getText().toString());
            }
        });

        // Trên bàn phím của anh em có nút search thực hiện sự kiện này
        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH){
                    searchFoodByKey(autoCompleteTextView.getText().toString());
                    return true;
                }
                else return false;
            }
        });

        popularFoodAdapter = new HomePopularFoodAdapter(foodID -> {
            // Tạo một instance của ThongTinMonFrag
            ThongTinMonFrag thongTinMonFrag = new ThongTinMonFrag();

            // Truyền foodId cho ThongTinMonFrag thông qua Bundle
            Bundle args = new Bundle();
            args.putString("foodID", foodID);
            thongTinMonFrag.setArguments(args);

            // Thay thế Fragment hiện tại bằng ThongTinMonFrag
            mainActivity.replaceFrag(thongTinMonFrag, "Thông tin món ăn");
        }, appDatabase, mainActivity, getContext(), username);
        rcvPopularFood.setAdapter(popularFoodAdapter);

        // Tải dữ liệu từ SQLite
        loadCategories();
        loadAllFoods();

        return view;
    }

    //
    @Override
    public void onFoodClick(String foodID) {
        // Xử lý khi người dùng nhấn vào món ăn
        ThongTinMonFrag thongTinMonFrag = new ThongTinMonFrag();

        // Truyền foodID qua Bundle
        Bundle args = new Bundle();
        args.putString("foodID", foodID);
        thongTinMonFrag.setArguments(args);

        // Thay thế Fragment hiện tại
        mainActivity.replaceFrag(thongTinMonFrag, "Thông tin món ăn");
    }

    @Override
    public void onCategoryClick(String categoryId) {
        loadFoodsByCategory(categoryId);
    }


    // Phương thức để tải tất cả danh mục từ SQLite
    private void loadCategories() {
        executorService.execute(() -> {
            List<Category> categories = appDatabase.categoryDAO().getAllCategory();
            getActivity().runOnUiThread(() -> categoryAdapter.setCategoryData(categories));
        });
    }

    // Phương thức để tải tất cả món ăn từ SQLite
    private void loadAllFoods() {
        executorService.execute(() -> {
            List<Food> foods = appDatabase.foodDAO().getAllFoods();
            getActivity().runOnUiThread(() -> popularFoodAdapter.setFoodData(foods));
        });
    }

    // Phương thức để tải các món ăn theo ID của danh mục từ SQLite
    private void loadFoodsByCategory(String categoryId) {
        executorService.execute(() -> {
            List<Food> foods = appDatabase.foodDAO().getFoodsByCategoryId(categoryId);
            getActivity().runOnUiThread(() -> popularFoodAdapter.setFoodData(foods));
        });
    }
    void searchFoodByKey(String keyword){
        SearchResult result = new SearchResult();
        Bundle bundle = new Bundle();
        bundle.putString("keyword",keyword);
        result.setArguments(bundle);
        mainActivity.replaceFrag(result,"Kết quả tìm kiếm");
    }
}