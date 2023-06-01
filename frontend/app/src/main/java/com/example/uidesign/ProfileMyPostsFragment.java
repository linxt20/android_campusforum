package com.example.uidesign;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uidesign.model.Comment;
import com.example.uidesign.model.Post;
import com.example.uidesign.profile.BoardItem;
import com.example.uidesign.profile.BoardItemList;
import com.example.uidesign.profile.RecycleAdapter;
import com.example.uidesign.utils.GlobalVariables;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileMyPostsFragment extends Fragment {

    private RecyclerView recyclerView;//声明RecyclerView
    private RecycleAdapter adapter;//声明适配器
    SharedPreferences prefs;

    private String userID;

    public BoardItemList boardItemList = new BoardItemList();

    public ProfileMyPostsFragment(String userID) {
        this.userID = userID;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile_my_posts, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);


        // TODO 从后端获得数据Post的List并展示在Recyclerview上
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("userid", userID)
                .add("type", "create")
                .build();
        Request request = new Request.Builder()
                .url(GlobalVariables.get_user_posts_url)
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
                Log.d("ProfileMyPost response: ", responseText);
                final List<Post> myResponse = new Gson().fromJson(responseText, new TypeToken<List<Post>>(){}.getType());
                for(int i = 0; i < myResponse.size(); i++){
                    Log.d("ProfileMyPostsFragment", myResponse.get(i).toString());
                    String[] images = myResponse.get(i).getResource_list();
                    if(images.length == 0 || myResponse.get(i).getResource_type().equals("mp4")) {
                        // TODO 处理以下没有图片的情况。。(改成无图片)
                        boardItemList.insert("nopic.jpg", myResponse.get(i).getTitle(), myResponse.get(i).getCreate_time(), myResponse.get(i).getPostid());
                    }
                    else {
                        boardItemList.insert(images[0], myResponse.get(i).getTitle(), myResponse.get(i).getCreate_time(), myResponse.get(i).getPostid());
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new RecycleAdapter(getActivity(), boardItemList);
                        // TODO 可能有点慢，目前还不知道怎么改。。
                        adapter.setRecyclerItemClickListener(new RecycleAdapter.OnRecyclerItemClickListener() {
                            @Override
                            public void onRecyclerItemClick(int position) {
                                BoardItem current = boardItemList.get(position);
                                Intent intent = new Intent(requireActivity(), DetailActivity.class);
                                intent.putExtra("postid", current.getPostid());
                                startActivity(intent);
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