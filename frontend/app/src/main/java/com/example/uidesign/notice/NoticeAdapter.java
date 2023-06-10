package com.example.uidesign.notice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uidesign.MessageActivity;
import com.example.uidesign.OthersProfileActivity;
import com.example.uidesign.R;
import com.example.uidesign.model.User;
import com.example.uidesign.utils.GlobalVariables;
import com.example.uidesign.utils.ImageDownloader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeViewHolder> {

    private final NoticeList noticeList;
    private Context mContext;
    private final LayoutInflater inflater;

    public NoticeAdapter(Context context, NoticeList cItemList) {
        inflater = LayoutInflater.from(context);
        this.noticeList = cItemList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate an item view.
        View mItemView = inflater.inflate(
                R.layout.single_notice, parent, false);
        return new NoticeViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        // Retrieve the data for that position.
        String content = noticeList.get(position).getContent();
        String dateTime = noticeList.get(position).getCreate_time();
        String title = noticeList.get(position).getTitle();
        String type = noticeList.get(position).getType();
        String userId = noticeList.get(position).getRelated_userid();

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("userid", userId)
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
                Log.d("NoticeAdapter", "responseText: " + responseText);
                // if(responseText == null) return;
                final User myResponse = new Gson().fromJson(responseText, new TypeToken<User>(){}.getType());

                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.usernameView.setText(myResponse.getUsername());
                        ImageDownloader headDownloader = new ImageDownloader(holder.userheadView);
                        headDownloader.execute(GlobalVariables.name2url(myResponse.getUser_head()));
                    }
                });
            }
        });

        // Add the data to the view holder.
        // !!!!!!!!!!!!
        holder.contentView.setText(content); // 调用postList中第position的post的方法
        holder.contentView.setVisibility(View.GONE);
        holder.titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.contentView.getVisibility() == View.VISIBLE) {
                    holder.contentView.setVisibility(View.GONE);
                }else{
                    holder.contentView.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.timeView.setText(dateTime);
        holder.titleView.setText(title);

        holder.userDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new  Intent(mContext, OthersProfileActivity.class);
                intent.putExtra("userID", userId);
                mContext.startActivity(intent);
            }
        });

        if(type.equals("chat")){
            holder.noticeDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new  Intent(mContext, MessageActivity.class);
                    intent.putExtra("otherUserID", userId);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }
}

class NoticeViewHolder extends RecyclerView.ViewHolder{
    private static final String LOG_TAG = NoticeViewHolder.class.getSimpleName();
    public LinearLayout userDetail, noticeDetail;
    public final ImageView userheadView;
    public final TextView usernameView, contentView, timeView, titleView;
    public NoticeViewHolder(@NonNull View itemView, NoticeAdapter adapter) {
        super(itemView);
        ScaleAnimation zoomInAnimation = new ScaleAnimation(0, 1, 0, 1);
        zoomInAnimation.setDuration(500);
        itemView.startAnimation(zoomInAnimation);
        // Initialize the views.
        this.userheadView = itemView.findViewById(R.id.noticeUserImage);
        this.usernameView = itemView.findViewById(R.id.noticeUsername);
        this.timeView = itemView.findViewById(R.id.notificationCreateTime);
        this.contentView = itemView.findViewById(R.id.notificationContent);
        this.titleView = itemView.findViewById(R.id.notification_title);
        this.userDetail = itemView.findViewById(R.id.noticeUserDetail);
        this.noticeDetail = itemView.findViewById(R.id.notificationDetail);
    }
}
