package com.example.uidesign;

public class GlobalVariables  {
    public static String host = "10.0.2.2";
    public static String login_url = "http://" + host + ":8080/login";
    public static String register_url = "http://" + host + ":8080/register";
    public static String get_image_url = "http://" + host + ":8080/api/images/";

    public static String get_posts_url = "http://" + host + ":8080/post/get_all";
    public static String test_image_url = "http://" + host + ":8080/api/test_image";
    public static String name2url(String  name) {
        return "http://" + host + ":8080/static/" + name;
    }
}