package com.example.datn_toystoryshop.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.datn_toystoryshop.Model.Order_Detail_Model;
import com.example.datn_toystoryshop.Model.Product_Model;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.ProductCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Order_Adapter_Detail extends RecyclerView.Adapter<Order_Adapter_Detail.ProductViewHolder> {
    private List<Order_Detail_Model> productList;
    private APIService apiService;
    private TotalAmountCallback totalAmountCallback;
    private String ProductType;

    public interface TotalAmountCallback {
        void onTotalAmountCalculated(double totalAmount, int quantity, String productType);
    }

    public Order_Adapter_Detail(List<Order_Detail_Model> productList, APIService apiService, TotalAmountCallback totalAmountCallback) {
        this.productList = productList;
        this.apiService = apiService;
        this.totalAmountCallback = totalAmountCallback;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_oder, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Order_Detail_Model product = productList.get(position);
        holder.productName.setText(product.getProductId());  // Hiển thị mã sản phẩm
        holder.productQuantity.setText("x" + product.getCurrentQuantity());  // Hiển thị số lượng
        holder.productType.setText(product.getSelectedColor());  // Hiển thị màu sắc
        ProductType = product.getSelectedColor();
        // Nếu cần hiển thị ảnh sản phẩm, bạn có thể sử dụng thư viện Glide hoặc Picasso để tải ảnh từ URL
        Glide.with(holder.productImage.getContext()).load(product.getProductImg()).into(holder.productImage);
        // Gọi API để lấy tên sản phẩm
        loadProductById(apiService, product.getProductId(), new ProductCallback() {
            @Override
            public void onSuccess(Product_Model productModel) {
                double totalPrice;
                double Price = productModel.getPrice();
                int quantity = product.getCurrentQuantity();
                double totalAmount = Price * quantity;

                holder.productName.setText(productModel.getNamePro()); // Cập nhật tên sản phẩm
                holder.productPrice.setText(String.format("%,.0fđ", productModel.getPrice())); // Cập nhật tên sản phẩm
                Log.e("Oder_Adapter", "aaaaaaaaaaaaaaaaaaaaaavdv: " + totalAmount);
                // Truyền totalAmount về màn hình chính
                if (totalAmountCallback != null) {
                    totalAmountCallback.onTotalAmountCalculated(totalAmount, quantity, ProductType);
                }

            }

            @Override
            public void onFailure(Throwable t) {
                holder.productName.setText("Unknown Product"); // Hiển thị thông báo lỗi nếu không lấy được
                Log.e("Oder_Adapter", "Failed to load product name: " + t.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productQuantity, productType, productPrice;
        ImageView productImage;  // Nếu bạn cần hiển thị ảnh sản phẩm

        public ProductViewHolder(View view) {
            super(view);
            productName = view.findViewById(R.id.product_name);
            productQuantity = view.findViewById(R.id.product_quantity);
            productType = view.findViewById(R.id.product_type);
            productImage = view.findViewById(R.id.product_image);  // Khởi tạo ImageView nếu bạn cần hiển thị ảnh
            productPrice = view.findViewById(R.id.product_price);
        }
    }

    // Hàm gọi API
    private void loadProductById(APIService apiService, String prodId, ProductCallback callback) {
        Call<Product_Model> call = apiService.getProductById(prodId);
        call.enqueue(new Callback<Product_Model>() {
            @Override
            public void onResponse(Call<Product_Model> call, Response<Product_Model> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Response unsuccessful or body is null"));
                }
            }

            @Override
            public void onFailure(Call<Product_Model> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }
}
