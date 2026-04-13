package com.example.oderfoodapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.oderfoodapp.object.History;

import java.util.List;

@Dao
public interface HistoryDAO {
    @Insert
    long insert(History history);

    @Query("SELECT * FROM tblHistory WHERE username = :username")
    List<History> getHistoryByUser(String username);

    @Query("SELECT * FROM tblHistory ORDER BY transactionDate DESC")
    List<History> getAllHistory();

    @Query("Select * from tblHistory where transactionID = :transactionID and username = :username")
    History getAHistoryByID(int transactionID,String username);
}
