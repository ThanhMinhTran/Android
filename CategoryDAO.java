package com.example.oderfoodapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.oderfoodapp.object.Category;
import com.example.oderfoodapp.object.Food;

import java.util.List;

@Dao
public interface CategoryDAO {
    @Insert
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM tblCategory")
    List<Category> getAllCategory();

    @Query("SELECT * FROM tblCategory WHERE categoryID = :categoryID")
    List<Category> checkCategoryID(String categoryID);

    // Phương thức trả về số lượng bản ghi có `categoryID` cụ thể
    @Query("SELECT COUNT(*) FROM tblCategory WHERE categoryID = :categoryID")
    int checkDanhMucChuaTonTai(String categoryID);
}
