package com.example.uidesign;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.uidesign.profile.BoardItem;
import com.example.uidesign.profile.BoardItemList;
import com.example.uidesign.profile.ProfilePagerAdapter;
import com.example.uidesign.profile.RecycleAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment {
    Button settings;
    Button message;
    SharedPreferences sharedPreferences;

    private RecyclerView recyclerView;//声明RecyclerView
    private RecycleAdapter adapter;//声明适配器

    private  BoardItemList boardItemList = new BoardItemList();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        sharedPreferences = getActivity().getSharedPreferences("com.example.android.myapp", Context.MODE_PRIVATE);

        // TODO 从后端获得name 头像 关注被关注等信息
        settings = view.findViewById(R.id.settingsButton);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Changed!!!!!
                Intent intent = new Intent(getContext(),SettingsActivity.class);
                startActivity(intent);
            }
        });
        message = view.findViewById(R.id.messageButton);
       message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Changed!!!!!
                Intent intent = new Intent(getContext(),TestActivity.class);
                startActivity(intent);
            }
        });

        TabLayout tabLayout = view.findViewById(R.id.tabsInProfile);
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerInProfile);

        List<String> tabTitles = Arrays.asList("我的帖子",  "收藏");
        ProfilePagerAdapter sectionsPagerAdapter = new ProfilePagerAdapter(getActivity(), tabTitles);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabTitles.get(position)));
        tabLayoutMediator.attach();

        return view;
    }
}