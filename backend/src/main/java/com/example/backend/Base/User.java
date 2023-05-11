package com.example.backend.Base;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;
@Document("User")
public class User {
    // mongodb唯一id
    String id;
    // 用户ID
    String userID;
    // 密码
    String password;
    public User(String userID, String password) {
        this.userID = userID;
        this.password = password;
    }
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) { this.userID = userID; }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) { this.password = password; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
