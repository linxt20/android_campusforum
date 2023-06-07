package com.example.uidesign;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.uidesign.model.Comment;
import com.example.uidesign.model.Post;
import com.example.uidesign.model.User;
import com.example.uidesign.utils.GlobalVariables;
import com.example.uidesign.utils.ImageDownloader;
import com.example.uidesign.utils.TitleGenerator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddPostActivity extends AppCompatActivity {
    private final BeanList BeanList = new BeanList();
    private final static int REQUEST_ACCESS_FINE_LOCATION_PERMISSION = 10;
    String username;
    String user_head;
    String currentTime;
    String userID;
    String locationString = "";
    Boolean isVideo = false;
    public ImageView image_user;
    public TextView UsernameView, locationView;
    public TextView createTime;
    private static final int REQUEST_CODE_PICK_IMAGE = 1, REQUEST_CODE_PICK_VIDEO = 2, REQUEST_CODE_TAKE_PHOTO = 3, REQUEST_CODE_TAKE_VIDEO = 4;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 100, REQUEST_CAMERA_PERMISSION= 101;
    private EditText Titletext;
    private EditText contenttext;
    public final ImageView[] imageshow = new ImageView[6];
    public VideoView videoView;
    public Uri videoUri;
    public int[] imageViewIds = {R.id.imageV1, R.id.imageV2, R.id.imageV3, R.id.imageV4, R.id.imageV5,R.id.imageV6};
    public Uri[] urilist = new Uri[6];
    public String[] stringlist = new String[6];
    public int count;
    private SharedPreferences sharedPreferences;
    private LinearLayout linearLayout;
    private Button currentSelectedButton;

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
        ImageButton selectImageButton = findViewById(R.id.selectImageButton);
        ImageButton selectVideoButton = findViewById(R.id.selectVideoButton);
        ImageButton takePhotoButton = findViewById(R.id.takePhotoButton);
        ImageButton takeVideoButton = findViewById(R.id.takeVideoButton);
        videoView = findViewById(R.id.videoView);
        locationView = findViewById(R.id.locationTextView);
        locationView.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        for (int i = 0; i < 6; i++) {
            imageshow[i] = findViewById(imageViewIds[i]);
            imageshow[i].setVisibility(View.GONE);
        }
        UsernameView.setText(username);
        createTime.setText(currentTime);
        ImageDownloader headDownloader = new ImageDownloader(image_user);
        headDownloader.execute(GlobalVariables.name2url(user_head));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        }

        sharedPreferences = getSharedPreferences("com.example.android.myapp", MODE_PRIVATE);
        loadDraft();

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.setVisibility(View.GONE);
                if(count<6){
                    imageshow[count].setBackground(null);
                    selectImageFromGallery();
                }
            }
        });

        selectVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 6; i++) {
                    imageshow[i].setVisibility(View.GONE);
                }
                selectVideoFromGallery();
            }
        });

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.setVisibility(View.GONE);
                if(count<6){
                    imageshow[count].setBackground(null);
                    takePhoto();
                }
            }
        });

        takeVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 6; i++) {
                    imageshow[i].setVisibility(View.GONE);
                }
                takeVideo();
            }
        });
        linearLayout = findViewById(R.id.linear_layout);


        for (int i = 1; i <= 10; i++) {
            Button button = new Button(this);
            button.setText("标签" + i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentSelectedButton != null) {
                        currentSelectedButton.setTextColor(ContextCompat.getColor(view.getContext(), R.color.black));  // set the color of previously selected button to default color
                    }
                    Button clickedButton = (Button)view;
                    clickedButton.setTextColor(ContextCompat.getColor(view.getContext(), R.color.teal_200));  // set the color of selected button to blue
                    currentSelectedButton = clickedButton;  // save the reference to the newly selected button
                }
            });
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 10);
            button.setLayoutParams(layoutParams);

            linearLayout.addView(button);
        }
    }

    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
        else{
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
        }
    }

    private void takeVideo(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
        else{
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(intent, REQUEST_CODE_TAKE_VIDEO);
        }
    }

    private void selectVideoFromGallery() {
        //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // get multiple images (no more than 9) from imagepicker
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_VIDEO);
    }
    private void selectImageFromGallery() {
        //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // get multiple images (no more than 9) from imagepicker
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    // TODO 实现selectVideoFromGallery()方法

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            isVideo = false;
            ClipData clipData = data.getClipData();
            if(clipData != null && clipData.getItemCount() > 0 && clipData.getItemCount() <= 6) {
                // get multiple images
                count = 0;
                int i = 0;
                for (i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    urilist[count] = imageUri;
                    Log.d("LOG_NAME", "uri: " + urilist[count]);
                    stringlist[count] = urilist[count].toString();
                    try {
                        imageshow[count].setVisibility(View.VISIBLE);
                        imageshow[count].setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeStream(getContentResolver().openInputStream(urilist[count]))));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    count++;
                }
                for(; i < 3; i++) {
                    imageshow[i].setVisibility(View.INVISIBLE);
                }
                for(; i < 6; i++) {
                    imageshow[i].setVisibility(View.GONE);
                }
            } else if(clipData == null){
                // get single image
                count = 0;
                Uri imageUri = data.getData();
                urilist[count] = imageUri;
                Log.d("LOG_NAME", "uri: " + urilist[count]);
                stringlist[count] = urilist[count].toString();
                try {
                    imageshow[count].setVisibility(View.VISIBLE);
                    imageshow[count].setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeStream(getContentResolver().openInputStream(urilist[count]))));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                count++;
                for(int i = 1; i < 3; i++) {
                    imageshow[i].setVisibility(View.INVISIBLE);
                }
                for(int i = 3; i < 6; i++) {
                    imageshow[i].setVisibility(View.GONE);
                }
            } else {
                // 选中的图片数量不符合要求
                Toast.makeText(this, "请选择1到6张图片", Toast.LENGTH_SHORT).show();
            }

