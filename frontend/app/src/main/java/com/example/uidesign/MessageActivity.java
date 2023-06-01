package com.example.uidesign;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.uidesign.message.MessageItem;
import com.example.uidesign.message.MessageListAdapter;
import com.example.uidesign.model.Chat;
import com.example.uidesign.model.Sentence;
import com.example.uidesign.utils.GlobalVariables;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.*;

public class MessageActivity extends AppCompatActivity {
    public List<MessageItem> mMessageList;
    SharedPreferences prefs;
    String myUserID, otherUserID;
    MessageListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_list_activity);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new MessageListAdapter(this, mMessageList);
        OkHttpClient client = new OkHttpClient();
        prefs = getSharedPreferences("com.example.android.myapp", 0);
        myUserID = prefs.getString("userID", "");
        // TODO 设置在点进来时将对方的userid作为intent传进来
        Intent intent = getIntent();
        otherUserID = intent.getStringExtra("otherUserID");
        ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // do nothing, no data to handle
                    }
                }
        );
        // TODO 轮询
        RequestBody body = new FormBody.Builder()
                .add("user1_id", myUserID)
                .add("user2_id", otherUserID)
                .build();
        Request request = new Request.Builder()
                .url(GlobalVariables.get_chat_url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, okhttp3.Response response) {
                try {
                    String responseData = response.body().string();
                    Log.d("MessageActivity", responseData);
                    final Chat myResponse = new Gson().fromJson(responseData, new TypeToken<Chat>(){}.getType());
                    for(Sentence sentence: myResponse.getSentence_list()){
                        //(String message, String senderid, String senderImg, String senderName, String createdAt)
                        // convert Date to string
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        mMessageList.add(
                                new MessageItem(sentence.getContent(), sentence.getSender_id(), "9.jpg",
                                        "test", formatter.format(sentence.getCreate_time())));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter =  new MessageListAdapter(MessageActivity.this, mMessageList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}