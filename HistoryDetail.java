package com.example.oderfoodapp.object;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "tblhistorydetail",
    primaryKeys = {"transactionID","foodID"},
    foreignKeys = {
        @ForeignKey(
                entity = History.class,
                parentColumns = "transactionID",
                childColumns = "transactionID"
        ),
            @ForeignKey(
                    entity = Food.class,
                    parentColumns = "foodID",
                    childColumns = "foodID"
            )
    }
)
public class HistoryDetail {
    private int transactionID;
    @NonNull
    private String foodID;
    private int quantity;

    public HistoryDetail() {
    }

    public HistoryDetail(int transactionid, String foodID, int quantity) {
        this.transactionID = transactionid;
        this.foodID = foodID;
        this.quantity = quantity;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
