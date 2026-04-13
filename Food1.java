package com.example.oderfoodapp.object;

public class Food1 {
    private String name;
    private int quantity;
    private int price;
    private String image;
    private String categories;

    public Food1(String name, int quantity, int price, String image) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.image = image;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public Food1(String name, String image, int price, String categories) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.categories = categories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

