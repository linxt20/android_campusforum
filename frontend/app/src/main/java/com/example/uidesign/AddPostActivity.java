package com.example.uidesign;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPostActivity extends AppCompatActivity {
    private final BeanList BeanList = new BeanList();
    String username;
    String user_head;
    String currentTime;
    public ImageView image_user;
    public TextView UsernameView;
    public TextView createTime;
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 100;
    private EditText Titletext;
    private EditText contenttext;
    public final ImageView[] imageshow = new ImageView[6];
    public int[] imageViewIds = {R.id.imageV1, R.id.imageV2, R.id.imageV3, R.id.imageV4, R.id.imageV5,R.id.imageV6};
    public Uri[] urilist = new Uri[6];
    public String[] stringlist = new String[6];
    public int count;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        Intent intent = getIntent();
        currentTime = intent.getStringExtra("currentTime");
        username = intent.getStringExtra("username");
        user_head = getIntent().getStringExtra("user_head");

        image_user = findViewById(R.id.touxiang);
        UsernameView = findViewById(R.id.yonghuming);
        createTime = findViewById(R.id.shijian);
        Titletext = findViewById(R.id.biaoti);
        contenttext = findViewById(R.id.neirong);
        Button selectImageButton = findViewById(R.id.selectImageButton);
        for (int i = 0; i < 6; i++) {
            imageshow[i] = findViewById(imageViewIds[i]);
            imageshow[i].setVisibility(View.GONE);
        }
        UsernameView.setText(username);
        createTime.setText(currentTime);
        try {
            image_user.setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(user_head)))));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        }
        sharedPreferences = getSharedPreferences("com.example.android.draft", MODE_PRIVATE);
        loadDraft();

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count<6){
                    imageshow[count].setBackground(null);
                    selectImageFromGallery();
                    imageshow[count].setVisibility(View.VISIBLE);
                    if(count<3){
                        int temp = count+1;
                        while (temp<3){
                            imageshow[temp].setVisibility(View.INVISIBLE);
                            temp++;
                        }
                        while(temp<6){
                            imageshow[temp].setVisibility(View.GONE);
                            temp++;
                        }
                    }
                    else if(count<6){
                        int temp = count+1;
                        while(temp<6){
                            imageshow[temp].setVisibility(View.INVISIBLE);
                            temp++;
                        }
                    }
                }
            }
        });
    }
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            urilist[count] = data.getData();
            stringlist[count] = urilist[count].toString();
            try {
                imageshow[count].setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeStream(getContentResolver().openInputStream(urilist[count]))));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            count++;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "读取外部存储权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "读取外部存储权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void goback(View view){
        saveDraft();
        onBackPressed();
    }

    public void reset(View view){
        Titletext.setText("");
        contenttext.setText("");
        for (int i = 0; i < 6; i++) {
            imageshow[i].setBackground(null);
            imageshow[i].setVisibility(View.GONE);
            urilist[i]=null;
            stringlist[i]="";
        }
        count = 0;
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.clear();
        preferencesEditor.apply();
    }

    public void push(View view){
        // 这个insert是无效的，因为beanlist在addpostactivity文件的生命周期里面，活动结束了beanlist也无法存储，所以无法在动态那边显示，
        // 这里等后续后端接口出来，调用后端的插入接口，再在动态那边调用后端的获取接口即可
        BeanList.insert(username,currentTime,Titletext.getText().toString(),contenttext.getText().toString(),
                0,0,0,user_head,stringlist);
        reset(view);
        goback(view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDraft();
    }

    private void loadDraft(){
        String titletext = sharedPreferences.getString("titletext", "");
        String content = sharedPreferences.getString("content","");
        String imageUriString = sharedPreferences.getString("uris_key", null);
        count = sharedPreferences.getInt("count",0);
        Log.e("test", "count"+count);
        Titletext.setText(titletext);
        contenttext.setText(content);
        if (imageUriString != null) {
            String[] uriStringArray = imageUriString.split(",");
            for (int i = 0; i < uriStringArray.length; i++) {
                if (!uriStringArray[i].equals("null")) {
                    stringlist[i] = uriStringArray[i];
                    urilist[i] = Uri.parse(uriStringArray[i]);
                    try {
                        imageshow[i].setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeStream(getContentResolver().openInputStream(urilist[i]))));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    urilist[i] = null;
                    stringlist[i] ="";
                }
            }
        }
        if(count==0){
            for (int i = 0; i < 6; i++) {
                imageshow[i].setVisibility(View.GONE);
            }
        }
        else if(count<3){
            for(int i=0;i<count;i++){
                imageshow[i].setVisibility(View.VISIBLE);
            }
            int temp = count;
            while (temp<3){
                imageshow[temp].setVisibility(View.INVISIBLE);
                temp++;
            }
            while(temp<6){
                imageshow[temp].setVisibility(View.GONE);
                temp++;
            }
        }
        else if(count<6){
            for(int i=0;i<count;i++){
                imageshow[i].setVisibility(View.VISIBLE);
            }
            int temp = count;
            while(temp<6){
                imageshow[temp].setVisibility(View.INVISIBLE);
                temp++;
            }
        }
    }

    private void saveDraft() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String titletext = Titletext.getText().toString();
        String content = contenttext.getText().toString();
        editor.putString("titletext", titletext);
        editor.putString("content",content);
        StringBuilder uriString = new StringBuilder();
        for (int i = 0; i < stringlist.length; i++) {
            if (stringlist[i] != "") {
                uriString.append(stringlist[i]);
            } else {
                uriString.append("null"); // 使用 "null" 作为占位符
            }
            if (i < stringlist.length - 1) {
                uriString.append(",");
            }
        }
        editor.putString("uris_key", uriString.toString());
        editor.putInt("count",count);
        editor.apply();
    }
}
