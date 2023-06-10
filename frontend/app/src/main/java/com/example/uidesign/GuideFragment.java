package com.example.uidesign;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uidesign.follows.FollowPagerAdapter;
import com.example.uidesign.utils.ZoomOutPageTransformer;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class GuideFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabsFollowList);
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerFollowList);

        List<String> tabTitles = Arrays.asList("我的关注",  "我的粉丝");
        SharedPreferences prefs = getActivity().getSharedPreferences("com.example.android.myapp", MODE_PRIVATE);
        String userID = prefs.getString("userID", "");
        FollowPagerAdapter sectionsPagerAdapter = new FollowPagerAdapter(this, tabTitles, userID);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabTitles.get(position)));
        tabLayoutMediator.attach();
        return view;
    }
}