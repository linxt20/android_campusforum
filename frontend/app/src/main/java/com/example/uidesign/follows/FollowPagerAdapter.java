package com.example.uidesign.follows;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.uidesign.FollowListFragment;
import com.example.uidesign.ProfileMyPostsFragment;
import com.example.uidesign.ProfileMyStarsFragment;

import java.util.List;

public class FollowPagerAdapter extends FragmentStateAdapter {

    private final List<String> tabTitles;
    private final String userid;

    public FollowPagerAdapter(FragmentActivity fragmentActivity, List<String> tabTitles, String userid) {
        super(fragmentActivity);
        this.tabTitles = tabTitles;
        this.userid = userid;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FollowListFragment(userid, false);
            case 1:
                return new FollowListFragment(userid, true);
            default:
                throw new IllegalStateException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return tabTitles.size();
    }
}