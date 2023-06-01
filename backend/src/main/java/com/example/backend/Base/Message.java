package com.example.backend.Base;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import java.util.Date;

@Document("message")
public class Message {
    @NonNull
     String title;//通知标题
    @NonNull
    String content;//通知详情
    @NonNull
    Date create_time;//创建时间

    String type;//通知类型 comment:自己发的帖子被回复  like:点赞通知 自己发的帖子被点赞 post:关注通知 关注的人发了帖子 chat:私信通知 他人私信自己

    String related_userid;//相关用户id 用于跳转


    public Message(String title,
                   String content,
                   Date create_time,
                   String type,
                   String related_userid
                   ) {
        this.content = content;
        this.title = title;
        this.create_time = create_time;
        this.type = type;
        this.related_userid = related_userid;
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

    public String getType() {
        return type;
    }

    public String getRelated_userid() {
        return related_userid;
    }


}
