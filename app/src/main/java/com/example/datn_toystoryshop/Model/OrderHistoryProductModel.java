package com.example.datn_toystoryshop.Model;

public class OrderHistoryProductModel {
    private String prodId;
    private int revenue;
    private int quantity;
    private String prodSpecification;

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProdSpecification() {
        return prodSpecification;
    }

    public void setProdSpecification(String prodSpecification) {
        this.prodSpecification = prodSpecification;
    }
}
