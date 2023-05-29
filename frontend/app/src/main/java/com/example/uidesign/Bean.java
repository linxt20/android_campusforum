package com.example.uidesign;

import android.net.Uri;

import com.example.uidesign.model.Comment;

import java.util.List;

public class Bean {
    private String Username = "";
    private String createAt = "";
    private String tag = "";
    private String title = "";
    private String content = "";

    private int comment_count = 0;
    private Comment[] comment_list;
    private int like_count = 0;
    private int if_like = 0;
    private int star_count = 0;
    private int if_star = 0;
    private String user_head;

    private String[] imagelist = new String[6];

    public Bean( String Username,
                 String createAt,
                 String tag,
                 String title,
                 String content,
                 int comment_count,
                 int like_count,
                 int if_like,
                 int star_count,
                 int if_star,
                 String user_head,
                 String[] imagelist,
                 Comment[] commentList) {
        this.Username = Username;
        this.createAt = createAt;
        this.tag = tag;
        this.title = title;
        this.content = content;
        this.comment_count = comment_count;
        this.like_count = like_count;
        this.if_like = if_like;
        this.star_count = star_count;
        this.if_star = if_star;
        this.user_head = user_head;
        this.imagelist = imagelist;
        this.comment_list = commentList;

    }
    public String getUsername() {
        return Username;
    }
    public String getcreateAt() {return createAt;}
    public String gettag(){return tag;}
    public String gettitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public int getcomment_count() {
        return comment_count;
    }
    public int getIf_like(){ return if_like;}
    public int getlike_count() {
        return like_count;
    }
    public int getIf_star(){ return if_star;}
    public int getstar_count() {
        return star_count;
    }

    public String getuser_head() { return user_head;}

    public String[] getimagelist() { return imagelist; }

    public Comment[] getcomment_list() { return comment_list;}
}
