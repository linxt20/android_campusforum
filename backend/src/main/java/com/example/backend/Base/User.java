package com.example.backend.Base;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;
@Document("User")
public class User {
    // mongodb唯一id
    @Id
    String userid;
    //用户名
    String username;
    // 密码
    String password;
    String description;//简介
    String user_head;
    String star_post_list;//收藏的动态列表

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        // TODO user_head 的默认地址
        this.user_head = "jyjjyyds.jpg";
        ObjectId tmp_id = new ObjectId();
        this.userid=tmp_id.toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUser_head(String user_head) {
        this.user_head = user_head;
    }

    public String getUser_head() {
        return user_head;
    }

    //public void setusername(String userID) { this.userID = userID; }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) { this.password = password; }
    public String getId() { return userid; }

}
