package com.example.uidesign.utils;

public class GlobalVariables  {
    public static String host = "10.0.2.2";
    public static String login_url = "http://" + host + ":8080/user/login";
    public static String register_url = "http://" + host + ":8080·/user/register";
    public static String get_image_url = "http://" + host + ":8080/api/images/";

    public static String get_posts_url = "http://" + host + ":8080/post/get_all";
    public static String test_image_url = "http://" + host + ":8080/api/test_image";
    public static String get_user_url = "http://" + host + ":8080/user/get_user_info";
    public static String name2url(String  name) {
        return "http://" + host + ":8080/static/" + name;
    }
}