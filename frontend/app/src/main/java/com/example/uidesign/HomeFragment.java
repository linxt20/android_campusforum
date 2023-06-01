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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private LinearLayout linearLayout;
    private Button currentSelectedButton;

    private EditText searchText;
    private ImageView searchButton;
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
        searchText = view.findViewById(R.id.tv_search);
        searchButton = view.findViewById(R.id.IV_serach);


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
                    clickedButton.setTextColor(ContextCompat.getColor(view.getContext(), R.color.teal_200));  // set the color of selected button to blue
                    currentSelectedButton = clickedButton;  // save the reference to the newly selected button
                }
            });


            linearLayout.addView(button);
        }

        List<String> tabTitles = Arrays.asList("新发表","热门", "关注");
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getActivity(), tabTitles, "");
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabTitles.get(position)));
        tabLayoutMediator.attach();

        searchButton.setOnClickListener(v -> {
            String text = searchText.getText().toString();
            if (text.length() == 0) {
                Toast.makeText(getContext(), "请输入要查找的内容!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getContext(), "Searching...",
                    Toast.LENGTH_SHORT).show();
            SectionsPagerAdapter sectionsPagerAdapter2 = new SectionsPagerAdapter(getActivity(), tabTitles, text);
            viewPager.setAdapter(sectionsPagerAdapter2);
            viewPager.setCurrentItem(0);
            TabLayoutMediator tabLayoutMediator2 = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabTitles.get(position)));
            tabLayoutMediator2.attach();
        });
        return view;
    }
}