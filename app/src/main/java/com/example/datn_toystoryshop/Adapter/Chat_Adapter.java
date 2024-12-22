package com.example.datn_toystoryshop.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datn_toystoryshop.Model.ChatMessage_Model;
import com.example.datn_toystoryshop.R;

import java.util.List;

public class Chat_Adapter extends RecyclerView.Adapter<Chat_Adapter.ChatViewHolder> {
    private List<ChatMessage_Model> chatList;
    private String documentId;

    public Chat_Adapter(List<ChatMessage_Model> chatList, String documentId) {
        this.chatList = chatList;
        this.documentId = documentId;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage_Model message = chatList.get(position);
        // Kiểm tra người gửi
        if (message.getuserId() != null && message.getuserId().equals("support1")) {            // Tin nhắn của khách hàng (bên trái)
            holder.textViewMessage.setBackgroundResource(R.drawable.bg_message_received);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.textViewMessage.getLayoutParams();
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID; // Căn phải
            params.startToStart = ConstraintLayout.LayoutParams.UNSET; // Không căn trái
            holder.textViewMessage.setLayoutParams(params);
        } else {
            // Tin nhắn của chăm sóc khách hàng (bên phải)
            holder.textViewMessage.setBackgroundResource(R.drawable.bg_message_sent);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.textViewMessage.getLayoutParams();
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID; // Căn trái
            params.endToEnd = ConstraintLayout.LayoutParams.UNSET; // Không căn phải
            holder.textViewMessage.setLayoutParams(params);
        }

        // Đặt nội dung tin nhắn
        holder.textViewMessage.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
        }
    }
}