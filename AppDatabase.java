package com.example.oderfoodapp.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.oderfoodapp.object.Cart;
import com.example.oderfoodapp.object.Category;
import com.example.oderfoodapp.object.FavoriteFood;
import com.example.oderfoodapp.object.Feedback;
import com.example.oderfoodapp.object.Food;
import com.example.oderfoodapp.object.History;
import com.example.oderfoodapp.object.HistoryDetail;
import com.example.oderfoodapp.object.User;
import com.example.oderfoodapp.object.Voucher;
import com.example.oderfoodapp.object.VoucherDetail;

import java.util.concurrent.Executors;

@Database(entities = {
        Food.class,
        Category.class,
        User.class,
        Feedback.class,
        Cart.class,
        History.class,
        FavoriteFood.class,
        HistoryDetail.class,
        Voucher.class,
        VoucherDetail.class,

}, version = 16)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "OrderFoodApp.db";
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    public abstract com.example.oderfoodapp.database.FoodDAO foodDAO();
    public abstract com.example.oderfoodapp.database.CategoryDAO categoryDAO();
    public abstract com.example.oderfoodapp.database.UserDAO userDAO();
    public abstract com.example.oderfoodapp.database.CartDAO cartDAO();
    public abstract com.example.oderfoodapp.database.HistoryDAO historyDAO();
    public abstract com.example.oderfoodapp.database.FavoriteDAO favoriteDAO();
    public abstract com.example.oderfoodapp.database.daoDetailHistory daoDetailHistory();
    public abstract com.example.oderfoodapp.database.daoVoucher DaoVoucher();
    public abstract com.example.oderfoodapp.database.daoVoucherDetail voucherDetaildao();


    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadExecutor().execute(() -> {
                // Tạo tài khoản admin mặc định (đã thay tên)
                User thanhAdmin = new User("thanh", "Trần Minh Thành", "thanh@gmail.com", "Hà Nội", "0123456789", "profile", "Thanh1234", true);
                User duyAdmin = new User("duy", "Hoàng Minh Duy", "duy@gmail.com", "Hà Nội", "0987654321", "profile", "Duy1234", true);
                User vuthv = new User("vuvt", "Nguyễn Thế Vũ", "vu@gmail.com", "Hà Nội", "0984321678", "profile", "Vu1234", true);

                // Các user thường
                User thanhtm = new User("thanhtm", "Trần Minh Thành", "thanhtm@gmail.com", "Hoàng Mai", "0934512999", "profile", "Thanhtm", false);

                AppDatabase dbInstance = getInstance(null);
                dbInstance.userDAO().insert(thanhAdmin);
                dbInstance.userDAO().insert(duyAdmin);
                dbInstance.userDAO().insert(vuthv);
                dbInstance.userDAO().insert(thanhtm);


                // Tạo các đối tượng Voucher
                Voucher voucher1 = new Voucher("FREESHIP20", "Miễn phí vận chuyển cho đơn hàng từ 200K", "freeship", 200000, 0.0f);
                Voucher voucher2 = new Voucher("SALE30K", "Giảm giá 10% cho đơn hàng từ 300K", "percentage", 300000, 10.0f);
                Voucher voucher3 = new Voucher("NEWUSER10%", "Giảm 10% cho khách hàng mới, đơn hàng từ 100K", "percentage", 100000, 10.0f);
                Voucher voucher4 = new Voucher("SALE5%", "Giảm 5% cho đơn hàng từ 50K", "percentage", 50000, 5.0f);

                dbInstance.DaoVoucher().insertVoucher(voucher1);
                dbInstance.DaoVoucher().insertVoucher(voucher2);
                dbInstance.DaoVoucher().insertVoucher(voucher3);
                dbInstance.DaoVoucher().insertVoucher(voucher4);

                // Gán voucher cho user
                VoucherDetail vc1 = new VoucherDetail(1, "thanh");
                VoucherDetail vc2 = new VoucherDetail(2, "thanh");
                VoucherDetail vc3 = new VoucherDetail(3, "thanh");
                VoucherDetail vc4 = new VoucherDetail( 4, "thanh");
// Các voucher cho tài khoản "thanhtm"
                VoucherDetail vcThanhtm1 = new VoucherDetail(1, "thanhtm");
                VoucherDetail vcThanhtm2 = new VoucherDetail(2, "thanhtm");
                VoucherDetail vcThanhtm3 = new VoucherDetail(3, "thanhtm");
                VoucherDetail vcThanhtm4 = new VoucherDetail(4, "thanhtm");

// Các voucher cho tài khoản "duy"
                VoucherDetail vcDuy1 = new VoucherDetail(1, "duy");
                VoucherDetail vcDuy2 = new VoucherDetail(2, "duy");
                VoucherDetail vcDuy3 = new VoucherDetail(3, "duy");
                VoucherDetail vcDuy4 = new VoucherDetail(4, "duy");

// Các voucher cho tài khoản "vuvt"
                VoucherDetail vcVuvt1 = new VoucherDetail(1, "vuvt");
                VoucherDetail vcVuvt2 = new VoucherDetail(2, "vuvt");
                VoucherDetail vcVuvt3 = new VoucherDetail(3, "vuvt");
                VoucherDetail vcVuvt4 = new VoucherDetail(4, "vuvt");

                dbInstance.voucherDetaildao().insertVoucherDetail(vc1);
                dbInstance.voucherDetaildao().insertVoucherDetail(vc2);
                dbInstance.voucherDetaildao().insertVoucherDetail(vc3);
                dbInstance.voucherDetaildao().insertVoucherDetail(vc4);
                // Tài khoản thanhtm
                dbInstance.voucherDetaildao().insertVoucherDetail( vcThanhtm1);
                dbInstance.voucherDetaildao().insertVoucherDetail( vcThanhtm2);
                dbInstance.voucherDetaildao().insertVoucherDetail( vcThanhtm3);
                dbInstance.voucherDetaildao().insertVoucherDetail( vcThanhtm4);

// Tài khoản duy
                dbInstance.voucherDetaildao().insertVoucherDetail(vcDuy1);
                dbInstance.voucherDetaildao().insertVoucherDetail(vcDuy2);
                dbInstance.voucherDetaildao().insertVoucherDetail(vcDuy3);
                dbInstance.voucherDetaildao().insertVoucherDetail(vcDuy4);

// Tài khoản vuvt
                dbInstance.voucherDetaildao().insertVoucherDetail(vcVuvt1);
                dbInstance.voucherDetaildao().insertVoucherDetail(vcVuvt2);
                dbInstance.voucherDetaildao().insertVoucherDetail(vcVuvt3);
                dbInstance.voucherDetaildao().insertVoucherDetail(vcVuvt4);


            });
        }
    };
}
