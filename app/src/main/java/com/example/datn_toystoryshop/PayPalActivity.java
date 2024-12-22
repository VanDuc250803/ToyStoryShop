package com.example.datn_toystoryshop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.datn_toystoryshop.Detail.Order_Confirm_Detail;
import com.example.datn_toystoryshop.Model.Order_Model;
import com.example.datn_toystoryshop.Model.VnPayCreate_Model;
import com.example.datn_toystoryshop.Model.Vnpay_Model;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayPalActivity extends Activity {

    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_USER_ID = "user_id";
    public static final int REQUEST_CODE_PAYMENT = 1;
    private APIService apiService;
    private WebView webView;

    private Long amount;
    private String userId, orderId;

    private String redirectedUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pal);  // Set layout cho activity này

        webView = findViewById(R.id.web_view);

        System.out.println("PayPalActivity.onCreate");
        apiService = RetrofitClient.getAPIService();
        // Check key và lấy dữ liệu từ Intent, nếu không thì về screen trước
        Intent intent = getIntent();


        orderId = getIntent().getStringExtra("orderId");
        Log.e("PayPalActivity", "pppppppppppppp" + orderId);

        amount = intent.getLongExtra(KEY_AMOUNT, 0);
        userId = intent.getStringExtra(KEY_USER_ID);

        System.out.println("PayPalActivity.onCreate: amount = " + amount + ", user_id = " + userId);

        APIService paymentService = RetrofitClient.getAPIServicePayment();
        Call<Vnpay_Model> call = paymentService.createCheckoutVnPay(new VnPayCreate_Model(amount, userId, "https://github.com/naustudio/vn-payments/issues/25"));

        System.out.println("PayPalActivity.onCreate: call = " + call.request().url());

        call.enqueue(new Callback<Vnpay_Model>() {
            @Override
            public void onResponse(Call<Vnpay_Model> call, Response<Vnpay_Model> response) {
                if (response.isSuccessful()) {
                    Vnpay_Model vnpayModel = response.body();
                    if (vnpayModel != null) {
                        // Lấy url thanh toán từ response
                        String paymentUrl = vnpayModel.getUrl();
                        Log.d("PayPalActivity", "Payment URL: " + paymentUrl);
                        // Chuyển sang trang thanh toán
                        openWebView(paymentUrl);
                    }
                } else {
                    Log.e("PayPalActivity", "Failed to get payment URL: " + response.message());
                    Log.e("PayPalActivity", "Response code: " + response.code());
                    Log.e("PayPalActivity", "Response body: " + response.errorBody());
                    Toast.makeText(PayPalActivity.this, "Failed to get payment URL", Toast.LENGTH_LONG).show();
                    // Trở về màn hình home
                    Intent in = new Intent(PayPalActivity.this, Home_screen.class);
                    in.putExtra("documentId", userId);
                    startActivity(in);
                }
            }

            @Override
            public void onFailure(Call<Vnpay_Model> call, Throwable t) {
                t.printStackTrace();
                Log.e("PayPalActivity", "Failed to get payment URL: " + t.getMessage());
                Toast.makeText(PayPalActivity.this, "Failed to get payment URL", Toast.LENGTH_LONG).show();
                // Trở về màn hình home
                Intent in = new Intent(PayPalActivity.this, Home_screen.class);
                in.putExtra("documentId", userId);
                startActivity(in);
            }
        });
    }
    private void openWebView(String url) {
        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);



        WebViewClient client = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                redirectedUrl = url;
                super.onPageFinished(view, url);
                handlePaymentResult();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                redirectedUrl = url;
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                redirectedUrl = url;
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                redirectedUrl= failingUrl;
                handlePaymentResult();
            }
        };

        webView.setWebViewClient(client);
        webView.loadUrl(url);
    }

    private void handlePaymentResult() {
        // Check url của trang web
        if (redirectedUrl != null) {
            if (redirectedUrl.contains("vnp_TransactionStatus=00")) {
                // Nếu url chứa "vnp_TransactionStatus=00" thì thanh toán thành công
                Log.d("PayPalActivity", "Payment successful.");
                Toast.makeText(this, "Payment successful!", Toast.LENGTH_LONG).show();
                String newStatus = "Chờ lấy hàng"; // Trạng thái mới
                deleteOrder(orderId, newStatus);
                //go to home screen
                Intent in = new Intent(PayPalActivity.this, Thanks_payment.class);
                in.putExtra("documentId", userId);
                in.putExtra("amount", amount);
                Log.e("OrderHistoryAdapter", "j66666666666666666Browse_Fragment  2   " + amount);
                in.putExtra("orderId", orderId);
                startActivity(in);
                finish();
            } else if (redirectedUrl.contains("vnp_TransactionStatus=02")) {
                // Nếu url chứa "vnp_TransactionStatus=02" thì người dùng hủy thanh toán
                Log.d("PayPalActivity", "Payment canceled.");
                Toast.makeText(this, "Payment canceled!", Toast.LENGTH_LONG).show();

                //go to home screen
                Intent in = new Intent(PayPalActivity.this, Home_screen.class);
                in.putExtra("documentId", userId);
                startActivity(in);
                finish();
            } else if (redirectedUrl.contains("Error")) {
                // Nếu url chứa "vnp_TransactionStatus=02" thì người dùng hủy thanh toán
                Log.d("PayPalActivity", "Payment canceled.");
                Toast.makeText(this, "Payment canceled!", Toast.LENGTH_LONG).show();

                //go to home screen
                Intent in = new Intent(PayPalActivity.this, Home_screen.class);
                in.putExtra("documentId", userId);
                startActivity(in);
            }
        }
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
                    Toast.makeText(PayPalActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    Log.d("API", "Cập nhật thành công: " + response.body().toString());
                } else {
                    // Xử lý lỗi trả về từ server
                    Toast.makeText(PayPalActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                    Log.e("API", "Lỗi trả về: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Order_Model> call, Throwable t) {
                // Lỗi kết nối hoặc các lỗi khác
                Toast.makeText(PayPalActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API", "Lỗi kết nối: " + t.getMessage());
            }
        });
    }

}


