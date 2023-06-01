package com.example.uidesign.message;

import com.example.uidesign.model.User;

public class Message {
    String message;
    User sender;
    String createdAt;

    public Message(String message, User sender, String createdAt) {
        this.message = message;
        this.sender = sender;
        this.createdAt = createdAt;
    }
    public String getMessage() {
        return message;
    }
    public User getSender() {
        return sender;
    }
    public String getCreatedAt() {
        return createdAt;
    }
}