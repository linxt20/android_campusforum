package com.example.uidesign.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uidesign.DetailActivity;
import com.example.uidesign.GlobalVariables;
import com.example.uidesign.ImageDownloader;
import com.example.uidesign.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecycleAdapter extends RecyclerView.Adapter<BoardItemViewHolder> {

    private final BoardItemList boardItemList;
    private final LayoutInflater inflater;

    public RecycleAdapter(Context context, BoardItemList bItemList) {
        inflater = LayoutInflater.from(context);
        this.boardItemList = bItemList;
    }

    @NonNull
    @Override
    public BoardItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate an item view.
        View mItemView = inflater.inflate(
                R.layout.list_item_board, parent, false);
        return new BoardItemViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardItemViewHolder holder, int position) {
        // Retrieve the data for that position.
        String title = boardItemList.get(position).getTitle();
        String dateTime = boardItemList.get(position).getDateTime();
        String image = boardItemList.get(position).getImage();
        // Add the data to the view holder.
        // !!!!!!!!!!!!
        holder.titleView.setText(title); // 调用postList中第position的post的方法
        // TODO image resource 怎么设置
        //Context context = holder.imgView.getContext(); // 真的很怪
        //int id = context.getResources().getIdentifier(image, "drawable", context.getPackageName());
        //Log.d("RecycleAdapter", "avatar: " + id);
        //holder.imgView.setImageResource(id);

        ImageDownloader imageDownloader = new ImageDownloader(holder.imgView);
        imageDownloader.execute(image);
        holder.timeView.setText(dateTime);
    }

    @Override
    public int getItemCount() {
        return boardItemList.size();
    }
}

class BoardItemViewHolder extends RecyclerView.ViewHolder{
    private static final String LOG_TAG = BoardItemViewHolder.class.getSimpleName();

    public final ImageView imgView;
    public final TextView titleView, timeView;
    public final ConstraintLayout item;
    public BoardItemViewHolder(@NonNull View itemView, RecycleAdapter adapter) {
        super(itemView);
        // Initialize the views.
        this.imgView = itemView.findViewById(R.id.image_content);
        this.titleView = itemView.findViewById(R.id.text_title);
        this.timeView = itemView.findViewById(R.id.text_date_time);
        this.item =itemView.findViewById(R.id.clickable_post_item);

        this.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到Details页面
                int position = getAdapterPosition(); // 获取点击的项目的索引
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                String title = titleView.getText().toString();
                String time = timeView.getText().toString();
                intent.putExtra("title", title);
                intent.putExtra("text", "This is a text");
                intent.putExtra("Username", "aaa");
                intent.putExtra("Content", "This is a content");
                intent.putExtra("createAt", time);
                intent.putExtra("user_head", "https://codegeex.cn");
                intent.putExtra("imageList", "CodeGeeX");
                }
            });
    }
}