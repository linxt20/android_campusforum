package com.example.uidesign;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class SectionsPagerAdapter extends FragmentStateAdapter {

    private final List<String> tabTitles;
    private String search_key;

    public SectionsPagerAdapter(FragmentActivity fragmentActivity, List<String> tabTitles, String search_key) {
        super(fragmentActivity);
        this.tabTitles = tabTitles;
        this.search_key = search_key;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new NewPostsFragment(search_key);
            case 1:
                return new HotFragment();
            case 2:
                return new FollowFragment();
            default:
                throw new IllegalStateException("Invalid position: " + position);
        }
    }
    @Override
    public int getItemCount() {
        return tabTitles.size();
    }
}

