package com.example.uidesign.follows;

import android.util.Log;

public class FollowItem {
    private String imageUser;
    private String username, userid;

    private String content;

    public FollowItem(String image, String username, String content, String userid) {
        Log.d("followItem: ",  "image: " + image + ", username: " + username + ", content: " + content + ", userid: " + userid);
        this.imageUser = image;
        this.username = username;
        this.content = content;
        this.userid = userid;
    }

    public String getUserid() {
        return userid;
    }
    public String getImage() {
        return imageUser;
    }


    public String getUsername() {
        return username;
    }


    public String getContent() {
        return content;
    }

}