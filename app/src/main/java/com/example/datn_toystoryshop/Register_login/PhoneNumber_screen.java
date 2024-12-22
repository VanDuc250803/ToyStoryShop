package com.example.datn_toystoryshop.Register_login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datn_toystoryshop.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneNumber_screen extends AppCompatActivity {

    private String email, password, name, verificationId;
    private EditText edPhoneNumber;
    private Spinner spinnerCountryCode;
    private TextView btnNext;
    private FirebaseAuth mAuth;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        edPhoneNumber = findViewById(R.id.edtPhoneNumber);
        spinnerCountryCode = findViewById(R.id.spinnerCountryCode);
        btnNext = findViewById(R.id.btnNext);
        imgBack = findViewById(R.id.btnBack);
        mAuth = FirebaseAuth.getInstance();
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        // Xử lý sự kiện nút quay lại
        imgBack.setOnClickListener(v -> onBackPressed());

        // Xử lý sự kiện nút "Next"
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = edPhoneNumber.getText().toString().trim();
                String countryCode = spinnerCountryCode.getSelectedItem().toString();

                // Kiểm tra số điện thoại có được nhập hay không
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(PhoneNumber_screen.this, getString(R.string.Toast_infor), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gửi mã xác minh (OTP)
                sendVerificationCode(phoneNumber);
            }
        });
    }


    //Gửi mã OTP tới số điện thoại đã nhập
    private void sendVerificationCode(String phoneNumber) {
        String countryCode = spinnerCountryCode.getSelectedItem().toString();

        // Loại bỏ số 0 đầu tiên nếu có
        if (phoneNumber.startsWith("0")) {
            phoneNumber = phoneNumber.substring(1);
        }

        // Ghép mã quốc gia và số điện thoại
        String fullPhoneNumber = countryCode + phoneNumber;

        // Kiểm tra định dạng số điện thoại chuẩn E.164
        if (!fullPhoneNumber.matches("^\\+[1-9]\\d{1,14}$")) {
            Toast.makeText(PhoneNumber_screen.this, getString(R.string.Toast_wrong), Toast.LENGTH_LONG).show();
            return;
        }

        // Thiết lập và gửi yêu cầu xác minh số điện thoại
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(fullPhoneNumber)         // Số điện thoại chuẩn E.164
                .setTimeout(60L, TimeUnit.SECONDS)       // Thời gian chờ OTP
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    // Callback xử lý kết quả gửi mã OTP
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                // OTP được xác minh tự động
                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    // Không cần nhập OTP
                }

                // Gửi OTP thất bại
                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(PhoneNumber_screen.this, getString(R.string.send_otp_error), Toast.LENGTH_LONG).show();
                }

                // OTP đã được gửi thành công
                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    verificationId = s;

                    Intent intent = new Intent(PhoneNumber_screen.this, PhoneOTP_screen.class);
                    intent.putExtra("verificationId", verificationId);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    intent.putExtra("phoneNumber", edPhoneNumber.getText().toString().trim());
                    startActivity(intent);
                }
            };
}
