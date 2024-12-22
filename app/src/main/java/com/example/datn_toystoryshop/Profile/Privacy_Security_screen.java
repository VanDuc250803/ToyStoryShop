package com.example.datn_toystoryshop.Profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.datn_toystoryshop.R;

public class Privacy_Security_screen extends AppCompatActivity {

    private CheckBox checkboxAgree;
    private TextView btnAccept;
    private TextView tvPrivacy;
    private ImageView imgBack;
    private static final String PREFS_NAME = "PrivacyPrefs";
    private static final String KEY_CHECKBOX_AGREE = "checkbox_agree_privacy";
    private boolean isAccepted = false;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_security_screen);


        checkboxAgree = findViewById(R.id.checkbox_agree_privacy);
        btnAccept = findViewById(R.id.btn_accept_privacy);
        tvPrivacy = findViewById(R.id.tvPrivacy);
        imgBack = findViewById(R.id.btnBack_privacy);
        if (nightMode) {
            imgBack.setImageResource(R.drawable.back_icon);
        } else {
            imgBack.setImageResource(R.drawable.back_icon_1);
        }
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkboxAgree.setChecked(false);
                btnAccept.setEnabled(false);

                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean(KEY_CHECKBOX_AGREE, false);
                editor.apply();

                onBackPressed();
            }
        });

        tvPrivacy.setText(getString(R.string.privacy_content));

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isChecked = preferences.getBoolean(KEY_CHECKBOX_AGREE, false);
        checkboxAgree.setChecked(isChecked);
        btnAccept.setEnabled(isChecked);

        if (isChecked) {
            btnAccept.setBackgroundTintList(getResources().getColorStateList(R.color.accept_enabled_color)); // Đổi màu nút Accept thành màu xanh #001A61
        } else {
            btnAccept.setBackgroundTintList(getResources().getColorStateList(R.color.gray)); // Nếu chưa đồng ý, đặt màu xám
        }

        checkboxAgree.setOnCheckedChangeListener((buttonView, isChecked1) -> {
            btnAccept.setEnabled(isChecked1);
            if (isChecked1) {
                // Đổi màu nút Accept thành màu #001A61 khi CheckBox được chọn
                btnAccept.setBackgroundTintList(getResources().getColorStateList(R.color.accept_enabled_color));
            } else {
                // Đổi lại màu về màu mặc định (ví dụ: màu xám nhạt) khi CheckBox không được chọn
                btnAccept.setBackgroundTintList(getResources().getColorStateList(R.color.gray));
            }
        });

        btnAccept.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_CHECKBOX_AGREE, checkboxAgree.isChecked());
            editor.apply();

            isAccepted = true;

            Toast.makeText(Privacy_Security_screen.this, getString(R.string.accepted_message_privacy), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        if (!isAccepted) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.confirm_exit_without_accepting_privacy))
                    .setPositiveButton(getString(R.string.exit_button_ter), (dialog, which) -> {
                        checkboxAgree.setChecked(false);
                        btnAccept.setEnabled(false);

                        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putBoolean(KEY_CHECKBOX_AGREE, false);
                        editor.apply();

                        super.onBackPressed();
                    })
                    .setNegativeButton(getString(R.string.stay_button_ter), null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isChecked = preferences.getBoolean(KEY_CHECKBOX_AGREE, false);
        Log.d("PrivacySecurityScreen", "On resume, checkbox checked: " + isChecked);
    }
}