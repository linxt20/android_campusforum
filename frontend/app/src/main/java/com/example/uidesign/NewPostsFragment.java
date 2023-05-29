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
import com.example.uidesign.utils.GlobalVariables;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.*;

public class NewPostsFragment extends Fragment {
    private final BeanList BeanList = new BeanList();
    SharedPreferences prefs;
    String userID;
    BeanListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public String convertComments2String(Comment[] comments){
        if(comments == null) return "";
        // TODO 这个太随便了一定要改！！！
        String result = "";
        for(Comment comment: comments){
            result += comment.getAuthor_id() + ",," + comment.getCreate_time() + ",," + comment.getContent() + ";;";
        };
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_posts, container, false);
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

        //TODO 这里是插入了一个默认的动态，这里等后端的获取接口完成，调用获取接口就能获得所有的动态信息，然后在插入adapter

        OkHttpClient client = new OkHttpClient();
        prefs = getActivity().getSharedPreferences("com.example.android.myapp", 0);
        userID = prefs.getString("userID", "");

        RequestBody body = new FormBody.Builder()
                .add("userid", userID)
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
                Log.d("NewPostFragment response: ", responseText);
                final List<Post> myResponse = new Gson().fromJson(responseText, new TypeToken<List<Post>>(){}.getType());
                for(int i = 0; i < myResponse.size(); i++){
                    Log.d("NewPostFragmentsFragment", myResponse.get(i).toString());
                    String[] images = myResponse.get(i).getResource_list();
                    if(images.length == 0) return;
                    // TODO 这里将 List<Comment> 转换为 Comment[]有些繁琐，后续可能要修改
                    Comment[] comments = new Comment[myResponse.get(i).getComment_count()];
                    Log.d("NewPostFragment", "comments: " + myResponse.get(i).getComment_list());
                    for(int j = 0; j < myResponse.get(i).getComment_count(); j++){
                        comments[j] = myResponse.get(i).getComment_list().get(j);
                    }
                    BeanList.insert(myResponse.get(i).getAuthor_name(), myResponse.get(i).getCreate_time(), myResponse.get(i).getTag(), myResponse.get(i).getTitle()
                    , myResponse.get(i).getContent(),myResponse.get(i).getComment_count(), myResponse.get(i).getLike_count(), myResponse.get(i).getIf_like()
                    , myResponse.get(i).getStar_count(), myResponse.get(i).getIf_star(), myResponse.get(i).getAuthor_head(), myResponse.get(i).getResource_list(), comments);
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
                                String Username = current.getUsername();
                                String createAt = current.getcreateAt();
                                String tag = current.gettag();
                                String title = current.gettitle();
                                String Content = current.getContent();
                                int comment_count = current.getcomment_count();
                                int like_count = current.getlike_count();
                                int if_like = current.getIf_like();
                                int star_count = current.getstar_count();
                                int if_star = current.getIf_star();
                                String user_head = current.getuser_head();
                                String[] imagelist = current.getimagelist();
                                Comment[] commentlist = current.getcomment_list();
                                Intent intent = new Intent(requireActivity(), DetailActivity.class);
                                intent.putExtra("Username", Username);
                                intent.putExtra("createAt", createAt);
                                intent.putExtra("tag",tag);
                                intent.putExtra("title", title);
                                intent.putExtra("Content", Content);
                                intent.putExtra("comment_count",comment_count);
                                intent.putExtra("like_count",like_count);
                                intent.putExtra("if_like",if_like);
                                intent.putExtra("star_count",star_count);
                                intent.putExtra("if_star",if_star);
                                intent.putExtra("user_head", user_head);
                                intent.putExtra("imagelist", imagelist);
                                intent.putExtra("commentlist",convertComments2String(commentlist));
                                activityLauncher.launch(intent);
                            }
                        });
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                });
            }
        });


        Button btn = view.findViewById(R.id.add_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddPostActivity.class);
                // TODO 诶这个世界好像不对吧应该是发送的时间
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateTime = dateFormat.format(new Date());
                // TODO 好像需要发送userID从后端获取用户名和头像
                String username = "Zero"; // 替换为实际用户名
                // 将时间和用户名作为额外数据添加到 Intent
                intent.putExtra("currentTime", currentDateTime);
                intent.putExtra("username", username);
                intent.putExtra("user_head","giao.jpg");
                startActivity(intent);
            }
        });

        return view;
    }
}