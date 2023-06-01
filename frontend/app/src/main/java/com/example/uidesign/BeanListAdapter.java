package com.example.uidesign;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uidesign.comment.CommentItemList;
import com.example.uidesign.comment.CommentRecycleAdapter;
import com.example.uidesign.model.Comment;
import com.example.uidesign.utils.GlobalVariables;
import com.example.uidesign.utils.ImageDownloader;

import java.io.IOException;
import java.text.SimpleDateFormat;

import okhttp3.*;

public class BeanListAdapter extends RecyclerView.Adapter<BeanListAdapter.BeanViewHolder> {
    private final BeanList BeanList;
    CommentRecycleAdapter adapter;
    private final LayoutInflater inflater;
    private Context mContext;
    public BeanListAdapter(Context context, BeanList BeanList) {
        inflater = LayoutInflater.from(context);
        this.BeanList = BeanList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public BeanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate an item view.
        View mItemView = inflater.inflate(
                R.layout.single_item, parent, false);
        return new BeanViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BeanViewHolder holder, int position) {
        // Retrieve the data for that position.
        Bean current = BeanList.get(position);
        String Username = current.getUsername();
        String createAt = current.getcreateAt();
        String title = current.gettitle();
        String Content = current.getContent();
        int comment_count = current.getcomment_count();
        int like_count = current.getlike_count();
        int star_count = current.getstar_count();
        String user_head = current.getuser_head();
        String[] imagelist = current.getimagelist();
        // Add the data to the view holder.
        holder.UsernameView.setText(Username);
        holder.createTime.setText(createAt);
        holder.Titletext.setText(title);
        holder.Contenttext.setText(Content);
        String markdownText = "This is **bold** text.\n\n" +
                "![Image](https://example.com/image.jpg)";

//        final Markwon markwon = Markwon.builder(this)
//                .build();
//
//        markwon.setMarkdown(holder.Contenttext, markdownText);
        holder.commenttext.setText(String.valueOf(comment_count));
        holder.liketext.setText(String.valueOf(like_count));
        holder.startext.setText(String.valueOf(star_count));
        holder.tagView.setText(current.gettag());
        Comment[] comments = current.getcomment_list();
        Log.d("BeanListAdapter",  "comment count: " + current.getcomment_count());
        CommentItemList CommentList = new CommentItemList();
        // Date to String
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Comment comment: comments) {
            // TODO 这里通过id从后端获得user_head和username
            CommentList.insert(comment.getAuthor_head(),comment.getAuthor_name(), formatter.format(comment.getCreate_time()), comment.getContent());
        }
        adapter = new CommentRecycleAdapter(mContext, CommentList);
        holder.recyclerView.setAdapter(adapter);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        ImageDownloader headDownloader = new ImageDownloader(holder.image_user);
        headDownloader.execute(GlobalVariables.name2url(user_head));
        if(current.getType().equals("jpg")){
            holder.videoView.setVisibility(View.GONE);
            // 这里是六个图片的显示逻辑
            int i = 0;
            for(i = 0; i < imagelist.length; i++){
                try {
                    ImageDownloader imageDownloader = new ImageDownloader(holder.imageshow[i]);
                    imageDownloader.execute(GlobalVariables.name2url(imagelist[i]));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if(i == 0){
                while(i<6){
                    holder.imageshow[i].setVisibility(View.GONE);
                    i++;
                }
            }
            while (i<3){
                holder.imageshow[i].setVisibility(View.INVISIBLE);
                i++;
            }
            while(i<6){
                holder.imageshow[i].setVisibility(View.GONE);
                i++;
            }
        }
        else if(current.getType().equals("mp4")){
            holder.gridLayout.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.VISIBLE);
            //holder.videoView.setVideoPath(GlobalVariables.name2url(imagelist[0]));
            Uri uri = Uri.parse(GlobalVariables.name2url(imagelist[0]));
            holder.videoView.setVideoURI(uri);
            holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d("BeanListAdapter", "mp4: " + GlobalVariables.name2url(imagelist[0]));
                    holder.videoView.start();
                }
            });

        }
        if(current.getIf_like() == 0){
            holder.likeimage.setImageResource(R.drawable.baseline_favorite_border_24);
        }
        else {
            holder.likeimage.setImageResource(R.drawable.love);
        }
        if(current.getIf_star() == 0){
            holder.starimage.setImageResource(R.drawable.baseline_star_border_24);
        }
        else {
            holder.starimage.setImageResource(R.drawable.collect_red);
        }
        SharedPreferences prefs = ((Activity)mContext).getSharedPreferences("com.example.android.myapp", MODE_PRIVATE);
        holder.image_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 如果点击的是自己的头像，则不做任何处理 TODO：或者进入个人主页？
                if(current.getUserid().equals(prefs.getString("userID", "")))
                    return;
                Intent intent = new Intent(mContext, OthersProfileActivity.class);
                intent.putExtra("userID", current.getUserid());
                mContext.startActivity(intent);
            }
        });
        // 这是点赞和收藏按钮的点击函数，这里需要等待后端的api写好，然后传递信息
        holder.likearea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 调用后端
                RequestBody body = new FormBody.Builder()
                        .add("postid", current.getPostid())
                        .add("userid", prefs.getString("userID", ""))
                        .add("state", "0")  // 点赞
                        .add("cancel", current.getIf_like() + "")
                        .build();
                Request request = new Request.Builder()
                        .url(GlobalVariables.like_or_star_url)
                        .post(body)
                        .build();
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.d("BeanListAdapter", "点赞失败");
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.d("BeanListAdapter， reponse: ", responseData);
                        if(current.getIf_like() == 0){
                            current.setIf_like(1);
                        }
                        else {
                            current.setIf_like(0);
                        }
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 更新UI的代码放在这里
                                holder.setLikeView(current, responseData);
                            }
                        });
                        Log.d("BeanListAdapter", "点赞成功");
                    }
                });
            }
        });
        holder.stararea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 调用后端
                RequestBody body = new FormBody.Builder()
                        .add("postid", current.getPostid())
                        .add("userid", prefs.getString("userID", ""))
                        .add("state", "1")  // 收藏
                        .add("cancel", current.getIf_star() + "")
                        .build();
                Request request = new Request.Builder()
                        .url(GlobalVariables.like_or_star_url)
                        .post(body)
                        .build();
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.d("BeanListAdapter", "点赞失败");
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.d("BeanListAdapter， reponse: ", responseData);
                        if(current.getIf_star() == 0){
                            current.setIf_star(1);
                        }
                        else {
                            current.setIf_star(0);
                        }
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 更新UI的代码放在这里
                                holder.setStarView(current, responseData);
                            }
                        });
                        Log.d("BeanListAdapter", "点赞成功");
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return BeanList == null? 0 :BeanList.size();
    }

    public BeanList getBeanList() {
        return BeanList;
    }

    class BeanViewHolder extends RecyclerView.ViewHolder{
        public final TextView UsernameView;
        public final TextView createTime;

        public final TextView Titletext;
        public final TextView Contenttext;
        public final TextView commenttext;
        public final TextView liketext;
        public final TextView startext;
        public final ImageView image_user;
        public final ImageView[] imageshow = new ImageView[6];
        public final LinearLayout getdetailarea;
        public final LinearLayout likearea;
        public final LinearLayout stararea;

        public final ImageView likeimage;
        public final ImageView starimage;
        public final TextView tagView;
        public RecyclerView recyclerView;
        public final VideoView videoView;
        public final GridLayout gridLayout;

        public void setLikeView(Bean current, String responseData){
            liketext.setText(responseData);
            if(current.getIf_like() == 0){
                likeimage.setImageResource(R.drawable.baseline_favorite_border_24);
            }
            else {
                likeimage.setImageResource(R.drawable.love);
            }
        }

        public void setStarView(Bean current, String responseData){
            startext.setText(responseData);
            if(current.getIf_star() == 0){
                starimage.setImageResource(R.drawable.baseline_star_border_24);
            }
            else {
                starimage.setImageResource(R.drawable.collect_red);
            }
        }

        public BeanViewHolder(@NonNull View itemView) {
            super(itemView);
            UsernameView = itemView.findViewById(R.id.username);
            createTime = itemView.findViewById(R.id.createat);
            Titletext = itemView.findViewById(R.id.titletext);
            Contenttext = itemView.findViewById(R.id.contenttext);
            commenttext = itemView.findViewById(R.id.comment);
            liketext = itemView.findViewById(R.id.liketext);
            startext = itemView.findViewById(R.id.startext);
            image_user = itemView.findViewById(R.id.userhead);
            getdetailarea = itemView.findViewById(R.id.getdetailarea);
            likearea = itemView.findViewById(R.id.like);
            stararea = itemView.findViewById(R.id.star);
            likeimage = itemView.findViewById(R.id.likeimage);
            starimage = itemView.findViewById(R.id.starimage);
            tagView = itemView.findViewById(R.id.Tagshow);
            recyclerView = itemView.findViewById(R.id.commentlist);
            videoView = itemView.findViewById(R.id.videoView);
            gridLayout = itemView.findViewById(R.id.grid);

            int[] imageViewIds = {R.id.imageView1, R.id.imageView2, R.id.imageView3, R.id.imageView4, R.id.imageView5,R.id.imageView6};

            for (int i = 0; i < 6; i++) {
                imageshow[i] = itemView.findViewById(imageViewIds[i]);
            }

            getdetailarea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnItemClickListener!=null){
                        mOnItemClickListener.onRecyclerItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public OnRecyclerItemClickListener mOnItemClickListener;

    public void setRecyclerItemClickListener(OnRecyclerItemClickListener listener){
        mOnItemClickListener = listener;
    }

    public interface OnRecyclerItemClickListener{
        void onRecyclerItemClick(int position);
    }


}