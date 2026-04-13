package com.example.oderfoodapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oderfoodapp.database.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DangKy extends AppCompatActivity {

    private LinearLayout linearDangKy;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dang_ky);

        EditText edtUsername = findViewById(R.id.edtUsernameDK);
        EditText edtPassword = findViewById(R.id.edtPassDK);
        EditText edtNhapLaiMK = findViewById(R.id.edt_NhapLaiMK);

        Button btnDangKy = findViewById(R.id.btn_DangKy);
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                String nhapLaiMK = edtNhapLaiMK.getText().toString();

                // Kiểm tra xem các trường có trống không
                if (username.isEmpty() || password.isEmpty() || nhapLaiMK.isEmpty()) {
                    Toast.makeText(DangKy.this, "Vui lòng nhập đủ thông tin.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.equals(nhapLaiMK)) {
                    executorService.execute(() -> {
                        AppDatabase.getInstance(DangKy.this).userDAO().taoTK(username, password);
                        runOnUiThread(() -> {
                            Toast.makeText(DangKy.this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                            // Chuyển đến màn hình đăng nhập sau khi đăng ký thành công
                            Intent intent = new Intent(DangKy.this, DangNhap.class);
                            startActivity(intent);
                            finish();
                        });
                    });
                } else {
                    // Hiển thị thông báo lỗi nếu mật khẩu không khớp
                    Toast.makeText(DangKy.this, "Mật khẩu không khớp! Vui lòng nhập lại.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        TextView txtDangNhap = findViewById(R.id.tv_DangNhap);
        txtDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtDangNhap.setTextColor(Color.BLACK);
                // Chuyển đến màn hình đăng nhập
                Intent intent = new Intent(DangKy.this, DangNhap.class);
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


}
