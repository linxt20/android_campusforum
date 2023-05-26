package com.example.uidesign.model;

import java.util.ArrayList;
import java.util.List;

public class Post {
    //每一条动态
    String postid;//唯一的标签 怎么生成还要再考虑 mongodb的唯一标签？
    String author_id;//作者id
    String author_name;//作者名字
    String author_head;//作者头像
    String create_time;//创建时间 前端接口为createAt
    String title;//标题
    String content;//内容  !!支持md，可能需要修改
    int comment_count;//评论量
    int like_count;//点赞量
    List<String> like_userid_list;//点赞用户id列表
    int star_count;//收藏量
    List<String> star_userid_list;//收藏用户id列表

    int if_like;//对于目前浏览的用户，是否点赞 0没有，1有
    int if_star;//对于目前浏览的用户，是否收藏 0没有，1有

    int resource_num;//图片或视频的数量

    String resource_type;//资源类型 图片为jpg，视频为mp4
    String[] resource_list;//资源列表 记录其在static/images中的名称

    String tag;//标签仅一个

    public Post(String postid,
                String author_id,
                String create_time,
                String title,
                String content,
                String tag,
                int resource_num,
                String resource_type,
                int like_count,
                int star_count,
                List<String> like_userid_list,
                List<String> star_userid_list,
                int comment_count,
                String[] resource_list){

        System.out.println("Start new1 post");
        this.author_id=author_id;
        this.create_time=create_time;
        this.title=title;
        this.content=content;
        this.resource_num=resource_num;
        this.resource_type=resource_type;
        this.tag=tag;
        this.postid=postid;
        this.resource_list=resource_list;
        //新建时，自动置为0
        this.like_count=like_count;
        this.comment_count=comment_count;
        this.star_count=star_count;
        this.if_like=0;
        this.if_star=0;
        this.like_userid_list=like_userid_list;
        this.star_userid_list=star_userid_list;
    }



    public String getPostid(){
        return postid;
    }

    public String[] getResource_list() {
        return resource_list;
    }

    public String getId() {
        return postid;
    }

    public String getAuthor_id() {
        return author_id;
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

    public String getTag() {
        return tag;
    }

    public int getComment_count() {
        return comment_count;
    }

    public List<String> getLike_userid_list() {
        return like_userid_list;
    }

    public List<String> getStar_userid_list() {
        return star_userid_list;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public void setAuthor_head(String author_head) {
        this.author_head = author_head;
    }

    public void setIf_like(int if_like) {
        this.if_like = if_like;
    }
    public void setIf_star(int if_star) {
        this.if_star = if_star;
    }

    public void add_like(String userid){
        this.like_count=this.like_count+1;
        if(this.like_userid_list==null){
            this.like_userid_list=new ArrayList<String>();
        }
        this.like_userid_list.add(userid);
    }

    public void add_star(String userid){
        this.star_count=this.star_count+1;
        if(this.star_userid_list==null){
            this.star_userid_list=new ArrayList<String>();
        }
        this.star_userid_list.add(userid);
    }

    public void cancel_like(String userid){
        this.like_count--;
        this.like_userid_list.remove(userid);
    }

    public void cancel_star(String userid){
        this.star_count--;
        this.star_userid_list.remove(userid);
    }

}

