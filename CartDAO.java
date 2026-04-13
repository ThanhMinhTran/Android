package com.example.oderfoodapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.oderfoodapp.object.Cart;
import com.example.oderfoodapp.object.Category;

import java.util.List;

@Dao
public interface CartDAO {
    @Insert
    void insert(Cart cart);

    @Update
    void update(Cart cart);

    @Delete
    void delete(Cart cart);

    @Query("SELECT * FROM tblCart WHERE username = :username")
    List<Cart> getAllCartsByUserId(String username);

    @Query("INSERT INTO tblCart (username, foodID, foodQuantity, cartPrice) VALUES (:username, :foodID, :foodQuantity, :cartPrice)")
    void insertCart(String username, String foodID, int foodQuantity, float cartPrice);

    @Query("UPDATE tblCart SET foodQuantity = :quantity WHERE cartID = :cartID")
    void updateQuantity(int cartID, int quantity);

    @Query("DELETE FROM tblCart WHERE cartID = :cartID AND username = :username")
    void deleteItem(int cartID, String username);

    @Query("SELECT * FROM tblCart WHERE username = :username AND foodID = :foodID LIMIT 1")
    Cart getCartByUserAndFood(String username, String foodID);
}
