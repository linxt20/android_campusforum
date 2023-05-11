package com.example.uidesign;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;

public class DetailActivity extends AppCompatActivity {
    public TextView UsernameView;
    public TextView createTime;
    public TextView Titletext;
    public TextView Contenttext;
    public ImageView image_user;
    public final ImageView[] imageshow = new ImageView[6];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String text = getIntent().getStringExtra("text");
        String Username = getIntent().getStringExtra("Username");
        String createAt = getIntent().getStringExtra("createAt");
        String title = getIntent().getStringExtra("title");
        String Content = getIntent().getStringExtra("Content");
        String user_head = getIntent().getStringExtra("user_head");
        String[] imagelist = getIntent().getStringArrayExtra("imagelist");

        UsernameView = findViewById(R.id.username);
        createTime = findViewById(R.id.createat);
        Titletext = findViewById(R.id.titletext);
        Contenttext = findViewById(R.id.contenttext);
        image_user = findViewById(R.id.userhead);
        int[] imageViewIds = {R.id.imageView1, R.id.imageView2, R.id.imageView3, R.id.imageView4, R.id.imageView5,R.id.imageView6};

        for (int i = 0; i < 6; i++) {
            imageshow[i] = findViewById(imageViewIds[i]);
            imageshow[i].setBackground(null);
        }

        UsernameView.setText(Username);
        createTime.setText(createAt);
        Titletext.setText(title);
        Contenttext.setText(Content);
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