package com.example.backend.Controller;

import com.example.backend.Base.Comment;
import com.example.backend.Base.Post;
import com.example.backend.Base.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/post")
public class PostListController {
    @Autowired
    MongoTemplate mongoTemplate;


    @PostMapping("/get_all")
    public ResponseEntity<List<Post>> post_get(@RequestParam String userid) {
        //之后会增加search key，返回固定的帖子
        /*
        * 功能：返回所有帖子
        * 输入： userid 用户id
        * 输出： List<Post> 所有帖子
        * */

        try{
            //获取帖子
            System.out.println("Start get all post");
            List<Post> rv=mongoTemplate.findAll(Post.class);
            System.out.println(rv);
            //根据用户id 设置用户名称及头像

            ///////////// 此处开始测试代码////////////
            List<String> tmp=new ArrayList<>();
            int resource_num = 1;
            String[] resource_list=new String[resource_num];
            resource_list[0]="1.jpg";
            Post post_new = new Post("1",userid,"2020-01-01 07:31:00","title","content","tag",resource_num,"jpg",
                    0,0,tmp,tmp,0,resource_list);
            String[] resource_list2=new String[resource_num];
            resource_list2[0]="2.jpg";
            Post post_new2 = new Post("2",userid,"2020-01-01 07:31:01","title22","content","tag",resource_num,"jpg",
                    0,0,tmp,tmp,0,resource_list2);
            rv.add(post_new);
            rv.add(post_new2);
            ///////////// 此处结束测试代码////////////
            for(Post post:rv){
                Query query = new Query();
                query.addCriteria(Criteria.where("userid").is(post.getAuthor_id()));
                User user = mongoTemplate.findOne(query, User.class);
                if(user==null){
                    System.out.println("Error: user not found");
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                post.setAuthor_name(user.getUsername());
                post.setAuthor_head(user.getUser_head());
            }
            //System.out.println(rv.get(0).getAuthor_name());
            //System.out.println("haha1");
            //根据用户id设置Post的点赞、收藏状态，若userid在点赞列表中则设置为true，反之为false
            for(Post post:rv){
                if(post.getLike_userid_list()!=null){
                    if(post.getLike_userid_list().contains(userid)){
                        post.setIf_like(1);
                    }
                    else{
                        post.setIf_like(0);
                    }
                }
                else{
                    post.setIf_like(0);
                }
                if(post.getStar_userid_list()!=null){
                    if(post.getStar_userid_list().contains(userid)){
                        post.setIf_star(1);
                    }
                    else{
                        post.setIf_star(0);
                    }
                }
                else{
                    post.setIf_star(0);
                }
            }
            //System.out.println("haha3");
            return new ResponseEntity<>(rv, HttpStatus.OK);

        }
        catch(Exception e){
            System.out.println("Error: "+e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/new_post")
    public ResponseEntity<String[]> new_post(@RequestParam String userid,//用户唯一
                           @RequestParam String createAt,
                           @RequestParam String title,
                           @RequestParam String content,
                           @RequestParam String tag,
                           @RequestParam int resource_num,//资源的数量
                           @RequestParam String resource_type //资源类型 图片为jpg，视频为mp4
                           ) {
        // TODO: 上传图片未整合
        /*
        功能：新建一个post
        输入：userid 用户id 唯一
             createAt post创建时间
             title 标题
             content 内容
             tag 标签
             resource_num 资源数量
             resource_type 资源类型 图片为jpg，视频为mp4
        输出：
            成功： List<String> img_id 每个图片的全局唯一id 用于之后的图片上传
            失败： badrequest
        */

        try{
            System.out.println("Start new post");
            //初始化post
            List<String> tmp=new ArrayList<>();
            ObjectId tmp_id = new ObjectId();
            String postid =tmp_id.toString();
            String[] resource_list=new String[resource_num];
            int i=0;
            for(i=0;i<resource_num;i++){
                String tmp_resource_id = postid+"_"+i+"."+resource_type;
                System.out.println("tmp_resource_id "+tmp_resource_id);
                resource_list[i]=tmp_resource_id;
            }
            List<Comment> tmp_comment=new ArrayList<>();
            Post post_new = new Post(postid,userid,createAt,title,content,tag,resource_num,resource_type,
                    0,0,tmp,tmp,0,resource_list,tmp_comment);

            //在User的数据库中找到user，将post加入user的my_post_list
            Query query = new Query();
            query.addCriteria(Criteria.where("userid").is(userid));
            User user = mongoTemplate.findOne(query, User.class);
            if(user==null){
                System.out.println("Error: user not found");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            user.addMyPost(postid);

            //将post加入数据库，返回图片id
            mongoTemplate.insert(post_new);
            String[] rv=post_new.getResource_list();
            return new ResponseEntity<>(rv, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/like_or_star")
    public ResponseEntity<Integer> add_like_or_star(
            @RequestParam String postid,
            @RequestParam String userid,
            @RequestParam int state,
            @RequestParam int cancel
    ){
        /*
        * 功能：点赞或收藏
        * 输入：postid 帖子id
        *      userid 用户id
        *      state 0为点赞，1为收藏
        *      cancel  0为第一次点，1为取消前面的收藏/点赞
        * 输出：count // 新的收藏数或者点赞数
        * */

        try{
            System.out.println("Start like or star");
            Query query = new Query();
            query.addCriteria(Criteria.where("postid").is(postid));
            Post post = mongoTemplate.findOne(query, Post.class);
            if(post==null){
                System.out.println("Error: post not found");
                return ResponseEntity.notFound().build();
            }
//            System.out.println("haha2");
//            System.out.println(post.getPostid());
//            System.out.println(post.getLike_count());
            //在User的数据库中找到user，将post加入user的like_post_list或star_post_list，或删除user的like_post_list或star_post_list中的postid
            Query query2 = new Query();
            query2.addCriteria(Criteria.where("userid").is(userid));
            User user = mongoTemplate.findOne(query2, User.class);
            if(user==null){
                System.out.println("Error: user not found");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if(state==0){
                //在User的数据库中找到user，将post加入user的like_post_list或删除user的like_post_list中的postid
                if(cancel==0){
                    post.add_like(userid);
                    user.addLikePost(postid);
                }
                else{
                    post.cancel_like(userid);
                    user.cancelLikePost(postid);
                }
            }
            else{
                //在User的数据库中找到user，将post加入user的star_post_list或删除user的star_post_list中的postid
                //添加
                if(cancel==0){
                    post.add_star(userid);
                    user.addStarPost(postid);
                }
                //删除
                else{
                    post.cancel_star(userid);
                    user.cancelStarPost(postid);
                }
            }
//            System.out.println("haha3");
//            System.out.println(post.getPostid());
//            System.out.println(post.getLike_count());
            mongoTemplate.save(post);
            int rv;
            if(state==0){
                rv=post.getLike_count();
            }
            else{
                rv=post.getStar_count();
            }
            System.out.println("Like or star success");
            System.out.println(rv);
            return new ResponseEntity<>(rv, HttpStatus.OK);
        }
        catch(Exception e){
            System.out.println("Error: "+e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }













}


