package com.example.oderfoodapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.oderfoodapp.object.Food;
import com.example.oderfoodapp.object.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM tblUser")
    List<User> getAllUser();

    @Query("SELECT * FROM tblUser WHERE username = :username")
    List<User> checkUsername(String username);

    @Query("SELECT * FROM tblUser WHERE username = :username AND password = :password")
    User checkDangNhapUser(String username, String password);

    @Query("INSERT INTO tblUser (username, password, quyen) VALUES (:username, :password, 0)")
    void taoTK(String username, String password);

    @Query("SELECT quyen FROM tblUser WHERE username = :username LIMIT 1")
    boolean checkQuyenUser(String username);

    @Query("SELECT * FROM tblUser WHERE username = :username LIMIT 1")
    User checkUser(String username);

}
