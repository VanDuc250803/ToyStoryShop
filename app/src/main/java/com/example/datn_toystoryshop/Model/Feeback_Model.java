package com.example.datn_toystoryshop.Model;

public class Feeback_Model {
    private String _id;          // Mã ID của yeu thic
    private String cusId;
    private String prodId;
    private int start;
    private String content;
    private String dateFeed;

    public Feeback_Model(String _id, String cusId, String prodId, int start, String content, String dateFeed) {
        this._id = _id;
        this.cusId = cusId;
        this.prodId = prodId;
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

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
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