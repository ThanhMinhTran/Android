package com.example.oderfoodapp;

import android.Manifest;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.database.AppDatabase;
import com.example.oderfoodapp.object.Category;
import com.example.oderfoodapp.recyclerViewAdapter.QuanLyDanhMucAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuanLyDanhMuc extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_PERMISSION = 100;

    private EditText edtCategoryId, edtCategoryName;
    private Button btnAddImg, btnAdd, btnUpdate, btnDelete;
    private TextView txtImg;
    private RecyclerView rcvCategoryManage;

    private Uri selectedImageUri;
    private List<Category> mListCategory;
    private QuanLyDanhMucAdapter adapter;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quanlydanhmuc);

        initUI();

        adapter = new QuanLyDanhMucAdapter(category -> {
            edtCategoryId.setText(category.getCategoryID());
            edtCategoryName.setText(category.getName());
            txtImg.setText("Ảnh đã chọn");
            txtImg.setTextColor(Color.GREEN);
            selectedImageUri = Uri.parse(category.getImage());
        });

        rcvCategoryManage.setLayoutManager(new LinearLayoutManager(this));
        rcvCategoryManage.setAdapter(adapter);

        btnAddImg.setOnClickListener(v -> checkPermissionAndOpenGallery());
        btnAdd.setOnClickListener(v -> addCategory());
        btnUpdate.setOnClickListener(v -> updateCategory());
        btnDelete.setOnClickListener(v -> deleteCategory());

        refreshData();
    }

    private void initUI() {
        edtCategoryId = findViewById(R.id.edtDanhMucID);
        edtCategoryName = findViewById(R.id.edtTenDanhMuc);
        btnAddImg = findViewById(R.id.btnAddImgCategory);
        btnAdd = findViewById(R.id.btnAddCategory);
        btnUpdate = findViewById(R.id.btnUpdateCategory);
        btnDelete = findViewById(R.id.btnDeleteCategory);
        txtImg = findViewById(R.id.txtImgCategory);
        rcvCategoryManage = findViewById(R.id.rcvCategoryManage);
    }

    private void checkPermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSION);
            }
        } else {
            // Android 12 trở xuống
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            }
        }
    }
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            Toast.makeText(this, "Bạn cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            String path = copyImageToInternalStorage(uri);
            if (path != null) {
                selectedImageUri = Uri.parse(path);
                txtImg.setText("Ảnh đã chọn");
                txtImg.setTextColor(Color.GREEN);
            }
        }
    }

    private String copyImageToInternalStorage(Uri uri) {
        File folder = new File(getFilesDir(), "images");
        if (!folder.exists()) folder.mkdirs();

        String fileName = UUID.randomUUID().toString() + ".jpg";
        File imageFile = new File(folder, fileName);

        try (InputStream in = getContentResolver().openInputStream(uri);
             FileOutputStream out = new FileOutputStream(imageFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addCategory() {
        String id = edtCategoryId.getText().toString().trim();
        String name = edtCategoryName.getText().toString().trim();

        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(name) || selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Category category = new Category(id, name, selectedImageUri.toString());

        executorService.execute(() -> {
            if (AppDatabase.getInstance(this).categoryDAO().checkCategoryID(id).size() > 0) {
                runOnUiThread(() -> Toast.makeText(this, "Mã danh mục đã tồn tại!", Toast.LENGTH_SHORT).show());
            } else {
                AppDatabase.getInstance(this).categoryDAO().insert(category);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
                    resetForm();
                    refreshData();
                });
            }
        });
    }

    private void updateCategory() {
        String id = edtCategoryId.getText().toString().trim();
        String name = edtCategoryName.getText().toString().trim();

        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(name) || selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Category category = new Category(id, name, selectedImageUri.toString());

        executorService.execute(() -> {
            AppDatabase.getInstance(this).categoryDAO().update(category);
            runOnUiThread(() -> {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                resetForm();
                refreshData();
            });
        });
    }

    private void deleteCategory() {
        String id = edtCategoryId.getText().toString().trim();
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, "Nhập mã danh mục cần xóa", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            List<Category> list = AppDatabase.getInstance(this).categoryDAO().checkCategoryID(id);
            if (!list.isEmpty()) {
                Category toDelete = list.get(0);
                AppDatabase.getInstance(this).categoryDAO().delete(toDelete);

                File img = new File(toDelete.getImage());
                if (img.exists()) img.delete();

                runOnUiThread(() -> {
                    Toast.makeText(this, "Đã xóa danh mục!", Toast.LENGTH_SHORT).show();
                    resetForm();
                    refreshData();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Không tìm thấy danh mục!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void refreshData() {
        executorService.execute(() -> {
            mListCategory = AppDatabase.getInstance(this).categoryDAO().getAllCategory();
            runOnUiThread(() -> adapter.setData(mListCategory));
        });
    }

    private void resetForm() {
        edtCategoryId.setText("");
        edtCategoryName.setText("");
        txtImg.setText("Chưa có ảnh");
        txtImg.setTextColor(Color.RED);
        selectedImageUri = null;
    }
}
