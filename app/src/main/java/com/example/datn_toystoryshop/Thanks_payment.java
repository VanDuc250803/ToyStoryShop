package com.example.datn_toystoryshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.datn_toystoryshop.Detail.OrderHist_Detail;
import com.example.datn_toystoryshop.Shopping.Cart_screen;

public class Thanks_payment extends AppCompatActivity {
    private ImageView btnBack, cart_full_icon;
    private TextView payment_status, btnHome_menu, btnPurchase_order;
    private String documentId, orderId;
    public static final String KEY_AMOUNT = "amount";
    private Handler inactivityHandler;
    private Runnable inactivityRunnable;
    private Long amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanks_payment);

        // Ánh xạ các view
        btnBack = findViewById(R.id.btnBack);
        cart_full_icon = findViewById(R.id.cart_full_icon);
        payment_status = findViewById(R.id.payment_status);
        btnHome_menu = findViewById(R.id.btnHome_menu);
        btnPurchase_order = findViewById(R.id.btnPurchase_order);

        // Lấy dữ liệu từ Intent
        documentId = getIntent().getStringExtra("documentId");
        amount = getIntent().getLongExtra(KEY_AMOUNT, 0);
        orderId = getIntent().getStringExtra("orderId");
        Log.e("OrderHistoryAdapter", "j66666666666666666Browse_Fragment    " + documentId);
        Log.e("OrderHistoryAdapter", "j66666666666666666Browse_Fragment     " + amount);
        Log.e("OrderHistoryAdapter", "j66666666666666666Browse_Fragment    " + orderId);
        payment_status.setText(String.format(" %,.0fđ", (double) amount));

        // Cài đặt các sự kiện click
        btnBack.setOnClickListener(v -> {
            navigateToHome();
        });

        cart_full_icon.setOnClickListener(v -> {
            Intent intent = new Intent(Thanks_payment.this, Cart_screen.class);
            intent.putExtra("documentId", documentId);
            startActivity(intent);
            finish();
        });

        btnHome_menu.setOnClickListener(v -> {
            navigateToHome();
        });

        btnPurchase_order.setOnClickListener(v -> {
            Intent intent = new Intent(Thanks_payment.this, OrderHist_Detail.class);
            intent.putExtra("documentId", documentId);
            intent.putExtra("orderId", orderId);
            startActivity(intent);
            finish();
        });

        // Khởi tạo Handler và Runnable
        inactivityHandler = new Handler(Looper.getMainLooper());
        inactivityRunnable = this::navigateToHome;

        // Bắt đầu đếm ngược 15 giây
        startInactivityTimer();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        // Reset timer nếu có bất kỳ tương tác nào
        resetInactivityTimer();
    }

    private void startInactivityTimer() {
        inactivityHandler.postDelayed(inactivityRunnable, 15000); // 15 giây
    }

    private void resetInactivityTimer() {
        inactivityHandler.removeCallbacks(inactivityRunnable);
        startInactivityTimer();
    }

    private void navigateToHome() {
        Intent intent = new Intent(Thanks_payment.this, Home_screen.class);
        intent.putExtra("documentId", documentId);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Loại bỏ Callback khi Activity bị hủy
        inactivityHandler.removeCallbacks(inactivityRunnable);
    }
}
