package com.example.datn_toystoryshop.Detail;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.datn_toystoryshop.Adapter.Feedback_Adapter_Product;
import com.example.datn_toystoryshop.Adapter.Product_detail_Image_Adapter;
import com.example.datn_toystoryshop.Contact_support.Chat_contact;
import com.example.datn_toystoryshop.Model.Cart_Model;
import com.example.datn_toystoryshop.Model.Favorite_Model;
import com.example.datn_toystoryshop.Model.Feeback_Model;
import com.example.datn_toystoryshop.Model.Feeback_Rating_Model;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.RetrofitClient;
import com.example.datn_toystoryshop.Shopping.Favorite_screen;
import com.example.datn_toystoryshop.Shopping.Cart_screen;
import com.example.datn_toystoryshop.Shopping.Order_screen;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Product_detail extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvProductName, tvProductPrice, tvproductDescription, productStockValue, productBrandValue1, productBrandValue2;
    private ImageView imgBack, shareButton, heartIcon, heart_icon1;
    private LinearLayout dotIndicatorLayout, chatIcon, cartIcon, voucherText;
    private List<View> dotIndicators = new ArrayList<>();
    private double productPrice;
    private boolean statusPro;
    private int owerId, quantity, cateId;
    private String productId, productName, desPro, creatDatePro, listPro, brand;
    private RelativeLayout cart_full_icon;
    private ArrayList<String> productImg; // Danh sách URL ảnh của sản phẩm
    private int currentImageIndex = 0; // Vị trí ảnh hiện tại
    private Handler handler = new Handler(); // Tạo Handler để cập nhật ảnh
    private Runnable imageSwitcherRunnable;
    private View viewDetail1, viewDetail2, viewDetail3;
    private String favoriteId;
    private RecyclerView recyclerViewFeedback;
    private FrameLayout out_of_stock_overlay;
    private boolean isFavorite = false;
    private APIService apiService;
    private int currentQuantity = 1; // Số lượng sản phẩm ban đầu là 1
    private String selectedColor; // Quy cách mặc định
    private String documentId;
    private Feedback_Adapter_Product feedbackAdapterProduct;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;
    private ScrollView scrollView;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        tvProductName = findViewById(R.id.productTitle);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        productStockValue = findViewById(R.id.productStockValue);
        out_of_stock_overlay = findViewById(R.id.out_of_stock_overlay);
        productBrandValue1 = findViewById(R.id.productBrandValue1);
        productBrandValue2 = findViewById(R.id.productBrandValue2);
        tvProductPrice = findViewById(R.id.productPrice);
        tvproductDescription = findViewById(R.id.productDescription);
        dotIndicatorLayout = findViewById(R.id.dotIndicatorLayout);
        imgBack = findViewById(R.id.btnBack);
        shareButton = findViewById(R.id.shareButton);
        chatIcon = findViewById(R.id.chatIcon);
        cartIcon = findViewById(R.id.cartIcon);
        voucherText = findViewById(R.id.voucherText);
        heart_icon1 = findViewById(R.id.heart_icon1);
        viewDetail1 = findViewById(R.id.view_detail_1);
        viewDetail2 = findViewById(R.id.view_detail_2);
        viewDetail3 = findViewById(R.id.view_detail_3);
        cart_full_icon = findViewById(R.id.cart_full_icon);
        heartIcon = findViewById(R.id.heart_icon);
        scrollView = findViewById(R.id.scrollView);
        ratingBar = findViewById(R.id.ratingBar);
        recyclerViewFeedback = findViewById(R.id.recyclerViewFeedback);

        // Khởi tạo APIService bằng RetrofitClient
        apiService = RetrofitClient.getAPIService();
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);

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
        //
        favoriteId = intent.getStringExtra("favoriteId");

        // Hiển thị dữ liệu

        tvProductName.setText(productName);
        tvProductPrice.setText(String.format("%,.0fđ", productPrice));
        productStockValue.setText(String.valueOf(quantity));
        tvproductDescription.setText(desPro);
        productBrandValue1.setText(brand);
        productBrandValue2.setText(brand);
        fetchAndDisplayAverageRating(productId);



        if (quantity == 0) {
            // Ẩn các view cartIcon và voucherText
            cartIcon.setEnabled(false);
            voucherText.setEnabled(false);
            out_of_stock_overlay.setVisibility(View.VISIBLE);
            // Hiển thị thông báo hết hàng
            Toast.makeText(Product_detail.this, "Sản phẩm hiện đang hết hàng!", Toast.LENGTH_SHORT).show();
        }

        ////
        recyclerViewFeedback.setLayoutManager(new LinearLayoutManager(this));
        loadFeedback(productId);


        ViewPager2 productImagePager = findViewById(R.id.productImage);
        Product_detail_Image_Adapter adapter = new Product_detail_Image_Adapter(this, productImg);
        productImagePager.setAdapter(adapter);
        // Tạo dot indicator dựa trên số lượng ảnh
        createDotIndicators(productImg.size());

        // Cập nhật dot indicator khi vuốt ảnh
        productImagePager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentImageIndex = position;
                updateDotIndicator();
            }
        });
        checkIfFavorite(productId);
        // Tạo Runnable để tự động thay đổi ảnh sau 3 giây
        imageSwitcherRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentImageIndex < productImg.size()) {
                    productImagePager.setCurrentItem(currentImageIndex++, true);
                } else {
                    currentImageIndex = 0;
                    productImagePager.setCurrentItem(currentImageIndex, true);
                }

                // Lặp lại sau 3 giây
                handler.postDelayed(this, 3000);
            }
        };
        // Bắt đầu tự động chuyển ảnh
        handler.postDelayed(imageSwitcherRunnable, 3000);
        cart_full_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Product_detail.this, Cart_screen.class);
                intent.putExtra("documentId", documentId);
                startActivity(intent);
            }
        });
        heart_icon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Product_detail.this, Favorite_screen.class);
                intent.putExtra("documentId", documentId);
                startActivity(intent);
            }
        });

        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Product_detail.this, Chat_contact.class);
                intent.putExtra("documentId", documentId);
                startActivity(intent);
            }
        });
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddToCartDialog();
            }
        });
        voucherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAddTopayDialog();
            }
        });

        imgBack.setOnClickListener(v -> onBackPressed());
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp(productName);
            }
        });
        heartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavorite) { // Nếu chưa yêu thích
                    heartIcon.setColorFilter(Color.RED);
                    addFavorite();
                } else { // Nếu đã yêu thích
                    heartIcon.setColorFilter(Color.parseColor("#A09595"));
                    deleteFavorite(productId); // Xóa khỏi yêu thích trước khi thêm lại
                }
            }
        });
        swipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(SwipeRefreshLayout parent, View child) {
                return scrollView.getScrollY() > 0;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            recreate(); // Khởi động lại Activity
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy Handler khi Activity bị hủy để tránh rò rỉ bộ nhớ
        handler.removeCallbacks(imageSwitcherRunnable);
    }
    public void fetchAndDisplayAverageRating(String prodId) {
        Log.e("OrderHistoryAdapter", "Fetching average rating for prodId: " + prodId);

        APIService apiService = RetrofitClient.getAPIService();
        apiService.getAverageRating(prodId).enqueue(new Callback<Feeback_Rating_Model>() {
            @Override
            public void onResponse(Call<Feeback_Rating_Model> call, Response<Feeback_Rating_Model> response) {
                Log.e("API_RESPONSE", "Response code: " + response.code() + ", Message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    Feeback_Rating_Model feedback = response.body();
                    float averageRating = feedback.getAverageRating(); // Lấy số sao trung bình
                    Log.e("AverageRating", "Calculated average rating: " + averageRating);
                    ratingBar.setRating(averageRating); // Hiển thị rating trên RatingBar
                } else {
                    Log.e("API_RESPONSE", "Response is not successful or body is null.");
                    ratingBar.setRating(0); // Không có dữ liệu -> đặt rating mặc định
                }
            }

            @Override
            public void onFailure(Call<Feeback_Rating_Model> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch data: " + t.getMessage());
                ratingBar.setRating(0); // Khi lỗi -> đặt rating mặc định
            }
        });
    }
    public void loadFeedback(String prodId) {

        APIService apiService = RetrofitClient.getAPIService();
        apiService.getFeeback(prodId).enqueue(new Callback<List<Feeback_Model>>() {
            @Override
            public void onResponse(Call<List<Feeback_Model>> call, Response<List<Feeback_Model>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ProductDetail", "Feedback items retrieved: " + response.body().size());

                    // Tạo và thiết lập Feedback_Adapter_Product
                    feedbackAdapterProduct = new Feedback_Adapter_Product(Product_detail.this, response.body(), apiService, documentId);
                    recyclerViewFeedback.setAdapter(feedbackAdapterProduct);
                }
            }

            @Override
            public void onFailure(Call<List<Feeback_Model>> call, Throwable t) {
                Log.e("ProductDetail", "Failed to load feedback: " + t.getMessage());
            }
        });
    }

    private void showAddTopayDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_quantity_picker);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.BOTTOM;
            params.y = 5; // Cách cạnh dưới 5dp

            window.setAttributes(params);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Loại bỏ background mặc định nếu có
        }
        ImageView productImage = dialog.findViewById(R.id.productImage);
        TextView productTitle = dialog.findViewById(R.id.productTitle);
        TextView Price = dialog.findViewById(R.id.productPrice);
        Spinner colorSpinner = dialog.findViewById(R.id.colorSpinnerdialog);
        TextView productStock = dialog.findViewById(R.id.productStock);
        TextView quantityText = dialog.findViewById(R.id.tvQuantity);

        TextView btnDecrease = dialog.findViewById(R.id.btnDecrease);
        TextView btnIncrease = dialog.findViewById(R.id.btnIncrease);
        TextView btnAddToCart = dialog.findViewById(R.id.btnAddToCart);

        productTitle.setText(productName);
        Price.setText(String.format("%,.0fđ", productPrice));
        productStock.setText( String.valueOf(quantity));
        btnAddToCart.setText("Mua Ngay");
        if (!productImg.isEmpty()) {
            String firstImage = productImg.get(0);
            Glide.with(this)
                    .load(firstImage)
                    .into(productImage);
        }

        // Thiết lập giá trị ban đầu
        quantityText.setText(String.valueOf(currentQuantity));


        // Sự kiện giảm số lượng
        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuantity > 1) { // Kiểm tra để không giảm dưới 1
                    currentQuantity--;
                    quantityText.setText(String.valueOf(currentQuantity));
                }
            }
        });

        // Sự kiện tăng số lượng
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentQuantity < quantity){
                    currentQuantity++;
                    quantityText.setText(String.valueOf(currentQuantity));
                }

            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.color_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Gán adapter vào Spinner
        colorSpinner.setAdapter(adapter);

        colorSpinner.setSelection(0);// Chọn mục đầu tiên trong Spinner
