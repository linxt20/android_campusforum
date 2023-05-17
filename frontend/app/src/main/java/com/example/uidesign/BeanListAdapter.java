package com.example.uidesign;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;

public class BeanListAdapter extends RecyclerView.Adapter<BeanListAdapter.BeanViewHolder> {
    private final BeanList BeanList;
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
        holder.commenttext.setText(String.valueOf(comment_count));
        holder.liketext.setText(String.valueOf(like_count));
        holder.startext.setText(String.valueOf(star_count));
        try {
            holder.image_user.setBackground(new BitmapDrawable(mContext.getResources(), BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(Uri.parse(user_head)))));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        // 这里是六个图片的显示逻辑
        int i = 0;
        for(i = 0;i<imagelist.length;i++){
            try {
                holder.imageshow[i].setBackground(new BitmapDrawable(mContext.getResources(), BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(Uri.parse(imagelist[i])))));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
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
        // 这是点赞和收藏按钮的点击函数，这里需要等待后端的api写好，然后传递信息
        holder.likearea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(current.getIf_like() == 0){
                    holder.likeimage.setImageResource(R.drawable.love);
                }
                else {
                    holder.likeimage.setImageResource(R.drawable.baseline_favorite_border_24);
                }
            }
        });
        holder.stararea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(current.getIf_star() == 0){
                    holder.starimage.setImageResource(R.drawable.collect_red);
                }
                else {
                    holder.starimage.setImageResource(R.drawable.baseline_star_border_24);
                }
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

            int[] imageViewIds = {R.id.imageView1, R.id.imageView2, R.id.imageView3, R.id.imageView4, R.id.imageView5,R.id.imageView6};

            for (int i = 0; i < 6; i++) {
                imageshow[i] = itemView.findViewById(imageViewIds[i]);
                imageshow[i].setBackground(null);
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