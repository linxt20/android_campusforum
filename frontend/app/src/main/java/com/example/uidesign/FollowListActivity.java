package com.example.uidesign;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import com.example.uidesign.follows.FollowPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class FollowListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_list);

        TabLayout tabLayout = findViewById(R.id.tabsInFollowList);
        ViewPager2 viewPager = findViewById(R.id.viewPagerInFollowList);

        Intent intent = getIntent();
        List<String> tabTitles;
        if(intent.getStringExtra("type").equals("me")){
            tabTitles = Arrays.asList("我的关注",  "我的粉丝");
        }
        else{
            tabTitles = Arrays.asList("Ta的关注",  "Ta的粉丝");
        }
        FollowPagerAdapter sectionsPagerAdapter = new FollowPagerAdapter(this, tabTitles, intent.getStringExtra("userid"));
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabTitles.get(position)));
        tabLayoutMediator.attach();

    }
}