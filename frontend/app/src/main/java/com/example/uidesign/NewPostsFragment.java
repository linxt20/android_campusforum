package com.example.uidesign;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import java.text.SimpleDateFormat;
import java.util.Date;
public class NewPostsFragment extends Fragment {
    private final BeanList BeanList = new BeanList();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_posts, container, false);

        // 这里是插入了一个默认的动态，这里等后端的获取接口完成，调用获取接口就能获得所有的动态信息，然后在插入adapter
        String[] imagelist = new String[2];
        for (int i = 0; i < 2; i++) {
            imagelist[i] = "content://media/external/images/media/40";
        }

        BeanList.insert("Zero","2022-02-01 10:30:00","我的新年计划",
                "今年我决定要更加健康地生活，所以我打算每天都去跑步和做瑜伽，同时控制饮食，希望能在年底达成我的目标。",
                0,0,0,"content://media/external/images/media/40",imagelist);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        BeanListAdapter adapter = new BeanListAdapter(getContext(), BeanList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button btn = view.findViewById(R.id.add_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddPostActivity.class);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateTime = dateFormat.format(new Date());
                // 获取用户名
                String username = "Zero"; // 替换为实际用户名
                // 将时间和用户名作为额外数据添加到 Intent
                intent.putExtra("currentTime", currentDateTime);
                intent.putExtra("username", username);
                intent.putExtra("user_head","content://media/external/images/media/40");

                startActivity(intent);
            }
        });

        ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // do nothing, no data to handle
                    }
                }
        );

        adapter.setRecyclerItemClickListener(new BeanListAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(int position) {
                Bean current = BeanList.get(position);
                String Username = current.getUsername();
                String createAt = current.getcreateAt();
                String title = current.gettitle();
                String Content = current.getContent();
                String user_head = current.getuser_head();
                String[] imagelist = current.getimagelist();
                Intent intent = new Intent(requireActivity(), DetailActivity.class);
                intent.putExtra("Username", Username);
                intent.putExtra("createAt", createAt);
                intent.putExtra("title", title);
                intent.putExtra("Content", Content);
                intent.putExtra("user_head", user_head);
                intent.putExtra("imagelist", imagelist);
                activityLauncher.launch(intent);
            }
        });
        return view;
    }
}