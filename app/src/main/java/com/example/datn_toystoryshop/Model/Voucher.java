package com.example.datn_toystoryshop.Model;

import com.google.gson.annotations.SerializedName;

public class Voucher {
    private String _id;
    private String type_voucher;
    private int quantity_voucher;
    private int price_reduced;
    private String discount_code;
    private boolean isSelected; // Thêm thuộc tính isSelected


    public Voucher(String _id, String type_voucher, int quantity_voucher, int price_reduced, String discount_code, boolean isSelected) {
        this._id = _id;
        this.type_voucher = type_voucher;
        this.quantity_voucher = quantity_voucher;
        this.price_reduced = price_reduced;
        this.discount_code = discount_code;
        this.isSelected = isSelected;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getType_voucher() {
        return type_voucher;
    }

    public void setType_voucher(String type_voucher) {
        this.type_voucher = type_voucher;
    }

    public int getQuantity_voucher() {
        return quantity_voucher;
    }

    public void setQuantity_voucher(int quantity_voucher) {
        this.quantity_voucher = quantity_voucher;
    }

    public int getPrice_reduced() {
        return price_reduced;
    }

    public void setPrice_reduced(int price_reduced) {
        this.price_reduced = price_reduced;
    }

    public String getDiscount_code() {
        return discount_code;
    }

    public void setDiscount_code(String discount_code) {
        this.discount_code = discount_code;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

