package com.example.backend.Base;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

public class Chat {

    @Id
    String chat_id;//聊天id 唯一标识
    String user1_id;
    String user1_name;
    String user1_head;
    String user2_id;//两个用户的id
    String user2_name;//两个用户的名字
    String user2_head;//两个用户的头像
    List<Sentence> sentence_list;//聊天记录

    Date last_time;//最后一条消息的时间

    public Chat(String user1_id,
                String user1_name,
                String user1_head,
                String user2_id,
                String user2_name,
                String user2_head,
                List<Sentence> sentence_list,
                Date last_time){
        this.user1_id=user1_id;
        this.user1_name=user1_name;
        this.user1_head=user1_head;
        this.user2_id=user2_id;
        this.user2_name=user2_name;
        this.user2_head=user2_head;
        this.sentence_list=sentence_list;
        this.last_time=last_time;
        //利用objectid设置chat_id
        ObjectId tmp_id = new ObjectId();
        this.chat_id=tmp_id.toString();
    }

    public String getChat_id() {
        return chat_id;
    }

    public String getUser1_id() {
        return user1_id;
    }

    public String getUser1_name() {
        return user1_name;
    }

    public String getUser1_head() {
        return user1_head;
    }

    public String getUser2_id() {
        return user2_id;
    }

    public String getUser2_name() {
        return user2_name;
    }

    public String getUser2_head() {
        return user2_head;
    }

    public List<Sentence> getSentence_list() {
        return sentence_list;
    }

    public Date getLast_time() {
        return last_time;
    }

    public void addSentence(Sentence sentence){
        sentence_list.add(sentence);
        this.last_time=sentence.getCreate_time();
    }



}
