package com.example.uidesign.notice;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NoticeList {
    private ArrayList<Notice> data = new ArrayList<>();
    private int count = 0;

    public NoticeList() {
    }

    public void insert(String title,
                       String content,
                       String create_time,
                       String type,
                       String related_userid
    ) {
        data.add(new Notice(title, content, create_time, type, related_userid));
        count++;
    }

    public Notice get(int index) {
        return data.get(index);
    }

    public int size() {
        return data.size();
    }
}
