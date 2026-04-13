package com.example.oderfoodapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.oderfoodapp.object.Food;

import java.util.List;

@Dao
public interface FoodDAO {
    @Insert
    void insert(Food food);

    @Update
    void update(Food food);

    @Delete
    void delete(Food food);

    @Query("SELECT * FROM tblFood")
    List<Food> getAllFoods();

    @Query("SELECT * FROM tblFood WHERE foodID = :foodID")
    List<Food> checkFoodID(String foodID);

    @Query("SELECT * FROM tblFood WHERE categoriesID = :categoryID")
    List<Food> getFoodsByCategoryId(String categoryID);

    @Query("SELECT * FROM tblFood WHERE foodID = :foodID LIMIT 1")
    Food getFoodByID(String foodID);

    @Query("select * from tblFood where name like '%' || :keyword || '%' ")
    List<Food> getFoodByKey(String keyword);
}
