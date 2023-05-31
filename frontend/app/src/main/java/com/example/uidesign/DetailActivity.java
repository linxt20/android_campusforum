package com.example.uidesign;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uidesign.comment.CommentItem;
import com.example.uidesign.comment.CommentItemList;
import com.example.uidesign.comment.CommentRecycleAdapter;
import com.example.uidesign.model.Comment;
import com.example.uidesign.model.Post;
import com.example.uidesign.utils.GlobalVariables;
import com.example.uidesign.utils.ImageDownloader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.*;

public class DetailActivity extends AppCompatActivity {
    public TextView UsernameView;
    public TextView createTime;
    public TextView tagshow;
    public TextView Titletext;
    public TextView Contenttext;

    public TextView commenttext;
    public TextView liketext;
    public TextView startext;
    public LinearLayout likearea;
    public LinearLayout stararea;
    String postid;

    public ImageView likeimage;
    public ImageView starimage;
    public LinearLayout commentButton, commentArea;
    public Button sendCommentButton;
    public EditText editTextComment;
    public ImageView image_user;
    CommentRecycleAdapter adapter;
    Boolean showCommentLayout = false;

    SharedPreferences prefs;
    public final ImageView[] imageshow = new ImageView[6];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        UsernameView = findViewById(R.id.username);
        createTime = findViewById(R.id.createat);
        tagshow = findViewById(R.id.Tagshow);
        Titletext = findViewById(R.id.titletext);
        Contenttext = findViewById(R.id.contenttext);
        commenttext = findViewById(R.id.comment);
        liketext = findViewById(R.id.liketext);
        startext = findViewById(R.id.startext);
        likearea = findViewById(R.id.like);
        stararea = findViewById(R.id.star);
        likeimage = findViewById(R.id.likeimage);
        starimage = findViewById(R.id.starimage);
        image_user = findViewById(R.id.userhead);
        commentArea = findViewById(R.id.commentLinearLayout);
        commentButton = findViewById(R.id.commentButton);
        sendCommentButton = findViewById(R.id.sendCommentButton);
        editTextComment = findViewById(R.id.editTextComment);
        commentArea.setVisibility(View.GONE);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showCommentLayout) {
                    commentArea.setVisibility(View.GONE);
                    showCommentLayout = false;
                } else {
                    commentArea.setVisibility(View.VISIBLE);
                    showCommentLayout = true;
                }
            }
        });
        postid = getIntent().getStringExtra("postid");
        prefs = getSharedPreferences("com.example.android.myapp", MODE_PRIVATE);

        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextComment.getText().toString().trim().equals("")) {
                    Toast.makeText(DetailActivity.this, "请输入评论",Toast.LENGTH_SHORT).show();
                    return;
                }
                String userID = prefs.getString("userID", "");
                String content = editTextComment.getText().toString();
                // get time
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = formatter.format(date);
                RequestBody body = new FormBody.Builder()
                        .add("userid", userID)
                        .add("postid", postid)
                        .add("createAt", time)
                        .add("content", content)
                        .build();
                Request request = new Request.Builder()
                        .url(GlobalVariables.send_comment_url)
                        .post(body)
                        .build();
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback(){
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.d("DetailActivity", "Failed to connect!");
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String responseText = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    commentArea.setVisibility(View.GONE);
                                    showCommentLayout = false;
                                    Toast.makeText(DetailActivity.this, "评论成功",Toast.LENGTH_SHORT).show();
                                    initView();
                                }
                            });
                        }
                    }
                });
            }
        });

        int[] imageViewIds = {R.id.imageView1, R.id.imageView2, R.id.imageView3, R.id.imageView4, R.id.imageView5,R.id.imageView6};

        for (int i = 0; i < 6; i++) {
            imageshow[i] = findViewById(imageViewIds[i]);
            imageshow[i].setBackground(null);
        }

        initView();

        // 这是点赞和收藏按钮的点击函数，这里需要等待后端的api写好，然后传递信息
        likearea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
//                if(if_like == 0){
//                    likeimage.setImageResource(R.drawable.love);
//                }
//                else {
//                    likeimage.setImageResource(R.drawable.baseline_favorite_border_24);
//                }
            }
        });
        stararea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
//                if(if_star == 0){
//                    starimage.setImageResource(R.drawable.collect_red);
//                }
//                else {
//                    starimage.setImageResource(R.drawable.baseline_star_border_24);
//                }
            }
        });

    }

    public void initView(){
        String userID = prefs.getString("userID", "");
        RequestBody body = new FormBody.Builder()
                .add("userid", userID)
                .add("postid", postid)
                .build();
        Request request = new Request.Builder()
                .url(GlobalVariables.get_post_url)
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("DetailActivity", "Failed to connect!");
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseText = response.body().string();
                    final Post myResponse = new Gson().fromJson(responseText, new TypeToken<Post>(){}.getType());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UsernameView.setText(myResponse.getAuthor_name());
                            createTime.setText(myResponse.getCreate_time());
                            tagshow.setText(myResponse.getTag());
                            Titletext.setText(myResponse.getTitle());
                            Contenttext.setText(myResponse.getContent());
                            List<Comment> comments = myResponse.getComment_list();
                            Log.d("DetailActivity", "comments: " + comments.toString());
                            if(comments.toString().equals("[]")) commenttext.setText("0");
                            else commenttext.setText(comments.size() + "");
                            liketext.setText(myResponse.getLike_count() + "");
                            startext.setText(myResponse.getStar_count() + "");
                            if(myResponse.getIf_like() == 0){
                                likeimage.setImageResource(R.drawable.baseline_favorite_border_24);
                            }
                            else {
                                likeimage.setImageResource(R.drawable.love);
                            }
                            if(myResponse.getIf_star() == 0){
                                starimage.setImageResource(R.drawable.baseline_star_border_24);
                            }
                            else {
                                starimage.setImageResource(R.drawable.collect_red);
                            }
                            CommentItemList commentItemList = new CommentItemList();
                            for(Comment comment:comments){
                                commentItemList.insert(comment.getAuthor_head(), comment.getAuthor_name(), comment.getCreate_time().toString(), comment.getContent());
                            }
                            // TODO 这里getBaseContext()可能会有问题
                            adapter = new CommentRecycleAdapter(getBaseContext(), commentItemList);
                            RecyclerView recyclerView = findViewById(R.id.detailCommentList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

                            ImageDownloader headDownloader = new ImageDownloader(image_user);
                            headDownloader.execute(GlobalVariables.name2url(myResponse.getAuthor_head()));
                            int i = 0;
                            for(i = 0; i < myResponse.getResource_list().length; i++){
                                try {
                                    ImageDownloader imageDownloader = new ImageDownloader(imageshow[i]);
                                    imageDownloader.execute(GlobalVariables.name2url(myResponse.getResource_list()[i]));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            while (i<3){
                                imageshow[i].setVisibility(View.INVISIBLE);
                                i++;
                            }
                            while(i<6){
                                imageshow[i].setVisibility(View.GONE);
                                i++;
                            }
                        }
                    });
                }
            }
        });
    }

    public void getback(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}