package com.example.oderfoodapp.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.oderfoodapp.R;
import com.example.oderfoodapp.database.AppDatabase;
import com.example.oderfoodapp.object.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChangePWFrag extends Fragment {

    private EditText edtMKHienTai, edtMKMoi, edtNhapLaiMK;
    private Button btnDoiMK;
    private String currentUsername;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ChangePWFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_pw_frag, container, false);

        edtMKHienTai = view.findViewById(R.id.edt_MKHienTai);
        edtMKMoi = view.findViewById(R.id.edt_MKMoi);
        edtNhapLaiMK = view.findViewById(R.id.edt_NhapLaiMKMoi);
        btnDoiMK = view.findViewById(R.id.btn_DoiMK);

        // lấy phiên người dùng hiện tại
        currentUsername = getActivity().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE)
                .getString("username", "");


        btnDoiMK.setOnClickListener(v -> changePassword());

        return view;
    }

    private void changePassword() {

        String mkHienTai = edtMKHienTai.getText().toString().trim();
        String mkMoi = edtMKMoi.getText().toString().trim();
        String nhapLaiMK = edtNhapLaiMK.getText().toString().trim();

        // check nhập đầy đủ
        if (TextUtils.isEmpty(mkHienTai) || TextUtils.isEmpty(mkMoi) || TextUtils.isEmpty(nhapLaiMK)) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mkMoi.equals(nhapLaiMK)) {
            Toast.makeText(getContext(), "Mật khẩu mới và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isPasswordValid(mkMoi)) {
            Toast.makeText(getContext(), "Mật khẩu mới không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // thực thi truy vấn sqlite
        executorService.execute(() -> {
            User user = AppDatabase.getInstance(getContext()).userDAO().checkUser(currentUsername);

            if (user != null && user.getPassword().equals(mkHienTai)) {
                // Update password
                user.setPassword(mkMoi);
                AppDatabase.getInstance(getContext()).userDAO().update(user);

                // lưu MK vào SharedPreferences
                getActivity().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE)
                        .edit().putString("password", mkMoi).apply();

                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    clearFields();
                });
            } else {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // Password validation method (you can improve it with more complex logic)
    private boolean isPasswordValid(String password) {
        return password.length() >= 8 &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*\\d.*") &&
                !password.contains(" ");  // Example validation criteria
    }

    private void clearFields() {
        edtMKHienTai.setText("");
        edtMKMoi.setText("");
        edtNhapLaiMK.setText("");
    }

}