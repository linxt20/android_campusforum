package com.example.uidesign.profile;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.uidesign.ProfileMyPostsFragment;
import com.example.uidesign.ProfileMyStarsFragment;

import java.util.List;

public class ProfilePagerAdapter extends FragmentStateAdapter {

    private final List<String> tabTitles;
    private final String userid;

    public ProfilePagerAdapter(FragmentActivity fragmentActivity, List<String> tabTitles, String userid) {
        super(fragmentActivity);
        this.tabTitles = tabTitles;
        this.userid = userid;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProfileMyPostsFragment(userid);
            case 1:
                return new ProfileMyStarsFragment(userid);
            default:
                throw new IllegalStateException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return tabTitles.size();
    }
}