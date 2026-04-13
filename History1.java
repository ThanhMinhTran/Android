package com.example.oderfoodapp.object;

public class History1 {
    private String name;
    private String image;
    private int price;
    private String date; // Thêm trường cho ngày đặt

    public History1(String name, String image, int price, String date) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.date = date;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
