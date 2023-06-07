package com.example.uidesign.message;

import com.example.uidesign.model.User;

public class MessageItem {
    String message;
    // TODO 把user改成string userid
    String senderid;
    String senderImg, senderName;
    String createdAt, date;

    public MessageItem(String message, String senderid, String senderImg, String senderName, String createdAt, String date) {
        this.message = message;
        this.senderid = senderid;
        this.senderImg = senderImg;
        this.senderName = senderName;
        this.createdAt = createdAt;
        this.date = date;
    }
    public String getMessage() {
        return message;
    }
    public String getSenderid() {
        return senderid;
    }
    public String getSenderName() {
        return senderName;
    }
    public String getSenderImg() {
        return senderImg;
    }
    public String getCreatedAt() {
        return createdAt;
    }

    public String getDate() {
        return date;
    }
}