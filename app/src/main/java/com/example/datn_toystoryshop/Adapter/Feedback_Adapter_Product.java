package com.example.datn_toystoryshop.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datn_toystoryshop.Model.Feeback_Model;
import com.example.datn_toystoryshop.R;
import com.example.datn_toystoryshop.Server.APIService;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Feedback_Adapter_Product extends RecyclerView.Adapter<Feedback_Adapter_Product.FeedbackViewHolder> {

    private Context context;
    private List<Feeback_Model> feedbackList;
    private FirebaseFirestore db;
    private String documentId, name;

    public Feedback_Adapter_Product(Context context, List<Feeback_Model> feedbackList, APIService apiService, String documentId) {
        this.context = context;
        this.feedbackList = feedbackList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public Feedback_Adapter_Product.FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feedback, parent, false);
        return new Feedback_Adapter_Product.FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Feedback_Adapter_Product.FeedbackViewHolder holder, int position) {
        Feeback_Model feedback = feedbackList.get(position);

        // holder.userName.setText(feedback.getCusId()); // Hoặc thay bằng tên khách hàng thực
        loadUserDataByDocumentId(feedback.getCusId(), holder.userName);
        holder.tvContent.setText(feedback.getContent());
        holder.tvDateFeed.setText(feedback.getDateFeed().substring(0, 10)); // Chỉ lấy ngày
        holder.bindStars((int) feedback.getStart());
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {

        TextView userName, tvContent, tvDateFeed;
        ImageView[] stars;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvDateFeed = itemView.findViewById(R.id.tvDateFeed);

            stars = new ImageView[]{
                    itemView.findViewById(R.id.star1),
                    itemView.findViewById(R.id.star2),
                    itemView.findViewById(R.id.star3),
                    itemView.findViewById(R.id.star4),
                    itemView.findViewById(R.id.star5)
            };
        }

        public void bindStars(int rating) {
            for (int i = 0; i < stars.length; i++) {
                if (i < rating) {
                    stars[i].setVisibility(View.VISIBLE); // Hiển thị sao được đánh giá
                } else {
                    stars[i].setVisibility(View.GONE); // Ẩn sao không được đánh giá
                }
            }
        }
    }

    private void loadUserDataByDocumentId(String documentId, TextView userName) {
        DocumentReference docRef = db.collection("users").document(documentId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Lấy tất cả dữ liệu từ tài liệu
                    name = document.getString("name");

                    // Hiển thị dữ liệu trên log
                    Log.d("UserData", "Name: " + name);

                    // Cập nhật giao diện nếu cần
                    userName.setText(name);
                } else {
                    Log.d("UserData", "No such document");
                }
            } else {
                Log.w("UserData", "get failed with ", task.getException());
            }
        });
    }

}

