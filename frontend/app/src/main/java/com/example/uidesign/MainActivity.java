package com.example.uidesign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.uidesign.utils.GlobalVariables;

import java.io.IOException;

import okhttp3.*;
public class MainActivity extends AppCompatActivity {
    Button login,gotoregister;
    EditText username,password;
    OkHttpClient client;
    TextView loginmessage;
    SharedPreferences prefs;
    private static final String URL = GlobalVariables.login_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new OkHttpClient();
        // 检查用户是否已登录，如果未登录，则返回到登录页面
        prefs = getSharedPreferences("com.example.android.myapp", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        //isLoggedIn = false;
        if (isLoggedIn) {
            Intent intent = new Intent(this, ContentAll.class);
            startActivity(intent);
            finish();
        }
        login = findViewById(R.id.loginbutton);
        gotoregister = findViewById(R.id.gotoregisterbutton);
        username = findViewById(R.id.loginusername);
        password = findViewById(R.id.loginpassword);
        loginmessage = findViewById(R.id.loginmessage);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 这里需要添加获取用户名和密码，传输到后端验证，如果验证成功，那么登录，如果验证失败，输出弹窗
                String usernametext = username.getText().toString();
                String passwordtext = password.getText().toString();
                sendMessage(usernametext,passwordtext);
            }
        });

        gotoregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 启动 HomeActivity
                Intent intent = new Intent(MainActivity.this, register.class);
                startActivity(intent);
            }
        });
    }
    private void sendMessage(String usernametext,String passwordtext) {
        RequestBody body = new FormBody.Builder()
                .add("username", usernametext)
                .add("password",passwordtext)
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
                            loginmessage.setText(responseText);
                            if(!responseText.equals("用户名不存在") &&!responseText.equals("密码错误")){
                                prefs.edit().putBoolean("isLoggedIn", true).apply();
                                prefs.edit().putString("userID", responseText).apply();
                                // 启动 HomeActivity
                                Intent intent = new Intent(MainActivity.this, ContentAll.class);
                                startActivity(intent);

                                // 结束当前活动
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }
}