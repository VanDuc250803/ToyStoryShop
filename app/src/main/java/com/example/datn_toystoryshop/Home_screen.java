package com.example.datn_toystoryshop;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.datn_toystoryshop.Fragment.ArtStory_Fragment;
import com.example.datn_toystoryshop.Fragment.Browse_Fragment;
import com.example.datn_toystoryshop.Fragment.History_Fragment;
import com.example.datn_toystoryshop.Fragment.Home_Fragment;
import com.example.datn_toystoryshop.Fragment.Profile_Fragment;

import com.example.datn_toystoryshop.Server.WebSocketClient;
import com.example.datn_toystoryshop.Shopping.Favorite_screen;
import com.example.datn_toystoryshop.Shopping.Cart_screen;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home_screen extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    private TextView header_title;
    private ImageView heart_icon;
    private RelativeLayout cart_full_icon;

    private static final String CHANNEL_ID = "home_notification_channel";
    private static final String PREFS_NAME = "NotificationPrefs";
    private static final String NOTIFICATION_BLOCKED_KEY = "isNotificationBlocked";
    private WebSocketClient webSocketClient;

    private NotificationManager notificationManager;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNaviView);
        frameLayout = findViewById(R.id.fragmentLayout);
        header_title = findViewById(R.id.header_title);
        cart_full_icon = findViewById(R.id.cart_full_icon);
        heart_icon = findViewById(R.id.heart_icon);

        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);
        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        documentId = intent.getStringExtra("documentId");


        // Khởi tạo NotificationManager
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo Notification Channel nếu cần (dành cho Android 8.0 trở lên)
        createNotificationChannel();

        // Hiển thị thông báo chào mừng nếu thông báo đang được bật
        showWelcomeNotification();

        // Khởi tạo và bắt đầu WebSocketClient
        webSocketClient = new WebSocketClient(this, notificationManager, documentId);
        webSocketClient.start();


        // Truyền dữ liệu cho Fragment
        Profile_Fragment profileFragment = new Profile_Fragment();
        Home_Fragment homeFragment = new Home_Fragment();
        Bundle bundle = new Bundle();
        // Chỉ thêm vào Bundle nếu không null
        if (documentId != null) {
            bundle.putString("documentId", documentId);

        }

        homeFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentLayout, homeFragment)
                .commit();



        setupClickListeners();
        // Hiển thị Home_Fragment ngay khi vào màn hình Home
        loadFragment(new Home_Fragment(), bundle);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    loadFragment(new Home_Fragment(), bundle);
                } else if (itemId == R.id.nav_browse) {
                    loadFragment(new Browse_Fragment(), bundle);
                } else if (itemId == R.id.nav_history) {
                    loadFragment(new History_Fragment(), bundle);
                } else if (itemId == R.id.nav_ArtStory) {
                    loadFragment(new ArtStory_Fragment(), bundle);
                } else {
                    loadFragment(new Profile_Fragment(), bundle);
                }


                return true;
            }
        });
    }
    private void setupClickListeners() {
        header_title.setOnClickListener(v -> recreate());

        cart_full_icon.setOnClickListener(v -> navigateToScreen(Cart_screen.class));

        heart_icon.setOnClickListener(v -> navigateToScreen(Favorite_screen.class));
    }

    private void navigateToScreen(Class<?> targetScreen) {
        Intent intent = new Intent(Home_screen.this, targetScreen);
        intent.putExtra("documentId", documentId);
        startActivity(intent);
    }

    private void loadFragment(Fragment fragment, Bundle args) {
        if (args != null) {
            fragment.setArguments(args);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayout, fragment);
        fragmentTransaction.commit();

        // Cập nhật tiêu đề dựa trên Fragment
        if (fragment instanceof Home_Fragment) {
            header_title.setText(getString(R.string.app_name));
        } else if (fragment instanceof Browse_Fragment) {
            header_title.setText(getString(R.string.browse_menu));
        } else if (fragment instanceof History_Fragment) {
            header_title.setText(getString(R.string.history_menu));
        } else if (fragment instanceof ArtStory_Fragment) {
            header_title.setText(getString(R.string.artstory_menu));
        } else if (fragment instanceof Profile_Fragment) {
            header_title.setText(getString(R.string.profile_menu));
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Home Notification Channel";
            String description = "Channel for home screen notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Đăng ký channel với hệ thống
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showWelcomeNotification() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isNotificationBlocked = sharedPreferences.getBoolean(NOTIFICATION_BLOCKED_KEY, false);

        if (!isNotificationBlocked) {
            // Tạo thông báo chào mừng
            Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_logo) // Thay bằng icon của ứng dụng
                    .setContentTitle("Chào mừng!")
                    .setContentText("Cảm ơn bạn đã sử dụng ToyStory Shop.")
                    .setPriority(Notification.PRIORITY_DEFAULT);

            if (notificationManager != null) {
                notificationManager.notify(2, builder.build());
            }

        } else {
            // Thông báo bị tắt
            Toast.makeText(this, "Thông báo đang bị tắt.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Dừng WebSocketClient khi Activity bị hủy
        if (webSocketClient != null) {
            webSocketClient.stop();
        }
    }
}
