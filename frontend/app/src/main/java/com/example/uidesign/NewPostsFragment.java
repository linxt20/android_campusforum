package com.example.uidesign;

import android.app.Activity;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;

import com.example.uidesign.model.Comment;
import com.example.uidesign.model.Post;
import com.example.uidesign.model.User;
import com.example.uidesign.utils.GlobalVariables;
import com.example.uidesign.utils.ImageDownloader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.*;

public class NewPostsFragment extends Fragment {
    private BeanList BeanList = new BeanList();
    SharedPreferences prefs;
    String userID;
    BeanListAdapter adapter;
    View view;
    String search_key, tag, sort;
    public List<String> following = new ArrayList<>();
    RecyclerView recyclerView;
    ActivityResultLauncher<Intent> activityLauncher;

    public NewPostsFragment(String search_key, String tag, String sort) {
        this.search_key = search_key;
        this.tag = tag;
        this.sort = sort;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_posts, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        activityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // do nothing, no data to handle
                    }
                }
        );

        Button btn = view.findViewById(R.id.add_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddPostActivity.class);
                // TODO 诶这个世界好像不对吧应该是发送的时间
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateTime = dateFormat.format(new Date());
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
                        Log.d("NewPostFragment", "responseText: " + responseText);
                        // if(responseText == null) return;
                        final User myResponse = new Gson().fromJson(responseText, new TypeToken<User>(){}.getType());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 将时间和用户名作为额外数据添加到 Intent
                                intent.putExtra("currentTime", currentDateTime);
                                intent.putExtra("username", myResponse.getUsername());
                                intent.putExtra("user_head",myResponse.getUser_head());
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        OkHttpClient client = new OkHttpClient();
        prefs = getActivity().getSharedPreferences("com.example.android.myapp", 0);
        userID = prefs.getString("userID", "");

        adapter = new BeanListAdapter(getContext(), BeanList);

        client = new OkHttpClient();

        Log.d("NewPost", "search: " + search_key + "====================x");
        RequestBody body = new FormBody.Builder()
                .add("userid", userID)
                .add("search_key", search_key)
                .add("tag",tag)
                .add("type", "all")
                .add("sort_by", sort)
                .build();
        Request request = new Request.Builder()
                .url(GlobalVariables.get_posts_url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.d("NewPostFragment response", responseText);
                final List<Post> myResponse = new Gson().fromJson(responseText, new TypeToken<List<Post>>(){}.getType());
                if(myResponse == null) return;
                BeanList = new BeanList();
                for(int i = myResponse.size() - 1; i >=0 ; i--){
                    Log.d("NewPostFragmentsFragment", myResponse.get(i).toString());
                    // TODO 这里将 List<Comment> 转换为 Comment[]有些繁琐，后续可能要修改
                    Comment[] comments = new Comment[myResponse.get(i).getComment_count()];
                    Log.d("NewPostFragment", "comments: " + myResponse.get(i).getComment_list());
                    for(int j = 0; j < myResponse.get(i).getComment_count(); j++){
                        comments[j] = myResponse.get(i).getComment_list().get(j);
                    }
                    Log.d("NewPostFragment", "head: " + myResponse.get(i).getAuthor_head());
                    BeanList.insert(myResponse.get(i).getAuthor_name(), myResponse.get(i).getCreate_time(), myResponse.get(i).getTag(), myResponse.get(i).getTitle()
                            , myResponse.get(i).getContent(),myResponse.get(i).getComment_count(), myResponse.get(i).getLike_count(), myResponse.get(i).getIf_like()
                            , myResponse.get(i).getStar_count(), myResponse.get(i).getIf_star(), myResponse.get(i).getAuthor_head(), myResponse.get(i).getResource_list()
                            , comments, myResponse.get(i).getPostid(), myResponse.get(i).getAuthor_id(), myResponse.get(i).getResource_type(), myResponse.get(i).getIf_following(), myResponse.get(i).getLocation());
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter =  new BeanListAdapter(getContext(), BeanList);
                        // TODO 可能有点慢，目前还不知道怎么改。。。
                        adapter.setRecyclerItemClickListener(new BeanListAdapter.OnRecyclerItemClickListener() {
                            @Override
                            public void onRecyclerItemClick(int position) {
                                Bean current = BeanList.get(position);
                                String postid = current.getPostid();
                                Intent intent = new Intent(requireActivity(), DetailActivity.class);
                                intent.putExtra("postid", postid);
                                activityLauncher.launch(intent);
                            }
                        });
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                });
            }
        });

    }

}