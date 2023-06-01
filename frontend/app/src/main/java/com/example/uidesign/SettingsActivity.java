package com.example.uidesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uidesign.model.Post;
import com.example.uidesign.utils.GlobalVariables;
import com.example.uidesign.utils.ImageDownloader;
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
    private TextView editPersonalInfoButton, editPasswordButton;
    private EditText editTextUsername, editTextDescription, editTextPassword, editTextConfirmPassword;
    private Button submitPersonalInfoButton, submitPasswordButton;
    private LinearLayout expandableLayoutInfo;
    private LinearLayout expandableLayoutPassword;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 100;
    private ImageButton beforeView;
    private Button submitButton;
    private boolean isVisibleInfo = false, isVisiblePassword = false;
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
        expandableLayoutInfo = findViewById(R.id.expandableLayout);
        expandableLayoutInfo.setVisibility(View.GONE);
        expandableLayoutPassword = findViewById(R.id.expandableLayoutPassword);
        expandableLayoutPassword.setVisibility(View.GONE);

        editPersonalInfoButton = findViewById(R.id.editPersonalInfoTextView);
        editPersonalInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVisibleInfo) {
                    expandableLayoutInfo.setVisibility(View.GONE);
                    isVisibleInfo = false;
                } else {
                    expandableLayoutInfo.setVisibility(View.VISIBLE);
                    isVisibleInfo = true;
                }
            }
        });

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextDescription = findViewById(R.id.editTextDescription);
        submitPersonalInfoButton = findViewById(R.id.submitPersonalInfoButton);
        submitPersonalInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextUsername.getText().toString();
                String description = editTextDescription.getText().toString();
                if (username.equals("") || description.equals("")) {
                    Toast.makeText(SettingsActivity.this, "Please fill in all the blanks", Toast.LENGTH_LONG).show();
                } else {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = new FormBody.Builder()
                            .add("userid",  sharedPreferences.getString("userID", ""))
                            .add("username", username)
                            .add("description", description)
                            .build();

                    Request request = new Request.Builder()
                            .url(GlobalVariables.update_user_info_url)
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
                                String res = response.body().string();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView nameView = findViewById(R.id.usernameTextView);
                                        nameView.setText(username);
                                        TextView descriptionView = findViewById(R.id.descriptionTextView);
                                        descriptionView.setText(description);
                                        Toast.makeText(SettingsActivity.this, "Successfully updated user info",
                                                Toast.LENGTH_LONG).show();
                                        isVisibleInfo = false;
                                        expandableLayoutInfo.setVisibility(View.GONE);
                                    }
                                });
                            }
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SettingsActivity.this, "用户名已存在",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        }
                    });
                }
            }
        });

        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextPasswordConfirm);
        submitPasswordButton = findViewById(R.id.submitPasswordButton);
        submitPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editTextPassword.getText().toString();
                String passwordConfirm = editTextConfirmPassword.getText().toString();
                if (password.equals(passwordConfirm)) {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = new FormBody.Builder()
                            .add("userid",  sharedPreferences.getString("userID", ""))
                            .add("password", password)
                            .build();

                    Request request = new Request.Builder()
                            .url(GlobalVariables.update_password_url)
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SettingsActivity.this, "修改密码成功！",
                                                Toast.LENGTH_LONG).show();
                                        isVisiblePassword = false;
                                        expandableLayoutPassword.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(SettingsActivity.this, "两次输入密码不一致", Toast.LENGTH_LONG).show();
                }
            }
        });

        editPasswordButton = findViewById(R.id.editPasswordTextView);
        editPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVisiblePassword) {
                    expandableLayoutPassword.setVisibility(View.GONE);
                    isVisiblePassword = false;
                } else {
                    expandableLayoutPassword.setVisibility(View.VISIBLE);
                    isVisiblePassword = true;
                }
            }
        });
        Intent intent = getIntent();
        ImageView imageUser = findViewById(R.id.profileCircleImageView);
        ImageDownloader headDownloader = new ImageDownloader(imageUser);
        headDownloader.execute(GlobalVariables.name2url(intent.getStringExtra("imageName")));
        TextView nameView = findViewById(R.id.usernameTextView);
        nameView.setText(intent.getStringExtra("username"));
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        descriptionTextView.setText(intent.getStringExtra("description"));

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
                //receiveImage();
            }
        });
    }

    // 用来测试后端给前端传图片，目前有了url可以不用这个方法～
    public void receiveImage(){
//        ImageView imageView = findViewById(R.id.profileCircleImageView);
//        String imageUrl = "http://8.130.126.81:8080/images/you.jpg"; // 替换为您的图片URL
//        ImageDownloader imageDownloader = new ImageDownloader(imageView);
//        imageDownloader.execute(imageUrl);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(GlobalVariables.get_image_url + "you.jpg")  // 替换为实际的Spring Boot应用程序URL和图片文件名
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
                    ImageView imageView = findViewById(R.id.profileCircleImageView);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });

                } else {
                    // 处理请求失败的情况
                }
            }
        });
    }

    public void sendImage(){
        // get multiple images (no more than 9) from imagepicker
        Intent intent = new  Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");

        // 启动文件选择器
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            ClipData clipData = data.getClipData();
            if(clipData != null && clipData.getItemCount() > 0 && clipData.getItemCount() <= 9) {
                // get multiple images
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    String mediaType = MediaType.parse(getContentResolver().getType(imageUri)).toString();
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(imageUri);
                        File tempFile = saveInputStreamToFile(inputStream); // 自定义方法，将输入流保存为临时文件
                        inputStream.close();
                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("image", tempFile.getName(), RequestBody.create(MediaType.parse(mediaType), tempFile))
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
            } else if(clipData == null){
                // get single image
                Uri imageUri = data.getData();
                String mediaType = MediaType.parse(getContentResolver().getType(imageUri)).toString();
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                    File tempFile = saveInputStreamToFile(inputStream); // 自定义方法，将输入流保存为临时文件
                    inputStream.close();
                    // RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image", tempFile.getName(), RequestBody.create(MediaType.parse(mediaType), tempFile))
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
            } else {
                // 选中的图片数量不符合要求
                Toast.makeText(this, "请选择1到9张图片", Toast.LENGTH_SHORT).show();
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
                                Post post = (Post) parsedData.get("post");

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
