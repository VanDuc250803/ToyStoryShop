package com.example.datn_toystoryshop.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datn_toystoryshop.R;

import java.util.List;

public class Home_Image_Adapter extends RecyclerView.Adapter<Home_Image_Adapter.ViewHolder> {

    private List<Integer> imageList;
    private OnImageClickListener listener;

    public Home_Image_Adapter(List<Integer> imageList, OnImageClickListener listener) {
        if (imageList == null || imageList.isEmpty()) {
            throw new IllegalArgumentException("Image list must not be null or empty");
        }
        this.imageList = imageList;
        this.listener = listener;
    }

    public interface OnImageClickListener {
        void onImageClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int imageResId = imageList.get(position);
        holder.imageView.setImageResource(imageResId);

        // Đặt sự kiện nhấp vào
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}

