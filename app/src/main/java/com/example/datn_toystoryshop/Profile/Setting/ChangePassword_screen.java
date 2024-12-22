package com.example.datn_toystoryshop.Profile.Setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datn_toystoryshop.Profile.Setting_screen;
import com.example.datn_toystoryshop.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword_screen extends AppCompatActivity {

    private TextView btnSave;
    private ImageView imgBack;
    private TextInputEditText edPassword, edRpPassword;
    private String documentId, phone, name, email;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_screen);

        // Ánh xạ
        btnSave = findViewById(R.id.btn_save);
        imgBack = findViewById(R.id.ivBack);
        edPassword = findViewById(R.id.edpassword);
        edRpPassword = findViewById(R.id.edrppassword);

        // Night mode icon
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);
        imgBack.setImageResource(nightMode ? R.drawable.back_icon : R.drawable.back_icon_1);

        // Quay lại màn hình trước
        imgBack.setOnClickListener(v -> onBackPressed());

        // Firestore
        db = FirebaseFirestore.getInstance();

        // Lấy documentId từ Intent
        Intent intent = getIntent();
        documentId = intent.getStringExtra("documentId");
        if (documentId == null) {
            Toast.makeText(this, "Không tìm thấy ID tài liệu.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lưu mật khẩu mới
        btnSave.setOnClickListener(v -> {
            String newPassword = edPassword.getText().toString().trim();
            String repeatPassword = edRpPassword.getText().toString().trim();

            if (newPassword.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(repeatPassword)) {
                Toast.makeText(this, "Mật khẩu không khớp.", Toast.LENGTH_SHORT).show();
                return;
            }

            saveDataToFirestore(newPassword);
        });
    }

    private void saveDataToFirestore(String newPassword) {
        DocumentReference docRef = db.collection("users").document(documentId);

        // Lấy mật khẩu đã mã hóa từ Firestore
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String hashedPassword = documentSnapshot.getString("password");

                if (hashedPassword != null && BCrypt.checkpw(newPassword, hashedPassword)) {
                    Toast.makeText(this, "Mật khẩu mới trùng với mật khẩu cũ.", Toast.LENGTH_SHORT).show();
                } else {
                    // Mã hóa mật khẩu mới
                    String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

                    // Cập nhật mật khẩu mới vào Firestore
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("email", email);
                    userData.put("name", name);
                    userData.put("phoneNumber", phone);
                    userData.put("password", newHashedPassword);

                    docRef.update(userData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Cập nhật mật khẩu thành công.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, Setting_screen.class);
                                intent.putExtra("documentId", documentId); // Truyền documentId
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                            })

                            .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi cập nhật mật khẩu.", Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(this, "Không tìm thấy tài khoản.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi tải dữ liệu.", Toast.LENGTH_SHORT).show());
    }
}