//            urilist[count] = data.getData();
//            Log.d("LOG_NAME", "uri: " + urilist[count]);
//            stringlist[count] = urilist[count].toString();
//            try {
//                imageshow[count].setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeStream(getContentResolver().openInputStream(urilist[count]))));
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//            count++;
        }
        else if (requestCode == REQUEST_CODE_PICK_VIDEO && resultCode == RESULT_OK && data!= null){
            isVideo = true;
            videoUri = data.getData();
            Log.d("LOG_NAME", "uri: " + videoUri);
            try {
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(videoUri);
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Log.d("AddPostAdapter", "mp4: " + videoUri.toString());
                        videoView.start();
                        mp.setLooping(true); // 将MediaPlayer设置为循环播放
                    }
                });
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // 视频播放完成时重新开始播放
                        videoView.start();
                    }
                });
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
            for(int i = 1; i < 3; i++) {
                imageshow[i].setVisibility(View.INVISIBLE);
            }
            for(int i = 3; i < 6; i++) {
                imageshow[i].setVisibility(View.GONE);
            }
        }
        else if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            // 相机拍摄成功，可以在这里处理拍摄的照片
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // get single image
            count = 0;
            // 将照片存入图库
            String imageUri = MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, TitleGenerator.generateTitle(), null);

             // 获取存入图库后的URI
            Uri uri = Uri.parse(imageUri);
            urilist[count] = uri;
            Log.d("LOG_NAME", "uri: " + urilist[count]);
            stringlist[count] = urilist[count].toString();
            try {
                imageshow[count].setVisibility(View.VISIBLE);
                imageshow[count].setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeStream(getContentResolver().openInputStream(urilist[count]))));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            count++;
            for(int i = 1; i < 3; i++) {
                imageshow[i].setVisibility(View.INVISIBLE);
            }
            for(int i = 3; i < 6; i++) {
                imageshow[i].setVisibility(View.GONE);
            }
        } else if (requestCode == REQUEST_CODE_TAKE_VIDEO && resultCode == RESULT_OK) {
            isVideo = true;
            videoUri = data.getData();
            videoView.setVisibility(View.VISIBLE);
            Log.d("LOG_NAME", "uri: " + videoUri);
            try {
                videoView.setVideoURI(videoUri);
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Log.d("AddPostAdapter", "mp4: " + videoUri.toString());
                        videoView.start();
                        mp.setLooping(true); // 将MediaPlayer设置为循环播放
                    }
                });
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // 视频播放完成时重新开始播放
                        videoView.start();
                    }
                });
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
            for(int i = 0; i < 6; i++) {
                imageshow[i].setVisibility(View.GONE);
            }
        }
    }

    private File saveInputStreamToFile(InputStream inputStream) throws IOException {
        File tempFile = File.createTempFile("temp", null); // 创建临时文件
        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead); // 将输入流的数据写入临时文件
        }

        outputStream.close();
        inputStream.close();

        return tempFile;
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
        else if(requestCode == REQUEST_ACCESS_FINE_LOCATION_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "GPS已开启",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "GPS被拒绝",Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 相机权限已授予，可以继续调用相机接口
                takePhoto();
            } else {
                // 相机权限被拒绝
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void goback(View view){
        saveDraft();
        // onBackPressed();
        Intent intent = new Intent(this, ContentAll.class);
        startActivity(intent);
        finish();
        //startActivity(new Intent(this, ContentAll.class));
    }

    public void reset(View view){
        Titletext.setText("");
        contenttext.setText("");
        for (int i = 0; i < 6; i++) {
            imageshow[i].setBackground(null);
            imageshow[i].setVisibility(View.GONE);
            urilist[i]=null;
            videoView.setVisibility(View.GONE);
            videoUri = null;
            isVideo = false;
            stringlist[i]="";
        }
        count = 0;
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        Boolean tmpIsLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        String tmpUserID = sharedPreferences.getString("userID", "");
        preferencesEditor.clear();
        preferencesEditor.putBoolean("isLoggedIn", tmpIsLoggedIn);
        preferencesEditor.putString("userID", tmpUserID);
        preferencesEditor.apply();
    }

    public void push(View view){
        // 这个insert是无效的，因为beanlist在addpostactivity文件的生命周期里面，活动结束了beanlist也无法存储，所以无法在动态那边显示，
        // TODO 这里等后续后端接口出来，调用后端的插入接口，再在动态那边调用后端的获取接口即可
//        BeanList.insert(username,currentTime,"标签", Titletext.getText().toString(),contenttext.getText().toString(),
//                0,0,0,0,0,user_head,stringlist,null);
        OkHttpClient client = new OkHttpClient();
        SharedPreferences prefs = getSharedPreferences("com.example.android.myapp", MODE_PRIVATE);
        userID = prefs.getString("userID", "");

        // get current time
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        currentTime = simpleDateFormat.format(date);
        // TODO 处理标签
        RequestBody body = new FormBody.Builder()
                .add("userid", userID)
                .add("createAt", currentTime)
                .add("title", Titletext.getText().toString())
                .add("content", contenttext.getText().toString())
                .add("tag", currentSelectedButton == null? "": currentSelectedButton.getText().toString())
                .add("resource_num", isVideo? "1" : count+"")
                .add("resource_type", isVideo? "mp4" : "jpg")
                .add("location", locationString)
                .build();
        Request request = new Request.Builder()
                .url(GlobalVariables.add_post_url)
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
                Log.d("AddPostActivity response: ", responseText);
                final String myResponse[] = new Gson().fromJson(responseText, new TypeToken<String[]>(){}.getType());
                for(int i = 0; i < myResponse.length; i++){
                    Log.d("AddPostActivity", i + " image: " + myResponse[i]);
                    if(isVideo){
                        sendVideo(myResponse[i]);
                        break;
                    }
                    sendImage(i, myResponse[i]);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reset(view);
                        goback(view);
                    }
                });
            }
        });
        Toast.makeText(this, "PUSHED",Toast.LENGTH_SHORT).show();
    }

    public void sendVideo(String name){
        String mediaType = MediaType.parse(getContentResolver().getType(videoUri)).toString();
        InputStream inputStream = null;
        OkHttpClient client = new OkHttpClient();
        try {
            inputStream = getContentResolver().openInputStream(videoUri);
            File tempFile = saveInputStreamToFile(inputStream); // 自定义方法，将输入流保存为临时文件
            inputStream.close();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", tempFile.getName(), RequestBody.create(MediaType.parse(mediaType), tempFile))
                    .addFormDataPart("name", name)
                    .build();

            // RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageData); // 或者使用临时文件：RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
            Request request = new Request.Builder()
                    .url(GlobalVariables.test_image_url)
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // 处理响应
                    Log.d("LOG_NAME", response.body().string());
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    // 处理异常
                }
            });

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendImage(int position, String name){
        Uri imageUri = urilist[position];
        String mediaType = MediaType.parse(getContentResolver().getType(imageUri)).toString();
        InputStream inputStream = null;
        OkHttpClient client = new OkHttpClient();
        try {
            inputStream = getContentResolver().openInputStream(imageUri);
            File tempFile = saveInputStreamToFile(inputStream); // 自定义方法，将输入流保存为临时文件
            inputStream.close();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", tempFile.getName(), RequestBody.create(MediaType.parse(mediaType), tempFile))
                    .addFormDataPart("name", name)
                    .build();

            // RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageData); // 或者使用临时文件：RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
            Request request = new Request.Builder()
                    .url(GlobalVariables.test_image_url)
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // 处理响应
                    Log.d("LOG_NAME", response.body().string());
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    // 处理异常
                }
            });

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDraft();
    }

    private void loadDraft(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        }
        String titletext = sharedPreferences.getString("titletext", "");
        String content = sharedPreferences.getString("content","");
        String imageUriString = sharedPreferences.getString("uris_key", null);
        count = sharedPreferences.getInt("count",0);
        Log.d("test", "count"+count);
        Titletext.setText(titletext);
        contenttext.setText(content);
        if (imageUriString != null) {
            String[] uriStringArray = imageUriString.split(",");
            for (int i = 0; i < uriStringArray.length; i++) {
                if (!uriStringArray[i].equals("null")) {
                    stringlist[i] = uriStringArray[i];
                    urilist[i] = Uri.parse(uriStringArray[i]);
                } else {
                    urilist[i] = null;
                    stringlist[i] ="";
                }
            }
        }
        // TODO 狗permission takePersistableUriPermission()
