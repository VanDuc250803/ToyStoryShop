package com.example.datn_toystoryshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.datn_toystoryshop.Detail.OrderHist_Detail;
import com.example.datn_toystoryshop.Detail.Refund_Detail;
import com.example.datn_toystoryshop.Model.Order_Model;
import com.example.datn_toystoryshop.Model.Product_Model;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.ProductCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Order_Refund_Product_Adapter extends RecyclerView.Adapter<Order_Refund_Product_Adapter.ProductViewHolder> {
    private Context context;
    private List<Order_Model.ProductDetail> productList;
    private APIService apiService;
    private String orderId,refundId;
    private String documentId;

    public Order_Refund_Product_Adapter(Context context, List<Order_Model.ProductDetail> productList , APIService apiService, String orderId, String documentId, String refundId) {
        this.context = context;
        this.productList = productList;
        this.apiService = apiService;
        this.refundId = refundId;
        this.orderId = orderId;
        this.documentId = documentId;
    }

    @NonNull
    @Override
    public Order_Refund_Product_Adapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history_1, parent, false);
        return new Order_Refund_Product_Adapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Order_Refund_Product_Adapter.ProductViewHolder holder, int position) {
        Order_Model.ProductDetail product = productList.get(position);
        Log.e("OrderHistoryAdapter", "j66666666666666666gggg 11 2" + orderId);
        // Hiển thị thông tin sản phẩm
        holder.productQuantity.setText(String.format("x %d", product.getQuantity()));
        holder.tvProductType.setText(product.getProdSpecification());
        holder.productPrice.setText(String.format("%,.0fđ", product.getRevenue()));
        holder.itemView.setOnClickListener(v -> {
            // Chuyển đến màn hình chi tiết sản phẩm
            Intent intent = new Intent(context, Refund_Detail.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("refundId", refundId);
            intent.putExtra("documentId", documentId);
            context.startActivity(intent);
        });
        String prodId = product.getProdId();
        loadProductById(apiService, prodId, new ProductCallback() {
            @Override
            public void onSuccess(Product_Model product) {
                List<String> images = product.getImgPro();
                if (images != null && !images.isEmpty()) {
                    Glide.with(context).load(images.get(0)).into(holder.productImage);
                }
                holder.productName.setText(product.getNamePro());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProductList(List<Order_Model.ProductDetail> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productQuantity, productPrice,tvProductType;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.ivProductImage);
            productName = itemView.findViewById(R.id.tvProductName);
            productQuantity = itemView.findViewById(R.id.tvProductQuantity);
            productPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductType = itemView.findViewById(R.id.tvProductType);

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
