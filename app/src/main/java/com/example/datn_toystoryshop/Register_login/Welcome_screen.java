package com.example.datn_toystoryshop.Register_login;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datn_toystoryshop.R;

public class Welcome_screen extends AppCompatActivity {

    private static final int SPLASH_SCREEN_TIME_OUT = 3000; // 3 giây
    private View loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Tạo hiệu ứng load cho thanh trắng
         loadingBar = findViewById(R.id.loadingBar);
        ObjectAnimator animator = ObjectAnimator.ofFloat(loadingBar, "scaleX", 0f, 1f);
        animator.setDuration(SPLASH_SCREEN_TIME_OUT); // 3 giây
        animator.start();

        // Điều hướng đến màn hình chính sau khi load xong
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Welcome_screen.this, SignIn_screen.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN_TIME_OUT);
    }
}
