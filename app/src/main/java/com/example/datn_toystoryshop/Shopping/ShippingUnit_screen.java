package com.example.datn_toystoryshop.Shopping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datn_toystoryshop.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ShippingUnit_screen extends AppCompatActivity {
    private ImageView imgBack;
    private TextView tvFastShipping, tvExpressShipping, tvFastShippingOldPrice, tvFastShippingNewPrice, tvExpressShippingPrice, btnConfirm, tvFastShippingDetails, tvExpressShippingDetails, tvExpressShippingNewPrice;
    private RadioButton radioexpress, radiofast;
    private LinearLayout LLexpress, LLfast;
    private String shipping_method = "";
    private String shipping_price = "";
    private double totalShipDiscount;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;

    private String getFormattedDate(int daysToAdd, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, new Locale("vi", "VN"));
        return dateFormat.format(calendar.getTime());
    }

    private String formatCurrency(double amount) {
        // Định dạng số tiền theo kiểu VND
        NumberFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount) + "đ";
    }

    private void updateShippingPrice() {
        if (totalShipDiscount == 0) {
            tvFastShippingOldPrice.setText("₫40.000"); // Hiển thị giá cũ mặc định
            tvExpressShippingPrice.setText("₫80.000"); // Hiển thị giá cũ mặc định
            tvFastShippingNewPrice.setVisibility(View.GONE); // Ẩn giá mới nếu không có giảm giá
            tvExpressShippingNewPrice.setVisibility(View.GONE); // Ẩn giá mới nếu không có giảm giá
            return;
        }
        if (radiofast.isChecked()) {
            // Tính toán giá mới cho giao hàng nhanh
            double newPriceFast = 40000.0 - totalShipDiscount;

            // Hiển thị giá cũ (gạch ngang) cho giao hàng nhanh
            String oldPriceFastText = formatCurrency(40000.0);
            SpannableString spannableOldPriceFast = new SpannableString(oldPriceFastText);
            spannableOldPriceFast.setSpan(new StrikethroughSpan(), 0, oldPriceFastText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableOldPriceFast.setSpan(new ForegroundColorSpan(Color.parseColor("#888888")), 0, oldPriceFastText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvFastShippingOldPrice.setText(spannableOldPriceFast);

            // Hiển thị giá mới cho giao hàng nhanh
            tvFastShippingNewPrice.setVisibility(View.VISIBLE);
            tvFastShippingNewPrice.setText(formatCurrency(newPriceFast));

            // Ẩn giá mới của giao hàng hỏa tốc
            tvExpressShippingNewPrice.setVisibility(View.GONE);
            tvExpressShippingPrice.setText("₫80.000");
        } else if (radioexpress.isChecked()) {
            // Hiển thị giá mới cho giao hàng hỏa tốc
            double newPriceExpress = 80000.0 - totalShipDiscount;
            tvExpressShippingNewPrice.setVisibility(View.VISIBLE);
            tvExpressShippingNewPrice.setText(formatCurrency(newPriceExpress));

            // Hiển thị giá cũ (gạch ngang) cho giao hàng hỏa tốc
            String oldPriceExpressText = formatCurrency(80000.0);
            SpannableString spannableOldPriceExpress = new SpannableString(oldPriceExpressText);
            spannableOldPriceExpress.setSpan(new StrikethroughSpan(), 0, oldPriceExpressText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableOldPriceExpress.setSpan(new ForegroundColorSpan(Color.parseColor("#888888")), 0, oldPriceExpressText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvExpressShippingPrice.setText(spannableOldPriceExpress);

            // Ẩn giá mới của giao hàng nhanh
            tvFastShippingNewPrice.setVisibility(View.GONE);
            tvFastShippingOldPrice.setText("₫40.000");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_unit_screen);

        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);
        imgBack = findViewById(R.id.imgBack);
        tvFastShipping = findViewById(R.id.tvFastShipping);
        tvExpressShipping = findViewById(R.id.tvExpressShipping);
        tvFastShippingOldPrice = findViewById(R.id.tvFastShippingOldPrice);
        tvFastShippingNewPrice = findViewById(R.id.tvFastShippingNewPrice);
        tvExpressShippingNewPrice = findViewById(R.id.tvExpressShippingNewPrice);
        tvExpressShippingPrice = findViewById(R.id.tvExpressShippingPrice);
        btnConfirm = findViewById(R.id.btnConfirm);
        tvFastShippingDetails = findViewById(R.id.tvFastShippingDetails);
        tvExpressShippingDetails = findViewById(R.id.tvExpressShippingDetails);
        radioexpress = findViewById(R.id.radioexpress);
        radiofast = findViewById(R.id.radiofast);
        LLexpress = findViewById(R.id.LLexpress);
        LLfast = findViewById(R.id.LLfast);

        if (nightMode) {
            imgBack.setImageResource(R.drawable.back_icon);
        } else {
            imgBack.setImageResource(R.drawable.back_icon_1);
        }

        // Đặt ngày giao hàng dự kiến
        String FastDate = getFormattedDate(7, "'ngày' dd 'tháng' MM 'năm' yyyy");
        String ExpressDate = getFormattedDate(2, "'ngày' dd 'tháng' MM 'năm' yyyy");
        tvFastShippingDetails.setText("Nhận Voucher trị giá ₫15.000 nếu đơn hàng được giao đến bạn sau " + FastDate);
        tvExpressShippingDetails.setText("Nhận Voucher trị giá ₫15.000 nếu đơn hàng được giao đến bạn sau " + ExpressDate);

        Intent intent = getIntent();
        totalShipDiscount = intent.getDoubleExtra("totalShipDiscount", 0); // Giá trị mặc định là 0 nếu không có dữ liệu
        String ShipDiscount = intent.getStringExtra("ShipDiscount");
        Log.e("API_ERROR", "bttttttttttttttttttttt" + totalShipDiscount);
        Log.e("API_ERROR", "btttttttttttttttttttttff " + ShipDiscount);

        if (ShipDiscount != null) {
            if ("Nhanh".equals(ShipDiscount)) {
                radiofast.setChecked(true);
                radioexpress.setChecked(false);
            }
            if ("Hỏa Tốc".equals(ShipDiscount)) {
                radioexpress.setChecked(true);
                radiofast.setChecked(false);
            }
        }

        // Gọi cập nhật giá lần đầu
        updateShippingPrice();

        LLfast.setOnClickListener(v -> {
            radiofast.setChecked(true);
            radioexpress.setChecked(false);
            updateShippingPrice(); // Cập nhật giá
        });

        LLexpress.setOnClickListener(v -> {
            radioexpress.setChecked(true);
            radiofast.setChecked(false);
            updateShippingPrice(); // Cập nhật giá
        });

        radiofast.setOnClickListener(v -> {
            radiofast.setChecked(true);
            radioexpress.setChecked(false);
            updateShippingPrice(); // Cập nhật giá
        });

        radioexpress.setOnClickListener(v -> {
            radioexpress.setChecked(true);
            radiofast.setChecked(false);
            updateShippingPrice(); // Cập nhật giá
        });

        btnConfirm.setOnClickListener(v -> {
            if (radiofast.isChecked()) {
                shipping_method = "Nhanh";
                shipping_price = "₫40.000";
            } else if (radioexpress.isChecked()) {
                shipping_method = "Hỏa Tốc";
                shipping_price = "₫80.000";
            }
            Intent resultIntent = new Intent();
            resultIntent.putExtra("shipping_method", shipping_method);
            resultIntent.putExtra("shipping_price", shipping_price);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        imgBack.setOnClickListener(v -> onBackPressed());
    }
}
