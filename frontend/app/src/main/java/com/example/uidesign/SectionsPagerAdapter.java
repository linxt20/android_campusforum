package com.example.uidesign;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class SectionsPagerAdapter extends FragmentStateAdapter {

    private final List<String> tabTitles;
    private String search_key, tag, sort;

    public SectionsPagerAdapter(FragmentActivity fragmentActivity, List<String> tabTitles, String search_key, String tag, String sort) {
        super(fragmentActivity);
        this.tabTitles = tabTitles;
        this.search_key = search_key;
        this.tag = tag;
        // "按评论量排序", "按点赞量排序", "按发布时间排序"
        if(sort.equals("")){
            this.sort = "time";
        }
        else if(sort.equals("按评论数量排序")){
            this.sort = "comment";
        }
        else if(sort.equals("按点赞数量排序")){
            this.sort = "like";
        }
        else if(sort.equals("按发布时间排序")){
            this.sort = "time";
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new NewPostsFragment(search_key, tag, sort);
            case 1:
                return new HotFragment(search_key, tag, sort);
            case 2:
                return new FollowFragment(search_key, tag, sort);
            default:
                throw new IllegalStateException("Invalid position: " + position);
        }
    }
    @Override
    public int getItemCount() {
        return tabTitles.size();
    }
}

