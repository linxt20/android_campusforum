package com.example.uidesign;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.uidesign.follows.FollowItemList;
import com.example.uidesign.follows.FollowRecycleAdapter;
import com.example.uidesign.model.User;
import com.example.uidesign.utils.GlobalVariables;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class BlockActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView ifNoBlock;
    private FollowRecycleAdapter adapter;//声明适配器
    public FollowItemList followItemList = new FollowItemList();
    String userID;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);

        prefs = getSharedPreferences("com.example.android.myapp", 0);
        userID = prefs.getString("userID", "");
        ifNoBlock = findViewById(R.id.ifNoBlock);
        ifNoBlock.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.blockList);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("userid", userID)
                .build();
        Request request = new Request.Builder()
                .url(GlobalVariables.get_block_list_url)
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
                final List<User> myResponse = new Gson().fromJson(responseText, new TypeToken<List<User>>(){}.getType());
                if(myResponse.size() == 0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ifNoBlock.setVisibility(View.VISIBLE);
                        }
                    });
                }
                for(User user:myResponse){
                    followItemList.insert(user.getUser_head(), user.getUsername(), user.getDescription(), user.getId());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new FollowRecycleAdapter(BlockActivity.this, followItemList, true);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(BlockActivity.this));
                    }
                });
            }
        });

    }
}