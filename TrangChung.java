package com.example.oderfoodapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.oderfoodapp.database.AppDatabase;
import com.example.oderfoodapp.database.UserDAO;
import com.example.oderfoodapp.fragment.AdminFrag;
import com.example.oderfoodapp.fragment.CartFrag;
import com.example.oderfoodapp.fragment.ChangePWFrag;
import com.example.oderfoodapp.fragment.FavoriteFoodFrag;
import com.example.oderfoodapp.fragment.HistoryFrag;
import com.example.oderfoodapp.fragment.ManageAccFrag;
import com.example.oderfoodapp.fragment.ThongTinMonFrag;
import com.example.oderfoodapp.fragment.TrangChu;

import com.google.android.material.navigation.NavigationView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TrangChung extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerTrangChung;
    private NavigationView navView;

    //khai báo những const để xử lý hàm if else phía dưới
    private final int FRAG_TRANG_CHU = 0;
    private final int FRAG_MANAGE_ACC = 1;
    private final int FRAG_CHANGE_PW = 2;
    private final int FRAG_HISTORY = 3;
    private final int FRAG_CART = 4;
    private final int FRAG_FAVORITE = 5;
    private final int FRAG_ADMIN = 6;

    private int currentFrag = FRAG_TRANG_CHU;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean isAdmin = false;

    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_AVATAR = "avatar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.trang_chung);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerTrangChung = findViewById(R.id.drawer_TrangChu);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerTrangChung, toolbar, R.string.nav_open, R.string.nav_close);
        drawerTrangChung.addDrawerListener(toggle);
        toggle.syncState();

        navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);


        // Lấy Header View từ NavigationView và cập nhật ảnh và thông tin ngay lần đầu tiên
        updateThongTinNavHeader();

        // Kiểm tra quyền admin từ cơ sở dữ liệu
        checkAdmin();

        //trang chủ sẽ chạy đầu tiên khi đăng nhập thành công
        replaceFrag(new TrangChu(), "Trang chủ");
        navView.getMenu().findItem(R.id.item_trang_chu).setChecked(true);


        // Sử dụng OnBackPressedDispatcher để xử lý nút Back
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Xử lý Back tùy theo logic của bạn
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack(); // Quay lại Fragment trước đó
                } else {
                    finish(); // Thoát ứng dụng nếu không còn Fragment
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        // Sử dụng WindowInsets để điều chỉnh padding cho NavigationView
        ViewCompat.setOnApplyWindowInsetsListener(navView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Cài đặt padding để NavigationView không đè lên thanh trạng thái
            v.setPadding(0, systemBars.top, 0, 0); // Chỉ đặt padding trên cho thanh trạng thái
            return insets;
        });

    }

    // check quyền user
    private void checkAdmin() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString(KEY_USERNAME, "Guest");

        executorService.execute(() -> {
            UserDAO userDAO = AppDatabase.getInstance(TrangChung.this).userDAO();
            isAdmin = userDAO.checkQuyenUser(username);

            runOnUiThread(() -> {
                if (!isAdmin) {
                    navView.getMenu().findItem(R.id.item_admin).setVisible(false);
                }
            });
        });
    }

    // Phương thức cập nhật ảnh đại diện và thông tin của Header Navigation
    private void updateThongTinNavHeader() {
        View headerView = navView.getHeaderView(0);
        ImageView headerImage = headerView.findViewById(R.id.imgAnhDaiDien);
        TextView headerUsername = headerView.findViewById(R.id.header_username);
        TextView headerEmail = headerView.findViewById(R.id.header_email);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString(KEY_USERNAME, "Guest");
        String email = sharedPreferences.getString(KEY_EMAIL, "guest@example.com");
        String avatarUri = sharedPreferences.getString(KEY_AVATAR, null);

        headerUsername.setText(username);
        headerEmail.setText(email);

        if (avatarUri != null && !avatarUri.isEmpty()) {
            headerImage.setImageURI(null);  // Đặt null trước khi cập nhật ảnh để đảm bảo làm mới
            headerImage.setImageURI(Uri.parse(avatarUri));
            headerImage.invalidate();  // Buộc ImageView làm mới
        } else {
            headerImage.setImageResource(R.drawable.profile); // Ảnh mặc định
        }
    }

    //xửa lý logic các item trong menu toolbar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.item_trang_chu){
            if(currentFrag != FRAG_TRANG_CHU){
                replaceFrag(new TrangChu(), "Trang chủ");
                currentFrag = FRAG_TRANG_CHU;
            }
        }
        else if(id == R.id.item_manage_acc){
            if(currentFrag != FRAG_MANAGE_ACC){
                replaceFrag(new ManageAccFrag(), "Quản lý tài khoản");
                currentFrag = FRAG_MANAGE_ACC;
            }
        } else if (id == R.id.item_change_pw) {
            if(currentFrag != FRAG_CHANGE_PW){
                replaceFrag(new ChangePWFrag(), "Đổi mật khẩu");
                currentFrag = FRAG_CHANGE_PW;
            }
        } else if (id == R.id.item_history) {
            if(currentFrag != FRAG_HISTORY){
                replaceFrag(new HistoryFrag(), "Lịch sử mua");
                currentFrag = FRAG_HISTORY;
            }
        } else if (id == R.id.item_cart) {
            if(currentFrag != FRAG_CART){
                replaceFrag(new CartFrag(), "Giỏ hàng");
                currentFrag = FRAG_CART;
            }
        } else if (id == R.id.item_favorite) {
            if(currentFrag != FRAG_FAVORITE){
                replaceFrag(new FavoriteFoodFrag(), "Yêu thích");
                currentFrag = FRAG_FAVORITE;
            }
        } else if (id == R.id.item_admin) {
            if(currentFrag != FRAG_ADMIN){
                replaceFrag(new AdminFrag(), "Admin");
                currentFrag = FRAG_ADMIN;
            }
        } else if (id == R.id.item_logout) {
            // Đăng xuất: Xóa trạng thái đăng nhập và chuyển về trang đăng nhập
            logout();
        }

        drawerTrangChung.closeDrawer(GravityCompat.START);
        return true;
    }

    // phương thức đăng xuất, xóa phiên đăng nhập
    private void logout() {
        // Xóa phiên đăng nhập khỏi SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();  // Xóa tất cả các giá trị
        editor.apply();

        // Chuyển về trang đăng nhập
        Intent intent = new Intent(this, DangNhap.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    //lựa chọn các item frag để đưa vào phương thức if else trên
    public void replaceFrag(Fragment fragment, String title){
        FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction();
        fragTrans.replace(R.id.frame, fragment);
        fragTrans.addToBackStack(null);
        fragTrans.commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateThongTinNavHeader();
    }
}
