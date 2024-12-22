package com.example.datn_toystoryshop.Model;

public class OrderResponse {
    private String message; // Thông báo từ API
    private String orderId; // ID đơn hàng (lặp lại _id)
    private Order_Model data; // Dữ liệu đơn hàng

    // Getters và setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Order_Model getData() {
        return data;
    }

    public void setData(Order_Model data) {
        this.data = data;
    }
}

