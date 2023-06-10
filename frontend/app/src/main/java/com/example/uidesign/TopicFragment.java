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
import android.widget.TextView;

import com.example.uidesign.model.Comment;
import com.example.uidesign.model.Message;
import com.example.uidesign.model.Post;
import com.example.uidesign.notice.NoticeAdapter;
import com.example.uidesign.notice.NoticeList;
import com.example.uidesign.utils.GlobalVariables;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TopicFragment extends Fragment {
    RecyclerView recyclerView;
    NoticeAdapter adapter;
    SharedPreferences prefs;
    TextView ifNoMessage;
    String userID;
    NoticeList noticeList = new NoticeList();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.notification_list);
        ifNoMessage = view.findViewById(R.id.ifNoMessage);
        ifNoMessage.setVisibility(View.GONE);
        adapter = new NoticeAdapter(getContext(), noticeList);
        ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // do nothing, no data to handle
                    }
                }
        );

        OkHttpClient client = new OkHttpClient();
        prefs = getActivity().getSharedPreferences("com.example.android.myapp", 0);
        userID = prefs.getString("userID", "");

        RequestBody body = new FormBody.Builder()
                .add("userid", userID)
                .build();
        Request request = new Request.Builder()
                .url(GlobalVariables.get_notice_url)
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
                Log.d("TopicFragment response: ", responseText);
                final List<Message> myResponse = new Gson().fromJson(responseText, new TypeToken<List<Message>>(){}.getType());
                if(myResponse == null) return;
                if(myResponse.size() == 0){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ifNoMessage.setVisibility(View.VISIBLE);
                        }
                    });
                }
                for(int i = myResponse.size() - 1; i >= 0; i--){
                    Log.d("TopicFragment", myResponse.get(i).toString());
                    String title = myResponse.get(i).getTitle();
                    String content = myResponse.get(i).getContent();
                    Date createTime = myResponse.get(i).getCreate_time();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String type = myResponse.get(i).getType();
                    String related_userid = myResponse.get(i).getRelated_userid();
                    noticeList.insert(title, content, formatter.format(createTime), type, related_userid);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter =  new NoticeAdapter(getContext(), noticeList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                });
            }
        });

        return view;
    }
}