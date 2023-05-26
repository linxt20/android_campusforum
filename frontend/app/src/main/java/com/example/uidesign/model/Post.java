package com.example.uidesign.model;

public class Post {
    //每一条动态
    String postid;//唯一的标签 怎么生成还要再考虑 mongodb的唯一标签？
    String author_id;//作者id
    String author_name;//作者名字
    String create_time;//创建时间 前端接口为createAt
    String title;//标题
    String content;//内容 !!支持md，可能需要修改
    int comment_count;//评论量
    int like_count;//点赞量
    String[] like_userid_list;//点赞用户id列表
    int star_count;//收藏量
    String[] star_userid_list;//收藏用户id列表
    String author_head;//作者头像
    String[] resource_list;//资源列表 记录其在static/images中的名称

    String tag;//标签仅一个
}
