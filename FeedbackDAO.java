package com.example.oderfoodapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.oderfoodapp.object.Feedback;
import com.example.oderfoodapp.object.Food;

import java.util.List;

@Dao
public interface FeedbackDAO {
    @Insert
    void insert(Feedback feedback);

    @Update
    void update(Feedback feedback);

    @Delete
    void delete(Feedback feedback);

    @Query("SELECT * FROM tblFeedback")
    List<Feedback> getAllFeedback();

    @Query("SELECT * FROM tblFeedback WHERE feedbackID = :feedbackID")
    List<Feedback> checkFeedbackID(String feedbackID);


}
