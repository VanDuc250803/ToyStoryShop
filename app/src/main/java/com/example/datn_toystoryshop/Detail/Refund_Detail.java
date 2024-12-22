package com.example.datn_toystoryshop.Detail;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datn_toystoryshop.Adapter.OrderHist_Detail_Adapter;
import com.example.datn_toystoryshop.Contact_support.Chat_contact;
import com.example.datn_toystoryshop.Model.LoadConfirm_Model;
import com.example.datn_toystoryshop.Model.Order_Model;
import com.example.datn_toystoryshop.Model.RefundResponse;
import com.example.datn_toystoryshop.Model.Refund_Model;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.RetrofitClient;
import com.example.datn_toystoryshop.history.History_purchase_screen;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Refund_Detail extends AppCompatActivity {

    private View view_circle_1, view_circle_2, view_circle_3, view2, view3;
    private ImageView imgBack;
    private TextView tv1, tv2, tv3, show_more_oder, refund_amount, date_refund, refund_ID, reason_for_return ;
    private RecyclerView recycler_view_refund;
    private TextView btnOrder_details,btnCancel_request, btnDiscuss_further;
    private String orderId, documentId,refundId;
    private APIService apiService;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;
    private String refundStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_detail);
        view_circle_1 = findViewById(R.id.view_circle_1);
        view_circle_2 = findViewById(R.id.view_circle_2);
        view_circle_3 = findViewById(R.id.view_circle_3);

        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        imgBack = findViewById(R.id.imgBack);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        show_more_oder = findViewById(R.id.show_more_oder);
        refund_amount = findViewById(R.id.refund_amount);
        date_refund = findViewById(R.id.date_refund);
        refund_ID = findViewById(R.id.refundID);
        reason_for_return = findViewById(R.id.reason_for_return);
        recycler_view_refund = findViewById(R.id.recycler_view_refund);
        btnOrder_details = findViewById(R.id.btnOrder_details);
        btnCancel_request = findViewById(R.id.btnCancel_request);
        btnDiscuss_further = findViewById(R.id.btnDiscuss_further);

        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);
        imgBack.setOnClickListener(v -> onBackPressed());
        if (nightMode) {
            imgBack.setImageResource(R.drawable.back_icon);
        } else {
            imgBack.setImageResource(R.drawable.back_icon_1);
        }
        apiService = RetrofitClient.getAPIService();
         orderId = getIntent().getStringExtra("orderId");
        refundId = getIntent().getStringExtra("refundId");
        documentId = getIntent().getStringExtra("documentId");
        fetchRefundDetails(refundId);
        loadOrderDetails(orderId);
        btnOrder_details.setOnClickListener(v -> {

            Intent intent1 = new Intent(Refund_Detail.this, OrderHist_Detail.class);
            intent1.putExtra("documentId", documentId);
            intent1.putExtra("orderId", orderId);
            startActivity(intent1);
            finish();
        });
        btnCancel_request.setOnClickListener(v -> {
            // Kiểm tra trạng thái refund
            if ("Chờ xác nhận".equals(refundStatus)) { // Thay "refundStatus" bằng biến lưu trạng thái thực tế
                String newStatus = "Đã giao"; // Trạng thái mới
                deliveredOrder(orderId, newStatus);
                String newStatus1 = "Hủy hoàn hàng"; // Trạng thái mới
                cancelRefund(refundId, newStatus1);
                Log.d("API", "bbbbbbbbbbbbbbb " + refundId);
                Intent intent1 = new Intent(Refund_Detail.this, History_purchase_screen.class);
                intent1.putExtra("documentId", documentId);
                startActivity(intent1);
            } else {
                // Hiển thị thông báo nếu trạng thái không phù hợp
                Toast.makeText(Refund_Detail.this, "Chỉ có thể hủy khi trạng thái là 'Chờ xác nhận'", Toast.LENGTH_SHORT).show();
            }
        });
        btnDiscuss_further.setOnClickListener(v ->{

            Intent intent1 = new Intent(Refund_Detail.this, Chat_contact.class);
            intent1.putExtra("documentId", documentId);

            startActivity(intent1);
            finish();
        });

    }
    private void fetchRefundDetails(String refundId) {
        apiService.getRefundByID(refundId).enqueue(new Callback<Refund_Model>() {
            @Override
            public void onResponse(Call<Refund_Model> call, Response<Refund_Model> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Refund_Model refund = response.body();
                        // Cập nhật giao diện
                        String fullRefundID = refund.get_id();
                        String displayRefundID = fullRefundID.length() > 6 ? fullRefundID.substring(0, 6) + "..." : fullRefundID;
                    refund_ID.setText(displayRefundID);
                    refund_ID.setOnClickListener(v -> {
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Refund ID", fullRefundID);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(Refund_Detail.this, "Copied: " + fullRefundID, Toast.LENGTH_SHORT).show();
                        });

                        String fullContent = refund.getContent();
                        String content1 = fullContent.contains(":") ? fullContent.split(":")[0].trim() : fullContent;
                        reason_for_return.setText(content1);
                        date_refund.setText(refund.getOrderRefundDate());
                        refundStatus = refund.getRefundStatus();

                        Log.d("API", "bbbbbbbbbbbbbbb " + refund.getRefundStatus());
                        if ("Chờ xác nhận".equals(refund.getRefundStatus())) {
                            // Đổi màu sang cam
                            view_circle_1.setBackgroundResource(R.drawable.circle_filled_orange);
                            tv1.setTextColor(Color.parseColor("#FFA500"));
                        }
                        if ("Đã xác nhận".equals(refund.getRefundStatus())) {
                            // Đổi màu sang cam
                            view_circle_1.setBackgroundResource(R.drawable.circle_filled_orange);
                            tv1.setTextColor(Color.parseColor("#FFA500"));
                            view_circle_2.setBackgroundResource(R.drawable.circle_filled_orange);
                            tv2.setTextColor(Color.parseColor("#FFA500"));
                            view2.setBackgroundColor(Color.parseColor("#FFA500"));
                        }
                        if ("Đã nhận hàng hoàn".equals(refund.getRefundStatus())) {
                            // Đổi màu sang cam
                            view_circle_1.setBackgroundResource(R.drawable.circle_filled_orange);
                            tv1.setTextColor(Color.parseColor("#FFA500"));
                            view_circle_2.setBackgroundResource(R.drawable.circle_filled_orange);
                            tv2.setTextColor(Color.parseColor("#FFA500"));
                            view2.setBackgroundColor(Color.parseColor("#FFA500"));
                            view_circle_3.setBackgroundResource(R.drawable.circle_filled_orange);
                            view3.setBackgroundColor(Color.parseColor("#FFA500"));
                            tv3.setTextColor(Color.parseColor("#FFA500"));
                        }

                    } else {
                        Log.e("API", "Không có dữ liệu hoàn hàng.");
                    }
                }

            @Override
            public void onFailure(Call<Refund_Model> call, Throwable t) {
                Log.e("API", "Lỗi khi gọi API: " + t.getMessage());
            }
        });
    }

    private void loadOrderDetails(String orderId) {
        // Gọi API để lấy thông tin sản phẩm
        apiService.getOrderById(orderId).enqueue(new Callback<Order_Model>() {
            @Override
            public void onResponse(Call<Order_Model> call, Response<Order_Model> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order_Model orderModel = response.body();

                    // Cập nhật thông tin đơn hàng
                    refund_amount.setText(String.format(" %,.0fđ", (double) orderModel.getRevenue_all()));

                }
            }

            @Override
            public void onFailure(Call<Order_Model> call, Throwable t) {
                // Xử lý khi gọi API thất bại
            }
        });
    }
    private void deliveredOrder(String orderId, String newStatus) {
        // Tạo model để gửi dữ liệu
        Order_Model orderModel = new Order_Model();
        orderModel.setOrderStatus(newStatus); // Thiết lập trạng thái đơn hàng mới

        // Gọi API qua Retrofit
        Call<Order_Model> call = apiService.putorderUpdate(orderId, orderModel);
        call.enqueue(new Callback<Order_Model>() {
            @Override
            public void onResponse(Call<Order_Model> call, Response<Order_Model> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Thành công, hiển thị kết quả
                    Toast.makeText(Refund_Detail.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    Log.d("API", "Cập nhật thành công: " + response.body().toString());
                } else {
                    // Xử lý lỗi trả về từ server
                    Toast.makeText(Refund_Detail.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                    Log.e("API", "Lỗi trả về: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Order_Model> call, Throwable t) {
                // Lỗi kết nối hoặc các lỗi khác
                Toast.makeText(Refund_Detail.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API", "Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    private void cancelRefund(String refundId, String newStatus) {
        // Tạo model để gửi dữ liệu
        Refund_Model refundModel = new Refund_Model();
        refundModel.setRefundStatus(newStatus); // Thiết lập trạng thái đơn hàng mới

        // Gọi API qua Retrofit
        Call<Refund_Model> call = apiService.putRefundUpdate(refundId, refundModel);
        call.enqueue(new Callback<Refund_Model>() {
            @Override
            public void onResponse(Call<Refund_Model> call, Response<Refund_Model> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Thành công, hiển thị kết quả
                    Toast.makeText(Refund_Detail.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    Log.d("API", "Cập nhật thành công: " + response.body().toString());
                } else {
                    // Xử lý lỗi trả về từ server
                    Toast.makeText(Refund_Detail.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                    Log.e("API", "Lỗi trả về: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Refund_Model> call, Throwable t) {
                // Lỗi kết nối hoặc các lỗi khác
                Toast.makeText(Refund_Detail.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API", "Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}