package com.example.uidesign.profile;

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

}

