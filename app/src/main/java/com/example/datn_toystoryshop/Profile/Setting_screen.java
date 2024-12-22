package com.example.datn_toystoryshop.Profile;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Profile.Setting.ChangePassword_screen;
import com.example.datn_toystoryshop.Profile.Setting.Currency_Language_screen;
import com.example.datn_toystoryshop.Profile.Setting.UpdateInfo_screen;

public class Setting_screen extends AppCompatActivity {

    private TextView tvUpdateInfo, tvChangePassword, tvNotifications, tvLanguageCurrency;
    private ImageView imgBack;
    private Switch switchDarkMode, switchNotif;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String documentId;
    private boolean nightMode;

    private static final String PREFS_NAME = "NotificationPrefs";
    private static final String NOTIFICATION_BLOCKED_KEY = "isNotificationBlocked";
    private NotificationManager notificationManager;
    private static final String CHANNEL_ID = "notification_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_screen);

        // Khởi tạo NotificationManager
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Kiểm tra và yêu cầu quyền thông báo nếu đang trên Android 13 hoặc cao hơn
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        } else {
            // Nếu phiên bản thấp hơn Android 13, không cần yêu cầu quyền
            createNotificationChannel();
        }

        // Initialize Views
        tvUpdateInfo = findViewById(R.id.tv_update_info);
        tvChangePassword = findViewById(R.id.tv_change_password);
        tvNotifications = findViewById(R.id.tv_notifications);
//        switchDarkMode = findViewById(R.id.switch_dark_mode);
        tvLanguageCurrency = findViewById(R.id.tv_languagecurrency);
        imgBack = findViewById(R.id.btnBack);
        switchNotif = findViewById(R.id.switch_notif);

        Intent intent = getIntent();
        documentId = intent.getStringExtra("documentId");

        // SharedPreferences setup for dark mode toggle
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);
        editor = sharedPreferences.edit();

//        if (nightMode) {
//            switchDarkMode.setChecked(true);
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            imgBack.setImageResource(R.drawable.back_icon);
//        } else {
//            imgBack.setImageResource(R.drawable.back_icon_1);
//        }
//        switchDarkMode.setOnClickListener(v -> {
//            if (switchDarkMode.isChecked()) {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                editor.putBoolean("night", true);
//                Toast.makeText(this, getString(R.string.dark_mode_enabled_set) + " " + getString(R.string.dark_mode_on_set), Toast.LENGTH_SHORT).show();
//            } else {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                editor.putBoolean("night", false);
//                Toast.makeText(this, getString(R.string.dark_mode_enabled_set) + " " + getString(R.string.dark_mode_off_set), Toast.LENGTH_SHORT).show();
//            }
//            editor.apply();
//        });

        setOnClickListener(tvLanguageCurrency, Currency_Language_screen.class, documentId);
        setOnClickListener(tvUpdateInfo, UpdateInfo_screen.class, documentId);
        setOnClickListener(tvChangePassword, ChangePassword_screen.class, documentId);
        imgBack.setOnClickListener(v -> onBackPressed());

        // Load trạng thái chặn thông báo từ SharedPreferences và đặt trạng thái cho Switch
        SharedPreferences notificationPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isNotificationBlocked;

        if (!notificationPrefs.contains(NOTIFICATION_BLOCKED_KEY)) {
            // Lần đầu chạy ứng dụng, mặc định bật thông báo
            SharedPreferences.Editor notifEditor = notificationPrefs.edit();
            notifEditor.putBoolean(NOTIFICATION_BLOCKED_KEY, false); // Thông báo bật
            notifEditor.apply();
            isNotificationBlocked = false;
        } else {
            // Lấy trạng thái từ SharedPreferences
            isNotificationBlocked = notificationPrefs.getBoolean(NOTIFICATION_BLOCKED_KEY, false);
        }

        // Cập nhật trạng thái Switch và TextView
        switchNotif.setChecked(!isNotificationBlocked);
        updateNotificationText(!isNotificationBlocked);

        // Xử lý sự kiện khi người dùng bật/tắt Switch
        switchNotif.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor notifEditor = notificationPrefs.edit();
            notifEditor.putBoolean(NOTIFICATION_BLOCKED_KEY, !isChecked);
            notifEditor.apply();

            updateNotificationText(isChecked);

            if (isChecked) {
                Toast.makeText(this, "Thông báo đã bật", Toast.LENGTH_SHORT).show();
            } else {
                cancelAllNotifications();
                Toast.makeText(this, "Thông báo đã tắt", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnClickListener(View view, Class<?> targetClass, @Nullable String documentId) {
        view.setOnClickListener(v -> {
            Intent intent = new Intent(Setting_screen.this, targetClass);
            if (documentId != null) {
                intent.putExtra("documentId", documentId);
            }
            startActivity(intent);
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification Channel";
            String description = "Channel for notification toggle";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Đăng ký channel với hệ thống
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void cancelAllNotifications() {
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    private void updateNotificationText(boolean isNotificationEnabled) {
        tvNotifications.setText(isNotificationEnabled ? "Bật thông báo" : "Tắt thông báo");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Đồng bộ trạng thái Switch và TextView
        SharedPreferences notificationPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isNotificationBlocked = notificationPrefs.getBoolean(NOTIFICATION_BLOCKED_KEY, false);
        switchNotif.setChecked(!isNotificationBlocked);
        updateNotificationText(!isNotificationBlocked);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createNotificationChannel();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền thông báo để sử dụng tính năng này.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
