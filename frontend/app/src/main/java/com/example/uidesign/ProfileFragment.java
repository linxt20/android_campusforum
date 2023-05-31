package com.example.uidesign;

import static android.app.Activity.RESULT_OK;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uidesign.model.Post;
import com.example.uidesign.model.User;
import com.example.uidesign.profile.BoardItem;
import com.example.uidesign.profile.BoardItemList;
import com.example.uidesign.profile.ProfilePagerAdapter;
import com.example.uidesign.profile.RecycleAdapter;
import com.example.uidesign.utils.GlobalVariables;
import com.example.uidesign.utils.ImageDownloader;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileFragment extends Fragment {
    Button settings;
    Button message;
    TextView textUsername, textFollowersAndFollowings;
    ImageView imageUser;
    SharedPreferences sharedPreferences;
    String userID;
    View view;
    String imageName;

    private static final int REQUEST_CODE_SELECT_IMAGE = 233;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initView(){
        sharedPreferences = getActivity().getSharedPreferences("com.example.android.myapp", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", "");
        Log.d("ProfileFragment", "userID: " + userID);
        textUsername = (TextView)view.findViewById(R.id.text_user_name);
        textFollowersAndFollowings = (TextView)view.findViewById(R.id.text_followers_following_count);
        imageUser = (ImageView)view.findViewById(R.id.image_user);


        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("userid", userID)
                .build();
        Request request = new Request.Builder()
                .url(GlobalVariables.get_user_url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String responseText = response.body().string();
                Log.d("ProfileFragment", "responseText: " + responseText);
                // if(responseText == null) return;
                final User myResponse = new Gson().fromJson(responseText, new TypeToken<User>(){}.getType());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageName = myResponse.getUser_head();
                        textUsername.setText(myResponse.getUsername());
                        // TODO set followers and following count
                        textFollowersAndFollowings.setText(myResponse.getFans_list().size() + " followers • " + myResponse.getFans_list().size() + " following");
                        ImageDownloader headDownloader = new ImageDownloader(imageUser);
                        headDownloader.execute(GlobalVariables.name2url(myResponse.getUser_head()));
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initView();
        settings = view.findViewById(R.id.settingsButton);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Changed!!!!!
                Intent intent = new Intent(getContext(),SettingsActivity.class);
                intent.putExtra("imageName", imageName);
                intent.putExtra("username", textUsername.getText().toString());
                // TODO 传递个性签名
                startActivity(intent);
            }
        });
        message = view.findViewById(R.id.messageButton);
       message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Changed!!!!!
                Intent intent = new Intent(getContext(),TestActivity.class);
                startActivity(intent);
            }
        });
        // TODO 换头像
        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("userid", userID)
                        .build();
                Request request = new Request.Builder()
                        .url(GlobalVariables.change_avatar_url)
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("Image", "failed");
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText = response.body().string();
                        Log.d("Profile Image", responseText);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new  Intent(Intent.ACTION_GET_CONTENT);
                                imageName = responseText;
                                intent.setType("image/*");
                                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
                                Toast.makeText(getContext(), "头像已更换", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        TabLayout tabLayout = view.findViewById(R.id.tabsInProfile);
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerInProfile);

        List<String> tabTitles = Arrays.asList("我的帖子",  "收藏");
        ProfilePagerAdapter sectionsPagerAdapter = new ProfilePagerAdapter(getActivity(), tabTitles, userID);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabTitles.get(position)));
        tabLayoutMediator.attach();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OkHttpClient client = new OkHttpClient();
        Log.d("Profile Fragment", "entered a little!!");
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            String mediaType = MediaType.parse(getActivity().getContentResolver().getType(imageUri)).toString();
            InputStream inputStream = null;
            Log.d("Profile Fragment", "entered !!!!");
            Log.d("Profile Fragment", "got name: " + imageName);
            if(imageName == null) return;
            try {
                inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                File tempFile = saveInputStreamToFile(inputStream); // 自定义方法，将输入流保存为临时文件
                inputStream.close();
                // RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", tempFile.getName(), RequestBody.create(MediaType.parse(mediaType), tempFile))
                        .addFormDataPart("name", imageName)
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
}