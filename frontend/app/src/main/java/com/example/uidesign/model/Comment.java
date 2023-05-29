package com.example.uidesign.model;

public class Comment {
    String comment_id;
    String author_id;//评论作者的id
    String author_name;//评论作者的名字
    String author_head;//评论作者的头像
    String create_time;//评论创建时间
    String content;//评论内容

    Comment(String author_id,String create_time,String content){
        this.author_id=author_id;
        this.create_time=create_time;
        this.content=content;
    }

    public String getAuthor_id() {
        return author_id;
    }
    public String getContent() {
        return content;
    }
    public String getCreate_time() {
        return create_time;
    }
}

