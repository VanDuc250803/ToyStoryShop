package com.example.datn_toystoryshop.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.datn_toystoryshop.Adapter.Product_Star_Adapter;
import com.example.datn_toystoryshop.Model.Product_Model;
import com.example.datn_toystoryshop.R;

import java.util.List;

public class Popular_screen extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewPopular;
    private Product_Star_Adapter productAdapter;
    private List<Product_Model> productList;
    private String documentId;
    private SharedPreferences sharedPreferences;
    private boolean nightMode;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_screen);
        EditText searchBar = findViewById(R.id.search_bar);



        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);
        // Khởi tạo RecyclerView
        recyclerViewPopular = findViewById(R.id.recyPopuPro);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(this));
        imgBack = findViewById(R.id.ivBack);
        if (nightMode) {
            imgBack.setImageResource(R.drawable.back_icon);
        } else {
            imgBack.setImageResource(R.drawable.back_icon_1);
        }
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                productAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        Intent intent = getIntent();
        documentId = intent.getStringExtra("documentId");
        Log.e("OrderHistoryAdapter", "j8888888888888888Popular_screen" + documentId);
        productList = (List<Product_Model>) getIntent().getSerializableExtra("productListPopu");


        // Thiết lập Adapter
        productAdapter = new Product_Star_Adapter(this, productList, documentId);
        recyclerViewPopular.setAdapter(productAdapter);

        // Thêm ItemDecoration để tạo khoảng cách dưới mỗi item
        recyclerViewPopular.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                // Thêm khoảng cách dưới mỗi item (16px)
                outRect.bottom = 16;
            }
        });
        imgBack.setOnClickListener(v -> onBackPressed());
        swipeRefreshLayout.setOnRefreshListener(() -> {
            productAdapter.notifyDataSetChanged(); // Làm mới danh sách trong Adapter

            // Dừng hiệu ứng làm mới
            swipeRefreshLayout.setRefreshing(false);        });
    }
}