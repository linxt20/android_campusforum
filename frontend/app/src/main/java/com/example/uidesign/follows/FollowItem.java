package com.example.uidesign.follows;

import android.util.Log;

public class FollowItem {
    private String imageUser;
    private String username, userid;

    private String content;
    private String dateTime;

    public FollowItem(String image, String username, String dateTime, String content, String userid) {
        Log.d("followItem: ",  "image: " + image + ", username: " + username + ", dateTime: " + dateTime + ", content: " + content + ", userid: " + userid);
        this.imageUser = image;
        this.username = username;
        this.dateTime = dateTime;
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

    public String getDateTime() {
        return dateTime;
    }

    public String getContent() {
        return content;
    }

    // 用于排序形如 yyyy-mm-dd HH:MM:SS 的时间
    public int convertTime2Num() {
        String[] time = dateTime.split(" ");
        String[] date = time[0].split("-");
        String[] hour = time[1].split(":");
        int month = Integer.parseInt(date[1]);
        int day = Integer.parseInt(date[2]);
        int hourNum = Integer.parseInt(hour[0]);
        int minute = Integer.parseInt(hour[1]);
        int second = Integer.parseInt(hour[2]);
        int ret = month * 31 * 24 * 3600 + day * 24 * 3600 + hourNum * 3600 + minute * 60 + second;
        return ret;
    }
}