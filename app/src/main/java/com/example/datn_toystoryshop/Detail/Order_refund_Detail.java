package com.example.datn_toystoryshop.Detail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datn_toystoryshop.Adapter.Order_Delivered_Product_Adapter;
import com.example.datn_toystoryshop.Model.Order_Model;
import com.example.datn_toystoryshop.Model.Refund_Model;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.RetrofitClient;
import com.example.datn_toystoryshop.history.History_purchase_screen;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Order_refund_Detail extends AppCompatActivity {
private ImageView imgBack;
private RecyclerView recycler_view_oder;
private Spinner serviceSpinner;
private TextView refund_amount,tvemailrefund, btnreturn,show_more_oder;
private EditText description;

    private SharedPreferences sharedPreferences;
    private boolean nightMode;
    private FirebaseFirestore db;
    private String documentId, email, orderId;
    private APIService apiService;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_refund_detail);

    imgBack = findViewById(R.id.imgBack);
    recycler_view_oder = findViewById(R.id.recycler_view_oder);
    serviceSpinner = findViewById(R.id.spService);
    refund_amount = findViewById(R.id.refund_amount);
    tvemailrefund = findViewById(R.id.tvemailrefund);
    btnreturn = findViewById(R.id.btnreturn);
    description = findViewById(R.id.description);
    show_more_oder = findViewById(R.id.show_more_oder);
    apiService = RetrofitClient.getAPIService();
    sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
    nightMode = sharedPreferences.getBoolean("night", false);

    if (nightMode) {
        imgBack.setImageResource(R.drawable.back_icon);
    } else {
        imgBack.setImageResource(R.drawable.back_icon_1);
    }
    imgBack.setOnClickListener(v -> {
        Intent intent1 = new Intent(Order_refund_Detail.this, History_purchase_screen.class);
        intent1.putExtra("documentId", documentId);
        startActivity(intent1);
    });
    documentId = getIntent().getStringExtra("documentId");
    orderId = getIntent().getStringExtra("orderId");
    Log.d("API Response", "bbbbbbbbbbbbbbbbbbbbbbbbb  : " + documentId);
    Log.d("API Response", "bbbbbbbbbbbbbbbbbbbbbbbbb1  : " + orderId);

    db = FirebaseFirestore.getInstance();
    loadUserDataByDocumentId(documentId);

    String[] services = {
            " - Chọn loại - ",
            "Thiếu hàng",
            "Người bán gửi sai hàng",
            "Hàng bể vỡ",
            "Hàng lỗi, không hoạt động",
            "Khác với mô tả",
            "Hàng đã qua sử dụng",
            "Hàng nhái,giả",
            "Hàng nguyên vẹn nhưng không còn nhu cầu",
    };
    // ArrayAdapter cho Spinner
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, services);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    serviceSpinner.setAdapter(adapter);

    // Sự kiện chọn Spinner
    serviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selectedService = parent.getItemAtPosition(position).toString();
            Log.d("API Response", "bbbbbbbbbbbbbbbbbbbbbbbbb2  : " + selectedService);
            Toast.makeText(Order_refund_Detail.this, "Bạn đã chọn: " + selectedService, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Không làm gì cả
        }
    });

    recycler_view_oder.setLayoutManager(new LinearLayoutManager(this));
    loadOrderProducts(orderId);

    btnreturn.setOnClickListener(v -> {
        addToRefund(orderId, documentId);
               deleteOrder(orderId,"Hoàn hàng");
        Intent intent1 = new Intent(Order_refund_Detail.this, History_purchase_screen.class);
        intent1.putExtra("documentId", documentId);
        startActivity(intent1);

    });

    }
    private void loadOrderProducts(String orderId) {
        // Gọi API để lấy thông tin đơn hàng
        apiService.getOrderById(orderId).enqueue(new Callback<Order_Model>() {
            @Override
            public void onResponse(Call<Order_Model> call, Response<Order_Model> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order_Model order = response.body();
                    List<Order_Model.ProductDetail> productDetails = order.getProdDetails(); // Lấy danh sách sản phẩm
                    refund_amount.setText(String.format(" %,.0fđ", (double) order.getRevenue_all()));
                    // Hiển thị tối đa 2 sản phẩm đầu tiên
                    boolean isMoreThanTwo = productDetails.size() > 2;
                    List<Order_Model.ProductDetail> displayProducts = isMoreThanTwo ? productDetails.subList(0, 2) : productDetails;

                    // Gán adapter cho RecyclerView
                    Order_Delivered_Product_Adapter adapter = new Order_Delivered_Product_Adapter(
                            Order_refund_Detail.this,
                            displayProducts,
                            apiService,
                            orderId,
                            documentId
                    );
                    recycler_view_oder.setAdapter(adapter);

                    // Xử lý nút "Xem thêm" nếu có nhiều hơn 2 sản phẩm
                    if (isMoreThanTwo) {
                        show_more_oder.setVisibility(View.VISIBLE);

                        final boolean[] isExpanded = {false};
                        show_more_oder.setOnClickListener(v -> {
                            if (isExpanded[0]) {
                                // Thu gọn về 2 sản phẩm
                                adapter.updateProductList(productDetails.subList(0, 2));
                                show_more_oder.setText("Xem thêm");
                            } else {
                                // Hiển thị toàn bộ sản phẩm
                                adapter.updateProductList(productDetails);
                                show_more_oder.setText("Thu gọn");
                            }
                            isExpanded[0] = !isExpanded[0];
                        });
                    } else {
                        show_more_oder.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(Order_refund_Detail.this, "Không tìm thấy dữ liệu đơn hàng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order_Model> call, Throwable t) {
                Toast.makeText(Order_refund_Detail.this, "Lỗi khi lấy dữ liệu: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void loadUserDataByDocumentId(String documentId) {
        DocumentReference docRef = db.collection("users").document(documentId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Lấy tất cả dữ liệu từ tài liệu
                    email = document.getString("email");
                    tvemailrefund.setText(email);

                } else {
                    Log.d("UserData", "No such document");
                }
            } else {
                Log.w("UserData", "get failed with ", task.getException());
            }
        });
    }

    private void addToRefund(String orderId, String cusId) {
        // Lấy nội dung từ Spinner và EditText
        String selectedService = serviceSpinner.getSelectedItem().toString();
        String descriptionText = description.getText().toString().trim();

        // Tạo nội dung lý do hoàn hàng
        String refundReason = selectedService + ": " + descriptionText;


        Refund_Model refundModel = new Refund_Model(
                null, // ID sẽ được backend tạo tự động
                orderId, // ID đơn hàng
                cusId, // ID khách hàng
                refundReason , // Nội dung lý do hoàn hàng
                String.valueOf(System.currentTimeMillis()), // Thời gian hoàn hàng
                "Chờ xác nhận" // Trạng thái mặc định là đang chờ xử lý
        );


        Call<Refund_Model> call = apiService.addToRefund(refundModel);
        call.enqueue(new Callback<Refund_Model>() {
            @Override
            public void onResponse(Call<Refund_Model> call, Response<Refund_Model> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Order_refund_Detail.this, "Thêm vào refund thành công!", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(Order_refund_Detail.this, "Không thể thêm refund", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Refund_Model> call, Throwable t) {
                Toast.makeText(Order_refund_Detail.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Order_refund_Detail.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    Log.d("API", "Cập nhật thành công: " + response.body().toString());
                } else {
                    // Xử lý lỗi trả về từ server
                    Toast.makeText(Order_refund_Detail.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                    Log.e("API", "Lỗi trả về: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Order_Model> call, Throwable t) {
                // Lỗi kết nối hoặc các lỗi khác
                Toast.makeText(Order_refund_Detail.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API", "Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}