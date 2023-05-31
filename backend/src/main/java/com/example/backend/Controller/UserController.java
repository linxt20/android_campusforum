package com.example.backend.Controller;

import com.example.backend.Base.*;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        query.addCriteria(Criteria.where("username").is(username)); // userID查重
        if(mongoTemplate.findOne(query, User.class) != null) {
            System.out.println("用户名已存在");
            return new ResponseEntity<>("用户名或密码不能为空", HttpStatus.BAD_REQUEST);
        }
        // 把字符串存到数据库里
        List<String> tmp=new ArrayList<>();
        List<Message> tmp2=new ArrayList<>();
        List<Chat> tmp3=new ArrayList<>();
        User user_tmp = new User(username, password, "默认简介", "9.jpg", tmp,tmp, tmp,tmp, tmp,tmp, tmp,tmp2,tmp3);
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
        query.addCriteria(Criteria.where("userid").is(userid));
        User user = mongoTemplate.findOne(query, User.class);
        if(user == null) {
            System.out.println("用户不存在");
            return ResponseEntity.badRequest().body(null);
        }
        // debug
        System.out.println("star: " + user.getStar_post_list());
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/update_username_and_description")
    public ResponseEntity<String> update_user_info(@RequestParam String userid, @RequestParam String username,  @RequestParam String description) {
        /*
         * 功能：更新用户个人信息
         * 传入参数：
         *  userid：用户id
         *  username：用户名
         *  description：个人简介
         * 返回参数：
         *  String：success/fail
         * */
        System.out.println("Received update_user_info message" + userid + " " + username + " " + description);
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
        user.setDescription(description);
        mongoTemplate.save(user);
        return ResponseEntity.ok().body("success");
    }

    @PostMapping("/update_user_password")
    public ResponseEntity<String> update_user_password(@RequestParam String userid, @RequestParam String password) {
        /*
         * 功能：更新用户密码
         * 传入参数：
         *  userid：用户id
         *  password：新密码
         * 返回参数：
         *  String：success/fail
         * */
        System.out.println("Received update_user_password message" + userid + " " + password);
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
        // 把字符串存到数据库里
        user.setPassword(password);
        // 之后可能会有 密码正确性检查

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

    @PostMapping("/block_user")
    public ResponseEntity<String> block_user(@RequestParam String userid, @RequestParam String block_userid) {
        /*
         * 功能：屏蔽用户
         * 传入参数：
         *  userid：用户id
         *  block_userid：被屏蔽的用户id
         * 返回参数：
         *  String：success
         * */
        System.out.println("Received block_user message" + userid + " " + block_userid);
        if(userid.equals("") || block_userid.equals("")) {
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
        query = new Query();
        query.addCriteria(Criteria.where("userid").is(block_userid));
        User block_user = mongoTemplate.findOne(query, User.class);
        if(block_user == null) {
            System.out.println("被屏蔽用户不存在");
            return ResponseEntity.badRequest().body("被屏蔽用户不存在");
        }
        //如果已经屏蔽了，返回错误
        if(user.getBlock_list().contains(block_userid)) {
            System.out.println("已经屏蔽了");
            return ResponseEntity.badRequest().body("已经屏蔽了");
        }
        user.addBlock(block_userid);
        mongoTemplate.save(user);
        return ResponseEntity.ok().body("success");
    }

    @PostMapping("/unblock_user")
    public ResponseEntity<String> unblock_user(@RequestParam String userid, @RequestParam String unblock_userid) {
        /*
         * 功能：取消屏蔽用户
         * 传入参数：
         *  userid：用户id
         *  unblock_userid：被取消屏蔽的用户id
         * 返回参数：
         *  String：success
         * */
        System.out.println("Received unblock_user message" + userid + " " + unblock_userid);
        if(userid.equals("") || unblock_userid.equals("")) {
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
        query = new Query();
        query.addCriteria(Criteria.where("userid").is(unblock_userid));
        User unblock_user = mongoTemplate.findOne(query, User.class);
        if(unblock_user == null) {
            System.out.println("被取消屏蔽用户不存在");
            return ResponseEntity.badRequest().body("被取消屏蔽用户不存在");
        }
        //如果没有屏蔽，返回错误
        if(!user.getBlock_list().contains(unblock_userid)) {
            System.out.println("本来没有屏蔽");
            return ResponseEntity.badRequest().body("本来没有屏蔽");
        }
        user.cancelBlock(unblock_userid);
        mongoTemplate.save(user);
        return ResponseEntity.ok().body("success");
    }

    @PostMapping("/get_message")
    public ResponseEntity<List<Message>> get_message(@RequestParam String userid) {
        /*
         * 功能：获取用户的消息
         * 传入参数：
         *  userid：用户id
         * 返回参数：
         *  List<Message>：消息列表
         * */
        System.out.println("Received get_message message" + userid);
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
        List<Message> message_list = user.getMessage_list();
        return ResponseEntity.ok().body(message_list);
    }

    @PostMapping("/add_sentence")
    public ResponseEntity<String> add_sentence(@RequestParam String sender_id,
                                               @RequestParam String receiver_id,
                                               @RequestParam String content,
                                               @RequestParam String date){
        /*
            * 功能：添加一条私信
            * 传入参数：
            * sender_id：发送者id
            * receiver_id：接收者id
            * content：私信内容
            * date：发送时间
            * 返回参数：
            * String：success/失败原因
         */
        System.out.println("Received add_sentence message" + sender_id + " " + receiver_id + " " + content + " " + date);
        if(sender_id.equals("") || receiver_id.equals("") || content.equals("") || date.equals("")) {
            System.out.println("参数不能为空");
            return ResponseEntity.badRequest().body("参数不能为空");
        }
        //进行检查，如果用户名不存在，返回错误
        Query query = new Query();
        query.addCriteria(Criteria.where("userid").is(sender_id));
        User sender = mongoTemplate.findOne(query, User.class);
        if(sender == null) {
            System.out.println("发送者不存在");
            return ResponseEntity.badRequest().body("发送者不存在");
        }
        query = new Query();
        query.addCriteria(Criteria.where("userid").is(receiver_id));
        User receiver = mongoTemplate.findOne(query, User.class);
        if(receiver == null) {
            System.out.println("接收者不存在");
            return ResponseEntity.badRequest().body("接收者不存在");
        }
        //创建sentence
        //将date转换为date模式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date_tmp = null;
        try {
            date_tmp = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Sentence sentence = new Sentence(content,date_tmp,sender_id,receiver_id);
        //若chat_list中不存在两人的私信，则创建一个新的chat
        Chat chat = null;
        for(Chat c : sender.getChat_list()) {
            //如果chat中user1_id、user2_id能和两人id对应，顺序无所谓，则存在chat
            if((c.getUser1_id().equals(sender_id) && c.getUser2_id().equals(receiver_id)) ||
                    (c.getUser1_id().equals(receiver_id) && c.getUser2_id().equals(sender_id))) {
                chat = c;
                break;
            }
        }
        if(chat == null) {
            //创建一个新的chat
            List<Sentence> sentence_list = new ArrayList<>();
            sentence_list.add(sentence);
            chat = new Chat(sender_id,sender.getUsername(),sender.getUser_head(),
                    receiver_id,receiver.getUsername(),receiver.getUser_head(),sentence_list,date_tmp);
            sender.addChat(chat);
            receiver.addChat(chat);
        }
        else {
            //将sentence加入两用户的chat
            String chat_id=chat.getChat_id();
            sender.addChat_sentence(chat_id,sentence);
            receiver.addChat_sentence(chat_id,sentence);
        }
        mongoTemplate.save(sender);
        mongoTemplate.save(receiver);
        return ResponseEntity.ok().body("success");
    }

    









}
