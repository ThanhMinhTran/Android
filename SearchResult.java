package com.example.oderfoodapp.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.R;
import com.example.oderfoodapp.TrangChung;
import com.example.oderfoodapp.database.AppDatabase;
import com.example.oderfoodapp.object.Food;
import com.example.oderfoodapp.recyclerViewAdapter.HomePopularFoodAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class SearchResult extends Fragment {

    String keyword;
    List<Food> listFood = new ArrayList<>();
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    LinearLayout linearLayout;
    RecyclerView result;
    HomePopularFoodAdapter popularFoodAdapter;
    TrangChung mainActivity;
    String username;
    SharedPreferences sharedPreferences;
    AppDatabase appDatabase;

    public SearchResult() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            keyword = getArguments().getString("keyword");
        }
        mainActivity = (TrangChung) getActivity();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appDatabase = AppDatabase.getInstance(mainActivity);
        sharedPreferences = getContext().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        linearLayout = view.findViewById(R.id.notfound);
        result = view.findViewById(R.id.searchResult);
        Future<Food> future = (Future<Food>) executorService.submit(new Runnable() {
            @Override
            public void run() {
                listFood = AppDatabase.getInstance(getContext()).foodDAO().getFoodByKey(keyword);
            }
        });

        try {
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Nếu không tìm thấy món nào thì hiển thị không tìm thấy
        if (listFood.isEmpty()) {
            linearLayout.setVisibility(View.VISIBLE);
            result.setVisibility(View.GONE);
            return;

        }

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
        popularFoodAdapter.setFoodData(listFood);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        result.setLayoutManager(gridLayoutManager);
        result.setAdapter(popularFoodAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_result_frag, container, false);
    }
}