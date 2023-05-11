package com.example.backend.Base;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;
@Document("message")
public class Message {
    @NonNull
    private String message;
    public Message(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
