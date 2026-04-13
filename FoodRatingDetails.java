package com.example.oderfoodapp.object;

public class FoodRatingDetails {
    private String imgAvatar;
    private String customerName;
    private int ratingBarReview;
    private String customerReview;

    public FoodRatingDetails() {

    }

    public FoodRatingDetails(String imgAvatar, String customerName, int ratingBarReview, String customerReview) {
        this.imgAvatar = imgAvatar;
        this.customerName = customerName;
        this.ratingBarReview = ratingBarReview;
        this.customerReview = customerReview;
    }

    public String getImgAvatar() {
        return imgAvatar;
    }

    public void setImgAvatar(String imgAvatar) {
        this.imgAvatar = imgAvatar;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getRatingBarReview() {
        return ratingBarReview;
    }

    public void setRatingBarReview(int ratingBarReview) {
        this.ratingBarReview = ratingBarReview;
    }

    public String getCustomerReview() {
        return customerReview;
    }

    public void setCustomerReview(String customerReview) {
        this.customerReview = customerReview;
    }
}
