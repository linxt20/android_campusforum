package com.example.uidesign;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uidesign.model.User;
import com.example.uidesign.profile.BoardItemList;
import com.example.uidesign.profile.ProfilePagerAdapter;
import com.example.uidesign.profile.RecycleAdapter;
import com.example.uidesign.utils.GlobalVariables;
import com.example.uidesign.utils.ImageDownloader;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OthersProfileActivity extends AppCompatActivity {
    Button message, follow;
    Boolean  isFollowing =false;
    TextView textUsername, textFollowersAndFollowings;
    ImageView imageUser;
    int followersCount;
    SharedPreferences prefs;
    String userID;  // 这里是点开的这个人的id

    private BoardItemList boardItemList = new BoardItemList();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);
        // TODO intent传入userID
        userID = getIntent().getStringExtra("userID");
        textUsername = findViewById(R.id.text_user_name);
        textFollowersAndFollowings = findViewById(R.id.text_followers_following_count);
        imageUser = findViewById(R.id.image_user);
        follow = findViewById(R.id.followButton);
        prefs = getSharedPreferences("com.example.android.myapp", MODE_PRIVATE);


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
                Log.d("OtherProfile", "responseText: " + responseText);
                // if(responseText == null) return;
                final User myResponse = new Gson().fromJson(responseText, new TypeToken<User>(){}.getType());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textUsername.setText(myResponse.getUsername());
                        TextView textBio = findViewById(R.id.description);
                        textBio.setText(myResponse.getDescription());
                        // TODO set followers and following count
                        followersCount = myResponse.getFans_list().size();
                        textFollowersAndFollowings.setText(followersCount + " followers • " + myResponse.getFollow_list().size() + " following");
                        // if I am in the following list, set the button to "following"
                        if (myResponse.getFans_list().contains(prefs.getString("userID", ""))){
                            isFollowing = true;
                            follow.setText("取消关注");
                            follow.setBackgroundColor(Color.parseColor("#F44336"));

                        }
                        ImageDownloader headDownloader = new ImageDownloader(imageUser);
                        headDownloader.execute(GlobalVariables.name2url(myResponse.getUser_head()));
                    }
                });
            }
        });

        textFollowersAndFollowings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OthersProfileActivity.this, FollowListActivity.class);
                intent.putExtra("userid", userID);
                intent.putExtra("type", "others");
                startActivity(intent);
            }
        });

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient client = new OkHttpClient();
                Log.d("follow_userid", userID);
                Log.d("fans_id", prefs.getString("userID", ""));
                Log.d("type", isFollowing ? "unfollow" : "follow" );
                RequestBody body = new FormBody.Builder()
                        .add("follow_userid", userID)
                        .add("fans_id", prefs.getString("userID", ""))
                        .add("type", isFollowing ? "unfollow" : "follow")
                        .build();
                Request request = new Request.Builder()
                        .url(GlobalVariables.follow_url)
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                        final String responseText = response.body().string();
                        if(responseText.equals("success")){
                            isFollowing = !isFollowing;
                            String original =  textFollowersAndFollowings.getText().toString();
                            String[] tmp = original.split("•");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(isFollowing){
                                        Toast.makeText(OthersProfileActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                                        follow.setText("取消关注");
                                        follow.setBackgroundColor(Color.parseColor("#F44336"));
                                        followersCount++;
                                        textFollowersAndFollowings.setText(followersCount+ " followers •" + tmp[1]);
                                    }
                                    else{
                                        Toast.makeText(OthersProfileActivity.this, "取消关注成功", Toast.LENGTH_SHORT).show();
                                        follow.setText("关注");
                                        follow.setBackgroundColor(Color.parseColor("#03A9F4"));
                                        followersCount--;
                                        textFollowersAndFollowings.setText(followersCount + " followers •" + tmp[1]);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        message = findViewById(R.id.messageButton);
//        message.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Changed!!!!!
//                Intent intent = new Intent(OthersProfileActivity.this, TestActivity.class);
//                startActivity(intent);
//            }
//        });

        TabLayout tabLayout = findViewById(R.id.tabsInProfile2);
        ViewPager2 viewPager = findViewById(R.id.viewPagerInProfile2);

        List<String> tabTitles = Arrays.asList("Ta的帖子",  "收藏");
        ProfilePagerAdapter sectionsPagerAdapter = new ProfilePagerAdapter(OthersProfileActivity.this, tabTitles, userID);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabTitles.get(position)));
        tabLayoutMediator.attach();
    }
}