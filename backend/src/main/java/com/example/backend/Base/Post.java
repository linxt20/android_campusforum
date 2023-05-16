package com.example.backend.Base;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
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
    int star_count;//收藏量
    String author_head;//作者头像
    String[] image_list;//图片列表
    String[] tag_list;//标签列表 !!目前还没加

    public Post(String author_name,String create_time,String title,String content,String author_head,String[] image_list){
        this.author_name=author_name;
        this.create_time=create_time;
        this.title=title;
        this.content=content;
        this.author_head=author_head;
        this.image_list=image_list;
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

    public String[] getImage_list() {
        return image_list;
    }

    public String[] getTag_list() {
        return tag_list;
    }

    public int getComment_count() {
        return comment_count;
    }

    public Map getBaseInfo(){
        Map rv= new HashMap();
        //键值和前端对应
        rv.put("Username",this.author_name);
        rv.put("createAt",this.author_name);
        rv.put("title",this.author_name);
        rv.put("content",this.author_name);
        rv.put("comment_count",this.author_name);
        rv.put("like_count",this.like_count);
        rv.put("star_count",this.star_count);
        rv.put("comment_count",this.author_name);
        rv.put("comment_count",this.author_name);
        return rv;

    }

}
