package com.example.datn_toystoryshop.Detail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.datn_toystoryshop.Adapter.OrderHist_Detail_Adapter;
import com.example.datn_toystoryshop.Contact_support.Chat_contact;
import com.example.datn_toystoryshop.Home_screen;
import com.example.datn_toystoryshop.Model.LoadConfirm_Model;
import com.example.datn_toystoryshop.Model.Order_Model;
import com.example.datn_toystoryshop.Model.Product_Model;
import com.example.datn_toystoryshop.Profile.ContactSupport_screen;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.RetrofitClient;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Order_Delivered_Detail extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvTotalPrice, tvOrderStatus, tvPaymentMethod, address_name, address_phone, address_detail,btncancelOrder;
    private ImageView imgBack;
    private LinearLayout ivContactShop, ivSupportCenter;
    private APIService apiService;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;
    private RecyclerView rvProductList;
    private OrderHist_Detail_Adapter adapter;
    // Biến toàn cục
    private List<LoadConfirm_Model> productStates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_delivered_detail);


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
        btncancelOrder = findViewById(R.id.btncancelOrder);

        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);
        apiService = RetrofitClient.getAPIService();

        Intent intent = getIntent();
        String orderId = intent.getStringExtra("orderId");
        String documentId = intent.getStringExtra("documentId");
        Log.d("API Response", "Số lượng đơn hàngggggggggggg Confirm_Product_Adapter  : " + documentId);
        loadOrderDetails(orderId);
        rvProductList.setLayoutManager(new LinearLayoutManager(this));

        ivSupportCenter.setOnClickListener(v -> {
            Intent intent1 = new Intent(Order_Delivered_Detail.this, Chat_contact.class);
            intent1.putExtra("documentId", documentId);
            startActivity(intent1);
            finish();
        });
        ivContactShop.setOnClickListener(v -> {
            Intent intent1 = new Intent(Order_Delivered_Detail.this, ContactSupport_screen.class);
            intent1.putExtra("documentId", documentId);
            startActivity(intent1);
            finish();
        });
        btncancelOrder.setOnClickListener(v -> {
  Intent intent1 = new Intent(Order_Delivered_Detail.this, Order_refund_Detail.class);
            intent1.putExtra("documentId", documentId);
            intent1.putExtra("orderId", orderId);
  startActivity(intent1);
            finish();
        });

        imgBack.setOnClickListener(v -> onBackPressed());
        if (nightMode) {
            imgBack.setImageResource(R.drawable.back_icon);
        } else {
            imgBack.setImageResource(R.drawable.back_icon_1);
        }
        imgBack.setOnClickListener(v -> onBackPressed());
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadOrderDetails(orderId); // Gọi lại API để làm mới danh sách
        });
    }
    private void fetchProductById(String prodId) {
        apiService.getProductById(prodId).enqueue(new Callback<Product_Model>() {
            @Override
            public void onResponse(Call<Product_Model> call, Response<Product_Model> response) {

                if (response.isSuccessful() && response.body() != null) {
                    int quantity1 = response.body().getQuantity();
                    for (LoadConfirm_Model state : productStates) {
                        if (state.getProductId().equals(prodId)) {
                            state.setQuantity1(quantity1);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Product_Model> call, Throwable t) {
                Log.e("Queue Processing", "Network Error: " + t.getMessage());
            }
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
                        productStates.clear();
                        for (Order_Model.ProductDetail productDetail : orderModel.getProdDetails()) {
                            String productId = productDetail.getProdId();
                            int quantity = productDetail.getQuantity();
                            productStates.add(new LoadConfirm_Model(productId, quantity, 0)); // 0 là placeholder cho quantity1

                            // Gọi hàm fetchProductById để lấy quantity1
                            fetchProductById(productId);
                            Log.d("OrderDetails", "Product ID: " + productId + ", Quantity: " + quantity);

                            // Thực hiện các thao tác khác nếu cần
                        }
                        adapter = new OrderHist_Detail_Adapter(Order_Delivered_Detail.this, orderModel.getProdDetails(),apiService);
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



    public void updateProductItem(APIService apiService, String prodId, int quantity) {
        // Chuẩn bị dữ liệu cập nhật
        Product_Model productModel = new Product_Model();
        productModel.set_id(prodId);
        productModel.setQuantity(quantity);
        // Gọi API
        Call<Product_Model> call = apiService.putProductUpdate(prodId, productModel);
        call.enqueue(new Callback<Product_Model>() {
            @Override
            public void onResponse(Call<Product_Model> call, Response<Product_Model> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Order_Delivered_Detail.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Order_Delivered_Detail.this, "Cập nhật thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product_Model> call, Throwable t) {
                Toast.makeText(Order_Delivered_Detail.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deleteOrder(String orderId, String newStatus) {
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
                    Toast.makeText(Order_Delivered_Detail.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    Log.d("API", "Cập nhật thành công: " + response.body().toString());
                } else {
                    // Xử lý lỗi trả về từ server
                    Toast.makeText(Order_Delivered_Detail.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                    Log.e("API", "Lỗi trả về: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Order_Model> call, Throwable t) {
                // Lỗi kết nối hoặc các lỗi khác
                Toast.makeText(Order_Delivered_Detail.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API", "Lỗi kết nối: " + t.getMessage());
            }
        });
    }

}