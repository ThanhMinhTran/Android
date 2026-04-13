package com.example.oderfoodapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.oderfoodapp.object.VoucherDetail;

import java.util.List;

@Dao
public interface daoVoucherDetail {

    @Insert
    void insertVoucherDetail(VoucherDetail vc);

    @Query("select * from tblvoucherdetail where username = :username")
    List<VoucherDetail> getVoucherOfCustomer(String username);

}
