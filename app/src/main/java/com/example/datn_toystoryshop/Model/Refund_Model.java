package com.example.datn_toystoryshop.Model;

public class Refund_Model {
    private String _id;
    private String orderId;
    private String cusId;
    private String content;
    private String orderRefundDate;
    private String refundStatus;

    public Refund_Model() {
    }

    public Refund_Model(String _id, String orderId, String cusId, String content, String orderRefundDate, String refundStatus) {
        this._id = _id;
        this.orderId = orderId;
        this.cusId = cusId;
        this.content = content;
        this.orderRefundDate = orderRefundDate;
        this.refundStatus = refundStatus;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCusId() {
        return cusId;
    }

    public void setCusId(String cusId) {
        this.cusId = cusId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOrderRefundDate() {
        return orderRefundDate;
    }

    public void setOrderRefundDate(String orderRefundDate) {
        this.orderRefundDate = orderRefundDate;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }
}
