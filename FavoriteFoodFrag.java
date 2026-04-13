package com.example.oderfoodapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.R;
import com.example.oderfoodapp.TrangChung;
import com.example.oderfoodapp.database.AppDatabase;
import com.example.oderfoodapp.object.FavoriteFood;
import com.example.oderfoodapp.object.Food;
import com.example.oderfoodapp.recyclerViewAdapter.FavoriteFoodAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteFoodFrag extends Fragment implements FavoriteFoodAdapter.OnFoodClickListener {

    private RecyclerView recyclerViewFavoriteFood;
    private FavoriteFoodAdapter favoriteFoodAdapter;
    SharedPreferences sharedPreferences;
    String username;

    private AppDatabase appDatabase;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private TrangChung mainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TrangChung) {
            mainActivity = (TrangChung) context; // Truyền TrangChung để dùng replaceFrag
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorite_food_frag, container, false);

        appDatabase = AppDatabase.getInstance(getContext());
        recyclerViewFavoriteFood = view.findViewById(R.id.recyclerViewFavoriteFood);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewFavoriteFood.setLayoutManager(linearLayoutManager);

        favoriteFoodAdapter = new FavoriteFoodAdapter(getContext(), foodID -> {
            // Tạo một instance của ThongTinMonFrag
            ThongTinMonFrag thongTinMonFrag = new ThongTinMonFrag();

            // Truyền foodId cho ThongTinMonFrag thông qua Bundle
            Bundle args = new Bundle();
            args.putString("foodID", foodID);
            thongTinMonFrag.setArguments(args);

            // Thay thế Fragment hiện tại bằng ThongTinMonFrag
            mainActivity.replaceFrag(thongTinMonFrag, "Thông tin món ăn");
        });

        recyclerViewFavoriteFood.setAdapter(favoriteFoodAdapter);

        sharedPreferences = getActivity().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        loadAllFavorite(username);
        return view;
    }

    private void loadAllFavorite(String username) {
        executorService.execute(() -> {
            List<FavoriteFood> foods = appDatabase.favoriteDAO().getFavoriteFoodsWithName(username);
            getActivity().runOnUiThread(() -> favoriteFoodAdapter.setFavoriteData(foods));
        });
    }

    @Override
    public void onFoodClick(String food) {
        ThongTinMonFrag thongTinMonFrag = new ThongTinMonFrag();

        // Truyền foodID qua Bundle
        Bundle args = new Bundle();
        args.putString("foodID", food);
        thongTinMonFrag.setArguments(args);

        // Thay thế Fragment hiện tại
        mainActivity.replaceFrag(thongTinMonFrag, "Thông tin món ăn");
    }
}
