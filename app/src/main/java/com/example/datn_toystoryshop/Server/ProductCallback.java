package com.example.datn_toystoryshop.Server;

import com.example.datn_toystoryshop.Model.Product_Model;

public interface ProductCallback {
    void onSuccess(Product_Model product); // Được gọi khi lấy dữ liệu thành công

    void onFailure(Throwable t);           // Được gọi khi có lỗi xảy ra
}
