package com.example.oderfoodapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.database.AppDatabase;
import com.example.oderfoodapp.object.User;
import com.example.oderfoodapp.recyclerViewAdapter.QuanLyUserAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuanLyUser extends AppCompatActivity {
    private EditText edtUsername, edtEmail, edtAddress, edtPhone, edtPassword;
    private Button btnAddImg, btnAddUser, btnUpdateUser, btnDeleteUser;
    private TextView txtImgAvatar;
    private RecyclerView rcvUserManage;

    private QuanLyUserAdapter userAdapter;
    private List<User> mListUser;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_PERMISSION = 100;

    private Uri selectedImageUri;
    private String permission;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quanlyuser);

        init();

        userAdapter = new QuanLyUserAdapter(user -> {
            edtUsername.setText(user.getUsername());
            edtEmail.setText(user.getEmail());
            edtAddress.setText(user.getAddress());
            edtPhone.setText(user.getPhone());
            edtPassword.setText(user.getPassword());
            txtImgAvatar.setText("Ảnh đã chọn");
            txtImgAvatar.setTextColor(Color.BLUE);
        });

        rcvUserManage.setLayoutManager(new LinearLayoutManager(this));
        rcvUserManage.setAdapter(userAdapter);

        btnAddImg.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permission = Manifest.permission.READ_MEDIA_IMAGES;
            } else {
                permission = Manifest.permission.READ_EXTERNAL_STORAGE;
            }

            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                checkStoragePermission();
            }
        });

        btnAddUser.setOnClickListener(view -> addUser());
        btnUpdateUser.setOnClickListener(view -> updateUser());
        btnDeleteUser.setOnClickListener(view -> deleteUser());

        refreshData();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền để chọn ảnh!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                String internalImagePath = copyImageToInternalStorage(selectedImageUri);
                if (internalImagePath != null) {
                    txtImgAvatar.setText("Ảnh đã chọn");
                    txtImgAvatar.setTextColor(Color.BLUE);
                    selectedImageUri = Uri.parse(internalImagePath);
                }
            }
        }
    }

    private String copyImageToInternalStorage(Uri imageUri) {
        File imageFolder = new File(getFilesDir(), "images");
        if (!imageFolder.exists()) imageFolder.mkdirs();

        String fileName = UUID.randomUUID().toString() + ".jpg";
        File imageFile = new File(imageFolder, fileName);

        try (InputStream inputStream = getContentResolver().openInputStream(imageUri);
             FileOutputStream outputStream = new FileOutputStream(imageFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addUser() {
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(address) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(password) || selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(username, email, address, phone, selectedImageUri.toString(), password, false);

        executorService.execute(() -> {
            if (isUsernameExist(user)) {
                runOnUiThread(() -> Toast.makeText(this, "Người dùng đã tồn tại!", Toast.LENGTH_SHORT).show());
            } else {
                AppDatabase.getInstance(this).userDAO().insert(user);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Thêm người dùng thành công!", Toast.LENGTH_SHORT).show();
                    resetFormFields();
                    refreshData();
                });
            }
        });
    }

    private void updateUser() {
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(address) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(password) || selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            List<User> userList = AppDatabase.getInstance(this).userDAO().checkUsername(username);
            if (userList != null && !userList.isEmpty()) {
                User existingUser = userList.get(0);
                existingUser.setEmail(email);
                existingUser.setAddress(address);
                existingUser.setPhone(phone);
                existingUser.setPassword(password);
                existingUser.setAvatar(selectedImageUri.toString());

                AppDatabase.getInstance(this).userDAO().update(existingUser);

                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                String currentUsername = sharedPreferences.getString("username", "");
                if (username.equals(currentUsername)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", email);
                    editor.putString("avatar", selectedImageUri.toString());
                    editor.apply();
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Cập nhật người dùng thành công!", Toast.LENGTH_SHORT).show();
                    resetFormFields();
                    refreshData();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Người dùng không tồn tại!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void deleteUser() {
        String username = edtUsername.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Vui lòng nhập tên người dùng cần xóa", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            List<User> user = AppDatabase.getInstance(this).userDAO().checkUsername(username);
            if (user != null && !user.isEmpty()) {
                User userToDelete = user.get(0);

                AppDatabase.getInstance(this).userDAO().delete(userToDelete);

                File imageFile = new File(userToDelete.getAvatar());
                if (imageFile.exists()) imageFile.delete();

                runOnUiThread(() -> {
                    Toast.makeText(this, "Xóa người dùng thành công!", Toast.LENGTH_SHORT).show();
                    resetFormFields();
                    refreshData();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Người dùng không tồn tại!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void refreshData() {
        executorService.execute(() -> {
            mListUser = AppDatabase.getInstance(this).userDAO().getAllUser();
            runOnUiThread(() -> userAdapter.setData(mListUser));
        });
    }

    private void resetFormFields() {
        edtUsername.setText("");
        edtEmail.setText("");
        edtAddress.setText("");
        edtPhone.setText("");
        edtPassword.setText("");
        txtImgAvatar.setText("Chưa có ảnh");
        txtImgAvatar.setTextColor(Color.RED);
        selectedImageUri = null;
    }

    private void init() {
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.edtAddress);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnAddUser = findViewById(R.id.btnAddUser);
        btnUpdateUser = findViewById(R.id.btnUpdateUser);
        btnDeleteUser = findViewById(R.id.btnDeleteUser);
        btnAddImg = findViewById(R.id.btnChangeAvatar);
        txtImgAvatar = findViewById(R.id.txtImgAvatar);
        rcvUserManage = findViewById(R.id.rcvUserManage);
    }

    private boolean isUsernameExist(User user) {
        List<User> list = AppDatabase.getInstance(this).userDAO().checkUsername(user.getUsername());
        return list != null && !list.isEmpty();
    }
}
