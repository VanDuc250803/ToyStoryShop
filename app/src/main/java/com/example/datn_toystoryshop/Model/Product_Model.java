package com.example.datn_toystoryshop.Model;

import java.io.Serializable;
import java.util.List;

public class Product_Model implements Serializable {

    private String _id;          // Mã ID của sản phẩm
    private String owerId;          // ID chủ sở hữu
    private boolean statusPro;   // Tồn kho (true nếu còn hàng)
    private double price;        // Giá của sản phẩm
    private String desPro;       // Mô tả sản phẩm
    private String creatDatePro; // Ngày tạo sản phẩm
    private int quantity;        // Số lượng sản phẩm
    private String listPro;      // Danh sách trạng thái sản phẩm
    private List<String> imgPro;       // URL hình ảnh của sản phẩm
    private String namePro;      // Tên sản phẩm
    private String cateId;          // ID của danh mục sản phẩm
    private String brand;

    public Product_Model(String _id,
                         String desPro, String creatDatePro) {
        this._id = _id;
        this.owerId = owerId;
        this.statusPro = statusPro;
        this.price = price;
        this.desPro = desPro;
        this.creatDatePro = creatDatePro;
        this.quantity = quantity;
        this.listPro = listPro;
        this.imgPro = imgPro;
        this.namePro = namePro;
        this.cateId = cateId;
        this.brand = brand;
    }

    public Product_Model() {

    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getOwerId() {
        return owerId;
    }

    public void setOwerId(String owerId) {
        this.owerId = owerId;
    }

    public boolean isStatusPro() {
        return statusPro;
    }

    public void setStatusPro(boolean statusPro) {
        this.statusPro = statusPro;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDesPro() {
        return desPro;
    }

    public void setDesPro(String desPro) {
        this.desPro = desPro;
    }

    public String getCreatDatePro() {
        return creatDatePro;
    }

    public void setCreatDatePro(String creatDatePro) {
        this.creatDatePro = creatDatePro;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getListPro() {
        return listPro;
    }

    public void setListPro(String listPro) {
        this.listPro = listPro;
    }

    public List<String> getImgPro() {
        return imgPro;
    }

    public void setImgPro(List<String> imgPro) {
        this.imgPro = imgPro;
    }

    public String getNamePro() {
        return namePro;
    }

    public void setNamePro(String namePro) {
        this.namePro = namePro;
    }

    public String getCateId() {
        return cateId;
    }

    public void setCateId(String cateId) {
        this.cateId = cateId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}

