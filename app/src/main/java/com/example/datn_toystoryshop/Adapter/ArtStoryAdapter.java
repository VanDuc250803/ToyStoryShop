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
import com.example.datn_toystoryshop.Detail.ArtStory_detail;
import com.example.datn_toystoryshop.Model.ArtStoryModel;
import com.example.datn_toystoryshop.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArtStoryAdapter extends RecyclerView.Adapter<ArtStoryAdapter.ViewHolder> {
    private Context context;
    private List<ArtStoryModel> artStoryList;
    private String documentId;

    public ArtStoryAdapter(Context context, List<ArtStoryModel> artStoryList,String documentId) {
        this.context = context;
        this.artStoryList = artStoryList;
        this.documentId = documentId;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artstory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArtStoryModel artStory = artStoryList.get(position);

        // Thêm log để kiểm tra title của mỗi mục
        Log.d("ArtStoryAdapter", "Number of items in artStoryList: " + artStoryList.size());
        holder.title.setText(artStory.getTitle());
        holder.author.setText(artStory.getAuthor());
        holder.content.setText(artStory.getContent());

        if (artStory.getDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(artStory.getDate());
            holder.date.setText(formattedDate);
        }
        // Kiểm tra xem danh sách ảnh có tồn tại không và có phần tử nào không
        if (artStory.getImageUrl() != null && !artStory.getImageUrl().isEmpty()) {
            // Lấy ảnh đầu tiên
            String firstImageUrl = artStory.getImageUrl().get(0);

            // Thêm log để kiểm tra URL của ảnh đầu tiên
            Log.d("ArtStoryAdapter", "First image URL: " + firstImageUrl);

            Glide.with(context).load(firstImageUrl).override(120, 120).into(holder.image);
        }
        // Thiết lập sự kiện click để mở ArtStoryDetailActivity và truyền dữ liệu
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArtStory_detail.class);
            intent.putExtra("documentId", documentId);
            intent.putExtra("title", artStory.getTitle());
            intent.putExtra("author", artStory.getAuthor());
            intent.putExtra("date", artStory.getDate().getTime()); // truyền date dưới dạng milliseconds
            intent.putExtra("content", artStory.getContent());
            intent.putStringArrayListExtra("imageUrl", new ArrayList<>(artStory.getImageUrl()));
            intent.putStringArrayListExtra("caption", new ArrayList<>(artStory.getCaption()));
            context.startActivity(intent);
        });


    }


    @Override
    public int getItemCount() {
        return artStoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, content, date;

        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.articleTitle);
            author = itemView.findViewById(R.id.articleAuthor);
            image = itemView.findViewById(R.id.articleImage);
            content = itemView.findViewById(R.id.articleContent);
            date = itemView.findViewById(R.id.articleDate);
        }
    }
}
