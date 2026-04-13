package com.example.oderfoodapp.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.R;
import com.example.oderfoodapp.TrangChung;
import com.example.oderfoodapp.database.AppDatabase;
import com.example.oderfoodapp.object.Food;
import com.example.oderfoodapp.object.History;
import com.example.oderfoodapp.object.History1;
import com.example.oderfoodapp.recyclerViewAdapter.HistoryAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HistoryFrag extends Fragment {

    private RecyclerView recyclerViewHistory;
    private HistoryAdapter historyAdapter;
    private AppDatabase appDatabase;
    private SharedPreferences sharedPreferences;
    private String username;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private TrangChung mainActivity;
    private List<History> listHistory;

    public HistoryFrag() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_frag, container, false);
        // Lấy thông tin của người dùng
        sharedPreferences = getActivity().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        // Ép kiểu activity
        if (getActivity() instanceof TrangChung) {
            mainActivity = (TrangChung) getActivity();
        } else {
            return view;
        }

        // Khởi tạo RecyclerView
        recyclerViewHistory = view.findViewById(R.id.recyclerViewHistory);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewHistory.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        // Lấy dữ liệu History từ cơ sở dữ liệu
        Future<History> historyFuture = (Future<History>)executorService.submit(new Runnable() {
            @Override
            public void run() {
                listHistory = AppDatabase.getInstance(getContext()).historyDAO().getHistoryByUser(username);
            }
        });

        try {
            historyFuture.get();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        historyAdapter = new HistoryAdapter(mainActivity);
        historyAdapter.setHistoryData(listHistory);
        recyclerViewHistory.setAdapter(historyAdapter);
        return view;
    }
}