//        for(int i = 0; i < count; i++){
//            try {
//                imageshow[i].setVisibility(View.VISIBLE);
//                imageshow[i].setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeStream(getContentResolver().openInputStream(urilist[i]))));
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
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
        for (int i = 0; i < count; i++) {
            if (stringlist[i] != "") {
                uriString.append(stringlist[i]);
            } else {
                uriString.append("null"); // 使用 "null" 作为占位符
            }
            if (i < stringlist.length - 1) {
                uriString.append(",");
            }
        }
        Log.d("test", "uriString: "+uriString);
        editor.putString("uris_key", uriString.toString());
        editor.putInt("count",count);
        editor.apply();
    }


    public void getLocation(View view){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // 处理新的位置数据
                Geocoder geocoder = new Geocoder(AddPostActivity.this, Locale.CHINA);
                double latitude = location.getLatitude(); // 替换为你的纬度
                double longitude = location.getLongitude(); // 替换为你的经度
                Log.d("test", "la: " + latitude + ", lo: " + longitude);
                String address = "";
                try {
                    Address getAddress = geocoder.getFromLocation(latitude, longitude, 1).get(0);
                    for(int i = 0; i < getAddress.getMaxAddressLineIndex(); i++){
                        address += getAddress.getAddressLine(i);
                    }

                    if (!address.equals("")) {
                        //Address address = addresses.get(0);
                        //String addressLine = address.getAddressLine(0); // 获取第一行地址信息
                        Log.d("test address", "Address: " + address);
                        locationView.setVisibility(View.VISIBLE);
                        locationView.setText(address);
                    } else {
                        Log.d("test address", "No address found");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                locationString =  location.toString();
            }

            // 其他回调方法...
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION_PERMISSION);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            Log.d("test", "location: " + location.toString());
            locationString =  location.toString();
        } else {
            Log.d("test", "location: null");
        }
    }

}
