package com.example.uidesign.model;


import android.util.Log;

import java.util.List;

public class User {
    // mongodb唯一id
    String id;
    //用户名
    String username;
    // 密码
    String password;
    String description;//简介
    String user_head;
    List<String> star_post_list;//收藏的动态列表 记录post的id
    List<String> like_post_list;//点赞的动态列表 记录post的id
    List<String> my_post_list;//发表的动态列表 记录post的id
    List<String> comment_post_list;//评论过的动态列表 记录post的id
    List<String> follow_list;//关注的用户列表 记录userid
    List<String> fans_list;//粉丝列表 记录userid


    public User(String username,
                String password,
                String description,
                String user_head,
                List<String> star_post_list,
                List<String> like_post_list,
                List<String> my_post_list,
                List<String> follow_list,
                List<String> fans_list,
                List<String> comment_post_list,
                String id
    ) {
        this.username = username;
        this.password = password;
        this.user_head = user_head;
        this.description = description;
        this.star_post_list = star_post_list;
        this.like_post_list = like_post_list;
        this.my_post_list = my_post_list;
        this.follow_list = follow_list;
        this.fans_list = fans_list;
        this.comment_post_list = comment_post_list;
        this.id = id;
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
    public String getId() {
        Log.d("User.java", "getId: " + id);
        return id; }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getMy_post_list() {
        return my_post_list;
    }

    public List<String> getStar_post_list() {
        return star_post_list;
    }
    public void addMyPost(String postid){
        my_post_list.add(postid);
    }

    public void addStarPost(String postid){
        star_post_list.add(postid);
    }

    public void cancelStarPost(String postid){
        star_post_list.remove(postid);
    }

    public List<String> getLike_post_list() {
        return like_post_list;
    }

    public void addLikePost(String postid){
        like_post_list.add(postid);
    }

    public void cancelLikePost(String postid){
        like_post_list.remove(postid);
    }

    public void addFollow(String userid){
        follow_list.add(userid);
    }

    public void cancelFollow(String userid){
        follow_list.remove(userid);
    }

    public void addFans(String userid){
        fans_list.add(userid);
    }

    public void cancelFans(String userid){
        fans_list.remove(userid);
    }

    public List<String> getFollow_list() {
        return follow_list;
    }

    public List<String> getFans_list() {
        return fans_list;
    }

    public String getDescription() {
        return description;
    }

    public void toString(User user){
        System.out.println("username:"+user.getUsername());
        System.out.println("password:"+user.getPassword());
        System.out.println("description:"+user.getDescription());
        System.out.println("user_head:"+user.getUser_head());
        System.out.println("star_post_list:"+user.getStar_post_list());
        System.out.println("like_post_list:"+user.getLike_post_list());
        System.out.println("my_post_list:"+user.getMy_post_list());
        System.out.println("follow_list:"+user.getFollow_list());
        System.out.println("fans_list:"+user.getFans_list());
    }

    public void addCommentPost(String postid){
        comment_post_list.add(postid);
    }

    public List<String> getComment_post_list() {
        return comment_post_list;
    }
}
