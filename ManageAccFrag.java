package com.example.oderfoodapp.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.oderfoodapp.R;
import com.example.oderfoodapp.database.AppDatabase;
import com.example.oderfoodapp.object.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManageAccFrag extends Fragment {

    private static final int REQUEST_PERMISSION = 100;

    private EditText edtUsername, edtEmail, edtAddress, edtPhone;
    private ImageView imgAvatar;
    private TextView txtImgAvatarQL;
    private Button btnChangeAvatar, btnSave;
    private SharedPreferences sharedPreferences;
    private String currentUsername;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    public ManageAccFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manage_acc_frag, container, false);

        // Ánh xạ
        edtUsername = view.findViewById(R.id.edtUsernameQL);
        edtEmail = view.findViewById(R.id.edtEmailQL);
        edtAddress = view.findViewById(R.id.edtAddressQL);
        edtPhone = view.findViewById(R.id.edtPhoneQL);
        imgAvatar = view.findViewById(R.id.imgAnhDaiDien);
        btnChangeAvatar = view.findViewById(R.id.btnChangeAvatar);
        btnSave = view.findViewById(R.id.btnSaveQL);
        txtImgAvatarQL = view.findViewById(R.id.txtImgAvatarQL);

        sharedPreferences = getActivity().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", "");

        // Khởi tạo ActivityResultLauncher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        String savedImagePath = saveImageToInternalStorage(selectedImageUri);
                        if (savedImagePath != null) {
                            imgAvatar.setImageURI(Uri.parse(savedImagePath));
                            txtImgAvatarQL.setText("Ảnh đã chọn");
                            txtImgAvatarQL.setTextColor(Color.BLUE);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("avatar", savedImagePath);
                            editor.apply();
                        } else {
                            Toast.makeText(getContext(), "Không thể lưu ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        if (!TextUtils.isEmpty(currentUsername)) {
            loadUserData(currentUsername);
        }

        btnChangeAvatar.setOnClickListener(v -> checkPermissionAndPickImage());

        btnSave.setOnClickListener(v -> saveUserData());

        return view;
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSION);
            } else {
                openImagePicker();
            }
        } else {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            } else {
                openImagePicker();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        File imageFolder = new File(requireContext().getFilesDir(), "images");
        if (!imageFolder.exists()) imageFolder.mkdirs();

        String fileName = UUID.randomUUID().toString() + ".jpg";
        File imageFile = new File(imageFolder, fileName);

        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
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

    private void loadUserData(String username) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            User user = AppDatabase.getInstance(getContext()).userDAO().checkUser(username);
            if (user != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    edtUsername.setText(user.getUsername());
                    edtEmail.setText(user.getEmail());
                    edtAddress.setText(user.getAddress());
                    edtPhone.setText(user.getPhone());

                    String avatarUri = sharedPreferences.getString("avatar", "");
                    if (!TextUtils.isEmpty(avatarUri)) {
                        imgAvatar.setImageURI(Uri.parse(user.getAvatar()));
                    } else {
                        imgAvatar.setImageResource(R.drawable.profile);
                    }
                });
            }
        });
    }

    private void saveUserData() {
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            User user = AppDatabase.getInstance(getContext()).userDAO().checkUser(username);

            if (user == null) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Username không tồn tại", Toast.LENGTH_SHORT).show());
                }
                return;
            }

            user.setEmail(email);
            user.setAddress(address);
            user.setPhone(phone);

            String avatarUri = sharedPreferences.getString("avatar", "");
            if (!TextUtils.isEmpty(avatarUri)) {
                user.setAvatar(avatarUri);
            }

            AppDatabase.getInstance(getContext()).userDAO().update(user);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", email);
            editor.putString("address", address);
            editor.putString("phone", phone);
            editor.putString("avatar", avatarUri);
            editor.apply();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Thông tin đã được cập nhật", Toast.LENGTH_SHORT).show();
                    loadUserData(currentUsername);
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(getContext(), "Bạn cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData(currentUsername);
    }
}
