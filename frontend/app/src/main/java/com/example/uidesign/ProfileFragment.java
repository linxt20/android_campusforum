package com.example.uidesign;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.uidesign.model.Post;
import com.example.uidesign.model.User;
import com.example.uidesign.profile.BoardItem;
import com.example.uidesign.profile.BoardItemList;
import com.example.uidesign.profile.ProfilePagerAdapter;
import com.example.uidesign.profile.RecycleAdapter;
import com.example.uidesign.utils.GlobalVariables;
import com.example.uidesign.utils.ImageDownloader;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ProfileFragment extends Fragment {
    Button settings;
    Button message;
    TextView textUsername, textFollowersAndFollowings;
    ImageView imageUser;
    SharedPreferences sharedPreferences;
    String userID;

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
        userID = sharedPreferences.getString("userID", "");
        Log.d("ProfileFragment", "userID: " + userID);
        textUsername = (TextView)view.findViewById(R.id.text_user_name);
        textFollowersAndFollowings = (TextView)view.findViewById(R.id.text_followers_following_count);
        imageUser = (ImageView)view.findViewById(R.id.image_user);

        // TODO 从后端获得name 头像 关注被关注等信息
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("userid", userID)
                .build();
        Request request = new Request.Builder()
                .url(GlobalVariables.get_user_url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String responseText = response.body().string();
                Log.d("ProfileFragment", "responseText: " + responseText);
                // if(responseText == null) return;
                final User myResponse = new Gson().fromJson(responseText, new TypeToken<User>(){}.getType());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textUsername.setText(myResponse.getUsername());
                        // TODO set followers and following count
                        textFollowersAndFollowings.setText("xxx followers • xxx following");
                        ImageDownloader headDownloader = new ImageDownloader(imageUser);
                        headDownloader.execute(GlobalVariables.name2url(myResponse.getUser_head()));
                    }
                });
            }
        });

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