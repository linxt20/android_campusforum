package com.example.uidesign;

import java.util.ArrayList;

public class BeanList {
    private ArrayList<Bean> data = new ArrayList<>();
    public BeanList() {
    }

    public void insert( String Username,
                        String createAt,
                        String title,
                        String content,
                        int comment_count,
                        int like_count,
                        int star_count,
                        String user_head,
                        String[] imagelist) {
        data.add(0,new Bean(Username,createAt,title,content,comment_count,like_count,star_count,user_head,imagelist));
    }
    public Bean get(int index) {
        return data.get(index);
    }

    public int size() {
        return data.size();
    }
}
