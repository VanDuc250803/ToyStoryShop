package com.example.datn_toystoryshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.datn_toystoryshop.Model.Order_Model;
import com.example.datn_toystoryshop.Model.Product_Model;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.ProductCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHist_Detail_Adapter extends RecyclerView.Adapter<OrderHist_Detail_Adapter.ViewHolder> {

    private Context context;
    private List<Order_Model.ProductDetail> productList; // Danh sách sản phẩm từ đơn hàng
    private APIService apiService;

    // Constructor chính
    public OrderHist_Detail_Adapter(Context context, List<Order_Model.ProductDetail> productList, APIService apiService) {
        this.context = context;
        this.productList = productList;
        this.apiService = apiService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_order_hist_detail_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order_Model.ProductDetail product = productList.get(position);

        // Hiển thị thông tin sản phẩm
       // holder.tvProductName.setText(product.getProdSpecification());
        holder.tvProductType.setText(product.getProdSpecification());
        holder.tvProductQuantity.setText("x" + product.getQuantity());
        holder.tvProductPrice.setText(String.format("%,.0fđ", product.getRevenue()));
        String prodId = product.getProdId();
        loadProductById(apiService, prodId, new ProductCallback() {
            @Override
            public void onSuccess(Product_Model product) {
                List<String> images = product.getImgPro();
                if (images != null && !images.isEmpty()) {
                    Glide.with(context).load(images.get(0)).into(holder.ivProductImage);
                }
                holder.tvProductName.setText(product.getNamePro());
//
//                holder.tvProductQuantity.setText("x" + product.getQuantity());
//                holder.tvProductPrice.setText(String.format("₫%,.0f", product.getRevenue()));
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private TextView tvProductName, tvProductQuantity, tvProductPrice,tvProductType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductType = itemView.findViewById(R.id.tvProductType);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }

    // Phương thức gọi API để lấy chi tiết sản phẩm
    private void loadProductById(APIService apiService, String prodId, ProductCallback callback) {
        Call<Product_Model> call = apiService.getProductById(prodId);
        call.enqueue(new Callback<Product_Model>() {
            @Override
            public void onResponse(Call<Product_Model> call, Response<Product_Model> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Throwable("Product not found"));
                }
            }

            @Override
            public void onFailure(Call<Product_Model> call, Throwable t) {
                callback.onFailure(t);
            }
        });
}}
