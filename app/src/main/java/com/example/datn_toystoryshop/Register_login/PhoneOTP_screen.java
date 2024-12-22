package com.example.datn_toystoryshop.Register_login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datn_toystoryshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class PhoneOTP_screen extends AppCompatActivity {

    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private String name, email, phoneNumber, password;
    private TextView verifyButton;
    private ImageView imgBack;
    private String verificationId;
    private FirebaseAuth mAuth;

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
        verifyButton = findViewById(R.id.verifyButton);
        imgBack = findViewById(R.id.btnBack);
        mAuth = FirebaseAuth.getInstance();
        verificationId = getIntent().getStringExtra("verificationId");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        password = getIntent().getStringExtra("password");

        // Thiết lập tự động chuyển tiếp giữa các ô nhập OTP
        setupOtpNavigation();

        // Xử lý nút "Quay lại"
        imgBack.setOnClickListener(v -> onBackPressed());
        // Xử lý khi nhấn nút "Xác minh"
        verifyButton.setOnClickListener(v -> verifyOtp());
    }

    // Thiết lập tự động chuyển tiếp giữa các ô nhập OTP
    private void setupOtpNavigation() {
        setOtpMoveListener(otp1, otp2);
        setOtpMoveListener(otp2, otp3);
        setOtpMoveListener(otp3, otp4);
        setOtpMoveListener(otp4, otp5);
        setOtpMoveListener(otp5, otp6);
    }

    // Kiểm tra mã OTP và xác minh
    private void verifyOtp() {
        String code = otp1.getText().toString().trim() +
                otp2.getText().toString().trim() +
                otp3.getText().toString().trim() +
                otp4.getText().toString().trim() +
                otp5.getText().toString().trim() +
                otp6.getText().toString().trim();

        if (code.isEmpty() || code.length() < 6) {
            Toast.makeText(this, getString(R.string.otp_verify), Toast.LENGTH_SHORT).show();
            return;
        }

        verifyCode(code);
    }

    // Xác minh mã OTP
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    // Đăng nhập và lưu thông tin người dùng vào Firestore sau khi xác minh thành công
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveUserDataToFirestore();
                    } else {
                        Toast.makeText(this, getString(R.string.otp_fail), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Lưu thông tin người dùng vào Firestore
    private void saveUserDataToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Chuyển số điện thoại về định dạng +84 nếu cần
        if (phoneNumber.startsWith("0")) {
            phoneNumber = phoneNumber.replaceFirst("0", "+84");
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("phoneNumber", phoneNumber);
        user.put("password", hashedPassword);
        user.put("name", name);

        // Thêm thông tin người dùng vào Firestore
        db.collection("users")
                .add(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, getString(R.string.Toast_success_sign), Toast.LENGTH_SHORT).show();
                    navigateToSignIn();
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Chuyển đến màn hình đăng nhập sau khi xác minh thành công
    private void navigateToSignIn() {
        Intent intent = new Intent(PhoneOTP_screen.this, SignIn_screen.class);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("password", password);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // Thiết lập tự động chuyển focus giữa các EditText nhập OTP
    private void setOtpMoveListener(final EditText currentOtp, final EditText nextOtp) {
        currentOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    nextOtp.requestFocus(); // Chuyển focus sang ô tiếp theo
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần xử lý
            }
        });
    }
}
