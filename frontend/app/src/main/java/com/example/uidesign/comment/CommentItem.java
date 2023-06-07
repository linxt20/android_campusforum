package com.example.uidesign.comment;

import android.util.Log;

public class CommentItem {

    private String imageUser;
    private String username;

    private String content;
    private String dateTime;

    public CommentItem(String image, String username, String dateTime, String content) {
        this.imageUser = image;
        this.username = username;
        this.dateTime = dateTime;
        this.content = content;
    }

    public String getImage() {
        return imageUser;
    }


    public String getUsername() {
        return username;
    }

    public String getDateTime() {
        return dateTime;
    }
    
    public String getContent() {
        return content;
    }

}

