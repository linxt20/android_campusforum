package com.example.uidesign;

import android.net.Uri;

public class Bean {
    private String Username = "";
    private String createAt = "";
    private String title = "";
    private String content = "";

    private int comment_count = 0;
    private int like_count = 0;
    private int star_count = 0;

    private String user_head;

    private String[] imagelist = new String[6];

    public Bean( String Username,
                 String createAt,
                 String title,
                 String content,
                 int comment_count,
                 int like_count,
                 int star_count,
                 String user_head,
                 String[] imagelist) {
        this.Username = Username;
        this.createAt = createAt;
        this.title = title;
        this.content = content;
        this.comment_count = comment_count;
        this.like_count = like_count;
        this.star_count = star_count;
        this.user_head = user_head;
        this.imagelist = imagelist;
    }
    public String getUsername() {
        return Username;
    }
    public String getcreateAt() {return createAt;}
    public String gettitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public int getcomment_count() {
        return comment_count;
    }
    public int getlike_count() {
        return like_count;
    }
    public int getstar_count() {
        return star_count;
    }

    public String getuser_head() { return user_head;}

    public String[] getimagelist() { return imagelist; }
}
