package com.example.datn_toystoryshop.Shopping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.datn_toystoryshop.Adapter.Cart_Adapter;
import com.example.datn_toystoryshop.Home_screen;
import com.example.datn_toystoryshop.Model.Cart_Model;
import com.example.datn_toystoryshop.Model.Product_Model;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart_screen extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imgBack;
    private String productId;
    private int owerId;
    private boolean statusPro;
    private double productPrice;
    private String desPro;
    private String creatDatePro;
    private int quantity;
    private String listPro;
    private ArrayList<String> productImg;
    private String productName;
    private int cateId;
    private String brand;
    private String favoriteId;
    private List<Product_Model> productList;
    private int currentQuantity;
    private String documentId;
    private String selectedColor;
    private RecyclerView recyclerViewCart;
    private Cart_Adapter cartAdapter;
    private CheckBox checkBoxSelectAll;
    private TextView TotalPayment, btnCheckout, tvDiscount, tvFreeShipping;
    private LinearLayout tvVoucher, Lldiscount, llnot;
    private double totalProductDiscount = 0;
    private double totalShipDiscount = 0;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;
    private List<Cart_Model> cartList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart_screen);
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        imgBack = findViewById(R.id.imgBackCart);
        llnot = findViewById(R.id.llnot);
        recyclerViewCart = findViewById(R.id.rvOrderHistory);
        checkBoxSelectAll = findViewById(R.id.checkBoxSelectAll);
        TotalPayment = findViewById(R.id.tvTotalPayment);
        btnCheckout = findViewById(R.id.btnCheckout);
        tvVoucher = findViewById(R.id.tvVoucher);
        tvDiscount = findViewById(R.id.tvDiscount);
        Lldiscount = findViewById(R.id.Lldiscount);
        tvFreeShipping = findViewById(R.id.tvFreeShipping);
        if (nightMode) {
            imgBack.setImageResource(R.drawable.back_icon);
        } else {
            imgBack.setImageResource(R.drawable.back_icon_1);
        }
// Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        documentId = intent.getStringExtra("documentId");

        productId = intent.getStringExtra("productId");
        owerId = intent.getIntExtra("owerId", -1);
        statusPro = intent.getBooleanExtra("statusPro", false);
        productPrice = intent.getDoubleExtra("productPrice", 0.0);
        desPro = intent.getStringExtra("desPro");
        creatDatePro = intent.getStringExtra("creatDatePro");
        quantity = intent.getIntExtra("quantity", 0);
        listPro = intent.getStringExtra("listPro");
        productImg = intent.getStringArrayListExtra("productImg");
        productName = intent.getStringExtra("productName");
        cateId = intent.getIntExtra("cateId", -1);
        brand = intent.getStringExtra("brand");
        favoriteId = intent.getStringExtra("favoriteId");

        // Nhận thêm thuộc tính currentQuantity, customerId, và productSpecification
        currentQuantity = intent.getIntExtra("currentQuantity", 1);
        selectedColor = intent.getStringExtra("selectedColor");

        imgBack.setOnClickListener(v -> onBackPressed());
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadCartProducts(); // Gọi lại API để làm mới danh sách
        });
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Cart_Model> selectedItems = cartAdapter.getSelectedItems();
                ArrayList<String> productIds = new ArrayList<>();

                // Duyệt qua danh sách sản phẩm đã chọn
                for (Cart_Model cart : selectedItems) {
                    productIds.add(cart.get_id());
                }
                if (productIds.isEmpty()) {
                    Toast.makeText(Cart_screen.this, "Vui lòng chọn ít nhất một sản phẩm trước khi thanh toán!", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d("CartScreen", "aaaaaaaaaaaa productIds: " + productIds.toString());

                    // Chuyển dữ liệu qua Oder_screen
                    Intent intent = new Intent(Cart_screen.this, Order_screen.class);
                    intent.putStringArrayListExtra("productIds", productIds);
                    intent.putExtra("documentId", documentId);

                    if (totalShipDiscount != 0) {
                        intent.putExtra("totalShipDiscount", totalShipDiscount);
                    }
                    if (totalProductDiscount != 0) {
                        intent.putExtra("totalProductDiscount", totalProductDiscount);
                    }
                    startActivity(intent);
                }
            }
        });


        tvVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedCount1 = cartAdapter.countSelectedItems();
                if (selectedCount1 != 0) {
                    Intent intent = new Intent(Cart_screen.this, Voucher_screen.class);
                    startActivityForResult(intent, 100);
                    return;
                } else {
                    Toast.makeText(getApplicationContext(), "Vui lòng chọn sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialize product list and add sample products
        productList = new ArrayList<>();


        // Set up RecyclerView with LinearLayoutManager and Adapter
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));

        checkBoxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (cartAdapter != null) {

                cartAdapter.updateTotalPayment(isChecked);
                cartAdapter.notifyDataSetChanged();
                updateCheckoutButton();
                // Nếu bỏ chọn tất cả, đặt tổng thành 0
                if (!isChecked) {
                    // Gọi phương thức trong Adapter để bỏ chọn tất cả các sản phẩm
                    cartAdapter.deselectAllItems();
                    updateCheckoutButton();
                    updateTotalPayment(0);
        Lldiscount.setVisibility(View.GONE);
                }
            }
        });
        loadCartProducts();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // Nhận dữ liệu từ màn hình Voucher
            totalProductDiscount = data.getDoubleExtra("totalProductDiscount", 0.0);
            totalShipDiscount = data.getDoubleExtra("totalShipDiscount", 0.0);

            // Kiểm tra và tính tổng giảm giá
            double discountAmount = 0.0;

            if (totalProductDiscount > 0 && totalShipDiscount > 0) {
                // Nếu cả hai đều có giá trị
                discountAmount = totalProductDiscount + totalShipDiscount;
            } else if (totalProductDiscount > 0) {
                // Nếu chỉ có sản phẩm giảm giá
                discountAmount = totalProductDiscount;
            } else if (totalShipDiscount > 0) {
                // Nếu chỉ có giảm giá vận chuyển
                discountAmount = totalShipDiscount;
            }

            // Cập nhật giá trị cho tvDiscount
            if (discountAmount > 0) {
                String formattedDiscount = "";
                if (discountAmount >= 1000) {
                    // Chia cho 1000 để hiển thị theo dạng nghìn đồng
                    int thousands = (int) (discountAmount );
                    formattedDiscount = String.format("%,.0fđ", (double) thousands);

                } else {
                    // Nếu giá trị dưới 1000, hiển thị giá trị trực tiếp
                    formattedDiscount = String.format("%,.0fđ", discountAmount);
                }
                Lldiscount.setVisibility(View.VISIBLE);
                tvDiscount.setText(formattedDiscount);
            } else {
                tvDiscount.setVisibility(View.GONE);  // Nếu không có giảm giá, ẩn TextView
                Lldiscount.setVisibility(View.GONE);
            }
        }
    }

    public void updateTotalPayment(double total) {
        TotalPayment.setText(String.format("%,.0fđ", total));

        updateCheckoutButton();
    }

    public void loadCartProducts() {
        String cusId = documentId;

        if (cusId == null || cusId.isEmpty()) {
            Log.e("FavoriteScreen", "cusId không được để trống");
            return;
        }
        APIService apiService = RetrofitClient.getAPIService();
        apiService.getCarts(cusId).enqueue(new Callback<List<Cart_Model>>() {
            @Override
            public void onResponse(Call<List<Cart_Model>> call, Response<List<Cart_Model>> response) {
                swipeRefreshLayout.setRefreshing(false); // Dừng hiệu ứng làm mới

                if (response.isSuccessful() && response.body() != null) {
                    List<Cart_Model> cartItems = response.body();

                    if (cartItems.isEmpty()) {
                        // Xử lý khi danh sách trống
                        Log.d("CartScreen", "Giỏ hàng trống");
                        tvVoucher.setEnabled(false);
                        checkBoxSelectAll.setEnabled(false);
                        btnCheckout.setEnabled(false);
                        recyclerViewCart.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        llnot.setVisibility(View.VISIBLE);

                    } else {
                        // Xử lý khi có dữ liệu
                        Log.d("CartScreen", "Cart items retrieved: " + cartItems.size());
                      //  emptyCartTextView.setVisibility(View.GONE); // Ẩn thông báo nếu có dữ liệu

                        // Tạo và thiết lập Cart_Adapter
                        cartAdapter = new Cart_Adapter(Cart_screen.this, cartItems, apiService, documentId);
                        cartAdapter.setOnCartDataChangeListener(() -> checkBoxSelectAll.setChecked(false));
                        recyclerViewCart.setAdapter(cartAdapter);

                        // Thiết lập ItemTouchHelper cho RecyclerView
                        setupItemTouchHelper();
                        swipeRefreshLayout.setRefreshing(false);
                        llnot.setVisibility(View.GONE);
                        recyclerViewCart.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerViewCart.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    llnot.setVisibility(View.VISIBLE);
                    tvVoucher.setEnabled(false);
                    checkBoxSelectAll.setEnabled(false);
                    btnCheckout.setEnabled(false);
                }
            }
            @Override
            public void onFailure(Call<List<Cart_Model>> call, Throwable t) {
                recyclerViewCart.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                llnot.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupItemTouchHelper() {
        // Tạo một ItemTouchHelper từ phương thức getItemTouchHelper() của Cart_Adapter
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(cartAdapter.getItemTouchHelper());
        itemTouchHelper.attachToRecyclerView(recyclerViewCart);
    }

    private void updateCheckoutButton() {

        int selectedCount = cartAdapter.countSelectedItems();

        if (selectedCount == 0) {
            btnCheckout.setText("Mua Hàng");
            tvFreeShipping.setText("Chọn hoặc nhập mã");
            tvFreeShipping.setBackground(null);
            tvFreeShipping.setTextColor(Color.parseColor("#D1D1D1"));
        } else {
            tvFreeShipping.setText("Miễn Phí Vận Chuyển");
            tvFreeShipping.setBackgroundResource(R.drawable.bg_free_shipping);
            tvFreeShipping.setTextColor(Color.parseColor("#00C853"));
            btnCheckout.setText("Mua Hàng (" + selectedCount + ")");
        }
    }


}