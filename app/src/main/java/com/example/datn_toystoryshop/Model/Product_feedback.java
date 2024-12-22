package com.example.datn_toystoryshop.Model;

import java.util.List;

public class Product_feedback {
    private String prodId; // ID sản phẩm
    private int quantity;  // Số lượng sản phẩm đã bán
    private int revenue;   // Doanh thu của sản phẩm
    private String namePro; // Tên sản phẩm
    private List<String> imgPro; // Danh sách hình ảnh sản phẩm

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public String getNamePro() {
        return namePro;
    }

    public void setNamePro(String namePro) {
        this.namePro = namePro;
    }

    public List<String> getImgPro() {
        return imgPro;
    }

    public void setImgPro(List<String> imgPro) {
        this.imgPro = imgPro;
    }
}