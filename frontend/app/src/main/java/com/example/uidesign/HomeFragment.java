package com.example.uidesign;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private LinearLayout linearLayout, linearLayoutSort;
    private Button currentSelectedButton = null, currentSortButton = null;

    private EditText searchText;
    private ImageView searchButton, filterButton;
    private HorizontalScrollView tags, sort;
    private View border_line;
    private String search_key = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager);

        linearLayout = view.findViewById(R.id.linear_layout);
        linearLayoutSort = view.findViewById(R.id.linear_layout_sort);
        filterButton = view.findViewById(R.id.filter);
        searchText = view.findViewById(R.id.tv_search);
        searchButton = view.findViewById(R.id.IV_serach);
        tags = view.findViewById(R.id.horizontal_scroll_view);
        sort = view.findViewById(R.id.horizontal_scroll_view_sort);
        border_line = view.findViewById(R.id.border_line);
        tags.setVisibility(View.GONE);
        sort.setVisibility(View.GONE);
        border_line.setVisibility(View.GONE);
        List<String> tabTitles = Arrays.asList("所有","热门", "关注");
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getActivity(), tabTitles, "",
                currentSelectedButton == null? "": currentSelectedButton.getText().toString(),
                currentSortButton == null? "": currentSortButton.getText().toString());
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabTitles.get(position)));
        tabLayoutMediator.attach();

        for (int i = 1; i <= 10; i++) {
            Button button = new Button(getContext());
            button.setText("标签" + i);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 10);
            button.setLayoutParams(layoutParams);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentSelectedButton != null) {
                        currentSelectedButton.setTextColor(ContextCompat.getColor(view.getContext(), R.color.black));  // set the color of previously selected button to default color
                    }
                    Button clickedButton = (Button)view;
                    if(currentSelectedButton == clickedButton){
                        currentSelectedButton.setTextColor(ContextCompat.getColor(view.getContext(), R.color.black));
                        currentSelectedButton = null;
                        Toast.makeText(getContext(), "你取消了\"" +  button.getText().toString() + "\"标签",
                                Toast.LENGTH_SHORT).show();
                    }
                    else{
                        clickedButton.setTextColor(ContextCompat.getColor(view.getContext(), R.color.teal_200));  // set the color of selected button to blue
                        currentSelectedButton = clickedButton;  // save the reference to the newly selected button
                        Toast.makeText(getContext(), "你选择了\"" +  clickedButton.getText().toString() + "\"标签",
                                Toast.LENGTH_SHORT).show();
                    }
                    SectionsPagerAdapter sectionsPagerAdapter2 = new SectionsPagerAdapter(getActivity(), tabTitles, search_key ,
                            currentSelectedButton == null? "": currentSelectedButton.getText().toString(),
                            currentSortButton == null? "": currentSortButton.getText().toString());
                    viewPager.setAdapter(sectionsPagerAdapter2);
                    viewPager.setCurrentItem(0);
                    TabLayoutMediator tabLayoutMediator2 = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabTitles.get(position)));
                    tabLayoutMediator2.attach();
                }
            });


            linearLayout.addView(button);
        }
        String []names = {"按评论数量排序", "按点赞数量排序", "按发布时间排序"};

        for (int i = 0; i < 3; i++) {
            Button button = new Button(getContext());
            button.setText(names[i]);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(25, 10, 25, 10);
            button.setLayoutParams(layoutParams);
            if(i == 2){
                // 默认选择按时间排序
                currentSortButton = button;
                currentSortButton.setTextColor(ContextCompat.getColor(view.getContext(), R.color.teal_200));
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button clickedButton = (Button)view;
                    if(currentSortButton == clickedButton){
                        return;
                    }
                    if (currentSortButton != null) {
                        currentSortButton.setTextColor(ContextCompat.getColor(view.getContext(), R.color.black));  // set the color of previously selected button to default color
                    }
                    clickedButton.setTextColor(ContextCompat.getColor(view.getContext(), R.color.teal_200));  // set the color of selected button to blue
                    currentSortButton = clickedButton;  // save the reference to the newly selected button
                    Toast.makeText(getContext(), "你选择了\"" +  clickedButton.getText().toString() + "\"筛选条件",
                            Toast.LENGTH_SHORT).show();
                    // TODO
                    SectionsPagerAdapter sectionsPagerAdapter2 = new SectionsPagerAdapter(getActivity(), tabTitles, search_key, currentSelectedButton == null? "": currentSelectedButton.getText().toString() ,
                            currentSortButton == null? "": currentSortButton.getText().toString());
                    viewPager.setAdapter(sectionsPagerAdapter2);
                    viewPager.setCurrentItem(0);
                    TabLayoutMediator tabLayoutMediator2 = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabTitles.get(position)));
                    tabLayoutMediator2.attach();
                }
            });


            linearLayoutSort.addView(button);
        }

        filterButton.setOnClickListener(v -> {
            if (tags.getVisibility() == View.GONE) {
                tags.setVisibility(View.VISIBLE);
                sort.setVisibility(View.VISIBLE);
                border_line.setVisibility(View.VISIBLE);
            } else {
                tags.setVisibility(View.GONE);
                sort.setVisibility(View.GONE);
                border_line.setVisibility(View.GONE);
            }
        });

        searchButton.setOnClickListener(v -> {
            search_key = searchText.getText().toString();
            if (search_key .length() == 0) {
                Toast.makeText(getContext(), "请输入要查找的内容!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getContext(), "Searching...",
                    Toast.LENGTH_SHORT).show();
            SectionsPagerAdapter sectionsPagerAdapter2 = new SectionsPagerAdapter(getActivity(), tabTitles, search_key,
                    currentSelectedButton == null? "": currentSelectedButton.getText().toString(), currentSortButton == null? "": currentSortButton.getText().toString());
            viewPager.setAdapter(sectionsPagerAdapter2);
            viewPager.setCurrentItem(0);
            TabLayoutMediator tabLayoutMediator2 = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabTitles.get(position)));
            tabLayoutMediator2.attach();
        });
        return view;
    }
}