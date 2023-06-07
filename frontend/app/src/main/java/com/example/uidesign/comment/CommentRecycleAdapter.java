package com.example.uidesign.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uidesign.R;
import com.example.uidesign.utils.GlobalVariables;
import com.example.uidesign.utils.ImageDownloader;

public class CommentRecycleAdapter extends RecyclerView.Adapter<CommentItemViewHolder> {

    private final CommentItemList commentItemList;
    private final LayoutInflater inflater;

    public CommentRecycleAdapter(Context context, CommentItemList cItemList) {
        inflater = LayoutInflater.from(context);
        this.commentItemList = cItemList;
    }

    @NonNull
    @Override
    public CommentItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate an item view.
        View mItemView = inflater.inflate(
                R.layout.single_comment, parent, false);
        return new CommentItemViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentItemViewHolder holder, int position) {
        // Retrieve the data for that position.
        String content = commentItemList.get(position).getContent();
        String dateTime = commentItemList.get(position).getDateTime();
        String image = commentItemList.get(position).getImage();
        String username = commentItemList.get(position).getUsername();
        // Add the data to the view holder.
        // !!!!!!!!!!!!
        holder.contentView.setText(content); // 调用postList中第position的post的方法
        ImageDownloader imageDownloader = new ImageDownloader(holder.userheadView);
        imageDownloader.execute(GlobalVariables.name2url(image));
        holder.timeView.setText(dateTime);
        holder.usernameView.setText(username);
    }

    @Override
    public int getItemCount() {
        return commentItemList.size();
    }
}

class CommentItemViewHolder extends RecyclerView.ViewHolder{
    private static final String LOG_TAG = CommentItemViewHolder.class.getSimpleName();

    public final ImageView userheadView;
    public final TextView usernameView, contentView, timeView;
    public CommentItemViewHolder(@NonNull View itemView, CommentRecycleAdapter adapter) {
        super(itemView);
        AlphaAnimation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setDuration(500);
        itemView.startAnimation(fadeInAnimation);
        // Initialize the views.
        this.userheadView = itemView.findViewById(R.id.commentUserhead);
        this.usernameView = itemView.findViewById(R.id.commentUsername);
        this.timeView = itemView.findViewById(R.id.commentCreateAt);
        this.contentView = itemView.findViewById(R.id.commentContentText);
    }
}