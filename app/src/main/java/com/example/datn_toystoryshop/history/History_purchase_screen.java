package com.example.datn_toystoryshop.history;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.datn_toystoryshop.history.History_purchase.Canceled_Fragment;
import com.example.datn_toystoryshop.history.History_purchase.Confirm_Fragment;
import com.example.datn_toystoryshop.history.History_purchase.Delivered_Fragment;
import com.example.datn_toystoryshop.history.History_purchase.Delivery_Fragment;
import com.example.datn_toystoryshop.history.History_purchase.GetGoods_Fragment;
import com.example.datn_toystoryshop.history.History_purchase.Refund_Fragment;
import com.example.datn_toystoryshop.Home_screen;
import com.example.datn_toystoryshop.R;

public class History_purchase_screen extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    private boolean nightMode;
    private HorizontalScrollView horizontalScrollView;
    private String documentId;
    private ImageView imgBack;
    private LinearLayout confirm, get_goods, delivery, return_goods, delivered, canceled;
    private TextView confirmText, getGoodsText, deliveryText, return_goodsText, deliveredText, canceledText;
    private View confirmView, getGoodsView, deliveryView, return_goodsView, deliveredView, canceledView;
    private ViewPager2 viewPager;
    private FragmentStateAdapter fragmentStateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_purchase_screen);

        confirm = findViewById(R.id.confirm);
        get_goods = findViewById(R.id.get_goods);
        delivery = findViewById(R.id.delivery);
        return_goods = findViewById(R.id.return_goods);
        horizontalScrollView = findViewById(R.id.horizontalScrollView);
        delivered = findViewById(R.id.delivered);
        canceled = findViewById(R.id.canceled);
        confirmText = findViewById(R.id.confirmText);
        getGoodsText = findViewById(R.id.getGoodsText);
        deliveryText = findViewById(R.id.deliveryText);
        return_goodsText = findViewById(R.id.return_goodsText);
        deliveredText = findViewById(R.id.deliveredText);
        canceledText = findViewById(R.id.canceledText);
        confirmView = findViewById(R.id.confirmView);
        getGoodsView = findViewById(R.id.getGoodsView);
        deliveryView = findViewById(R.id.deliveryView);
        return_goodsView = findViewById(R.id.return_goodsView);
        deliveredView = findViewById(R.id.deliveredView);
        canceledView = findViewById(R.id.canceledView);
        viewPager = findViewById(R.id.viewPager);

        imgBack = findViewById(R.id.ivBack);
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);
        if (nightMode) {
            imgBack.setImageResource(R.drawable.back_icon);
        } else {
            imgBack.setImageResource(R.drawable.back_icon_1);
        }



        Intent intent = getIntent();
        documentId = intent.getStringExtra("documentId");

        imgBack.setOnClickListener(v -> {

                Intent intent1 = new Intent(History_purchase_screen.this, Home_screen.class);
                intent1.putExtra("documentId", documentId);
                startActivity(intent1);
                finish(); // Kết thúc Activity nếu không có Fragment nào

        });


        horizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                // Lấy vị trí của delivered trong HorizontalScrollView
                int scrollToX = delivered.getLeft();
                horizontalScrollView.smoothScrollTo(scrollToX, 0); // Cuộn mượt đến LinearLayout "delivered"
            }
        });
        // Cài đặt ViewPager2 và FragmentStateAdapter
        fragmentStateAdapter = new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @Override
            public Fragment createFragment(int position) {
                Fragment fragment;
                switch (position) {
                    case 0:
                        fragment = new Confirm_Fragment();
                        break;
                    case 1:
                        fragment = new GetGoods_Fragment();
                        break;
                    case 2:
                        fragment = new Delivery_Fragment();
                        break;
                    case 3:
                        fragment = new Refund_Fragment();
                        break;
                    case 4:
                        fragment = new Delivered_Fragment();
                        break;
                    case 5:
                        fragment = new Canceled_Fragment();
                        break;
                    default:
                        fragment = new Delivered_Fragment();
                }
                if (documentId != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("documentId", documentId);
                    fragment.setArguments(bundle);
                }
                return fragment;
            }

            @Override
            public int getItemCount() {
                return 6; // Số lượng Fragment
            }
        };

        viewPager.setAdapter(fragmentStateAdapter);

        // Đồng bộ tab và ViewPager2
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        highlightTextView(confirmText);
                        break;
                    case 1:
                        highlightTextView(getGoodsText);
                        break;
                    case 2:
                        highlightTextView(deliveryText);
                        break;
                    case 3:
                        highlightTextView(return_goodsText);
                        break;
                    case 4:
                        highlightTextView(deliveredText);
                        break;
                    case 5:
                        highlightTextView(canceledText);
                        break;
                }
                LinearLayout targetLayout = null;
                switch (position) {
                    case 0: targetLayout = confirm; break;
                    case 1: targetLayout = get_goods; break;
                    case 2: targetLayout = delivery; break;
                    case 3: targetLayout = return_goods; break;
                    case 4: targetLayout = delivered; break;
                    case 5: targetLayout = canceled; break;
                }

                if (targetLayout != null) {
                    final LinearLayout finalTargetLayout = targetLayout;
                    horizontalScrollView.post(() -> {
                        int scrollToX = finalTargetLayout.getLeft() - (horizontalScrollView.getWidth() / 2) + (finalTargetLayout.getWidth() / 2);
                        horizontalScrollView.smoothScrollTo(scrollToX, 0);
                    });
                }
            }
        });

        // Đặt sự kiện click cho các tab
        setupTab(confirm, 0);
        setupTab(get_goods, 1);
        setupTab(delivery, 2);
        setupTab(return_goods, 3);
        setupTab(delivered, 4);
        setupTab(canceled, 5);

        // Đặt ViewPager mặc định hiển thị "Delivered"
        viewPager.setCurrentItem(4, false);
        highlightTextView(deliveredText);
    }

    private void setupTab(LinearLayout layout, int position) {
        layout.setOnClickListener(v -> viewPager.setCurrentItem(position, true));
    }

    private void highlightTextView(TextView textView) {
        confirmView.setVisibility(View.GONE);
        getGoodsView.setVisibility(View.GONE);
        deliveryView.setVisibility(View.GONE);
        return_goodsView.setVisibility(View.GONE);
        deliveredView.setVisibility(View.GONE);
        canceledView.setVisibility(View.GONE);

        confirmText.setTextColor(getResources().getColor(android.R.color.black));
        getGoodsText.setTextColor(getResources().getColor(android.R.color.black));
        deliveryText.setTextColor(getResources().getColor(android.R.color.black));
        return_goodsText.setTextColor(getResources().getColor(android.R.color.black));
        deliveredText.setTextColor(getResources().getColor(android.R.color.black));
        canceledText.setTextColor(getResources().getColor(android.R.color.black));

        confirmText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        getGoodsText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        deliveryText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        return_goodsText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        deliveredText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        canceledText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);

        if (textView == confirmText) {
            confirmView.setVisibility(View.VISIBLE);
        } else if (textView == getGoodsText) {
            getGoodsView.setVisibility(View.VISIBLE);
        } else if (textView == deliveryText) {
            deliveryView.setVisibility(View.VISIBLE);
        } else if (textView == return_goodsText) {
            return_goodsView.setVisibility(View.VISIBLE);
        } else if (textView == deliveredText) {
            deliveredView.setVisibility(View.VISIBLE);
        } else if (textView == canceledText) {
            canceledView.setVisibility(View.VISIBLE);
        }

        textView.setTextColor(getResources().getColor(R.color.highlight_color));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
    }

}