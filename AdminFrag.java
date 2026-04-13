package com.example.oderfoodapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.oderfoodapp.QuanLyDanhMuc;
import com.example.oderfoodapp.QuanLyMon;
import com.example.oderfoodapp.QuanLyUser;
import com.example.oderfoodapp.R;

public class AdminFrag extends Fragment {

    public AdminFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_frag, container, false);

        // Tìm các nút trong layout
        Button btnFoodManage = view.findViewById(R.id.btnFoodManage);
        Button btnCategoryManage = view.findViewById(R.id.btnCategoryManage);
        Button btnUserManage = view.findViewById(R.id.btnUserManage);


        // Thiết lập sự kiện onClick cho từng nút
        btnFoodManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở FoodManageActivity
                Intent intent = new Intent(getActivity(), QuanLyMon.class);
                startActivity(intent);
            }
        });

        btnCategoryManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), QuanLyDanhMuc.class);
                startActivity(intent);
            }
        });

        btnUserManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), QuanLyUser.class);
                startActivity(intent);
            }
        });


        return view;
    }
}
