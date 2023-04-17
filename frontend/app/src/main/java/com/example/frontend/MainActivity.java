package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import okhttp3.*;
public class MainActivity extends AppCompatActivity {
    private Button buttonSend;
    private OkHttpClient client;
    private static final String URL = "http://10.0.2.2:8080/message"; // 使用你的服务器IP地址和端口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new OkHttpClient();
        buttonSend = findViewById(R.id.button_send);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("hello");
            }
        });
    }
    private void sendMessage(String message) {
        RequestBody body = new FormBody.Builder()
                .add("message", message)
                .build();

        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseText = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttonSend.setText(responseText);
                        }
                    });
                }
            }
        });
    }

}