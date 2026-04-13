package com.example.oderfoodapp.object;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tblVoucher")
public class Voucher {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int voucherid;
    private String vouchercode;
    private String mota;
    private String typevoucher;
    private float min;
    private float discount_percent;

    public Voucher(String vouchercode, String mota, String typevoucher, float min, float discount_percent) {
        this.vouchercode = vouchercode;
        this.mota = mota;
        this.typevoucher = typevoucher;
        this.min = min;
        this.discount_percent = discount_percent;
    }

    public int getVoucherid() {
        return voucherid;
    }

    public void setVoucherid(int voucherid) {
        this.voucherid = voucherid;
    }

    public String getVouchercode() {
        return vouchercode;
    }

    public void setVouchercode(String vouchercode) {
        this.vouchercode = vouchercode;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public String getTypevoucher() {
        return typevoucher;
    }

    public void setTypevoucher(String typevoucher) {
        this.typevoucher = typevoucher;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getDiscount_percent() {
        return discount_percent;
    }

    public void setDiscount_percent(float discount_percent) {
        this.discount_percent = discount_percent;
    }
}
