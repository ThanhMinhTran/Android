package com.example.oderfoodapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.oderfoodapp.object.Voucher;
import com.example.oderfoodapp.object.VoucherDetail;

import java.util.List;

@Dao
public interface daoVoucher {

    @Insert
    void insertVoucher(Voucher voucher);

    @Query("Select * from tblVoucher where voucherid = :voucherid")
    Voucher getVoucherByID(int voucherid);

}
