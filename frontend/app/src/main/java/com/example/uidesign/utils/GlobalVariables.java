package com.example.uidesign.utils;

public class GlobalVariables  {
    public static String host = "10.0.2.2";
    public static String login_url = "http://" + host + ":8080/user/login";
    public static String register_url = "http://" + host + ":8080/user/register";
    public static String update_user_info_url = "http://" + host + ":8080/user/update_username_and_description";
    public static String update_password_url = "http://" + host + ":8080/user/update_user_password";
    public static String follow_url = "http://" + host + ":8080/user/follow_or_unfollow";
    public static String get_image_url = "http://" + host + ":8080/api/images/";
    public static String get_user_posts_url = "http://" + host + ":8080/user/get_user_postlist";
    public static String get_posts_url = "http://" + host + ":8080/post/get_all";
    public static String send_comment_url = "http://" + host + ":8080/post/new_comment";
    public static String get_post_url = "http://" + host + ":8080/post/get_post";
    public static String test_image_url = "http://" + host + ":8080/api/test_image";
    public static String get_user_url = "http://" + host + ":8080/user/get_user_info";
    public static String add_post_url = "http://" + host + ":8080/post/new_post";
    public static String like_or_star_url = "http://" + host + ":8080/post/like_or_star";
    public static String change_avatar_url = "http://" + host + ":8080/user/update_user_avatar";
    public static String name2url(String  name) {
        return "http://" + host + ":8080/static/" + name;
    }
}