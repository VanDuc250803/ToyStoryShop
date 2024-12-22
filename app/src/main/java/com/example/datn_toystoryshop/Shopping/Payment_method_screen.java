package com.example.datn_toystoryshop.Shopping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datn_toystoryshop.R;

public class Payment_method_screen extends AppCompatActivity {
    private RadioButton radioShopeePay;
    private TextView tvAgree;
    private LinearLayout layoutShopeePay;
    private String paytext = "";
    private SharedPreferences sharedPreferences;
    private boolean nightMode;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method_screen);

        imgBack = findViewById(R.id.imgBack);
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);
        if (nightMode) {
            imgBack.setImageResource(R.drawable.back_icon);
        } else {
            imgBack.setImageResource(R.drawable.back_icon_1);
        }
        imgBack.setOnClickListener(v -> onBackPressed());
       // radioCOD = findViewById(R.id.radioCOD);
        radioShopeePay = findViewById(R.id.radioShopeePay);
        tvAgree = findViewById(R.id.tvAgree);
        layoutShopeePay = findViewById(R.id.layoutShopeePay);
      //  layoutCOD = findViewById(R.id.layoutCOD);

        Intent intent = getIntent();
        String currentPayment = intent.getStringExtra("currentPayment");

        // Thiết lập RadioButton theo giá trị hiện tại
        if (currentPayment != null) {
//            if ("Thanh toán khi nhận hàng".equals(currentPayment)) {
//                radioCOD.setChecked(true);
//                radioShopeePay.setChecked(false);
//            } else
                if ("Ví VNPAY (Yêu thích)".equals(currentPayment)) {
                radioShopeePay.setChecked(true);
              //  radioCOD.setChecked(false);
            }
        }
//        layoutCOD.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Khi click vào layout, đánh dấu RadioButton là checked
//                radioCOD.setChecked(true);
//                radioShopeePay.setChecked(false);
//            }
//        });
        layoutShopeePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi click vào layout, đánh dấu RadioButton là checked
                radioShopeePay.setChecked(true);
             //   radioCOD.setChecked(false);
            }
        });
//        radioCOD.setOnClickListener(v -> {
//            radioCOD.setChecked(true);
//            radioShopeePay.setChecked(false);
//        });
        radioShopeePay.setOnClickListener(v -> {
            radioShopeePay.setChecked(true);
           // radioCOD.setChecked(false);
        });

        tvAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (radioCOD.isChecked()) {
//                    paytext = "Thanh toán khi nhận hàng";
//                } else
                    if (radioShopeePay.isChecked()) {
                    paytext = "Ví VNPAY";
                }
                Intent intent = new Intent();
                intent.putExtra("paytext", paytext);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}