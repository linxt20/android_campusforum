package com.example.uidesign.comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CommentItemList {
    private ArrayList<CommentItem> data = new ArrayList<>();
    private int count = 0;

    public CommentItemList(ArrayList<String>Img, ArrayList<String>title, ArrayList<String> date) {
        // TODO 通过contents初始化List
//        for (int i = 0; i < title.size(); i++) {
//            data.add(new BoardItem(Img.get(i), title.get(i), date.get(i)));
//            count++;
//        }
    }

    public CommentItemList() {
    }

    public void insert(String Img, String username, String date, String content) {

        data.add(new CommentItem(Img,username,date, content));
        // TODO 在加入的时候就进行排序
        count++;
    }

    public  void sort() {
        Collections.sort(data, new Comparator<CommentItem>() {
            @Override
            public int compare(CommentItem o1, CommentItem o2) {
                return o2.convertTime2Num() - (o1.convertTime2Num());
            }
        });
    }

    public void delete(int number) {
        data.remove(number);
        count--;
    }

    public CommentItem get(int index) {
        return data.get(index);
    }

    public int size() {
        return data.size();
    }
}