package com.example.datn_toystoryshop.Model;

public class Cart_Model {
    private String _id;          // Mã ID của gio hang
    private String prodId;          // Mã ID của sản phẩm
    private int quantity;        // Số lượng sản phẩm
    private String cusId;      // id cua khach hang
    private String prodSpecification;
    private boolean selected;

    public Cart_Model() {
        this.selected = false;
    }

    public Cart_Model(String prodId, int quantity, String cusId, String prodSpecification) {
        this.prodId = prodId;
        this.quantity = quantity;
        this.cusId = cusId;
        this.prodSpecification = prodSpecification;
        this.selected = false;  // Mặc định là chưa chọn
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

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

    public String getCusId() {
        return cusId;
    }

    public void setCusId(String cusId) {
        this.cusId = cusId;
    }

    public String getProdSpecification() {
        return prodSpecification;
    }

    public void setProdSpecification(String prodSpecification) {
        this.prodSpecification = prodSpecification;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}