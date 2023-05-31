package com.example.backend.Base;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import java.util.Date;

@Document("message")
public class Message {
    @NonNull
    final String title;//通知标题
    @NonNull
    final String content;//通知详情
    @NonNull
    final Date create_time;//创建时间
    public Message(String title,String content, Date create_time) {
        this.content = content;
        this.title = title;
        this.create_time = create_time;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    @NonNull
    public Date getCreate_time() {
        return create_time;
    }

    public String getTitle() {
        return title;
    }


}
