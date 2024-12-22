package com.example.datn_toystoryshop.Model;

public class ChatMessage_Model {
    private String cusId;
    private String userId;
    private String message;
    private String chatType;

    public ChatMessage_Model(String cusId, String userId, String message, String chatType) {
        this.cusId = cusId;
        this.userId = userId;
        this.message = message;
        this.chatType = chatType;
    }


    public String getcusId() {
        return cusId;
    }

    public void setcusId(String cusId) {
        this.cusId = cusId;
    }

    public String getuserId() {
        return userId;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

}
