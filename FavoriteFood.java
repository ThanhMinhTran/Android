package com.example.oderfoodapp.object;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity (tableName = "tblFavorite",
        foreignKeys = {
                @ForeignKey(
                        entity = Food.class,
                        parentColumns = "foodID",
                        childColumns = "foodID",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "username",
                        childColumns = "username",
                        onDelete = ForeignKey.CASCADE
                )
        })

public class FavoriteFood {
    @PrimaryKey(autoGenerate = true)
    private int favoriteID;
    @NonNull
    private String username;
    @NonNull
    private String foodID;
    @NonNull
    private String image;
    private float price;
    @NonNull
    private String food_name;

    public FavoriteFood(int favoriteID, @NonNull String username, @NonNull String foodID,@NonNull String image, float price ,@NonNull String food_name) {
        this.favoriteID = favoriteID;
        this.username = username;
        this.foodID = foodID;
        this.image = image;
        this.price = price;
        this.food_name = food_name;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public int getFavoriteID() {
        return favoriteID;
    }

    public void setFavoriteID(int favoriteID) {
        this.favoriteID = favoriteID;
    }

    @NonNull
    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(@NonNull String foodID) {
        this.foodID = foodID;
    }

    @NonNull
    public String getImage() {
        return image;
    }

    public void setImage(@NonNull String image) {
        this.image = image;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @NonNull
    public String getUsername() {return username;}

    public void setUsername(@NonNull String username) {this.username = username;}
}
