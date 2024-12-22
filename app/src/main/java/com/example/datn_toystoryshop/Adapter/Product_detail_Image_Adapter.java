package com.example.datn_toystoryshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.datn_toystoryshop.R;

import java.util.List;

public class Product_detail_Image_Adapter extends RecyclerView.Adapter<Product_detail_Image_Adapter.ImageViewHolder> {
    private List<String> productImages;
    private Context context;

    public Product_detail_Image_Adapter(Context context, List<String> productImages) {
        this.context = context;
        this.productImages = productImages;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Glide.with(context).load(productImages.get(position)).placeholder(R.drawable.product1).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return productImages.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}