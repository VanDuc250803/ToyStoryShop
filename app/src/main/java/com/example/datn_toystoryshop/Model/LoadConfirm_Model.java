package com.example.datn_toystoryshop.Model;

public class LoadConfirm_Model {
    private String productId;
    private int quantity;
    private int quantity1;

    public LoadConfirm_Model(String productId, int quantity, int quantity1) {
        this.productId = productId;
        this.quantity = quantity;
        this.quantity1 = quantity1;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity1() {
        return quantity1;
    }

    public void setQuantity1(int quantity1) {
        this.quantity1 = quantity1;
    }
}
