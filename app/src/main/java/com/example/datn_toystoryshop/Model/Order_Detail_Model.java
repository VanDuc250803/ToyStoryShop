package com.example.datn_toystoryshop.Model;

public class Order_Detail_Model {
    private String productId;
    private int currentQuantity;
    private String customerId;
    private String selectedColor;
    private String productImg;

    public Order_Detail_Model(String productId, int currentQuantity, String customerId, String selectedColor, String productImg) {
        this.productId = productId;
        this.currentQuantity = currentQuantity;
        this.customerId = customerId;
        this.selectedColor = selectedColor;
        this.productImg = productImg;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }
}
