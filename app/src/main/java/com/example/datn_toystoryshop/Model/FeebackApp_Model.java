package com.example.datn_toystoryshop.Model;

public class FeebackApp_Model {
    private String _id;          // Mã ID của yeu thic
    private String cusId;           // Mã ID của sản phẩm
    private double start;
    private String content;
    private String dateFeed;

    public FeebackApp_Model(String _id, String cusId, double start, String content, String dateFeed) {
        this._id = _id;
        this.cusId = cusId;
        this.start = start;
        this.content = content;
        this.dateFeed = dateFeed;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCusId() {
        return cusId;
    }

    public void setCusId(String cusId) {
        this.cusId = cusId;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateFeed() {
        return dateFeed;
    }

    public void setDateFeed(String dateFeed) {
        this.dateFeed = dateFeed;
    }
}
