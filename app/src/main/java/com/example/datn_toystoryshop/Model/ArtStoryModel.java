package com.example.datn_toystoryshop.Model;

import java.util.Date;
import java.util.List;

public class ArtStoryModel {
    private String title;
    private String author;
    private Date date;
    private String description;
    private List<String> imageUrl; // Chứa danh sách các URL ảnh
    private List<String> caption;
    private String content;

    public ArtStoryModel(String title, String author, Date date, String description, List<String> imageUrl, List<String> caption, String content) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.description = description;
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getCaption() {
        return caption;
    }

    public void setCaption(List<String> caption) {
        this.caption = caption;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
