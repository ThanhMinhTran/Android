package com.example.oderfoodapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.oderfoodapp.object.HistoryDetail;

import java.util.List;

@Dao
public interface daoDetailHistory {

    @Insert
    void insertDetailHistory(HistoryDetail hiss);

    @Query("select * from tblhistorydetail where transactionid = :transactionid")
    List<HistoryDetail> getListHistory(int transactionid);
}
