package com.example.datn_toystoryshop.Profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datn_toystoryshop.Contact_support.Chat_contact;
import com.example.datn_toystoryshop.Contact_support.Email_contact;
import com.example.datn_toystoryshop.R;

public class ContactSupport_screen extends AppCompatActivity {
    TextView tvChat, tvPhone, tvEmail;
    private String documentId;
    ImageView imgBack;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_support_screen);

        tvChat = findViewById(R.id.tvChat);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        imgBack = findViewById(R.id.imgBackSp);

        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);
        documentId = getIntent().getStringExtra("documentId");
        Log.d("ContactSupport_screen", "Document ID received: " + documentId);
        if (nightMode) {
            imgBack.setImageResource(R.drawable.back_icon);
        } else {
            imgBack.setImageResource(R.drawable.back_icon_1);
        }
        tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "tel:0123987456";
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(phoneNumber));
                startActivity(intent);
            }
        });

        imgBack.setOnClickListener(v -> onBackPressed());

        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(ContactSupport_screen.this, Email_contact.class);
                emailIntent.putExtra("documentId", documentId);  // Truyền documentId sang Email_contact nếu cần
                startActivity(emailIntent);
            }
        });

        tvChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(ContactSupport_screen.this, Chat_contact.class);
                emailIntent.putExtra("documentId", documentId);  // Truyền documentId sang Email_contact nếu cần
                startActivity(emailIntent);
            }
        });
    }
}