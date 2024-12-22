package com.example.datn_toystoryshop.Model;

public class VnPayCreate_Model {
    private Long total;
    private String user;
    private String returnUrl;

    public VnPayCreate_Model(Long total, String user, String returnUrl) {
        this.total = total;
        this.user = user;
        this.returnUrl = returnUrl;
    }
}
