package com.example.oderfoodapp.object;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "tblvoucherdetail",
    primaryKeys = {"voucherid","username"},
    foreignKeys = {
        @ForeignKey(
            entity = User.class,
            parentColumns = "username",
            childColumns = "username"
        ),
            @ForeignKey(
                    entity = Voucher.class,
                    parentColumns = "voucherid",
                    childColumns = "voucherid"
            )

    }

)
public class VoucherDetail {
    private int voucherid;
    @NonNull
    private String username;

    public VoucherDetail(int voucherid, String username) {
        this.voucherid = voucherid;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getVoucherid() {
        return voucherid;
    }

    public void setVoucherid(int voucherid) {
        this.voucherid = voucherid;
    }
}
