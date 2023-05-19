package com.example.uidesign.profile;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class BoardItemList  {
    private ArrayList<BoardItem> data = new ArrayList<>();
    private int count = 0;

    public BoardItemList(ArrayList<String>Img, ArrayList<String>title, ArrayList<String> date) {
        // TODO 通过contents初始化List
//        for (int i = 0; i < title.size(); i++) {
//            data.add(new BoardItem(Img.get(i), title.get(i), date.get(i)));
//            count++;
//        }
    }

    public BoardItemList() {
    }

    public void insert(Bitmap Img, String title, String date) {

        data.add(new BoardItem(Img,title,date));
        // TODO 在加入的时候就进行排序
        count++;
    }

    public  void sort() {
        Collections.sort(data, new Comparator<BoardItem>() {
            @Override
            public int compare(BoardItem o1, BoardItem o2) {
                return o2.convertTime2Num() - (o1.convertTime2Num());
            }
        });
    }

    public void delete(int number) {
        data.remove(number);
        count--;
    }

    public BoardItem get(int index) {
        return data.get(index);
    }

    public int size() {
        return data.size();
    }
}