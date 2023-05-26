package com.example.uidesign;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uidesign.profile.BoardItemList;
import com.example.uidesign.profile.RecycleAdapter;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileMyPostsFragment extends Fragment {

    private RecyclerView recyclerView;//声明RecyclerView
    private RecycleAdapter adapter;//声明适配器

    public BoardItemList boardItemList = new BoardItemList();

    public ProfileMyPostsFragment() {
        // Required empty public constructor
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
        // TODO 从后端获得数据并展示在Recyclerview上
        // Create an adapter and supply the data to be displayed.
//        String imageUrl = GlobalVariables.get_image_url + 8 + ".jpg";
//        Request request = new Request.Builder()
//                .url(imageUrl)  // 替换为实际的Spring Boot应用程序URL和图片文件名
//                .build();
//        OkHttpClient client = new OkHttpClient();
        int picsCount = 20;

        for(int i = 0; i < picsCount; i++){
            String imageUrl = "http://" + GlobalVariables.host + ":8080/static/1.jpg"; // 替换为您的图片URL
            boardItemList.insert(imageUrl, "haha", "2020-05-20 11:11:01");
            if(boardItemList.size() == picsCount){
                // sort boardItemList by date
                //boardItemList.sort();
                adapter = new RecycleAdapter(getActivity(), boardItemList);
                // Connect the adapter with the recycler view.
                recyclerView.setAdapter(adapter);
                // Give the recycler view a default layout manager.
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        }

        /* for(int i = 0; i < picsCount; i++){
            int pos = i;
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // 处理请求失败的情况
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // 获取图片数据
                        Log.d("ProfileMyPostFragment", "Successful response");
                        InputStream inputStream = response.body().byteStream();
                        // 处理图片数据
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                        // 将图片展示在页面上
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(pos < 10)
                                    boardItemList.insert(bitmap, "Home " + pos, "2023-05-16 10:00:0" + pos);
                                else
                                    boardItemList.insert(bitmap, "Home " + pos, "2023-05-16 10:00:" + pos);
                                if(boardItemList.size() == picsCount){
                                    // sort boardItemList by date
                                    boardItemList.sort();
                                    adapter = new RecycleAdapter(getActivity(), boardItemList);
                                    // Connect the adapter with the recycler view.
                                    recyclerView.setAdapter(adapter);
                                    // Give the recycler view a default layout manager.
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                }
                            }
                        });
                    } else {
                        // 处理请求失败的情况
                        Log.d("ProfileMyPostFragment", "Err: " + response.body().string());
                    }
                }
            });
        }
        */

        return view;
    }
}