package com.example.uidesign;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;

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

    public ImageView likeimage;
    public ImageView starimage;
    public ImageView image_user;
    public final ImageView[] imageshow = new ImageView[6];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String text = getIntent().getStringExtra("text");
        String Username = getIntent().getStringExtra("Username");
        String tag = getIntent().getStringExtra("tag");
        String createAt = getIntent().getStringExtra("createAt");
        String title = getIntent().getStringExtra("title");
        String Content = getIntent().getStringExtra("Content");
        int comment_count = getIntent().getIntExtra("comment_count",0);
        int like_count = getIntent().getIntExtra("like_count",0);
        int if_like = getIntent().getIntExtra("if_like",0);
        int star_count = getIntent().getIntExtra("star_count",0);
        int if_star = getIntent().getIntExtra("if_star",0);
        String user_head = getIntent().getStringExtra("user_head");
        String[] imagelist = getIntent().getStringArrayExtra("imagelist");

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

        int[] imageViewIds = {R.id.imageView1, R.id.imageView2, R.id.imageView3, R.id.imageView4, R.id.imageView5,R.id.imageView6};

        for (int i = 0; i < 6; i++) {
            imageshow[i] = findViewById(imageViewIds[i]);
            imageshow[i].setBackground(null);
        }

        UsernameView.setText(Username);
        createTime.setText(createAt);
        tagshow.setText(tag);
        Titletext.setText(title);
        Contenttext.setText(Content);
        commenttext.setText(String.valueOf(comment_count));
        liketext.setText(String.valueOf(like_count));
        startext.setText(String.valueOf(star_count));
        if(if_like == 0){
            likeimage.setImageResource(R.drawable.baseline_favorite_border_24);
        }
        else {
            likeimage.setImageResource(R.drawable.love);
        }
        if(if_star == 0){
            starimage.setImageResource(R.drawable.baseline_star_border_24);
        }
        else {
            starimage.setImageResource(R.drawable.collect_red);
        }
        // 这是点赞和收藏按钮的点击函数，这里需要等待后端的api写好，然后传递信息
        likearea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(if_like == 0){
                    likeimage.setImageResource(R.drawable.love);
                }
                else {
                    likeimage.setImageResource(R.drawable.baseline_favorite_border_24);
                }
            }
        });
        stararea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(if_star == 0){
                    starimage.setImageResource(R.drawable.collect_red);
                }
                else {
                    starimage.setImageResource(R.drawable.baseline_star_border_24);
                }
            }
        });
        try {
            image_user.setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(user_head)))));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        int i = 0;
        for(i = 0;i<imagelist.length;i++){
            try {
                imageshow[i].setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(imagelist[i])))));
            } catch (FileNotFoundException e) {
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

    public void getback(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}