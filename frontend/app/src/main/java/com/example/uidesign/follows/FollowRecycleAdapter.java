package com.example.uidesign.follows;


import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uidesign.OthersProfileActivity;
import com.example.uidesign.R;
import com.example.uidesign.utils.GlobalVariables;
import com.example.uidesign.utils.ImageDownloader;

public class FollowRecycleAdapter extends RecyclerView.Adapter<com.example.uidesign.follows.FollowItemViewHolder> {

    private final FollowItemList followItemList;
    private final LayoutInflater inflater;
    private Context mContext;
    private Boolean isBlock;

    public FollowRecycleAdapter(Context context, FollowItemList cItemList, Boolean isBlock) {
        inflater = LayoutInflater.from(context);
        this.followItemList = cItemList;
        this.mContext = context;
        this.isBlock = isBlock;
    }

    @NonNull
    @Override
    public com.example.uidesign.follows.FollowItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate an item view.
        View mItemView = inflater.inflate(
                R.layout.single_follower, parent, false);
        return new com.example.uidesign.follows.FollowItemViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowItemViewHolder holder, int position) {
        // Retrieve the data for that position.
        String content = followItemList.get(position).getContent();
        String image = followItemList.get(position).getImage();
        String username = followItemList.get(position).getUsername();
        // Add the data to the view holder.
        // !!!!!!!!!!!!
        holder.contentView.setText(content); // 调用postList中第position的post的方法
        ImageDownloader imageDownloader = new ImageDownloader(holder.userheadView);
        imageDownloader.execute(GlobalVariables.name2url(image));
        holder.usernameView.setText(username);
        SharedPreferences prefs = ((Activity)mContext).getSharedPreferences("com.example.android.myapp", MODE_PRIVATE);

        if(!isBlock){
            // 只有关注列表能点开具体的人的profile
            holder.userheadView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 如果点击的是自己的头像，则不做任何处理 TODO：或者进入个人主页？
                    Log.d("followItemList: ", followItemList.get(position).getUsername());
                    if(followItemList.get(position).getUserid().equals(prefs.getString("userID", "")))
                        return;
                    Intent intent = new Intent(mContext, OthersProfileActivity.class);
                    intent.putExtra("userID", followItemList.get(position).getUserid());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return followItemList.size();
    }
}


class FollowItemViewHolder extends RecyclerView.ViewHolder{
    private static final String LOG_TAG = com.example.uidesign.follows.FollowItemViewHolder.class.getSimpleName();

    public final ImageView userheadView;
    public final TextView usernameView, contentView;

    public FollowItemViewHolder(@NonNull View itemView, com.example.uidesign.follows.FollowRecycleAdapter adapter) {
        super(itemView);
        ScaleAnimation zoomInAnimation = new ScaleAnimation(0, 1, 0, 1);
        zoomInAnimation.setDuration(500);
        itemView.startAnimation(zoomInAnimation);
        // Initialize the views.
        this.userheadView = itemView.findViewById(R.id.commentUserhead);
        this.usernameView = itemView.findViewById(R.id.commentUsername);
        this.contentView = itemView.findViewById(R.id.lastMessageText);
    }
}
