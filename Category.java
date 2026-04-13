package com.example.oderfoodapp.object;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tblCategory")
public class Category {
    @PrimaryKey
    @NonNull
    private String categoryID = "";
    private String name;
    private String image;

    public Category() {
    }

    public Category(@NonNull String categoryID, String name, String image) {
        this.categoryID = categoryID;
        this.name = name;
        this.image = image;
    }

    @NonNull
    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(@NonNull String categoryID) {
        this.categoryID = categoryID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
