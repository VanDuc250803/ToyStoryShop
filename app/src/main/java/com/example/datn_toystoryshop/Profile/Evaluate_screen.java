package com.example.datn_toystoryshop.Profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datn_toystoryshop.Model.FeebackApp_Model;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.RetrofitClient;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Evaluate_screen extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText etFeedback;
    private TextView btnSubmit;
    private ImageView imgBack;
    private String feedback;
    private float rating;
    private String documentId;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate_screen);

        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);

        ratingBar = findViewById(R.id.ratingBar);
        etFeedback = findViewById(R.id.etFeedback);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgBack = findViewById(R.id.btnBack);
        Intent intent = getIntent();
        documentId = intent.getStringExtra("documentId");
        Log.e("OrderHistoryAdapter", "j66666666666666666Favorite_screen" + documentId);

        // Nút quay lại đăng nhập
        imgBack.setOnClickListener(v -> onBackPressed());
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });
        if (nightMode) {
            imgBack.setImageResource(R.drawable.back_icon);
        } else {
            imgBack.setImageResource(R.drawable.back_icon_1);
        }
    }

    private void submitFeedback() {
        rating = ratingBar.getRating();
        feedback = etFeedback.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, getString(R.string.star_rating_eva), Toast.LENGTH_SHORT).show();
            return;
        }

        FeebackApp_Model feebackAppModel = new FeebackApp_Model(null, documentId, rating, feedback, new Date().toString());

        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        Call<FeebackApp_Model> call = apiService.addToFeebackApp(feebackAppModel);

        call.enqueue(new Callback<FeebackApp_Model>() {
            @Override
            public void onResponse(Call<FeebackApp_Model> call, Response<FeebackApp_Model> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.thank_review_eva), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("API_ERROR", "Thêm đánh giá thất bại, mã phản hồi: " + response.code());
                    Toast.makeText(getApplicationContext(), "Thêm đánh giá thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FeebackApp_Model> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Không thể kết nối tới API", Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Lỗi kết nối API khi thêm đánh giá", t);
            }
        });

        // Reset lại các trường
        ratingBar.setRating(0);
        etFeedback.setText("");
    }

}