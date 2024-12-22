package com.example.datn_toystoryshop.Register_login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.datn_toystoryshop.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ForgotOTP_screen extends AppCompatActivity {
    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private String phoneNumber, password, verificationId;
    private TextView verifyButton;
    private FirebaseAuth mAuth;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_otp_screen);

        otp1 = findViewById(R.id.otpEditText1);
        otp2 = findViewById(R.id.otpEditText2);
        otp3 = findViewById(R.id.otpEditText3);
        otp4 = findViewById(R.id.otpEditText4);
        otp5 = findViewById(R.id.otpEditText5);
        otp6 = findViewById(R.id.otpEditText6);
        imgBack = findViewById(R.id.btnBack);
        verifyButton = findViewById(R.id.verifyButton);
        mAuth = FirebaseAuth.getInstance();

        verificationId = getIntent().getStringExtra("verificationId");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        password = getIntent().getStringExtra("password");

        // Thiết lập tính năng tự động chuyển tiếp giữa các ô nhập OTP
        setOtpMoveListener(otp1, otp2);
        setOtpMoveListener(otp2, otp3);
        setOtpMoveListener(otp3, otp4);
        setOtpMoveListener(otp4, otp5);
        setOtpMoveListener(otp5, otp6);

        // Xử lý khi nhấn nút "Quay lại"
        imgBack.setOnClickListener(v -> onBackPressed());

        // Xử lý khi nhấn nút "Xác minh"
        verifyButton.setOnClickListener(v -> {
            String code = otp1.getText().toString().trim() +
                    otp2.getText().toString().trim() +
                    otp3.getText().toString().trim() +
                    otp4.getText().toString().trim() +
                    otp5.getText().toString().trim() +
                    otp6.getText().toString().trim();

            if (code.isEmpty() || code.length() < 6) {
                Toast.makeText(ForgotOTP_screen.this, getString(R.string.otp_verify), Toast.LENGTH_SHORT).show();
                return;
            }
            verifyCode(code);
        });
    }

    // Hàm xác minh mã OTP
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    // Đăng nhập với thông tin xác thực OTP
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                saveUserDataToFirestore(); // Lưu dữ liệu người dùng vào Firestore sau khi xác minh thành công
            } else {
                Toast.makeText(ForgotOTP_screen.this, getString(R.string.otp_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Lưu dữ liệu người dùng vào Firestore
    private void saveUserDataToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Định dạng lại số điện thoại nếu cần
        if (phoneNumber.startsWith("0")) {
            phoneNumber = phoneNumber.replaceFirst("0", "+84");
        }

        Map<String, Object> user = new HashMap<>();
        user.put("phoneNumber", phoneNumber);
        user.put("password", password);

        // Kiểm tra số điện thoại trong Firestore và cập nhật nếu đã tồn tại
        db.collection("users").whereEqualTo("phoneNumber", phoneNumber).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                document.getReference().update(user).addOnSuccessListener(aVoid -> {
                    showUserInfoDialog(phoneNumber); // Hiển thị dialog để đặt mật khẩu mới
                }).addOnFailureListener(e -> {
                    Toast.makeText(ForgotOTP_screen.this, getString(R.string.Toast_wrong), Toast.LENGTH_SHORT).show();
                });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(ForgotOTP_screen.this, getString(R.string.Toast_wrong), Toast.LENGTH_SHORT).show();
        });
    }

    // Hiển thị dialog cho phép đặt lại mật khẩu
    private void showUserInfoDialog(String phoneNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotOTP_screen.this);
        builder.setTitle(getString(R.string.forgot_otp_title));

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_reset_password, null);
        builder.setView(dialogView);

        TextInputEditText newPasswordInput = dialogView.findViewById(R.id.newPasswordInput);
        TextView phoneNumberText = dialogView.findViewById(R.id.phoneNumberText);
        phoneNumberText.setText(phoneNumber);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newPassword = Objects.requireNonNull(newPasswordInput.getText()).toString().trim();
            if (!isPasswordValid(newPassword)) {
                Toast.makeText(ForgotOTP_screen.this, getString(R.string.Toast_pass), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.isEmpty()) {
                updatePasswordInFirestore(phoneNumber, newPassword);
                Intent intent = new Intent(ForgotOTP_screen.this, SignIn_screen.class);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("newPassword", newPassword);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(ForgotOTP_screen.this, getString(R.string.forgot_otp_pass), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // Kiểm tra tính hợp lệ của mật khẩu
    private boolean isPasswordValid(String password) {
        return password.length() >= 6 && password.chars().anyMatch(Character::isUpperCase);
    }

    // Cập nhật mật khẩu mới trong Firestore
    private void updatePasswordInFirestore(String phoneNumber, String newPassword) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        db.collection("users").whereEqualTo("phoneNumber", phoneNumber).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                document.getReference().update("password", hashedPassword).addOnSuccessListener(aVoid -> {
                    Toast.makeText(ForgotOTP_screen.this, getString(R.string.forgot_otp_success), Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(ForgotOTP_screen.this, getString(R.string.forgot_otp_error), Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(ForgotOTP_screen.this, getString(R.string.Toast_wrong_sdt), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Tự động chuyển tiếp giữa các ô nhập OTP
    private void setOtpMoveListener(final EditText currentOtp, final EditText nextOtp) {
        currentOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    nextOtp.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        currentOtp.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && currentOtp.getText().length() == 0) {
                currentOtp.clearFocus();
                nextOtp.requestFocus();
                return true;
            }
            return false;
        });
    }
}