package com.example.uidesign.comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CommentItemList {
    private ArrayList<CommentItem> data = new ArrayList<>();
    private int count = 0;


    public CommentItemList() {
    }

    public void insert(String Img, String username, String date, String content, String userid) {

        data.add(new CommentItem(Img,username,date, content, userid));
        // TODO 在加入的时候就进行排序
        count++;
    }


    public CommentItem get(int index) {
        return data.get(index);
    }

    public int size() {
        return data.size();
    }
}