// Thiết lập OnItemSelectedListener cho Spinner
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Lấy giá trị được chọn từ Spinner
                selectedColor = parentView.getItemAtPosition(position).toString();

                // Kiểm tra xem giá trị đã chọn có phải là một trong hai giá trị cứng không
                if (selectedColor.equals("Giá trị 1") || selectedColor.equals("Giá trị 2")) {
                    // Hiển thị Toast
                    Toast.makeText(getApplicationContext(), "Bạn đã chọn: " + selectedColor, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Thực hiện nếu không có lựa chọn nào
            }
        });


        // Sự kiện Thêm vào Oder_screen
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Product_detail.this, Order_screen.class);

                // Truyền tất cả dữ liệu qua Intent
                intent.putExtra("productId", productId);
                intent.putExtra("quantity1", quantity);
                intent.putExtra("currentQuantity", currentQuantity);
                intent.putExtra("documentId", documentId); // ID khách hàng
                intent.putExtra("selectedColor", selectedColor);
                String firstProductImage = productImg.get(0);
                intent.putExtra("productImg", firstProductImage);                // Chuyển sang màn hình Oder_screen
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showAddToCartDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_quantity_picker);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.BOTTOM;
            params.y = 5; // Cách cạnh dưới 5dp

            window.setAttributes(params);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Loại bỏ background mặc định nếu có
        }
        ImageView productImage = dialog.findViewById(R.id.productImage);
        TextView productTitle = dialog.findViewById(R.id.productTitle);
        TextView Price = dialog.findViewById(R.id.productPrice);
        Spinner colorSpinner = dialog.findViewById(R.id.colorSpinnerdialog);
        TextView productStock = dialog.findViewById(R.id.productStock);
        TextView quantityText = dialog.findViewById(R.id.tvQuantity);

        TextView btnDecrease = dialog.findViewById(R.id.btnDecrease);
        TextView btnIncrease = dialog.findViewById(R.id.btnIncrease);
        TextView btnAddToCart = dialog.findViewById(R.id.btnAddToCart);

        productTitle.setText(productName);
        Price.setText(String.format("%,.0fđ", productPrice));
        productStock.setText("Kho : " + String.valueOf(quantity));
        btnAddToCart.setText("Thêm vào Giỏ hàng");
        if (!productImg.isEmpty()) {
            String firstImage = productImg.get(0);
            Glide.with(this)
                    .load(firstImage)
                    .into(productImage);
        }

        // Thiết lập giá trị ban đầu
        quantityText.setText(String.valueOf(currentQuantity));


        // Sự kiện giảm số lượng
        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuantity > 1) { // Kiểm tra để không giảm dưới 1
                    currentQuantity--;
                    quantityText.setText(String.valueOf(currentQuantity));

                }
            }
        });

        // Sự kiện tăng số lượng
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuantity < quantity) { // Kiểm tra giới hạn không vượt quá tồn kho
                    currentQuantity++;
                    quantityText.setText(String.valueOf(currentQuantity));
                } else {
                    Toast.makeText(getApplicationContext(), "Số lượng không được vượt quá kho (" + quantity + " sản phẩm)", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.color_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Gán adapter vào Spinner
        colorSpinner.setAdapter(adapter);

        colorSpinner.setSelection(0);// Chọn mục đầu tiên trong Spinner
// Thiết lập OnItemSelectedListener cho Spinner
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Lấy giá trị được chọn từ Spinner
                selectedColor = parentView.getItemAtPosition(position).toString();

                // Kiểm tra xem giá trị đã chọn có phải là một trong hai giá trị cứng không
                if (selectedColor.equals("Giá trị 1") || selectedColor.equals("Giá trị 2")) {
                    // Hiển thị Toast
                    Toast.makeText(getApplicationContext(), "Bạn đã chọn: " + selectedColor, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Thực hiện nếu không có lựa chọn nào
            }
        });


        // Sự kiện Thêm vào Giỏ hàng
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuantity > quantity) { // Kiểm tra lần cuối trước khi thêm vào giỏ
                    Toast.makeText(getApplicationContext(), "Số lượng vượt quá tồn kho!", Toast.LENGTH_SHORT).show();
                } else {
                    checkAndAddToCart(productId, documentId); // Hàm xử lý thêm vào giỏ
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void checkIfFavorite(String productId) {
        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        Call<Map<String, Boolean>> call = apiService.checkFavorite(productId);

        call.enqueue(new Callback<Map<String, Boolean>>() {
            @Override
            public void onResponse(Call<Map<String, Boolean>> call, Response<Map<String, Boolean>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean exists = response.body().get("exists");

                    if (Boolean.TRUE.equals(exists)) {
                        isFavorite = true; // Cập nhật trạng thái yêu thích
                        heartIcon.setColorFilter(Color.RED); // Đổi sang màu đỏ nếu đã yêu thích
                    } else {
                        isFavorite = false; // Đặt trạng thái không yêu thích
                        heartIcon.setColorFilter(Color.parseColor("#A09595")); // Đổi sang màu xám nếu chưa yêu thích
                    }
                } else {
                    Log.e("API_ERROR", "Không lấy được trạng thái yêu thích của sản phẩm.");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Boolean>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi kết nối tới API", t);
            }
        });
    }

    private void addToCart() {

        Cart_Model cartModel = new Cart_Model(
                productId,                 // ID của sản phẩm
                currentQuantity,        // Số lượng sản phẩm
                documentId,                   // ID khách hàng (thay thế bằng ID thực tế của người dùng)
                selectedColor              // Thông số sản phẩm (ví dụ: màu sắc đã chọn)
        );

        // Gọi API để thêm sản phẩm vào giỏ hàng
        Call<Cart_Model> call = apiService.addToCart(cartModel);
        call.enqueue(new Callback<Cart_Model>() {
            @Override
            public void onResponse(Call<Cart_Model> call, Response<Cart_Model> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Product_detail.this, "Thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Product_detail.this, "Không thể thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cart_Model> call, Throwable t) {
                Toast.makeText(Product_detail.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createDotIndicators(int count) {
        dotIndicators.clear();
        dotIndicatorLayout.removeAllViews();

        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.width = 10;
            params.height = 10;
            params.setMargins(4, 0, 4, 0);

            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.dot_inactive); // dot màu xám
            dotIndicators.add(dot);
            dotIndicatorLayout.addView(dot);
        }

        if (!dotIndicators.isEmpty()) {
            dotIndicators.get(0).setBackgroundResource(R.drawable.dot_active); // dot đầu tiên màu xanh
        }
    }

    private void checkAndAddToCart(String prodId, String documentId) {
        Log.e("OrderHistoryAdapter", "j66666666666666666aaaaaa1" + documentId);
        Call<JsonObject> call = apiService.checkProductInCart(prodId, documentId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonResponse = response.body();
                    boolean isInCart = jsonResponse.get("exists").getAsBoolean();

                    if (isInCart) {
                        fetchCartId(prodId, documentId);
                        Log.e("OrderHistoryAdapter", "j66666666666666666aaaaaaa" + documentId);
                        Log.d("CartCheck", "Sản phẩm đã có trong giỏ hàng!");
                    } else {
                        addToCart(); // Gọi hàm thêm sản phẩm vào giỏ
                    }
                } else {

                    Toast.makeText(Product_detail.this, "Không thể kiểm tra sản phẩm!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CartCheck", "Sản phẩm đã có trong giỏ hàng! " + t.getMessage(), t);
                Toast.makeText(Product_detail.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCartId(String prodId, String documentId) {
        Call<JsonObject> call = apiService.getCartId(prodId, documentId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonResponse = response.body();

                    String cartId = jsonResponse.get("cartId").getAsString();
                    String prodSpecification = jsonResponse.get("prodSpecification").getAsString();
                    int currentCartQuantity = Integer.parseInt(jsonResponse.get("quantity").getAsString());

                    // Tính tổng số lượng mới sau khi thêm
                    int updatedQuantity = currentQuantity + currentCartQuantity;

                    if (!prodSpecification.equals(selectedColor)) {
                        // Nếu thông số sản phẩm khác, thêm vào giỏ hàng như một item mới
                        addToCart();
                    } else {
                        // Kiểm tra tổng số lượng có vượt quá tồn kho hay không
                        if (updatedQuantity > quantity) {
                            Toast.makeText(Product_detail.this,
                                    "Tổng số lượng vượt quá tồn kho! Tồn kho: " + quantity,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Cập nhật số lượng trong giỏ hàng
                            updateCartItem(apiService, cartId, prodSpecification, updatedQuantity);
                            Toast.makeText(Product_detail.this,
                                    "Cập nhật giỏ hàng thành công! Số lượng mới: " + updatedQuantity,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.e("CartCheck", "Không thể lấy _id của cart! Response: " + response.code());
                    Toast.makeText(Product_detail.this, "Không thể lấy _id của cart!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CartCheck", "Lỗi kết nối khi lấy _id: " + t.getMessage(), t);
                Toast.makeText(Product_detail.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addFavorite() {
        Favorite_Model favoriteModel = new Favorite_Model(null, productId, documentId);

        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        Call<Favorite_Model> call = apiService.addToFavorites(favoriteModel);

        call.enqueue(new Callback<Favorite_Model>() {
            @Override
            public void onResponse(Call<Favorite_Model> call, Response<Favorite_Model> response) {
                if (response.isSuccessful()) {
                    favoriteId = response.body().get_id(); // Cập nhật ID mới
                    isFavorite = true;
                    heartIcon.setColorFilter(Color.RED);
                    Toast.makeText(getApplicationContext(), "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    // In lỗi chi tiết để xác định vấn đề
                    Log.e("API_ERROR", "Thêm yêu thích thất bại, mã phản hồi: " + response.code());
                    Toast.makeText(getApplicationContext(), "Thêm yêu thích thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Favorite_Model> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Không thể kết nối tới API", Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Lỗi kết nối API khi thêm yêu thích", t);
            }
        });
    }

    public void updateCartItem(APIService apiService, String cartId, String selectedItem, int currentQuantity) {
        // Chuẩn bị dữ liệu cập nhật
        Cart_Model cartModel = new Cart_Model();
        cartModel.set_id(cartId);
        cartModel.setProdSpecification(selectedItem);
        cartModel.setQuantity(currentQuantity); // Cập nhật số lượng

        // Gọi API
        Call<Cart_Model> call = apiService.putCartUpdate(cartId, cartModel);
        call.enqueue(new Callback<Cart_Model>() {
            @Override
            public void onResponse(Call<Cart_Model> call, Response<Cart_Model> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Cập nhật thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cart_Model> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteFavorite(String productId) {
        // Gọi API xóa yêu thích
        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        Call<Void> call = apiService.deleteFavorite(productId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    heartIcon.setColorFilter(Color.parseColor("#A09595"));
                    isFavorite = false; // Reset trạng thái sau khi xóa
                    Toast.makeText(getApplicationContext(), "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Xóa yêu thích thất bại", Toast.LENGTH_SHORT).show();
                    Log.e("API Response", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                Log.e("API Failure", "Error message: " + t.getMessage(), t);
            }
        });
    }


    private void updateDotIndicator() {
        for (int i = 0; i < dotIndicators.size(); i++) {
            dotIndicators.get(i).setBackgroundResource(
                    i == currentImageIndex ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }

    private void shareApp(String productName) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.intro_title_detail) + productName);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_with_friends_int));
        startActivity(shareIntent);
    }
}