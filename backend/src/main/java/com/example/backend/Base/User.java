package com.example.backend.Base;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;
@Document("User")
public class User {
    // mongodb唯一id
    @Id
    String id;
    //用户名
    String username;
    // 密码
    String password;
    String description;//简介
    String user_head;
    String user_email;
    String star_post_list;//收藏的动态列表

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        ObjectId tmp_id = new ObjectId();
        this.id=tmp_id.toString();
    }
    public String get_username() {
        return  username;
    }
    //public void setusername(String userID) { this.userID = userID; }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) { this.password = password; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
