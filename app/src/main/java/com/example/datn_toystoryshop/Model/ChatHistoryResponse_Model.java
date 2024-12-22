package com.example.datn_toystoryshop.Model;

import java.util.List;

public class ChatHistoryResponse_Model {
    private List<ChatMessage_Model> data;

    public List<ChatMessage_Model> getData() {
        return data;
    }

    public void setData(List<ChatMessage_Model> data) {
        this.data = data;
    }
}
