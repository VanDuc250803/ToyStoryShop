package com.example.datn_toystoryshop.Register_login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datn_toystoryshop.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignUp_screen extends AppCompatActivity {

    private TextInputEditText edemail, edname, edpassword, edrppassword;
    private TextView btnsignup, txtLogin;
    private ImageView imgBack;
    private FirebaseAuth mAuth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edname = findViewById(R.id.edname);
        edemail = findViewById(R.id.edemail);
        edpassword = findViewById(R.id.edpassword);
        edrppassword = findViewById(R.id.edrppassword);
        btnsignup = findViewById(R.id.btnsignup);
        imgBack = findViewById(R.id.btnBack);
        txtLogin = findViewById(R.id.txtLogin);
        mAuth = FirebaseAuth.getInstance();
        imgBack.setOnClickListener(v -> onBackPressed());

        // Xử lý sự kiện nhấn nút đăng ký
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edname.getText().toString().trim();
                String input = edemail.getText().toString().trim();
                String password = edpassword.getText().toString().trim();
                String rppassword = edrppassword.getText().toString().trim();

                if (input.isEmpty()) {
                    Toast.makeText(SignUp_screen.this, getString(R.string.Toast_infor), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isPasswordValid(password)) {
                    Toast.makeText(SignUp_screen.this, getString(R.string.Toast_pass), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(rppassword)) {
                    Toast.makeText(SignUp_screen.this, getString(R.string.Toast_wrong_password), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isPhoneNumber(input)) {
                    // Đăng ký qua số điện thoại
                    sendVerificationCode(input);
                } else if (isValidEmail(input)) {
                    // Đăng ký qua email
                    registerWithEmail(input, password, name);
                } else {
                    Toast.makeText(SignUp_screen.this, getString(R.string.Toast_format), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Điều hướng sang màn hình đăng nhập
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp_screen.this, SignIn_screen.class);
                startActivity(intent);
            }
        });
    }

    // Kiểm tra định dạng email
    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailPattern);
    }


    // Kiểm tra định dạng số điện thoại
    private boolean isPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^0[3|5|7|8|9][0-9]{8}$");
    }

    // Kiểm tra độ mạnh mật khẩu
    private boolean isPasswordValid(String password) {
        return password.length() >= 6 && password.chars().anyMatch(Character::isUpperCase);
    }

    // Đăng ký bằng email
    private void registerWithEmail(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUp_screen.this, getString(R.string.signup_sdt), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp_screen.this, PhoneNumber_screen.class);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        intent.putExtra("name", name);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignUp_screen.this, getString(R.string.Toast_failure_signup), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Gửi mã OTP tới số điện thoại
    private void sendVerificationCode(String phoneNumber) {
        if (!phoneNumber.startsWith("+84")) {
            phoneNumber = "+84" + phoneNumber.substring(1);
        }

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Callbacks xử lý OTP
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            String code = credential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            //    Toast.makeText(SignUp_screen.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;

            Intent intent = new Intent(SignUp_screen.this, PhoneOTP_screen.class);
            intent.putExtra("verificationId", verificationId);
            intent.putExtra("name", edname.getText().toString().trim());
            intent.putExtra("phoneNumber", edemail.getText().toString().trim());
            intent.putExtra("password", edpassword.getText().toString().trim());
            startActivity(intent);
        }
    };

    // Xác minh mã OTP
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    // Đăng nhập bằng OTP
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String phoneNumber = mAuth.getCurrentUser().getPhoneNumber();
                        String password = getIntent().getStringExtra("password");
                        String name = getIntent().getStringExtra("name");
                        savePasswordToFirestore(phoneNumber, password, name);
                    } else {
                        Toast.makeText(SignUp_screen.this, getString(R.string.otp_verify), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Lưu thông tin người dùng vào Firestore
    private void savePasswordToFirestore(String phoneNumber, String password, String name) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("phoneNumber", phoneNumber);
        user.put("password", password);
        user.put("name", name);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    String randomID = documentReference.getId();
                    // Log.d("SignUp_screen", "ID ngẫu nhiên: " + randomID);
                    checkUserPasswordInFirestore(randomID);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignUp_screen.this, getString(R.string.Toast_failure_signup), Toast.LENGTH_SHORT).show();
                });
    }

    // Kiểm tra dữ liệu trong Firestore
    private void checkUserPasswordInFirestore(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String password = documentSnapshot.getString("password");
                        String name = documentSnapshot.getString("name");
                        // Xử lý dữ liệu
                    } else {
                        //  Log.d("CheckUser", "Tài liệu không tồn tại");
                    }
                })
                .addOnFailureListener(e -> {
                    //  Log.d("CheckUser", "Lỗi khi kiểm tra dữ liệu: " + e.getMessage());
                });
    }
}
