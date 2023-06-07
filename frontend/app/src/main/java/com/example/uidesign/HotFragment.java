package com.example.uidesign;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.uidesign.model.Comment;
import com.example.uidesign.model.Post;
import com.example.uidesign.model.User;
import com.example.uidesign.utils.GlobalVariables;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HotFragment extends Fragment {

    private final BeanList BeanList = new BeanList();
    SharedPreferences prefs;
    String userID;
    BeanListAdapter adapter;
    String search_key, tag, sort;
    public List<String> following = new ArrayList<>();

    public HotFragment(String search_key, String tag, String sort) {
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
        View view = inflater.inflate(R.layout.fragment_hot, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        adapter = new BeanListAdapter(getContext(), BeanList);
        ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // do nothing, no data to handle
                    }
                }
        );

        prefs = getActivity().getSharedPreferences("com.example.android.myapp", 0);
        userID = prefs.getString("userID", "");

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
                Log.d("AddPost", "responseText: " + responseText);
                // if(responseText == null) return;
                final User myResponse = new Gson().fromJson(responseText, new TypeToken<User>(){}.getType());
                following = myResponse.getFollow_list();
            }
        });

        client = new OkHttpClient();
        /*
       @RequestParam String userid,
       @RequestParam String search_key,
       @RequestParam String tag,
       @RequestParam String sort_by)
       */
        Log.d("NewPost", "search: " + search_key + "====================x");
        body = new FormBody.Builder()
                .add("userid", userID)
                .add("search_key", search_key)
                .add("tag",tag)
                .add("sort_by", sort)
                .add("type", "hot")
                .build();
        request = new Request.Builder()
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
                Log.d("HotFragment response", responseText);
                final List<Post> myResponse = new Gson().fromJson(responseText, new TypeToken<List<Post>>(){}.getType());
                if(myResponse == null) return;
                BeanList.clear();
                for(int i = myResponse.size() - 1; i >= 0; i--){
                    Log.d("HotFragmentsFragment", myResponse.get(i).toString());
                    String[] images = myResponse.get(i).getResource_list();
                    // if(images.length == 0) return;
                    // TODO 这里将 List<Comment> 转换为 Comment[]有些繁琐，后续可能要修改
                    Comment[] comments = new Comment[myResponse.get(i).getComment_count()];
                    Log.d("HotFragment", "comments: " + myResponse.get(i).getComment_list());
                    for(int j = 0; j < myResponse.get(i).getComment_count(); j++){
                        comments[j] = myResponse.get(i).getComment_list().get(j);
                    }
                    Boolean isFollowing = false;
                    if(following.contains(myResponse.get(i).getAuthor_id())){
                        isFollowing = true;
                    }
                    Log.d("HotFragment", "head: " + myResponse.get(i).getAuthor_head());
                    BeanList.insert(myResponse.get(i).getAuthor_name(), myResponse.get(i).getCreate_time(), myResponse.get(i).getTag(), myResponse.get(i).getTitle()
                            , myResponse.get(i).getContent(),myResponse.get(i).getComment_count(), myResponse.get(i).getLike_count(), myResponse.get(i).getIf_like()
                            , myResponse.get(i).getStar_count(), myResponse.get(i).getIf_star(), myResponse.get(i).getAuthor_head(), myResponse.get(i).getResource_list()
                            , comments, myResponse.get(i).getPostid(), myResponse.get(i).getAuthor_id(), myResponse.get(i).getResource_type(), isFollowing,  myResponse.get(i).getLocation());
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

        return view;
    }
}