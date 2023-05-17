package com.example.uidesign.profile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.uidesign.ProfileMyPostsFragment;
import com.example.uidesign.ProfileMyStarsFragment;

import java.util.List;

public class ProfilePagerAdapter extends FragmentStateAdapter {

    private final List<String> tabTitles;

    public ProfilePagerAdapter(FragmentActivity fragmentActivity, List<String> tabTitles) {
        super(fragmentActivity);
        this.tabTitles = tabTitles;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProfileMyPostsFragment();
            case 1:
                return new ProfileMyStarsFragment();
            default:
                throw new IllegalStateException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return tabTitles.size();
    }
}