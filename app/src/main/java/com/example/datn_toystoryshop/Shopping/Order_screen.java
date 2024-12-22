package com.example.datn_toystoryshop.Shopping;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datn_toystoryshop.Adapter.Order_Adapter_Cart;
import com.example.datn_toystoryshop.Adapter.Order_Adapter_Detail;
import com.example.datn_toystoryshop.Home_screen;
import com.example.datn_toystoryshop.Model.Cart_Model;
import com.example.datn_toystoryshop.Model.OderProductDetail_Model;
import com.example.datn_toystoryshop.Model.OrderResponse;
import com.example.datn_toystoryshop.Model.Order_Detail_Model;
import com.example.datn_toystoryshop.Model.Order_Model;
import com.example.datn_toystoryshop.Model.Product_Model;
import com.example.datn_toystoryshop.Model.Voucher;
import com.example.datn_toystoryshop.PayPalActivity;
import com.example.datn_toystoryshop.Profile.Terms_Conditions_screen;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.SendMail;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.RetrofitClient;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Order_screen extends AppCompatActivity implements Order_Adapter_Detail.TotalAmountCallback {

    private ImageView imgBack;
    private TextView btnOrder, tvTotalAmount, total_amount, shipping_money, money_pay1, money_pay, tvPaymentDetail, clause, tvTotalAmountLabel, shipDiscountPrice, productDiscountPrice, estimated_delivery, voucher_info, old_price, new_price, shipping_method_name, show_more_oder;
    private LinearLayout addressLayout, ship, pay, productDiscounttv, shipDiscounttv;
    private RelativeLayout voucher;
    private EditText tvLeaveMessage;
    private RecyclerView recyclerView;
    private Handler handler = new Handler();
    private String productId, selectedColor, customerId, productImg, content, productType, name, phone, address, paytext, defaultName = "Trần Cương", defaultPhone = "0382200084", defaultAddress = "Số Nhà 3, Ngõ 80 Xuân Phương, Phường Phương Canh, Quận Nam Từ Liêm, Hà Nội", defaultPayText = "Ví VNPAY", documentId, email_confirm,voucherId;
    private double totalProductDiscount = 0, totalShipDiscount = 0, totalAmount, moneyPay, shippingCost = 40000;
    private int currentImageIndex = 0, currentQuantity, quantity, quantity1;
    private boolean isFavorite = false, nightMode;
    private ArrayList<String> productIds;
    private static final String CHANNEL_ID = "home_notification_channel", PREFS_NAME = "NotificationPrefs", NOTIFICATION_BLOCKED_KEY = "isNotificationBlocked";
    private NotificationManager notificationManager;
    private SharedPreferences sharedPreferences;
    private TextView addressName, addressDetail, addressPhone;
    private FirebaseFirestore db;



    private String getFormattedDate(int daysToAdd, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
        return new SimpleDateFormat(format, new Locale("vi", "VN")).format(calendar.getTime());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oder_screen);

        imgBack = findViewById(R.id.imgBack);
        btnOrder = findViewById(R.id.btnOrder);
        show_more_oder = findViewById(R.id.show_more_oder);
        addressLayout = findViewById(R.id.addressLayout);
        recyclerView = findViewById(R.id.recycler_view_oder);
        addressName = findViewById(R.id.address_name);
        addressPhone = findViewById(R.id.address_phone);
        addressDetail = findViewById(R.id.address_detail);
        tvLeaveMessage = findViewById(R.id.tvLeaveMessage1);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvTotalAmountLabel = findViewById(R.id.tvTotalAmountLabel);
        tvPaymentDetail = findViewById(R.id.tvPaymentDetail);
        productDiscounttv = findViewById(R.id.productDiscounttv);
        shipDiscounttv = findViewById(R.id.shipDiscounttv);
        shipDiscountPrice = findViewById(R.id.shipDiscountPrice);
        productDiscountPrice = findViewById(R.id.productDiscountPrice);
        clause = findViewById(R.id.clause);
        estimated_delivery = findViewById(R.id.estimated_delivery);
        voucher_info = findViewById(R.id.voucher_info);
        old_price = findViewById(R.id.old_price);
        new_price = findViewById(R.id.new_price);
        shipping_method_name = findViewById(R.id.shipping_method_name);
        total_amount = findViewById(R.id.total_amount);
        shipping_money = findViewById(R.id.shipping_money);
        money_pay1 = findViewById(R.id.money_pay1);
        money_pay = findViewById(R.id.money_pay);
        ship = findViewById(R.id.ship);
        pay = findViewById(R.id.pay);
        voucher = findViewById(R.id.voucher);

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
        ///dulieunhanCart
        productIds = intent.getStringArrayListExtra("productIds");
        totalShipDiscount = intent.getDoubleExtra("totalShipDiscount", 0);
        totalProductDiscount = intent.getDoubleExtra("totalProductDiscount", 0);

        if (totalShipDiscount != 0 && totalProductDiscount != 0) {
            String text = String.format("%,.0fđ", shippingCost);
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new StrikethroughSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#888888")), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            old_price.setText(spannableString);
            // Hiển thị các thông tin giảm giá
            shipDiscounttv.setVisibility(View.VISIBLE);
            productDiscounttv.setVisibility(View.VISIBLE);
            new_price.setVisibility(View.VISIBLE);
            shipDiscountPrice.setText(String.format("-₫%,.0f", totalShipDiscount));
            // Tính giá mới và set cho new_price
            double newPriceValue = shippingCost - totalShipDiscount;
            new_price.setText(String.format("₫%,.0f", newPriceValue));

            productDiscountPrice.setText(String.format("-₫%,.0f", totalProductDiscount));
            // Tính toán lại moneyPay sau khi nhận được giá trị mới
            calculateMoneyPay();
        }
        ///dulieunhanDetail
        productId = intent.getStringExtra("productId");
        currentQuantity = intent.getIntExtra("currentQuantity", 0);
        quantity1 = intent.getIntExtra("quantity1", 0);
        selectedColor = intent.getStringExtra("selectedColor");
        productImg = intent.getStringExtra("productImg");

        db = FirebaseFirestore.getInstance();
        loadUserDataByDocumentId(documentId);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        APIService apiService = RetrofitClient.getInstance().create(APIService.class);

        if (productIds != null && !productIds.isEmpty()) {

            // Sử dụng Oder_Adapter_Cart
            Order_Adapter_Cart adapter = new Order_Adapter_Cart(productIds, apiService, new Order_Adapter_Cart.OnTotalAmountChangeListener() {
                @Override
                public void onTotalAmountChanged(double totalAmount1, List<OderProductDetail_Model> productSummaries) {
                    totalAmount = totalAmount1;
                    // Định dạng chuỗi để hiển thị
                    String formattedTotalAmount = String.format("%,.0fđ", totalAmount1);
                    List<Order_Model.ProductDetail> productDetails = new ArrayList<>();
                    for (OderProductDetail_Model oderProductDetailModel : productSummaries) {
                        String productId = oderProductDetailModel.getId();
                        double totalAmount = oderProductDetailModel.getTotalPrice();
                        int quantity = oderProductDetailModel.getQuantity();
                        String productType = oderProductDetailModel.getProdSpecification();
                        int count = productIds.size();
                        tvTotalAmountLabel.setText("Tổng số tiền (" + count + " sản phẩm):");
                        Log.d("ProductIDCount", "Số lượng ID trong danh sách: " + count);

                        // Thêm sản phẩm vào danh sách productDetails
                        productDetails.add(new Order_Model.ProductDetail(productId, totalAmount, quantity, productType));

                    }
                    // Gán vào TextView
                    Log.e("API_ERROR", "Thêm oder thất bại, mã phản hồi: 2 " + totalAmount1);
                    tvTotalAmount.setText(formattedTotalAmount);
                    total_amount.setText(formattedTotalAmount);
                    calculateMoneyPay();

                    btnOrder.setOnClickListener(v -> {
                        submitOrder_Cart(productDetails,productId );
                        // Khởi tạo NotificationManager

                    });
                }
            });

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

       //      Hiển thị nút "Xem thêm" nếu danh sách có hơn 2 sản phẩm
//            if (productIds.size() > 2) {
//                show_more_oder.setVisibility(View.VISIBLE);
//            }
//
//            // Xử lý sự kiện nhấn "Xem thêm"
//            show_more_oder.setOnClickListener(v -> {
//                adapter.toggleShowAll(); // Gọi phương thức trong adapter để hiển thị tất cả
//                show_more_oder.setVisibility(View.GONE); // Ẩn nút "Xem thêm" sau khi nhấn
//
//            });
        } else {
            List<Order_Detail_Model> productList = new ArrayList<>();
            productList.add(new Order_Detail_Model(productId, currentQuantity, customerId, selectedColor, productImg));
            // Sử dụng Oder_Adapter_Detail
            Order_Adapter_Detail adapter = new Order_Adapter_Detail(productList, apiService, this);
            recyclerView.setAdapter(adapter);


        }
        // Đặt ngày giao hàng dự kiến
        String estimatedDeliveryDate = getFormattedDate(5, "'ngày' dd 'tháng' MM");
        estimated_delivery.setText("Đảm bảo nhận hàng vào " + estimatedDeliveryDate);
        String voucherExpiryDate = getFormattedDate(7, "'ngày' dd 'tháng' MM 'năm' yyyy");
        voucher_info.setText("Nhận Voucher trị giá ₫15.000 nếu đơn hàng được giao đến bạn sau " + voucherExpiryDate);

        String shippingmoney = String.format("%,.0fđ", shippingCost);
        shipping_money.setText(shippingmoney);
// Kiểm tra null và tính toán moneyPay theo các trường hợp


        // Cài đặt hành động cho các nút

        voucher.setOnClickListener(v -> {
            Intent intent1 = new Intent(Order_screen.this, Voucher_screen.class);
            if (totalShipDiscount != 0) {
                intent1.putExtra("totalShipDiscount", totalShipDiscount);
            }
            if (totalProductDiscount != 0) {
                intent1.putExtra("totalProductDiscount", totalProductDiscount);
            }
            startActivityForResult(intent1, 100);  // Sử dụng mã requestCode để nhận kết quả
        });

        pay.setOnClickListener(v -> {
            Intent intent1 = new Intent(Order_screen.this, Payment_method_screen.class);
            String currentPayment = tvPaymentDetail.getText().toString();
            if (!currentPayment.isEmpty()) {
                intent1.putExtra("currentPayment", currentPayment);  // Chỉ truyền khi đã có lựa chọn
            }
            startActivityForResult(intent1, 101);
        });

        ship.setOnClickListener(v -> {
            Intent intent1 = new Intent(Order_screen.this, ShippingUnit_screen.class);

            if (totalShipDiscount != 0) {
                intent1.putExtra("totalShipDiscount", totalShipDiscount);
            }
            String ShipDiscount = shipping_method_name.getText().toString();
            if (!ShipDiscount.isEmpty()) {
                intent1.putExtra("ShipDiscount", ShipDiscount);  // Chỉ truyền khi đã có lựa chọn
            }

            startActivityForResult(intent1, 102);
        });
        clause.setOnClickListener(v -> {
            Intent intent1 = new Intent(Order_screen.this, Terms_Conditions_screen.class);
            startActivity(intent1);
            finish();
        });

        addressLayout.setOnClickListener(v -> {
            Intent intent1 = new Intent(Order_screen.this, AddressList_Screen.class);
            intent1.putExtra("documentId", documentId);
            startActivityForResult(intent1, 103);
        });

        imgBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onTotalAmountCalculated(double totalAmount2, int quantity2, String productType2) {

        totalAmount = totalAmount2;
        quantity = quantity2;
        productType = productType2;
        if ("Nguyên set 12 hộp".equalsIgnoreCase(productType)) {
            totalAmount *= 12; // Nhân totalAmount với 12
        }
        String formattedTotalAmount = String.format("%,.0fđ", totalAmount);

        Log.e("API_ERROR", "Thêm order thất bạiThêm order thất bại, mã phản hồi: " + totalAmount2);
        Log.e("API_ERROR", "Thêm order thất bạiThêm order thất bại, mã phản hồi: " + totalAmount);
        tvTotalAmount.setText(formattedTotalAmount);
        total_amount.setText(formattedTotalAmount);
        tvTotalAmountLabel.setText("Tổng số tiền :");
        calculateMoneyPay();
        btnOrder.setOnClickListener(v -> {
            sumitOrder_Detail();

            finish();
        });
    }

    // Hàm nhận kết quả từ màn hình Voucher
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case 100:
                    // Nhận dữ liệu từ màn hình Voucher
                    totalProductDiscount = data.getDoubleExtra("totalProductDiscount", 0.0);
                    totalShipDiscount = data.getDoubleExtra("totalShipDiscount", 0.0);
                    voucherId = data.getStringExtra("voucherId");
                    //set gạch ngang cho giá cũ
                    String text = String.format("%,.0fđ", shippingCost);
                    SpannableString spannableString = new SpannableString(text);
                    spannableString.setSpan(new StrikethroughSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#888888")), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    old_price.setText(spannableString);
                    // Hiển thị các thông tin giảm giá
                    shipDiscounttv.setVisibility(View.VISIBLE);
                    productDiscounttv.setVisibility(View.VISIBLE);
                    new_price.setVisibility(View.VISIBLE);
                    shipDiscountPrice.setText(String.format("-₫%,.0f", totalShipDiscount));
                    // Tính giá mới và set cho new_price
                    double newPriceValue = shippingCost - totalShipDiscount;
                    new_price.setText(String.format("₫%,.0f", newPriceValue));

                    productDiscountPrice.setText(String.format("-₫%,.0f", totalProductDiscount));
                    // Tính toán lại moneyPay sau khi nhận được giá trị mới
                    calculateMoneyPay();
                    break;

                case 101:
                    // Nhận dữ liệu từ màn hình phương thức thanh toán
                     paytext = data.getStringExtra("paytext");
                    tvPaymentDetail.setText(paytext);
                    break;

                case 102:
                    // Xử lý dữ liệu từ Shipping_method_screen
                    // Nhận dữ liệu từ màn hình phương thức vận chuyển
                    String shipping_price = data.getStringExtra("shipping_price");
                    String shipping_method = data.getStringExtra("shipping_method");
                    if (totalShipDiscount != 0) {
                        new_price.setVisibility(View.VISIBLE);
                        // Tính giá mới và set cho new_price
                        double shippingPriceValue = Double.parseDouble(shipping_price.replaceAll("[^\\d]", ""));
                        double newPriceValue1 = shippingPriceValue - totalShipDiscount;
                        new_price.setText(String.format("₫%,.0f", newPriceValue1));
                        //set gạch ngang cho giá cũ
                        String text1 = String.format("%,.0fđ", shippingCost);
                        SpannableString spannableString1 = new SpannableString(text1);
                        spannableString1.setSpan(new StrikethroughSpan(), 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannableString1.setSpan(new ForegroundColorSpan(Color.parseColor("#888888")), 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        old_price.setText(spannableString1);
                    }
                    shipping_method_name.setText(shipping_method);
//                    String shippingmoney = String.format("%,.0fđ", shippingCost);
//                    shipping_money.setText(shippingmoney);

                    calculateMoneyPay();
                    // Kiểm tra phương thức giao hàng và xử lý tương ứng
                    if ("Nhanh".equals(shipping_method)) {
                        shippingCost = 40000;
                        String shippingmoney = String.format("%,.0fđ", shippingCost);
                        shipping_money.setText(shippingmoney);
                        calculateMoneyPay();
                        String text1 = String.format("%,.0fđ", shippingCost);
                        SpannableString spannableString1 = new SpannableString(text1);
                        spannableString1.setSpan(new StrikethroughSpan(), 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannableString1.setSpan(new ForegroundColorSpan(Color.parseColor("#888888")), 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        old_price.setText(spannableString1);
//                        String oldprice = String.format("%,.0fđ", shippingCost);
//                        old_price.setText(oldprice);
                        String estimatedDeliveryDate = getFormattedDate(5, "'ngày' dd 'tháng' MM");
                        estimated_delivery.setText("Đảm bảo nhận hàng vào " + estimatedDeliveryDate);
                        String voucherExpiryDate = getFormattedDate(7, "'ngày' dd 'tháng' MM 'năm' yyyy");
                        voucher_info.setText("Nhận Voucher trị giá ₫15.000 nếu đơn hàng được giao đến bạn sau " + voucherExpiryDate);

                    } else if ("Hỏa Tốc".equals(shipping_method)) {
                        shippingCost = 80000;
                        String shippingmoney = String.format("%,.0fđ", shippingCost);
                        shipping_money.setText(shippingmoney);
                        calculateMoneyPay();
                        String oldprice = String.format("%,.0fđ", shippingCost);
                        old_price.setText(oldprice);
                        // Xử lý cho phương thức Hỏa Tốc
                        String estimatedDeliveryDate = getFormattedDate(2, "'ngày' dd 'tháng' MM");
                        estimated_delivery.setText("Đảm bảo nhận hàng vào " + estimatedDeliveryDate);

                        String voucherExpiryDate = getFormattedDate(3, "'ngày' dd 'tháng' MM 'năm' yyyy");
                        voucher_info.setText("Nhận Voucher trị giá ₫15.000 nếu đơn hàng được giao đến bạn sau " + voucherExpiryDate);
                    }
                    case 103:
                         name = data.getStringExtra("selectedAddressName");
                         phone = data.getStringExtra("selectedAddressPhone");
                         address = data.getStringExtra("selectedAddress");
//                                String fullAddress = (address != null ? address : "") +
//                (detail != null ? ", " + detail : "");
        addressName.setText(name);
        addressPhone.setText(phone);
        addressDetail.setText(address);
                    break;
            }
        }
    }

    private void calculateMoneyPay() {

        // Kiểm tra null và tính toán moneyPay theo các trường hợp
        if (totalProductDiscount == 0 && totalShipDiscount == 0) {
            moneyPay = totalAmount + shippingCost;
        } else if (totalShipDiscount == 0) {
            moneyPay = (totalAmount + shippingCost) - totalProductDiscount;
        } else if (totalProductDiscount == 0) {
            moneyPay = (totalAmount + shippingCost) - totalShipDiscount;
        } else {
            moneyPay = (totalAmount + shippingCost) - totalProductDiscount - totalShipDiscount;
        }

        // Định dạng chuỗi để hiển thị với dấu phân cách hàng nghìn và đơn vị "đ"
        String formattedMoneyPay = String.format("%,.0fđ", moneyPay);

        // Gán giá trị cho TextView money_pay và money_pay1
        money_pay.setText(formattedMoneyPay);
        money_pay1.setText(formattedMoneyPay);
    }

    private void sumitOrder_Detail() {
        content = tvLeaveMessage.getText().toString();
        // Tạo một danh sách các sản phẩm trong đơn hàng
        List<Order_Model.ProductDetail> productDetails = new ArrayList<>();
        productDetails.add(new Order_Model.ProductDetail(productId, totalAmount, quantity, productType));
        name = name != null ? name : defaultName;
        phone = phone != null ? phone : defaultPhone;
        address = address != null ? address : defaultAddress;
        paytext = paytext != null ? paytext : defaultPayText;

        // Tạo đối tượng Order_Model với danh sách sản phẩm
        Order_Model orderModel = new Order_Model(
                null,                // _id
                documentId,             // cusId
                (int) moneyPay,
                name,
                phone,
                address,
                paytext,
                productDetails,      // prodDetails (danh sách sản phẩm)
                content,             // content (nội dung đơn hàng)
                "Chờ xác nhận", // orderStatus
                new Date().toString()// orderDate
        );

        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        Call<OrderResponse> call = apiService.addToOrder(orderModel);

        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful()) {
                    OrderResponse createdOrder = response.body(); // Đơn hàng vừa được tạo
                    Log.d("API_RESPONSE", "aaaaaaaaaaaaaaaaaaaaaaaaaaaResponse body: " + response.body());
                    if (createdOrder != null) {
                        String orderId1 = createdOrder.getOrderId(); // Lấy `orderId` từ phản hồi
                        Toast.makeText(getApplicationContext(), "Cảm ơn đã mua", Toast.LENGTH_SHORT).show();

                        // Chuyển sang màn hình thanh toán
                        Intent intent = new Intent(Order_screen.this, PayPalActivity.class);
                        intent.putExtra("amount", (long) Math.floor(moneyPay));
                        intent.putExtra("orderId", orderId1); // Truyền `orderId` vào intent
                        intent.putExtra("user_id", documentId);
                        startActivity(intent);
                        System.out.println("Đã chuyển sang màn hình thanh toán");
                    } else {
                        Log.e("API_ERROR", "Phản hồi API trả về null.");
                        Toast.makeText(getApplicationContext(), "Lỗi tạo đơn hàng", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getApplicationContext(), "Cảm ơn đã mua", Toast.LENGTH_SHORT).show();
                    if (voucherId != null ) {
                        String[] voucherIds = voucherId.split(",");

                        // Gọi hàm updateVoucherQuantity cho từng voucherId
                        for (String id : voucherIds) {
                            if (id != null && !id.trim().isEmpty()) {
                                updateVoucherQuantity(id); // Gọi hàm update cho mỗi voucherId
                            }
                        }                    }
                    if (email_confirm != null && !email_confirm.isEmpty()) {
                        fetchProductById(productId, new OnProductFetchedListener() {
                            @Override
                            public void onFetched(String productName, int quantity) {
                                if (productName != null) {
                                    Log.d("Product Info", "Fetched Product Name: " + productName);
                                    // Xử lý tên sản phẩm (ví dụ: thêm vào danh sách)
                                } else {
                                    Log.e("Product Info", "Không thể lấy tên sản phẩm.");
                                }
                            }
                        });

                        sendOrderConfirmationEmail(orderModel);
                    }
                    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    // Tạo Notification Channel nếu cần (dành cho Android 8.0 trở lên)
                    createNotificationChannel();
                    updateProductItem(apiService, productId, quantity1 - quantity);
                    // Hiển thị thông báo chào mừng nếu thông báo đang được bật
                    showWelcomeNotification();

//                    // Chuyển sang màn hình thanh toán
//
//                    Intent intent = new Intent(Order_screen.this, PayPalActivity.class);
//                    System.out.println("MoneyPay: " + moneyPay);
//                    System.out.println("documentId: " + documentId);
//                    intent.putExtra("amount", (long) Math.floor(moneyPay));
//                    intent.putExtra("orderId", orderId);
//                    intent.putExtra("user_id", documentId);
//                    startActivity(intent);
//                    System.out.println("Đã chuyển sang màn hình thanh toán");

                } else {
                    Log.e("API_ERROR", "Thêm order thất bại, mã phản hồi: " + response.code());
                    Toast.makeText(getApplicationContext(), "Thêm order thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Không thể kết nối tới API", Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Lỗi kết nối API khi thêm đánh giá", t);
            }
        });
    }
    public void sendOrderConfirmationEmail(Order_Model order) {
        StringBuilder productListBuilder = new StringBuilder();

        AtomicInteger counter = new AtomicInteger(0); // Bộ đếm để kiểm tra số sản phẩm đã xử lý
        int totalProducts = order.getProdDetails().size();

        for (Order_Model.ProductDetail productDetail : order.getProdDetails()) {
            fetchProductById(productDetail.getProdId(), (productName, quantity) -> {
                if (productName != null) {
                    productListBuilder.append(String.format( "<li>%s (Số lượng: %d, Giá: %,d VNĐ)</li>",
                            productName,
                            productDetail.getQuantity(),
                            (int) productDetail.getRevenue()));
                } else {

                    productListBuilder.append("<li>Không thể lấy thông tin sản phẩm</li>");
                }

                // Kiểm tra nếu tất cả sản phẩm đã được xử lý
                if (counter.incrementAndGet() == totalProducts) {
                    sendEmail(order, productListBuilder.toString());
                }
            });
        }
    }
    private void sendEmail(Order_Model order, String productList) {
        double totalDiscount = 0;
        if (totalShipDiscount != 0 && totalProductDiscount != 0) {
            totalDiscount = totalShipDiscount + totalProductDiscount;
        } else if (totalShipDiscount != 0) {
            totalDiscount = totalShipDiscount;
        } else if (totalProductDiscount != 0) {
            totalDiscount = totalProductDiscount;
        }
        String message = String.format(
                "<html>" +
                        "<body>" +
                        "<p>Kính gửi Quý khách hàng,</p>" +
                        "<p>Cảm ơn bạn đã mua sắm tại ToyStory Shop! Đơn hàng của bạn đã được xác nhận thành công. Dưới đây là thông tin chi tiết về đơn hàng:</p>" +
                        "<p><strong>Ngày đặt hàng:</strong> %s</p>" +
                        "<p><strong>Danh sách sản phẩm:</strong></p>" +
                        "<p>%s</p>" +
                        "<p><strong>Phí vận chuyển:</strong> %,d VNĐ</p>" +
                        "<p><strong>Tổng tiền giảm giá:</strong> %,d VNĐ</p>" +
                        "<p><strong>Tổng cộng:</strong> %,d VNĐ</p>" +
                        "<p><strong>Địa chỉ giao hàng:</strong></p>" +
                        "<p>%s</p>" +
                        "<p><strong>Họ tên:</strong> %s</p>" +
                        "<p><strong>Số điện thoại:</strong> %s</p>" +
                        "<p><strong>Hình thức thanh toán:</strong> %s</p>" +
                        "<p><strong>Thời gian dự kiến giao hàng:</strong> 3-5 ngày làm việc.</p>" +
                        "<p>Liên hệ hỗ trợ:</p>" +
                        "<p>Nếu bạn cần hỗ trợ thêm, vui lòng liên hệ bộ phận CSKH:</p>" +
                        "<ul>" +
                        "<li>Số điện thoại: 0123987456</li>" +
                        "<li>Email: toystory.shop.datn@gmail.com</li>" +
                        "</ul>" +
                        "<p>Cảm ơn bạn đã tin tưởng và ủng hộ ToyStory Shop!</p>" +
                        "<p>Trân trọng,</p>" +
                        "<p><strong>Đội ngũ ToyStory Shop</strong></p>" +
                        "</body>" +
                        "</html>",
                order.getOrderDate(),
                productList,
                (int) shippingCost,
                (int) totalDiscount,
                order.getRevenue_all(),
                order.getAddress_order(),
                order.getName_order(),
                order.getPhone_order(),
                order.getPayment_method()
        );

        // Gửi email bằng SendMail
        SendMail sendMail = new SendMail(email_confirm, "ToyStory Shop - Xác nhận đơn hàng của bạn!", message);
        sendMail.execute();
    }



    private void submitOrder_Cart(List<Order_Model.ProductDetail> productDetails,String productId) {
        String content = tvLeaveMessage.getText().toString();
        name = name != null ? name : defaultName;
        phone = phone != null ? phone : defaultPhone;
        address = address != null ? address : defaultAddress;
        paytext = paytext != null ? paytext : defaultPayText;
        // Tạo đối tượng Order_Model với danh sách sản phẩm
        Order_Model orderModel = new Order_Model(
                null,                // _id
                documentId,             // cusId
                (int) moneyPay,
                name,
                phone,
                address,
                paytext,
                productDetails,      // prodDetails (danh sách sản phẩm)
                content,             // content (nội dung đơn hàng)
                "Chờ xác nhận", // orderStatus
                new Date().toString()// orderDate
        );

        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        Call<OrderResponse> call = apiService.addToOrder(orderModel);

        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful()) {
                    OrderResponse createdOrder = response.body(); // Đơn hàng vừa được tạo
                    if (createdOrder != null) {
                        String orderId = createdOrder.getOrderId(); // Lấy `orderId` từ phản hồi
                        System.out.println("Order ID: " + orderId); // Kiểm tra log

                        Toast.makeText(getApplicationContext(), "Cảm ơn đã mua", Toast.LENGTH_SHORT).show();

                        // Chuyển sang màn hình thanh toán
                        Intent intent = new Intent(Order_screen.this, PayPalActivity.class);
                        intent.putExtra("amount", (long) Math.floor(moneyPay));
                        intent.putExtra("orderId", orderId); // Truyền `orderId` vào intent
                        intent.putExtra("user_id", documentId);
                        startActivity(intent);
                        System.out.println("Đã chuyển sang màn hình thanh toán");
                    } else {
                        Log.e("API_ERROR", "Phản hồi API trả về null.");
                        Toast.makeText(getApplicationContext(), "Lỗi tạo đơn hàng", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getApplicationContext(), "Cảm ơn đã mua", Toast.LENGTH_SHORT).show();
                    if (voucherId != null) {
                        String[] voucherIds = voucherId.split(",");

                        // Gọi hàm updateVoucherQuantity cho từng voucherId
                        for (String id : voucherIds) {
                            if (id != null && !id.trim().isEmpty()) {
                                updateVoucherQuantity(id); // Gọi hàm update cho mỗi voucherId
                            }
                        }
                    }

                    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Log.d("CartScreen", "Danh sách sản phẩm: " + productIds);
                    for (String cartId : productIds) {
                        Log.d("CartScreen", "Danh sách sản phẩm: 11 " + cartId);
                          deleteCartItem(apiService, cartId.trim()); // Truyền từng prodId vào hàm
                    }
                    // Tạo Notification Channel nếu cần (dành cho Android 8.0 trở lên)
                    createNotificationChannel();
                    // Tạo bản sao danh sách sản phẩm và gọi phương thức xử lý tuần tự
                    List<Order_Model.ProductDetail> productDetailsCopy = new ArrayList<>(productDetails);
                    updateProductsSequentially(productDetailsCopy,apiService);
                    sendOrderConfirmationEmail(orderModel);
                    // Hiển thị thông báo chào mừng nếu thông báo đang được bật
                    showWelcomeNotification();

//                    //Goto payment
//                    Intent in = new Intent(Order_screen.this, PayPalActivity.class);
//                    System.out.println("MoneyPay: " + moneyPay);
//                    System.out.println("documentId: " + documentId);
//                    in.putExtra("amount", (long) Math.floor(moneyPay));
//                    in.putExtra("user_id", documentId);
//                    startActivity(in);
//                    System.out.println("Đã chuyển sang màn hình thanh toán");

                } else {
                    Log.e("API_ERROR", "Thêm order thất bại, mã phản hồi: " + response.code());
                    Toast.makeText(getApplicationContext(), "Thêm order thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Không thể kết nối tới API", Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Lỗi kết nối API khi thêm order", t);
            }
        });
    }

    public interface OnProductFetchedListener {
        void onFetched(String productName, int quantity);
    }
    private void fetchProductById(String prodId, OnProductFetchedListener listener) {
        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        Call<Product_Model> call = apiService.getProductById(prodId);

        call.enqueue(new Callback<Product_Model>() {
            @Override
            public void onResponse(Call<Product_Model> call, Response<Product_Model> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product_Model product = response.body();
                    Log.d("Queue Processing", "Product Name: " + product.getNamePro());
                    // Truyền tên sản phẩm về qua listener
                    listener.onFetched(product.getNamePro(), product.getQuantity());
                } else {
                    Log.e("Queue Processing", "Response code: " + response.code());
                    listener.onFetched(null,0); // Trả về null nếu lỗi
                }
            }

            @Override
            public void onFailure(Call<Product_Model> call, Throwable t) {
                Log.e("Queue Processing", "Network Error: " + t.getMessage());
                listener.onFetched(null,0); // Trả về null nếu lỗi
            }
        });
    }
    public void updateProductItem(APIService apiService, String prodId, int inventory) {
        // Chuẩn bị dữ liệu cập nhật
        Product_Model productModel = new Product_Model();
        productModel.set_id(prodId);
        productModel.setQuantity(inventory);
        // Gọi API
        Call<Product_Model> call = apiService.putProductUpdate(prodId, productModel);
        call.enqueue(new Callback<Product_Model>() {
            @Override
            public void onResponse(Call<Product_Model> call, Response<Product_Model> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Order_screen.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Order_screen.this, "Cập nhật thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product_Model> call, Throwable t) {
                Toast.makeText(Order_screen.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductsSequentially(List<Order_Model.ProductDetail> productDetails,APIService apiService) {
        // Nếu danh sách rỗng, kết thúc
        if (productDetails.isEmpty()) {
            Log.d("Queue Processing", "Tất cả sản phẩm đã được xử lý!");
            return;
        }

        // Lấy sản phẩm đầu tiên
        Order_Model.ProductDetail detail = productDetails.remove(0); // Xóa sản phẩm đầu khỏi danh sách
        String productId = detail.getProdId();
        int quantity = detail.getQuantity();

        // Gọi API để lấy thông tin sản phẩm
        fetchProductById(productId, (productName, inventory) -> {
            if (productName != null) {
                int newQuantity = inventory - quantity;
                // Cập nhật số lượng sản phẩm trong kho
                if (newQuantity >= 0) {
                    updateProductItem(apiService, productId, newQuantity);

                } else {
                    Log.e("Queue Processing", "Không thể cập nhật sản phẩm: " + productId + ", tồn kho không đủ!");
                }
            } else {
                Log.e("Queue Processing", "Không thể xử lý sản phẩm: " + productId);
            }

            // Tiếp tục xử lý sản phẩm tiếp theo
            updateProductsSequentially(productDetails,apiService);
        });
    }



    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Home Notification Channel";
            String description = "Channel for home screen notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Đăng ký channel với hệ thống
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showWelcomeNotification() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isNotificationBlocked = sharedPreferences.getBoolean(NOTIFICATION_BLOCKED_KEY, false);

        if (!isNotificationBlocked) {
            // Tạo thông báo chào mừng
            Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_logo) // Thay bằng icon của ứng dụng
                    .setContentTitle("Đơn hàng của bạn đang đợi xác nhận")
                    .setContentText("Cảm ơn bạn đã mua sắm tại ToyStory Shop. Chúng tôi đang xử lý đơn hàng của bạn")
                    .setPriority(Notification.PRIORITY_DEFAULT);

            if (notificationManager != null) {
                notificationManager.notify(2, builder.build());
            }

        } else {
            // Thông báo bị tắt
            Toast.makeText(this, "Thông báo đang bị tắt.", Toast.LENGTH_SHORT).show();
        }
    }
    private void deleteCartItem(APIService apiService,String cartId) {
        Call<Void> call = apiService.deleteCartId(cartId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Order_screen.this, "Xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Order_screen.this, "Không thể xóa sản phẩm!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Order_screen.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    email_confirm = document.getString("email");
                } else {
                    Log.d("UserData", "No such document");
                }
            } else {
                Log.w("UserData", "get failed with ", task.getException());
            }
        });
    }
    public void updateVoucherQuantity(String voucherId) {
        // Lấy instance của APIService
        APIService apiService = RetrofitClient.getAPIService();
        Log.d("Voucher Update", "Số lượng còn lại:id3 " + voucherId);
        // Gửi yêu cầu đến server
        Call<Voucher> call = apiService.updateVoucherQuantity(voucherId);
        call.enqueue(new Callback<Voucher>() {
            @Override
            public void onResponse(Call<Voucher> call, Response<Voucher> response) {
                if (response.isSuccessful()) {
                    Voucher updatedVoucher = response.body();
                    if (updatedVoucher != null) {
                        // Xử lý kết quả trả về, ví dụ:
                    }
                } else {
                    Log.e("Voucher Update", "Không thể cập nhật voucher: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Voucher> call, Throwable t) {
                Log.e("Voucher Update", "Lỗi kết nối: " + t.getMessage());
            }
        });
    }


}
