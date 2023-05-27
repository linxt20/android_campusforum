package com.example.backend.Controller;

import com.example.backend.Base.Comment;
import com.example.backend.Base.Message;
import com.example.backend.Base.Post;
import com.example.backend.Base.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @描述 用户控制器，用于执行用户相关的业务
 **/
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    MongoTemplate mongoTemplate;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String username, @RequestParam String password) {
        System.out.println("Received register message" + username + " " + password);
        if(username.equals("") || password.equals("")) {
            System.out.println("用户名或密码不能为空");
            return new ResponseEntity<>("用户名或密码不能为空", HttpStatus.BAD_REQUEST);
        }
        // TODO 进行检查，如果用户名已经存在，返回错误
        Query query = new Query();
        query.addCriteria(Criteria.where("userID").is(username)); // userID查重
        if(mongoTemplate.findOne(query, User.class) != null) {
            System.out.println("用户名已存在");
            return new ResponseEntity<>("用户名或密码不能为空", HttpStatus.BAD_REQUEST);
        }
        // 把字符串存到数据库里
        List<String> tmp=new ArrayList<>();
        User user_tmp = new User(username, password, "默认简介", "9.jpg", tmp,tmp, tmp,tmp, tmp,tmp);
        String rv = user_tmp.getId();
        System.out.println(rv);
        mongoTemplate.insert(user_tmp);
        return new ResponseEntity<>(rv, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        System.out.println("Received login message" + username + " " + password);
        if(username.equals("") || password.equals("")) {
            System.out.println("用户名或密码不能为空");
            return ResponseEntity.badRequest().body("用户名或密码不能为空");
        }
        // TODO 进行检查，如果用户名不存在，返回错误
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        User user = mongoTemplate.findOne(query, User.class);
        if(user == null) {
            System.out.println("用户名不存在");
            return ResponseEntity.badRequest().body("用户名不存在");
        }
        if(!user.getPassword().equals(password)) {
            System.out.println("密码错误");
            return ResponseEntity.badRequest().body("密码错误");
        }
        String returnid = user.getId();
        return ResponseEntity.ok().body(returnid);
    }

    @PostMapping("/get_user_info")
    public ResponseEntity<User> get_user_info(@RequestParam String userid) {
        /*
        * 功能：返回用户个人信息、用户发过的帖子（如果需要）、用户收藏的帖子（如果需要）
        * 传入参数：
        *  userid：用户id
        * 返回参数：
        * User：包含用户个人信息
        * */
        System.out.println("Received get_user_info message" + userid);
        if(userid.equals("")) {
            System.out.println("用户id不能为空");
            return ResponseEntity.badRequest().body(null);
        }
        //进行检查，如果用户名不存在，返回错误
        Query query = new Query();
        query.addCriteria(Criteria.where("userID").is(userid));
        User user = mongoTemplate.findOne(query, User.class);
        if(user == null) {
            System.out.println("用户不存在");
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/update_user_info")
    public ResponseEntity<String> update_user_info(@RequestParam String userid, @RequestParam String username, @RequestParam String password, @RequestParam String introduction) {
        /*
         * 功能：更新用户个人信息
         * 传入参数：
         *  userid：用户id
         *  username：用户名
         *  password：密码
         *  introduction：个人简介
         * 返回参数：
         *  String：success/fail
         * */
        System.out.println("Received update_user_info message" + userid + " " + username + " " + password + " " + introduction);
        if(userid.equals("")) {
            System.out.println("用户id不能为空");
            return ResponseEntity.badRequest().body(null);
        }
        //进行检查，如果用户名不存在，返回错误
        Query query = new Query();
        query.addCriteria(Criteria.where("userid").is(userid));
        User user = mongoTemplate.findOne(query, User.class);
        if(user == null) {
            System.out.println("用户不存在");
            return ResponseEntity.badRequest().body("用户不存在");
        }
        //进行检查，如果用户名已经存在，返回错误
        query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        if(mongoTemplate.findOne(query, User.class) != null) {
            System.out.println("用户名已存在");
            return ResponseEntity.badRequest().body("用户名已存在");
        }
        //寻找用户每一个帖子，更新帖子的用户名
        List<String> post_list = user.getMy_post_list();
        if(post_list!=null){
            for(String postid:user.getMy_post_list()){
                query = new Query();
                query.addCriteria(Criteria.where("postid").is(postid));
                Post post = mongoTemplate.findOne(query, Post.class);
                if(post != null) {
                    post.setAuthor_name(username);
                    mongoTemplate.save(post);
                }
            }
        }
        //对用户评论过的帖子，即comment_post_list,找到其评论，把评论里的用户名改成新的用户名
        List<String> comment_post_list = user.getComment_post_list();
        if(comment_post_list!=null){
            for(String postid:user.getComment_post_list()){
                query = new Query();
                query.addCriteria(Criteria.where("postid").is(postid));
                Post post = mongoTemplate.findOne(query, Post.class);
                if(post != null) {
                    for(Comment comment:post.getComment_list()){
                        if(comment.getAuthor_id().equals(userid)){
                            comment.setAuthor_name(username);
                        }
                    }
                    mongoTemplate.save(post);
                }
            }
        }

        // 把字符串存到数据库里
        user.setUsername(username);
        user.setPassword(password);
        user.setDescription(introduction);
        mongoTemplate.save(user);
        return ResponseEntity.ok().body("success");
    }

    @PostMapping("/update_user_avatar")
    public ResponseEntity<String> update_user_avatar(@RequestParam String userid) {
        /*
         * 功能：更新用户头像
         * 传入参数：
         *  userid：用户id
         * 返回参数：
         *  String：userhead_id
         * */
        System.out.println("Received update_user_avatar message" + userid);
        if(userid.equals("")) {
            System.out.println("用户id不能为空");
            return ResponseEntity.badRequest().body("用户id不能为空");
        }
        //进行检查，如果用户名不存在，返回错误
        Query query = new Query();
        query.addCriteria(Criteria.where("userid").is(userid));
        User user = mongoTemplate.findOne(query, User.class);
        if(user == null) {
            System.out.println("用户不存在");
            return ResponseEntity.badRequest().body("用户不存在");
        }
        //用mongodb objectid给头像新建一个id,并把id存到数据库里,返回id
        ObjectId tmp_userhead_id = new ObjectId();
        String userhead_id = tmp_userhead_id.toString()+".jpg";
        user.setUser_head(userhead_id);

        //寻找用户每一个帖子，更新帖子的作者头像
        List<String> post_list = user.getMy_post_list();
        if(post_list!=null){
            for(String postid:user.getMy_post_list()){
                query = new Query();
                query.addCriteria(Criteria.where("postid").is(postid));
                Post post = mongoTemplate.findOne(query, Post.class);
                if(post != null) {
                    post.setAuthor_head(userhead_id);
                    mongoTemplate.save(post);
                }
            }
        }
        //对用户评论过的帖子，即comment_post_list,找到其评论，把评论里的头像改成新的头像
        List<String> comment_post_list = user.getComment_post_list();
        if(comment_post_list!=null){
            for(String postid:user.getComment_post_list()){
                query = new Query();
                query.addCriteria(Criteria.where("postid").is(postid));
                Post post = mongoTemplate.findOne(query, Post.class);
                if(post != null) {
                    for(Comment comment:post.getComment_list()){
                        if(comment.getAuthor_id().equals(userid)){
                            comment.setAuthor_head(userhead_id);
                        }
                    }
                    mongoTemplate.save(post);
                }
            }
        }
        mongoTemplate.save(user);
        return ResponseEntity.ok().body(userhead_id);
    }

    @PostMapping("/get_user_postlist")
    public ResponseEntity<List<Post>> get_user_postlist(@RequestParam String userid, @RequestParam String type) {
        /*
         * 功能：返回用户发过或收藏的帖子
         * 传入参数：
         *  userid：用户id
         *  type：“create”或“star” 用户发过的帖子还是收藏的帖子
         * 返回参数：
         *  List<Post>：用户发过的帖子
         * */
        System.out.println("Received get_user_postlist message" + userid + " " + type);
        if(userid.equals("")) {
            System.out.println("用户id不能为空");
            return ResponseEntity.badRequest().body(null);
        }
        //进行检查，如果用户名不存在，返回错误
        Query query = new Query();
        query.addCriteria(Criteria.where("userid").is(userid));
        User user = mongoTemplate.findOne(query, User.class);
        if(user == null) {
            System.out.println("用户不存在");
            return ResponseEntity.badRequest().body(null);
        }
        //根据传入的type，返回用户发过的帖子或收藏的帖子
        List<String> postid_list = new ArrayList<>();
        if(Objects.equals(type, "create"))
            postid_list = user.getMy_post_list();
        else if(Objects.equals(type, "star"))
            postid_list = user.getStar_post_list();
        else {
            System.out.println("type参数错误");
            return ResponseEntity.badRequest().body(null);
        }
        List<Post> post_list = new ArrayList<>();
        for (String s : postid_list) {
            query = new Query();
            query.addCriteria(Criteria.where("postid").is(s));
            Post post = mongoTemplate.findOne(query, Post.class);
            //检查用户是否喜欢或收藏了这个帖子，如果喜欢或收藏了，把is_star和is_like设为true
            if (post != null) {
                if (user.getStar_post_list().contains(post.getPostid())){
                    post.setIf_star(1);
                }
                else {
                    post.setIf_star(0);
                }
                if (user.getLike_post_list().contains(post.getPostid())){
                    post.setIf_like(1);
                }
                else {
                    post.setIf_like(0);
                }
            }
            post_list.add(post);
        }
        return ResponseEntity.ok().body(post_list);
    }

    @PostMapping("/get_follow_or_fans_list")
    public ResponseEntity<List<User>> get_follow_list(@RequestParam String userid, @RequestParam String type) {
        /*
         * 功能：返回用户关注或粉丝列表
         * 传入参数：
         *  userid：用户id
         *  type：“follow”或“fans” 用户关注的人还是粉丝
         * 返回参数：
         *  List<User>：用户关注或粉丝列表
         * */
        System.out.println("Received get_follow_or_fans_list message" + userid + " " + type);
        if(userid.equals("")) {
            System.out.println("用户id不能为空");
            return ResponseEntity.badRequest().body(null);
        }
        //进行检查，如果用户名不存在，返回错误
        Query query = new Query();
        query.addCriteria(Criteria.where("userid").is(userid));
        User user = mongoTemplate.findOne(query, User.class);
        if(user == null) {
            System.out.println("用户不存在");
            return ResponseEntity.badRequest().body(null);
        }
        //根据传入的type，返回用户关注或粉丝列表
        List<String> follow_list = new ArrayList<>();
        if(Objects.equals(type, "follow"))
            follow_list = user.getFollow_list();
        else if(Objects.equals(type, "fans"))
            follow_list = user.getFans_list();
        else {
            System.out.println("type参数错误");
            return ResponseEntity.badRequest().body(null);
        }
        List<User> user_list = new ArrayList<>();
        for (String s : follow_list) {
            query = new Query();
            query.addCriteria(Criteria.where("userid").is(s));
            User tmp_user = mongoTemplate.findOne(query, User.class);
            user_list.add(tmp_user);
        }
        return ResponseEntity.ok().body(user_list);
    }

    @PostMapping("/follow_or_unfollow")
    public ResponseEntity<String> follow_or_unfollow(@RequestParam String fans_id, @RequestParam String follow_userid, @RequestParam String type) {
        /*
         * 功能：关注或取消关注用户
         * 传入参数：
         *  fans_id：用户id
         *  follow_userid：被关注或取消关注的用户id
         *  type：“follow”或“unfollow” 关注还是取消关注
         * 返回参数：
         *  String：success
         * */
        System.out.println("Received follow_or_unfollow message" + fans_id + " " + follow_userid + " " + type);
        if(fans_id.equals("") || follow_userid.equals("")) {
            System.out.println("用户id不能为空");
            return ResponseEntity.badRequest().body("用户id不能为空");
        }
        //进行检查，如果用户名不存在，返回错误
        Query query = new Query();
        query.addCriteria(Criteria.where("userid").is(fans_id));
        User fans = mongoTemplate.findOne(query, User.class);
        if(fans == null) {
            System.out.println("用户不存在");
            return ResponseEntity.badRequest().body("用户不存在");
        }
        query = new Query();
        query.addCriteria(Criteria.where("userid").is(follow_userid));
        User follow_user = mongoTemplate.findOne(query, User.class);
        if(follow_user == null) {
            System.out.println("被关注用户不存在");
            return ResponseEntity.badRequest().body("被关注用户不存在");
        }
        //根据传入的type，关注或取消关注用户
        if(Objects.equals(type, "follow")) {
            //如果已经关注了，返回错误
            if(fans.getFollow_list().contains(follow_userid)) {
                System.out.println("已经关注了");
                return ResponseEntity.badRequest().body("已经关注了");
            }
            fans.addFollow(follow_userid);
            follow_user.addFans(fans_id);
        }
        else if(Objects.equals(type, "unfollow")) {
            //如果没有关注，返回错误
            if(!fans.getFollow_list().contains(follow_userid)) {
                System.out.println("本来没有关注");
                return ResponseEntity.badRequest().body("本来没有关注");
            }
            fans.cancelFollow(follow_userid);
            follow_user.cancelFans(fans_id);
        }
        else {
            System.out.println("type参数错误");
            return ResponseEntity.badRequest().body("type参数错误");
        }
        mongoTemplate.save(fans);
        mongoTemplate.save(follow_user);
        return ResponseEntity.ok().body("success");
    }


}
