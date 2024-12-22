package com.example.datn_toystoryshop.Model;

import com.google.gson.annotations.SerializedName;

public class Address_model {
    @SerializedName("_id")
    private String _id;          // _id từ MongoDB
    private String cusId;      // cusId từ Firebase (nếu có)
    private String name;        // tên người nhận
    private String phone;       // số điện thoại
    private String address;     // địa chỉ chính
    private String addressDetail; // chi tiết địa chỉ (số nhà, tầng...)
    private boolean isDefault;


    public Address_model(String _id, String cusId, String name, String phone, String address, String addressDetail, boolean isDefault) {
        this._id = _id;
        this.cusId = cusId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getcusId() {
        return cusId;
    }

    public void setcusId(String cusId) {
        this.cusId = cusId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
