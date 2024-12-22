package com.example.datn_toystoryshop.Adapter;

import static io.realm.Realm.getApplicationContext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.datn_toystoryshop.Model.Feeback_Model;
import com.example.datn_toystoryshop.Model.Order_Model;
import com.example.datn_toystoryshop.Model.Product_feedback;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.RetrofitClient;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private Context context;
    private List<Product_feedback> productList;
    private String documentId,textfeedback,prodId,dateFeed;
    private int rating;
    private APIService apiService;
    private FeedbackUpdateCallback feedbackUpdateCallback;
    public FeedbackAdapter(Context context, List<Product_feedback> productList, APIService apiService , String documentId, FeedbackUpdateCallback feedbackUpdateCallback) {
        this.context = context;
        this.productList = productList;
        this.documentId = documentId;
        this.apiService = apiService;
        this.feedbackUpdateCallback = feedbackUpdateCallback;
    }
    public interface FeedbackUpdateCallback {
        void onFeedbackSubmitted();
    }
    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feedback_all, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Product_feedback product = productList.get(position);
        // Hiển thị thông tin
        holder.tvProductName.setText(product.getNamePro());

        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float ratingValue, boolean fromUser) {
                rating = (int) ratingValue; // Cập nhật giá trị rating
            }
        });
        /////////////////////



        String cusId = documentId;



        ///////////////////////
        dateFeed = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date());
        // holder.tvFeedback.setText(product.getProdDetails().getProdId()); // Hoặc thông tin khác
        // Load hình ảnh nếu có
        if (!product.getImgPro().isEmpty()) {
            Glide.with(context)
                    .load(product.getImgPro().get(0))
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.imgProduct);
        }


        holder.btnRate.setOnClickListener(v -> {
            // Kiểm tra xem người dùng đã đánh giá sản phẩm chưa
            prodId = product.getProdId();
            checkFeedback(cusId, prodId, holder);
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvFeedback, btnRate;
        EditText edRemainingDays;
        RatingBar ratingBar;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvFeedback = itemView.findViewById(R.id.tvfeedback);
            edRemainingDays = itemView.findViewById(R.id.edRemainingDays);
            btnRate = itemView.findViewById(R.id.btnRate);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }



    //////////////////////////
    private void checkFeedback(String cusId, String prodId, FeedbackViewHolder holder) {
        apiService.checkFeedback(cusId, prodId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject result = response.body();
                    boolean hasFeedback = result.get("feedback").getAsBoolean();

                    if (hasFeedback) {
                        Toast.makeText(context, "Bạn đã đánh giá sản phẩm này.", Toast.LENGTH_SHORT).show();
                        holder.btnRate.setEnabled(false); // Vô hiệu hóa nút đánh giá
                        holder.ratingBar.setRating(0);
                    } else {
                        holder.btnRate.setEnabled(true); // Kích hoạt nút đánh giá
                        Toast.makeText(context, "Bạn đã đánh giá sản phẩm này11111.", Toast.LENGTH_SHORT).show();
                        textfeedback = holder.edRemainingDays.getText().toString(); // Lấy thông tin phản hồi
                        submitFeedback(holder); // Gửi đánh giá

                    }
                } else {
                    Log.e("APIError", "Không thể kiểm tra đánh giá: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("APIError", "Lỗi mạng khi kiểm tra đánh giá: " + t.getMessage());
            }
        });
    }
    private void submitFeedback(FeedbackViewHolder holder) {
        if (textfeedback == null || textfeedback.trim().isEmpty()) {
            textfeedback = null;
        }
        Log.d("FeedbackAdapter", "Feedbackyyyyyyyyyyyyyyyyyy: " + documentId);
        Log.d("FeedbackAdapter", "Feedbackyyyyyyyyyyyyyyyyyy: " + prodId);
        Log.d("FeedbackAdapter", "Feedbackyyyyyyyyyyyyyyyyyy: " + rating);
        Log.d("FeedbackAdapter", "Feedbackyyyyyyyyyyyyyyyyyy: " + textfeedback);
        Log.d("FeedbackAdapter", "Feedbackyyyyyyyyyyyyyyyyyy: " + dateFeed);
        // Tạo model phản ánh dữ liệu
        Feeback_Model feedback = new Feeback_Model(null, documentId, prodId, rating, textfeedback, dateFeed);

        // Gọi API
        Call<Feeback_Model> call = apiService.addFeedback(feedback);
        call.enqueue(new Callback<Feeback_Model>() {
            @Override
            public void onResponse(Call<Feeback_Model> call, Response<Feeback_Model> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Đánh giá đã được gửi!", Toast.LENGTH_SHORT).show();
                    // Gọi callback để cập nhật lại dữ liệu
                    if (feedbackUpdateCallback != null) {
                        feedbackUpdateCallback.onFeedbackSubmitted();
                    }
                    holder.ratingBar.setRating(0);
                    holder.tvFeedback.setText("Cảm ơn bạn đã đánh giá!");
                } else {
                    Log.e("APIError", "Error codeaaaaaaaaaaaaaaaaaaa: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(context, "Gửi thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Feeback_Model> call, Throwable t) {
                Toast.makeText(context, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
/////////////////////
}