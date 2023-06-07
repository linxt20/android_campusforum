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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.uidesign.message.MessageItem;
import com.example.uidesign.message.MessageListAdapter;
import com.example.uidesign.model.Chat;
import com.example.uidesign.model.Message;
import com.example.uidesign.model.Sentence;
import com.example.uidesign.model.User;
import com.example.uidesign.utils.GlobalVariables;
import com.example.uidesign.utils.ImageDownloader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;

public class MessageActivity extends AppCompatActivity {
    public List<MessageItem> mMessageList = new ArrayList<>();
    SharedPreferences prefs;
    ImageView sendButton;
    Handler handler;
    EditText sendText;
    // 设置轮询时间间隔（单位：毫秒）
    long interval = 1000; // 每隔1秒轮询一次
    String myUserID, otherUserID, otherUserImage;
    MessageListAdapter adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_list_activity);

        recyclerView = findViewById(R.id.recycler_gchat);
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
        RequestBody body2 = new FormBody.Builder()
                .add("userid", otherUserID)
                .build();
        Request request2 = new Request.Builder()
                .url(GlobalVariables.get_user_url)
                .post(body2)
                .build();
        client.newCall(request2).enqueue(new okhttp3.Callback() {
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
                otherUserImage = myResponse.getUser_head();
            }
        });


        // get now time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = formatter.format(System.currentTimeMillis());
        sendButton = findViewById(R.id.button_gchat_send);
        sendText = findViewById(R.id.edit_gchat_message);
        sendButton.setOnClickListener(v -> {
            RequestBody body = new FormBody.Builder()
                    .add("sender_id", myUserID)
                    .add("receiver_id", otherUserID)
                    .add("content", sendText.getText().toString())
                    .add("date", nowTime)
                    .build();
            Request request = new Request.Builder()
                    .url(GlobalVariables.send_chat_url)
                    .post(body)
                    .build();
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseData = response.body().string();
                    Log.d("MessageActivity", responseData);
                    if(responseData.equals("success")){
                        mMessageList.add(new MessageItem(sendText.getText().toString(), myUserID, GlobalVariables.name2url(otherUserImage),
                                "test", nowTime.split(" ")[1], nowTime.split(" ")[0].split("-", 2)[1]));
                        runOnUiThread(() -> {
                            adapter =  new MessageListAdapter(MessageActivity.this, mMessageList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                            // 将RecyclerView滚动到最后一个条目
                            int itemCount = adapter.getItemCount();
                            if (itemCount > 0) {
                                recyclerView.scrollToPosition(itemCount - 1);
                            }
                            sendText.setText("");
                        });
                    }
                }
            });
        });
        // TODO 轮询
        getMessages();
        RequestBody body = new FormBody.Builder()
                .add("user1_id", myUserID)
                .add("user2_id", otherUserID)
                .build();
        Request request = new Request.Builder()
                .url(GlobalVariables.get_chat_url)
                .post(body)
                .build();
        client = new OkHttpClient();
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
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd");
                        mMessageList.add(
                                new MessageItem(sentence.getContent(), sentence.getSender_id(), GlobalVariables.name2url(otherUserImage),
                                        "test", formatter.format(sentence.getCreate_time()), dateFormatter.format(sentence.getCreate_time())));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter =  new MessageListAdapter(MessageActivity.this, mMessageList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                            // 将RecyclerView滚动到最后一个条目
                            int itemCount = adapter.getItemCount();
                            if (itemCount > 0) {
                                recyclerView.scrollToPosition(itemCount - 1);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void back(View view){
        finish();
    }
    public void getMessages() {
        // 创建一个 Handler 用于处理轮询任务
        handler = new Handler(Looper.getMainLooper());

        // 定义一个 Runnable 任务，用于执行轮询请求
        Runnable pollRunnable = new Runnable() {
            @Override
            public void run() {
                // 在这里执行你的网络请求代码
                // ...
                Log.d("pollRunnable",  "running");
                RequestBody body = new FormBody.Builder()
                        .add("user1_id", myUserID)
                        .add("user2_id", otherUserID)
                        .build();
                Request request = new Request.Builder()
                        .url(GlobalVariables.get_chat_url)
                        .post(body)
                        .build();
                OkHttpClient client = new OkHttpClient();
                // 发起网络请求
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }
                    List<MessageItem> tmp;
                    @Override
                    public void onResponse(Call call, okhttp3.Response response) {
                        try {
                            // 解析响应数据
                            String responseData = response.body().string();
                            Log.d("MessageActivity lunxun:", responseData);
                            final Chat myResponse = new Gson().fromJson(responseData, new TypeToken<Chat>(){}.getType());
                            tmp = new ArrayList<>();
                            for(Sentence sentence: myResponse.getSentence_list()){
                                //(String message, String senderid, String senderImg, String senderName, String createdAt)
                                // convert Date to string
                                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                                SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd");
                                tmp.add(
                                        new MessageItem(sentence.getContent(), sentence.getSender_id(), GlobalVariables.name2url(otherUserImage),
                                                "test", formatter.format(sentence.getCreate_time()), dateFormatter.format(sentence.getCreate_time())));
                            }
                            // 更新UI
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 在这里更新你的UI
                                    // ...
                                    if(mMessageList.size() != tmp.size()){
                                        Log.d("pollRunnable",  "update!!");
                                        mMessageList = tmp;
                                        adapter =  new MessageListAdapter(MessageActivity.this, mMessageList);
                                        recyclerView.setAdapter(adapter);
                                        // 将RecyclerView滚动到最后一个条目
                                        int itemCount = adapter.getItemCount();
                                        if (itemCount > 0) {
                                            recyclerView.scrollToPosition(itemCount - 1);
                                        }
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                handler.postDelayed(this::run, interval);
            }
        };

        // 开始第一次轮询
        handler.postDelayed(pollRunnable, interval);
    }

    @Override
    public void onPause() {
        super.onPause();
        // 停止轮询
        handler.removeCallbacksAndMessages(null);
    }
}