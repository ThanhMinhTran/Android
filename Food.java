package com.example.oderfoodapp.object;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "tblFood",
        foreignKeys = @ForeignKey(
                entity = Category.class,               // Bảng tham chiếu (Category)
                parentColumns = "categoryID",          // Cột của bảng Category tham chiếu đến
                childColumns = "categoriesID",         // Cột trong bảng Food giữ khóa ngoại
                onDelete = ForeignKey.CASCADE          // Xóa các bản ghi liên quan khi xóa Category
        ))

public class Food {

    @PrimaryKey
    @NonNull
    private String foodID = "";
    private String name;
    private String description;
    private int quantity;
    private float price;
    private String image;
    private String categoriesID;
    private boolean isFavorite;

    public Food() {
    }

    public Food(@NonNull String foodID, String name, String description, int quantity, float price, String image, String categoriesID, boolean isFavorite) {
        this.foodID = foodID;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.image = image;
        this.categoriesID = categoriesID;
        this.isFavorite = isFavorite;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @NonNull
    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(@NonNull String foodID) {
        this.foodID = foodID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategoriesID() {
        return categoriesID;
    }

    public void setCategoriesID(String categoriesID) {
        this.categoriesID = categoriesID;
    }
}
