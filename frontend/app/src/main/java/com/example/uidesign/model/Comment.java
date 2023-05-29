package com.example.uidesign.model;

import java.util.Date;

public class Comment {
    String author_id;//评论作者的id
    String author_name;//评论作者的名字
    String author_head;//评论作者的头像
    Date create_time;//“yyyy-MM-dd HH:mm:ss” 评论创建时间
    String content;//评论内容

    public Comment(String author_id,
                   Date create_time,
                   String content,
                   String author_name,
                   String author_head){
        this.author_id=author_id;
        this.author_head=author_head;
        this.author_name=author_name;
        this.create_time=create_time;
        this.content=content;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public String getAuthor_head() {
        return author_head;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public String getContent() {
        return content;
    }

    public void setAuthor_head(String author_head) {
        this.author_head = author_head;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }
}
