package com.example.backend.Base;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document("Post")
public class Post {
    //每一条动态
    String id;//唯一的标签 怎么生成还要再考虑 mongodb的唯一标签？
    String author_name;//作者名字 前端接口为Username
    String create_time;//创建时间 前端接口为createAt
    String title;//标题
    String content;//内容  !!支持md，可能需要修改
    int comment_count;//评论量
    int like_count;//点赞量
    String[] like_userid_list;//点赞用户id列表
    int star_count;//收藏量
    String[] star_userid_list;//收藏用户id列表
    String author_head;//作者头像
    //String[] image_list;//图片列表
    List<ClassPathResource> image_list;//图片列表

    String[] tag_list;//标签列表 !!目前还没加

    public Post(String author_name,String create_time,String title,String content,String author_head){
        this.author_name=author_name;
        this.create_time=create_time;
        this.title=title;
        this.content=content;
        this.author_head=author_head;

        this.like_count=0;
        this.comment_count=0;
        this.star_count=0;
    }

    public String getId() {
        return id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public int getLike_count() {
        return like_count;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getCreate_time() {
        return create_time;
    }

    public int getStar_count() {
        return star_count;
    }

    public String getAuthor_head() {
        return author_head;
    }

//    public String[] getImage_list() {
//        //return image_list;
//    }

    public String[] getTag_list() {
        return tag_list;
    }

    public int getComment_count() {
        return comment_count;
    }

    public String[] getLike_userid_list() {
        return like_userid_list;
    }



}
