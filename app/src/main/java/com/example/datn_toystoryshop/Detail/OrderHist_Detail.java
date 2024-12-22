package com.example.datn_toystoryshop.Detail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.datn_toystoryshop.Adapter.OrderHist_Detail_Adapter;
import com.example.datn_toystoryshop.Contact_support.Chat_contact;
import com.example.datn_toystoryshop.Model.Order_Model;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.RetrofitClient;
import com.example.datn_toystoryshop.Profile.ContactSupport_screen;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHist_Detail extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvTotalPrice, tvOrderStatus, tvPaymentMethod, address_name, address_phone, address_detail;
    private ImageView imgBack;
    private LinearLayout ivContactShop, ivSupportCenter;
    private APIService apiService;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;
    private RecyclerView rvProductList;
    private OrderHist_Detail_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_hist_detail);

        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        address_name = findViewById(R.id.address_name);
        address_phone = findViewById(R.id.address_phone);
        address_detail = findViewById(R.id.address_detail);
        ivContactShop = findViewById(R.id.ivContactShop);
        rvProductList = findViewById(R.id.rvProductList);
        ivSupportCenter = findViewById(R.id.ivSupportCenter);
        imgBack = findViewById(R.id.btnBack);

        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);
        apiService = RetrofitClient.getAPIService();

        Intent intent = getIntent();
        String orderId = intent.getStringExtra("orderId");
        String documentId = intent.getStringExtra("documentId");
        Log.e("OrderHistoryAdapter", "j66666666666666666gggg " + orderId);
        loadOrderDetails(orderId);
        rvProductList.setLayoutManager(new LinearLayoutManager(this));

        ivSupportCenter.setOnClickListener(v -> {
            Intent intent1 = new Intent(OrderHist_Detail.this, Chat_contact.class);
            startActivity(intent1);
            finish();
        });
        ivContactShop.setOnClickListener(v -> {
            Intent intent1 = new Intent(OrderHist_Detail.this, ContactSupport_screen.class);
            startActivity(intent1);
            finish();
        });

        imgBack.setOnClickListener(v -> onBackPressed());
        if (nightMode) {
            imgBack.setImageResource(R.drawable.back_icon);
        } else {
            imgBack.setImageResource(R.drawable.back_icon_1);
        }
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadOrderDetails(orderId); // Gọi lại API để làm mới danh sách
        });
    }
    private void loadOrderDetails(String orderId) {
        // Gọi API để lấy thông tin sản phẩm
        apiService.getOrderById(orderId).enqueue(new Callback<Order_Model>() {
            @Override
            public void onResponse(Call<Order_Model> call, Response<Order_Model> response) {
                if (response.isSuccessful() && response.body() != null) {
                    swipeRefreshLayout.setRefreshing(false);
                    Order_Model orderModel = response.body();

                    // Cập nhật thông tin đơn hàng
                    tvOrderStatus.setText(orderModel.getOrderStatus());
                    tvPaymentMethod.setText(orderModel.getPayment_method());
                    address_name.setText(orderModel.getName_order());
                    address_phone.setText(orderModel.getPhone_order());
                    address_detail.setText(orderModel.getAddress_order());

                    tvTotalPrice.setText(String.format(" %,.0fđ", (double) orderModel.getRevenue_all()));


                    if (orderModel.getProdDetails() != null && !orderModel.getProdDetails().isEmpty()) {
                        APIService apiService = RetrofitClient.getAPIService();
                        adapter = new OrderHist_Detail_Adapter(OrderHist_Detail.this, orderModel.getProdDetails(),apiService);
                        rvProductList.setAdapter(adapter);
                    } else {
                        Log.e("OrderHist_Detail", "Danh sách sản phẩm rỗng hoặc null");
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    Log.e("OrderHist_Detail", "Response thất bại hoặc body null");
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<Order_Model> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                // Xử lý khi gọi API thất bại
            }
        });
    }
}