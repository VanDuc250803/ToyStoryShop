package com.example.datn_toystoryshop.Model;

public class OderProductDetail_Model {
    private String id;
    private double totalPrice;
    private int quantity;
    private String prodSpecification;

    public OderProductDetail_Model(String id, double totalPrice, int quantity, String prodSpecification) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
        this.prodSpecification = prodSpecification;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
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
