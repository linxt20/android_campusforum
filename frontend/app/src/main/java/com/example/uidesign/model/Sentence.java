package com.example.uidesign.model;


import java.util.Date;

public class Sentence {

    String content;
    Date create_time;
    String sender_id;
    String receiver_id;

    public Sentence(String content, Date create_time, String sender_id, String receiver_id) {
        this.content = content;
        this.create_time = create_time;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
    }

    public String getContent() {
        return content;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }



}

