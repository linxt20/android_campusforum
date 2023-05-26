package com.example.uidesign.profile;

import android.graphics.Bitmap;

public class BoardItem {

    private String image;
    private String title;
    private String dateTime;

    public BoardItem(String image, String title, String dateTime) {
        this.image = image;
        this.title = title;
        this.dateTime = dateTime;
    }

    public String getImage() {
        return image;
    }


    public String getTitle() {
        return title;
    }

    public String getDateTime() {
        return dateTime;
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

