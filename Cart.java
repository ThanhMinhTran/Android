package com.example.oderfoodapp.object;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "tblCart",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "username",
                        childColumns = "username",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Food.class,
                        parentColumns = "foodID",
                        childColumns = "foodID",
                        onDelete = ForeignKey.CASCADE
                )
        })

public class Cart {
    @PrimaryKey(autoGenerate = true)
    private int cartID;
    @NonNull
    private String username;
    @NonNull
    private String foodID;
    private int foodQuantity;
    private float cartPrice;

    public Cart() {
    }

    public Cart(int cartID, @NonNull String username, @NonNull String foodID, int foodQuantity, float cartPrice) {
        this.cartID = cartID;
        this.username = username;
        this.foodID = foodID;
        this.foodQuantity = foodQuantity;
        this.cartPrice = cartPrice;
    }

    public int getCartID() {
        return cartID;
    }

    public void setCartID(int cartID) {
        this.cartID = cartID;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(@NonNull String foodID) {
        this.foodID = foodID;
    }

    public int getFoodQuantity() {
        return foodQuantity;
    }

    public void setFoodQuantity(int foodQuantity) {
        this.foodQuantity = foodQuantity;
    }

    public float getCartPrice() {
        return cartPrice;
    }

    public void setCartPrice(float cartPrice) {
        this.cartPrice = cartPrice;
    }
}
