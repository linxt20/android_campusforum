package com.example.uidesign.utils;

public class GlobalVariables  {
    public static String tagTexts[] = {"校园资讯", "二手交易", "美食分享", "活动通知", "失物招领", "学习交流", "娱乐", "校园招聘", "日常", "校园问答"};
    public static String host = "10.0.2.2";//;"172.20.10.3"
    public static String login_url = "http://" + host + ":8080/user/login";
    public static String register_url = "http://" + host + ":8080/user/register";
    public static String get_chat_url = "http://" + host + ":8080/user/get_chat";
    public static String send_chat_url = "http://" + host + ":8080/user/add_sentence";
    public static String get_notice_url = "http://" + host + ":8080/user/get_message";
    public static String get_block_list_url = "http://" + host + ":8080/user/get_block_list";
    public static String update_user_info_url = "http://" + host + ":8080/user/update_username_and_description";
    public static String update_password_url = "http://" + host + ":8080/user/update_user_password";
    public static String user_follow_url = "http://" + host + ":8080/user/get_follow_or_fans_list";
    public static String user_block_url = "http://" + host + ":8080/user/block_user";
    public static String user_unblock_url = "http://" + host + ":8080/user/unblock_user";
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