package com.example.uidesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {
    final int REQUEST_CODE_SELECT_IMAGE = 1001;

    private TextView logoutView;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 100;
    private ImageButton beforeView;
    private Button submitButton;
    ObjectMapper objectMapper = new ObjectMapper();

    OkHttpClient client;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        }
        sharedPreferences = this.getSharedPreferences("com.example.android.myapp", Context.MODE_PRIVATE);
        logoutView = findViewById(R.id.logoutTextView);
        client = new OkHttpClient();
        logoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "You have been logged out",Toast.LENGTH_LONG).show();
                SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                preferencesEditor.clear();
                preferencesEditor.apply();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        beforeView = findViewById(R.id.beforeButton);
        beforeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendMessage(1);
                sendImage();
            }
        });
    }

    public void sendImage(){
        OkHttpClient client = new OkHttpClient();

        // 创建文件选择器Intent
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        // 启动文件选择器
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri imageUri = data.getData();
                Log.d("Settings", "Image Uri: " + imageUri);
                Log.d("Settings", MediaType.parse(getContentResolver().getType(imageUri)).toString());
                Log.d("Settings2", imageUri.getPath());
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                    File tempFile = saveInputStreamToFile(inputStream); // 自定义方法，将输入流保存为临时文件
                    // RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image", tempFile.getName(), RequestBody.create(MediaType.parse("image/jpeg"), tempFile))
                            .build();

                    // RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageData); // 或者使用临时文件：RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8080/test_image")
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
    }

    public void sendMessage(int number) {
        RequestBody body = new FormBody.Builder()
                .add("username", "1")
                .add("password", "1")
                .build();

        Request request = new Request.Builder()
                .url("http://101.5.9.60:8080/test_data")
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
                            // 解析 JSON 数据为 Java 对象
                            try {
                                Map<String, Object> parsedData = objectMapper.readValue(responseText, new TypeReference<Map<String, Object>>() {});

                                // 访问解析后的数据
                                String value1 = (String) parsedData.get("key1");
                                int value2 = (Integer) parsedData.get("key2");

                                System.out.println(value1); // 输出：value1
                                System.out.println(value2); // 输出：value2
                                Log.d("LOG_NAME", "key1: " + value1); // 输出：key1: value1
                                Log.d("LOG_NAME", "key2: " + value2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

    }
}
