package com.example.oderfoodapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.oderfoodapp.database.AppDatabase;
import com.example.oderfoodapp.object.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DangNhap extends AppCompatActivity {
    private static AppDatabase database;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // khai báo lưu phiên đăng nhập
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_AVATAR = "avatar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Kiểm tra trạng thái đăng nhập từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);

        if (isLoggedIn) {
            // Nếu đã đăng nhập, chuyển đến TrangChung
            startActivity(new Intent(this, TrangChung.class));
            finish();
            return;
        }

        setContentView(R.layout.dang_nhap);

        // Khởi tạo cơ sở dữ liệu Room(comment)
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "OrderFoodApp.db")
                .fallbackToDestructiveMigration() // Sử dụng khi phát triển để tự động xóa DB khi thay đổi
                .build();

        Button btnDangNhap = findViewById(R.id.btn_DangNhap);
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = ((TextView) findViewById(R.id.edtUsernameDN)).getText().toString();
                String password = ((TextView) findViewById(R.id.edt_PassDN)).getText().toString();

                // Kiểm tra xem các trường có trống không
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(DangNhap.this, "Vui lòng nhập đủ thông tin.", Toast.LENGTH_SHORT).show();
                    return;
                }

                executorService.execute(() -> {
                    User user = AppDatabase.getInstance(DangNhap.this).userDAO().checkDangNhapUser(username, password);
                    runOnUiThread(() -> {
                        if (user != null) {
                            if (user.isQuyen()) {
                                Toast.makeText(DangNhap.this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
                                // Điều hướng đến trang admin hoặc xử lý admin
                            } else {
                                Toast.makeText(DangNhap.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                // Điều hướng đến trang người dùng bình thường
                            }

                            // code lưu phiên đăng nhập
                            // Đăng nhập thành công, lưu trạng thái đăng nhập vào SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(KEY_IS_LOGGED_IN, true);
                            editor.putString(KEY_USERNAME, username);
                            editor.putString(KEY_EMAIL, user.getEmail());
                            editor.putString(KEY_AVATAR, user.getAvatar());
                            editor.apply();

                            // check thông tin hợp lệ
                            Intent intent = new Intent(DangNhap.this, TrangChung.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // không tìm thấy hoặc sai
                            Toast.makeText(DangNhap.this, "Username hoặc mật khẩu sai!", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });


        TextView quenMK = findViewById(R.id.tv_QuenMK);
        quenMK.setOnClickListener(v -> quenMK.setTextColor(Color.BLACK));

        TextView dangKi = findViewById(R.id.tv_DangKy);
        dangKi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đổi màu văn bản khi nhấn vào TextView
                dangKi.setTextColor(Color.BLACK);
                // Chuyển đến màn hình đăng ký
                Intent intent = new Intent(DangNhap.this, DangKy.class);
                startActivity(intent);
            }
        });



        // Tạo OnBackPressedCallback để xử lý sự kiện khi nhấn nút Back
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        };

        // Đăng ký callback
        getOnBackPressedDispatcher().addCallback(this, callback);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dangKy), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Phương thức để lấy database từ bất kỳ đâu(comment)
    public static AppDatabase getDatabase() {
        return database;
    }
}
