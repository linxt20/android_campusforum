package com.example.uidesign.comment;

import android.util.Log;

public class CommentItem {

    private String imageUser;
    private String username;

    private String content;
    private String dateTime;
    private String userid;

    public CommentItem(String image, String username, String dateTime, String content, String userid) {
        this.imageUser = image;
        this.username = username;
        this.dateTime = dateTime;
        this.content = content;
        this.userid = userid;
    }

    public  String getUserid() {
        return userid;
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

