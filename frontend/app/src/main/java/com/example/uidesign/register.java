package com.example.uidesign;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.uidesign.utils.GlobalVariables;

import java.io.IOException;

import okhttp3.*;
public class register extends AppCompatActivity {
    Button register,gobacklogin;
    EditText username,password;
    TextView registermessage;
    OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        client = new OkHttpClient();
        register = findViewById(R.id.registerbutton);
        gobacklogin = findViewById(R.id.gobackloginbutton);

        username = findViewById(R.id.registerusername);
        password = findViewById(R.id.registerpassword);
        registermessage = findViewById(R.id.registermessage);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 处理用户名和密码 传输到后端，验证成功或者失败都给弹窗
                String usernametext = username.getText().toString();
                String passwordtext = password.getText().toString();
                sendMessage(usernametext,passwordtext);
            }
        });

        gobacklogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void sendMessage(String usernametext,String passwordtext) {
        RequestBody body = new FormBody.Builder()
                .add("username", usernametext)
                .add("password",passwordtext)
                .build();

        Request request = new Request.Builder()
                .url(GlobalVariables.register_url)
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
                            registermessage.setText(responseText);
                            if(responseText.equals("saved")){
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }
}