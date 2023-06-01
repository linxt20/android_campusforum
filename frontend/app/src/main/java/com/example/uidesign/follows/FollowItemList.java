package com.example.uidesign.follows;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FollowItemList {
    private ArrayList<FollowItem> data = new ArrayList<>();
    private int count = 0;

    public FollowItemList(ArrayList<String>Img, ArrayList<String>title, ArrayList<String> date) {
        // TODO 通过contents初始化List
//        for (int i = 0; i < title.size(); i++) {
//            data.add(new BoardItem(Img.get(i), title.get(i), date.get(i)));
//            count++;
//        }
    }

    public FollowItemList() {
    }

    public void insert(String Img, String username, String date, String content, String userid) {
        data.add(new FollowItem(Img,username,date, content, userid));
        // TODO 在加入的时候就进行排序
        count++;
    }

    public  void sort() {
        Collections.sort(data, new Comparator<FollowItem>() {
            @Override
            public int compare(FollowItem o1, FollowItem o2) {
                return o2.convertTime2Num() - (o1.convertTime2Num());
            }
        });
    }

    public void delete(int number) {
        data.remove(number);
        count--;
    }

    public FollowItem get(int index) {
        return data.get(index);
    }

    public int size() {
        return data.size();
    }
}
