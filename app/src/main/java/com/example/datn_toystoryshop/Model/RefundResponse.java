package com.example.datn_toystoryshop.Model;

import java.util.List;

public class RefundResponse {
    private String message;
    private List<Refund_Model> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Refund_Model> getData() {
        return data;
    }

    public void setData(List<Refund_Model> data) {
        this.data = data;
    }
}
