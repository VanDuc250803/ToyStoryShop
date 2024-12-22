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
import com.example.datn_toystoryshop.Model.Product_Model;
import com.example.datn_toystoryshop.Detail.Product_detail;
import com.example.datn_toystoryshop.R;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Home_search_Adapter extends RecyclerView.Adapter<Home_search_Adapter.SuggestionViewHolder> {
    private List<Product_Model> suggestionList;
    private List<Product_Model> originalList;
    private Context context;
    private String documentId;

    public Home_search_Adapter(Context context, List<Product_Model> suggestionList, String documentId) {
        this.context = context;
        this.suggestionList = suggestionList;
        this.originalList = new ArrayList<>(suggestionList); // Sao chép danh sách ban đầu
        this.documentId = documentId;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_suggestion, parent, false);
        return new SuggestionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        Log.e("OrderHistoryAdapter", "j66666666666666666Suggestion_Adapter" + documentId);
        Product_Model product = suggestionList.get(position);

        holder.tvName.setText(product.getNamePro());
        holder.tvPrice.setText(String.format("%,.0fđ", product.getPrice()));
        // Kiểm tra danh sách ảnh của sản phẩm có ít nhất 1 ảnh, nếu có, chỉ hiển thị ảnh đầu tiên
        List<String> images = product.getImgPro();
        if (images != null && !images.isEmpty()) {
            // Tải ảnh đầu tiên trong danh sách
            Glide.with(context)
                    .load(images.get(0)) // Chỉ lấy ảnh đầu tiên
//                    .placeholder(R.drawable.product1)
                    .into(holder.imgProduct);
        } else {
            // Nếu không có ảnh nào, sử dụng ảnh mặc định
            holder.imgProduct.setImageResource(R.drawable.product1);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Product_detail.class);
                intent.putExtra("documentId", documentId);
                intent.putExtra("productId", product.get_id());// Truyền mã ID của sản phẩm
                intent.putExtra("owerId", product.getOwerId());// Truyền ID của chủ sở hữu sản phẩm
                intent.putExtra("statusPro", product.isStatusPro());// Truyền trạng thái tồn kho của sản phẩm (true nếu còn hàng, false nếu hết hàng)
                intent.putExtra("productPrice", product.getPrice());// Truyền giá của sản phẩm
                intent.putExtra("desPro", product.getDesPro());// Truyền mô tả của sản phẩm
                intent.putExtra("creatDatePro", product.getCreatDatePro());// Truyền ngày tạo sản phẩm
                intent.putExtra("quantity", product.getQuantity());// Truyền số lượng sản phẩm có sẵn
                intent.putExtra("listPro", product.getListPro());// Truyền danh sách trạng thái của sản phẩm (danh sách dưới dạng chuỗi)
                intent.putStringArrayListExtra("productImg", new ArrayList<>(product.getImgPro()));// Truyền danh sách URL hình ảnh của sản phẩm
                intent.putExtra("productName", product.getNamePro());// Truyền tên của sản phẩm
                intent.putExtra("cateId", product.getCateId());// Truyền ID danh mục của sản phẩm
                intent.putExtra("brand", product.getBrand());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestionList.size();
    }

    // Phương thức loại bỏ dấu trong chuỗi
    private String removeDiacritics(String str) {
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    // Phương thức lọc danh sách dựa trên tên sản phẩm không dấu
    public void filterList(String query) {
        String queryWithoutDiacritics = removeDiacritics(query).toLowerCase();
        suggestionList.clear();

        if (query.isEmpty()) {
            suggestionList.addAll(originalList);
        } else {
            for (Product_Model product : originalList) {
                String productNameWithoutDiacritics = removeDiacritics(product.getNamePro()).toLowerCase();
                if (productNameWithoutDiacritics.contains(queryWithoutDiacritics)) {
                    suggestionList.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView imgProduct;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.suggestion_product_name);
            tvPrice = itemView.findViewById(R.id.suggestion_product_price);
            imgProduct = itemView.findViewById(R.id.suggestion_product_img);
        }
    }
}
