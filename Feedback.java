package com.example.oderfoodapp.object;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "tblFeedback",
        foreignKeys = {
                @ForeignKey(entity = Food.class,
                        parentColumns = "foodID",
                        childColumns = "foodID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                        parentColumns = "username",
                        childColumns = "username",
                        onDelete = ForeignKey.CASCADE)
        })

public class Feedback {

    @PrimaryKey
    @NonNull
    String feedbackID = "";
    String username;
    String foodID;
    String feedback;
    int ratingStar;

    public Feedback() {
    }

    public Feedback(@NonNull String feedbackID, String username, String foodID, String feedback, int ratingStar) {
        this.feedbackID = feedbackID;
        this.username = username;
        this.foodID = foodID;
        this.feedback = feedback;
        this.ratingStar = ratingStar;
    }

    @NonNull
    public String getFeedbackID() {
        return feedbackID;
    }

    public void setFeedbackID(@NonNull String feedbackID) {
        this.feedbackID = feedbackID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public int getRatingStar() {
        return ratingStar;
    }

    public void setRatingStar(int ratingStar) {
        this.ratingStar = ratingStar;
    }
}
