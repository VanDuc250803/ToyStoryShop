package com.example.datn_toystoryshop.Model;

public class Feeback_Rating_Model {
    private String prodId;
    private float averageRating;
    private int totalFeedbacks;

    public Feeback_Rating_Model(String prodId, float averageRating, int totalFeedbacks) {
        this.prodId = prodId;
        this.averageRating = averageRating;
        this.totalFeedbacks = totalFeedbacks;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalFeedbacks() {
        return totalFeedbacks;
    }

    public void setTotalFeedbacks(int totalFeedbacks) {
        this.totalFeedbacks = totalFeedbacks;
    }
}
