package com.example.datn_toystoryshop.Register_login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.datn_toystoryshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class Forgot_pass extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ImageView imgBack;
    private EditText edmail;
    private TextView btnsenemail;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        btnsenemail = findViewById(R.id.btnsenemail);
        edmail = findViewById(R.id.edmail);
        imgBack = findViewById(R.id.btnBack);
        mAuth = FirebaseAuth.getInstance();
        imgBack.setOnClickListener(v -> onBackPressed());

        btnsenemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = edmail.getText().toString();

                if (input.isEmpty()) {
                    Toast.makeText(Forgot_pass.this, getString(R.string.Toast_email), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Phân biệt giữa email và số điện thoại
                if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                    if (isValidEmail(input)) {
                        sendPasswordResetEmail(input); // Gửi email reset mật khẩu
                    } else {
                        Toast.makeText(Forgot_pass.this, getString(R.string.Toast_format), Toast.LENGTH_SHORT).show();
                    }
                } else if (input.matches("\\d+")) { // Trường hợp nhập số điện thoại
                    sendVerificationCode(input);   // Gửi mã OTP
                } else {
                    Toast.makeText(Forgot_pass.this, getString(R.string.Toast_failure), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Hàm gửi email đặt lại mật khẩu
    private void sendPasswordResetEmail(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Kiểm tra xem email có tồn tại trong Firestore hay không
        db.collection("users") // Thay "users" bằng tên collection của bạn
                .whereEqualTo("email", email) // Kiểm tra trường email
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Email tồn tại trong Firestore
                        mAuth.sendPasswordResetEmail(email)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(Forgot_pass.this, getString(R.string.Toast_send), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Forgot_pass.this, SignIn_screen.class);
                                        intent.putExtra("email", email);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(Forgot_pass.this, getString(R.string.Toast_send_error), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Email không tồn tại trong Firestore
                        Toast.makeText(Forgot_pass.this, getString(R.string.Toast_send_error), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Lỗi khi truy vấn Firestore
                    //   Toast.makeText(Forgot_pass.this, getString(R.string.Toast_firestore_error) + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    // Hàm gửi mã OTP qua số điện thoại
    private void sendVerificationCode(String phoneNumber) {
        // Thêm mã quốc gia (Việt Nam: +84)
        String countryCode = "+84";
        if (phoneNumber.startsWith("0")) {
            phoneNumber = phoneNumber.substring(1); // Loại bỏ số 0 đầu
        }
        String fullPhoneNumber = countryCode + phoneNumber;

        // Kiểm tra định dạng số điện thoại
        if (!fullPhoneNumber.matches("^\\+[1-9]\\d{1,14}$")) {
            Toast.makeText(Forgot_pass.this, getString(R.string.Toast_wrong), Toast.LENGTH_LONG).show();
            return;
        }

        // Cấu hình gửi mã OTP
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(fullPhoneNumber)  // Số điện thoại E.164
                .setTimeout(60L, TimeUnit.SECONDS) // Thời gian timeout
                .setActivity(this)                // Activity hiện tại
                .setCallbacks(mCallbacks)         // Callback xử lý kết quả
                .build();

        // Gửi mã OTP
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Callback xử lý khi gửi mã OTP
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // Xác minh tự động thành công, không cần nhập mã OTP
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Forgot_pass.this, getString(R.string.Toast_wrong_sdt), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Forgot_pass.this, SignIn_screen.class);
            startActivity(intent);
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationId = s; // Lưu mã OTP

            // Chuyển sang màn hình nhập OTP
            Intent intent = new Intent(Forgot_pass.this, ForgotOTP_screen.class);
            intent.putExtra("verificationId", verificationId);
            intent.putExtra("phoneNumber", edmail.getText().toString().trim());
            startActivity(intent);
        }
    };

    // Kiểm tra định dạng email
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
