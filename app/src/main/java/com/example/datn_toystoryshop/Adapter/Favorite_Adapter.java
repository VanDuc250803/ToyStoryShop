package com.example.datn_toystoryshop.Adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.datn_toystoryshop.Model.Favorite_Model;
import com.example.datn_toystoryshop.Model.Product_Model;
import com.example.datn_toystoryshop.Detail.Product_detail;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.example.datn_toystoryshop.Server.ProductCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Favorite_Adapter extends RecyclerView.Adapter<Favorite_Adapter.FavoriteViewHolder> {
    private Context context;
    private List<Favorite_Model> favoriteList;
    private com.example.datn_toystoryshop.Server.APIService APIService;
    private String documentId;

    public Favorite_Adapter(Context context, List<Favorite_Model> favoriteList, APIService apiService, String documentId) {
        this.context = context;
        this.favoriteList = favoriteList;
        this.APIService = apiService;
        this.documentId = documentId;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Log.e("OrderHistoryAdapter", "j66666666666666666Favorite_Adapter" + documentId);
        Favorite_Model favorite = favoriteList.get(position);
        String favoriteId = favorite.get_id();

        String prodId = favorite.getProdId();
        // Gọi phương thức lấy dữ liệu từ MongoDB với prodId
        loadProductById(APIService, prodId, new ProductCallback() {
            @Override
            public void onSuccess(Product_Model product) {
                holder.tvTen.setText(product.getNamePro());
                holder.tvGia.setText(String.format("%,.0fđ", product.getPrice()));
                List<String> images = product.getImgPro();
                if (images != null && !images.isEmpty()) {
                    Glide.with(context).load(images.get(0)).into(holder.imageViewFav);
                }
                // Thiết lập sự kiện click để mở màn hình chi tiết
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Product_detail.class);

                        // Truyền các thuộc tính sản phẩm qua Intent
                        intent.putExtra("documentId", documentId);

                        intent.putExtra("productId", product.get_id());
                        intent.putExtra("owerId", product.getOwerId());
                        intent.putExtra("statusPro", product.isStatusPro());
                        intent.putExtra("productPrice", product.getPrice());
                        intent.putExtra("desPro", product.getDesPro());
                        intent.putExtra("creatDatePro", product.getCreatDatePro());
                        intent.putExtra("quantity", product.getQuantity());
                        intent.putExtra("listPro", product.getListPro());
                        intent.putStringArrayListExtra("productImg", new ArrayList<>(product.getImgPro()));
                        intent.putExtra("productName", product.getNamePro());
                        intent.putExtra("cateId", product.getCateId());
                        intent.putExtra("brand", product.getBrand());
                        // Truyền _id của Favorite_Model vào Intent
                        intent.putExtra("favoriteId", favoriteId);
                        context.startActivity(intent);
                    }
                });
                holder.heartIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFavorite(favorite.getProdId(), holder);
                        Log.e("FavoriteAdapter", "hhhhhhhhhhhhhhh " + favorite.getProdId());
                        holder.heartIcon.setColorFilter(Color.parseColor("#A09595"));
                        // Nếu bạn muốn thêm hiệu ứng thì có thể thêm logic ở đây
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("FavoriteAdapter", "Failed to load product details: " + t.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTen, tvGia, tvStatus;
        ImageView imageViewFav, heartIcon;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvTen);
            tvGia = itemView.findViewById(R.id.tvGia);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            imageViewFav = itemView.findViewById(R.id.image_view_fav);

            heartIcon = itemView.findViewById(R.id.heart_icon);
        }
    }

    private void loadProductById(APIService apiService, String prodId, ProductCallback callback) {
        Call<Product_Model> call = apiService.getProductById(prodId);
        call.enqueue(new Callback<Product_Model>() {
            @Override
            public void onResponse(Call<Product_Model> call, Response<Product_Model> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product_Model product = response.body();
                    Log.d("FavoriteAdapter", "Product retrieved: " + product.getNamePro() + ", Price: " + product.getPrice());
                    callback.onSuccess(product);
                } else {
                    Log.e("FavoriteAdapter", "Response unsuccessful or product body is null");
                }
            }

            @Override
            public void onFailure(Call<Product_Model> call, Throwable t) {
                Log.e("FavoriteAdapter", "API call failed: " + t.getMessage());
                callback.onFailure(t);
            }
        });
    }

    private void deleteFavorite(String productId, FavoriteViewHolder holder) {
        // Giả sử bạn đã có một APIService đã được định nghĩa cho việc xóa yêu thích
        Call<Void> call = APIService.deleteFavorite(productId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Xóa sản phẩm yêu thích thành công
                    favoriteList.remove(holder.getAdapterPosition()); // Xóa sản phẩm khỏi danh sách hiển thị
                    notifyItemRemoved(holder.getAdapterPosition()); // Cập nhật RecyclerView
                    notifyItemRangeChanged(holder.getAdapterPosition(), favoriteList.size()); // Cập nhật lại vị trí của các item
                } else {
                    Log.e("FavoriteAdapter", "Failed to delete favorite: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("FavoriteAdapter", "API call failed: " + t.getMessage());
            }
        });
    }
}