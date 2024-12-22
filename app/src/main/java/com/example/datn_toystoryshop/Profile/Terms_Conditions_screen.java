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

public class Terms_Conditions_screen extends AppCompatActivity {

    // Tên của SharedPreferences và key
    private static final String PREFS_NAME = "TermsPrefs";
    private static final String KEY_CHECKBOX_AGREE = "checkbox_agree";
    private CheckBox checkboxAgree;
    private TextView btnAccept;
    private TextView tvTerms;
    private ImageView imgBack;
    // Biến để kiểm tra người dùng đã nhấn Accept h ay chưa
    private boolean isAccepted = false;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions_screen);


        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);

        checkboxAgree = findViewById(R.id.checkbox_agree);
        btnAccept = findViewById(R.id.btn_accept);
        tvTerms = findViewById(R.id.tvTerms);
        imgBack = findViewById(R.id.btnBack);
        if (nightMode) {
            imgBack.setImageResource(R.drawable.back_icon);
        } else {
            imgBack.setImageResource(R.drawable.back_icon_1);
        }
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkboxAgree.setChecked(false);  // Reset CheckBox về không được chọn
                btnAccept.setEnabled(false);      // Tắt nút Accept

                // Lưu trạng thái mới vào SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean(KEY_CHECKBOX_AGREE, false); // Lưu trạng thái là chưa đồng ý
                editor.apply();

                // Hành động mặc định của nút Back
                onBackPressed(); // Gọi phương thức onBackPressed() để thực hiện hành động mặc định
            }
        });
        // Load điều khoản vào TextView từ strings.xml
        tvTerms.setText(getString(R.string.conditions_content_ter));

        // Lấy trạng thái của CheckBox từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isChecked = preferences.getBoolean(KEY_CHECKBOX_AGREE, false);
        checkboxAgree.setChecked(isChecked); // Set trạng thái cho CheckBox
        btnAccept.setEnabled(isChecked); // Nếu đã đồng ý thì nút Accept cũng được bật
        if (isChecked) {
            btnAccept.setBackgroundTintList(getResources().getColorStateList(R.color.accept_enabled_color)); // Đổi màu nút Accept thành màu xanh #001A61
        } else {
            btnAccept.setBackgroundTintList(getResources().getColorStateList(R.color.gray)); // Nếu chưa đồng ý, đặt màu xám
        }

        // Kiểm tra trạng thái của CheckBox khi người dùng thay đổi
        checkboxAgree.setOnCheckedChangeListener((buttonView, isChecked1) -> {
            // Nếu người dùng đã đồng ý thì nút Accept sẽ được bật
            btnAccept.setEnabled(isChecked1);
            if (isChecked1) {
                // Đổi màu nút Accept thành màu #001A61 khi CheckBox được chọn
                btnAccept.setBackgroundTintList(getResources().getColorStateList(R.color.accept_enabled_color));
            } else {
                // Đổi lại màu về màu mặc định (ví dụ: màu xám nhạt) khi CheckBox không được chọn
                btnAccept.setBackgroundTintList(getResources().getColorStateList(R.color.gray));
            }

        });

        // Xử lý khi người dùng nhấn nút Accept
        btnAccept.setOnClickListener(v -> {
            // Lưu trạng thái của CheckBox vào SharedPreferences khi người dùng nhấn nút Accept
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_CHECKBOX_AGREE, checkboxAgree.isChecked());
            editor.apply();

            // Đánh dấu rằng người dùng đã nhấn Accept
            isAccepted = true;

            Toast.makeText(Terms_Conditions_screen.this, getString(R.string.accepted_message_ter), Toast.LENGTH_SHORT).show();
            // Thực hiện các hành động khác sau khi chấp nhận điều khoản
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        if (!isAccepted) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.confirm_exit_without_accepting_ter))
                    .setPositiveButton(getString(R.string.exit_button_ter), (dialog, which) -> {
                        // Reset trạng thái của CheckBox nếu người dùng nhấn nút "Back" mà chưa Accept
                        checkboxAgree.setChecked(false);  // Reset CheckBox về không được chọn
                        btnAccept.setEnabled(false);      // Tắt nút Accept

                        // Lưu trạng thái mới vào SharedPreferences
                        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putBoolean(KEY_CHECKBOX_AGREE, false); // Lưu trạng thái là chưa đồng ý
                        editor.apply();

                        super.onBackPressed(); // Gọi super.onBackPressed() để thực hiện hành động mặc định của nút Back
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
        // Log trạng thái khi vào lại màn hình
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isChecked = preferences.getBoolean(KEY_CHECKBOX_AGREE, false);
        Log.d("TermsConditionsScreen", "On resume, checkbox checked: " + isChecked);
    }
}
