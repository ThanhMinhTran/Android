package com.example.oderfoodapp.object;

public class Categories {

    private String titleCategories;
    private String imgCategories;

    public Categories() {

    }

    public Categories(String titleCategories, String imgCategories) {
        this.titleCategories = titleCategories;
        this.imgCategories = imgCategories;
    }

    public String getTitleCategories() {
        return titleCategories;
    }

    public void setTitleCategories(String titleCategories) {
        this.titleCategories = titleCategories;
    }

    public String getImgCategories() {
        return imgCategories;
    }

    public void setImgCategories(String imgCategories) {
        this.imgCategories = imgCategories;
    }
}
