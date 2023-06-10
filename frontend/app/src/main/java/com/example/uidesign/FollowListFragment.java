package com.example.uidesign;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.uidesign.follows.FollowItemList;
import com.example.uidesign.follows.FollowRecycleAdapter;
import com.example.uidesign.model.Post;
import com.example.uidesign.model.User;
import com.example.uidesign.profile.BoardItem;
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

public class FollowListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FollowRecycleAdapter adapter;//声明适配器
    public FollowItemList followItemList = new FollowItemList();
    private TextView ifNoPeople;
    String userID;
    private Boolean isFollower;  //  true: follower, false: following

    public FollowListFragment(String userID, Boolean isFollower) {
        this.userID = userID;
        this.isFollower = isFollower;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        followItemList =  new FollowItemList();
        View view = inflater.inflate(R.layout.fragment_follow_list, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.followListRecyclerview);
        ifNoPeople = view.findViewById(R.id.ifNoPeople);
        ifNoPeople.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("userid", userID)
                .add("type",  isFollower? "fans" : "follow")
                .build();
        Request request = new Request.Builder()
                .url(GlobalVariables.user_follow_url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                followItemList =  new FollowItemList();
                String responseText = response.body().string();
                Log.d("followList response: ", responseText);
                final List<User> myResponse = new Gson().fromJson(responseText, new TypeToken<List<User>>(){}.getType());
                if(myResponse.size() == 0){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ifNoPeople.setVisibility(View.VISIBLE);
                        }
                    });
                }
                for(int i = 0; i < myResponse.size(); i++){
                    Log.d("FollowListFragment", myResponse.get(i).toString());
                    followItemList.insert(myResponse.get(i).getUser_head(), myResponse.get(i).getUsername(), myResponse.get(i).getDescription(), myResponse.get(i).getId());
                }
                Log.d("followList" , getActivity().getClass().toString());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new FollowRecycleAdapter(getActivity(), followItemList, false);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                });
            }
        });
    }
}