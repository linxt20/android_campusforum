package com.example.uidesign;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestActivity extends AppCompatActivity {

    private ArrayList<ImageView> imageViewArrayList = new ArrayList<ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        imageViewArrayList.add(findViewById(R.id.imageView11));
        imageViewArrayList.add(findViewById(R.id.imageView12));
        imageViewArrayList.add(findViewById(R.id.imageView13));
        imageViewArrayList.add(findViewById(R.id.imageView14));
        imageViewArrayList.add(findViewById(R.id.imageView15));
        imageViewArrayList.add(findViewById(R.id.imageView16));
        imageViewArrayList.add(findViewById(R.id.imageView17));
        imageViewArrayList.add(findViewById(R.id.imageView18));
        imageViewArrayList.add(findViewById(R.id.imageView19));

        Button submitButton = findViewById(R.id.getImageButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendMessage(1);
                //sendImage();
                receiveImage();
            }
        });
    }

    public void receiveImage(){

        OkHttpClient client = new OkHttpClient();

        for(int i = 0; i < 9; i++){
            final int position = i;
            String imageUrl = GlobalVariables.get_image_url + (i+1) + ".jpg";
            Request request = new Request.Builder()
                    .url(imageUrl)  // 替换为实际的Spring Boot应用程序URL和图片文件名
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // 处理请求失败的情况
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // 获取图片数据
                        InputStream inputStream = response.body().byteStream();
                        // 处理图片数据
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageViewArrayList.get(position).setImageBitmap(bitmap);
                            }
                        });

                    } else {
                        // 处理请求失败的情况
                    }
                }
            });
        }
    }
}