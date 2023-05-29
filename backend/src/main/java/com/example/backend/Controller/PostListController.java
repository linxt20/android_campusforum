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

import java.util.*;


@RestController
@RequestMapping("/post")
public class PostListController {
    @Autowired
    MongoTemplate mongoTemplate;


    @PostMapping("/get_all")
    public ResponseEntity<List<Post>> post_get(@RequestParam String userid,
                                               @RequestParam String search_key,
                                               @RequestParam String tag,
                                               @RequestParam String sort_by) {
        //之后会增加search key，返回固定的帖子
        /*
        * 功能：返回所有帖子
        * 输入：
        * userid: 用户id
        * search_key: 搜索关键词 和帖子的标题、内容、tag、用户名匹配；支持多关键词，按逻辑与进行匹配，关键词之间用空格分隔
        * tag: 标签
        * sort_by: 排序依据 //热度怎么计算？
        * 输出： List<Post> 所有帖子
        * */

        try{
            //获取帖子
            System.out.println("Start get all post");
            List<Post> rv=mongoTemplate.findAll(Post.class);
            System.out.println(rv);
            //根据tag筛选帖子
            if(!tag.equals("")){
                List<String> tag_list=Arrays.asList(tag.split(" "));
                List<Post> tmp=new ArrayList<>();
                for(Post post:rv){
                    if(post.getTag()!=null){
                        if(Objects.equals(post.getTag(), tag)){
                            tmp.add(post);
                        }
                    }
                }
                rv=tmp;
            }
            //根据关键词筛选帖子，若帖子标题、内容、tag、用户名中包含关键词则保留，无需一样
            if(!search_key.equals("")){
                List<String> key_list=Arrays.asList(search_key.split(" "));
                List<Post> tmp=new ArrayList<>();
                for(Post post:rv){
                    if(if_contain_serchkey_list(post,key_list)){
                        tmp.add(post);
                    }
                }
                rv=tmp;
            }

            //TODO 根据sortby排序帖子

            //根据用户id 设置用户名称及头像

            ///////////// 此处开始测试代码////////////
            List<String> tmp=new ArrayList<>();
            List<Comment> tmpComment = new ArrayList<>();
            Comment comment = new Comment("1","2021-01-01 07:31:10","haha");
            tmpComment.add(comment);
            int resource_num = 1;
            String[] resource_list=new String[resource_num];
            resource_list[0]="1.jpg";
            Post post_new = new Post("1",userid,"2020-01-01 07:31:00","title","content","tag",resource_num,"jpg",
                    0,0,tmp,tmp,tmpComment.size(),resource_list,tmpComment);
            String[] resource_list2=new String[resource_num];
            resource_list2[0]="2.jpg";
            Post post_new2 = new Post("2",userid,"2020-01-01 07:31:01","title22","content","tag",resource_num,"jpg",
                    0,0,tmp,tmp,tmpComment.size(),resource_list2,tmpComment);
            System.out.println("after new post: " + post_new.getComment_list().get(0).toString());
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
                post.setAuthor_head("giao.jpg"); // TODO (user.getUser_head());
            }
            
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


            return new ResponseEntity<>(rv, HttpStatus.OK);

        }
        catch(Exception e){
            System.out.println("Error: "+e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public boolean if_contain_serchkey_list(Post post, List<String> key_list){
        //判断post中是否包含key_list中的每一个值
        if(post.getTitle()!=null){
            for(String key:key_list){
                if(post.getTitle().contains(key)){
                    continue;
                }
                else{
                    return false;
                }
            }
        }
        if(post.getContent()!=null){
            for(String key:key_list){
                if(post.getContent().contains(key)){
                    continue;
                }
                else{
                    return false;
                }
            }
        }
        if(post.getTag()!=null){
            for(String key:key_list){
                if(post.getTag().contains(key)){
                    continue;
                }
                else{
                    return false;
                }
            }
        }
        if(post.getAuthor_name()!=null){
            for(String key:key_list){
                if(post.getAuthor_name().contains(key)){
                    continue;
                }
                else{
                    return false;
                }
            }
        }
        return true;
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
            //寻找用户的头像及名称
            Query query = new Query();
            query.addCriteria(Criteria.where("userid").is(userid));
            User user = mongoTemplate.findOne(query, User.class);
            if(user==null){
                System.out.println("Error: user not found");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            String user_head=user.getUser_head();
            String user_name=user.getUsername();
            Post post_new = new Post(postid,userid,user_name,user_head,createAt,title,content,tag,resource_num,resource_type,
                    0,0,tmp,tmp,0,resource_list,tmp_comment);

            //将post加入user的my_post_list
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

    @PostMapping("/new_comment")
    public ResponseEntity<String> new_comment(
            @RequestParam String postid,
            @RequestParam String userid,
            @RequestParam Date createAt,
            @RequestParam String content
    ){
        /*
        * 功能：新建评论
        * 输入：postid 帖子id
        *      userid 用户id
        *      createAt 评论创建时间
        *      content 评论内容
        * 输出：
        *     success/fail
        * */

        try{
            System.out.println("Start new comment");
            ObjectId tmp_id = new ObjectId();
            String commentid =tmp_id.toString();
            //初始化comment
            //寻找userid对应用户的username和userhead，将其加入comment
            Query query2 = new Query();
            query2.addCriteria(Criteria.where("userid").is(userid));
            User user = mongoTemplate.findOne(query2, User.class);
            if(user==null){
                System.out.println("Error: user not found");
                return ResponseEntity.badRequest().body("user not found");
            }
            String username=user.getUsername();
            String userhead=user.getUser_head();
            Comment comment = new Comment(userid,createAt,content,username,userhead);
            //将postid加入user的comment_post_list
            user.addCommentPost(postid);

            //在Post的数据库中找到post，将comment加入post的comment_list
            Query query = new Query();
            query.addCriteria(Criteria.where("postid").is(postid));
            Post post = mongoTemplate.findOne(query, Post.class);
            if(post==null){
                System.out.println("Error: post not found");
                return ResponseEntity.badRequest().body("post not found");
            }
            post.add_comment(comment);

            //保存
            mongoTemplate.save(user);
            mongoTemplate.save(post);
            System.out.println("New comment success");
            return ResponseEntity.ok().body("success");
        }
        catch(Exception e){
            System.out.println("Error: "+e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public List<Post> preprocess_post(List<Post> post_list,String userid){
        /*
        * 功能：预处理post_list，将其中的like和star信息加入
        * 输入：post_list
        * 输出：post_list
        * */
        for(Post post:post_list){
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
        return post_list;
    }

    @PostMapping("/get_post")
    public ResponseEntity<Post> get_post (String postid, String userid){
        //根据postid获取post,并根据userid设置post的like和star信息
        try{
            System.out.println("Start get post");
            Query query = new Query();
            query.addCriteria(Criteria.where("postid").is(postid));
            Post post = mongoTemplate.findOne(query, Post.class);
            if(post==null){
                System.out.println("Error: post not found");
                return ResponseEntity.notFound().build();
            }
            post.setIf_like(0);
            post.setIf_star(0);
            if(post.getLike_userid_list()!=null){
                if(post.getLike_userid_list().contains(userid)){
                    post.setIf_like(1);
                }
                else{
                    post.setIf_like(0);
                }
            }
            if(post.getStar_userid_list()!=null){
                if(post.getStar_userid_list().contains(userid)){
                    post.setIf_star(1);
                }
                else{
                    post.setIf_star(0);
                }
            }
            System.out.println("Get post success");
            return ResponseEntity.ok().body(post);
        }
        catch(Exception e){
            System.out.println("Error: "+e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }







}


