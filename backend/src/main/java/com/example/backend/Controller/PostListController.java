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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
                                               @RequestParam String type,
                                               @RequestParam String sort_by) {
        //之后会增加search key，返回固定的帖子
        /*
        * 功能：返回所有帖子
        * 输入：
        * userid: 用户id
        * search_key: 搜索关键词 和帖子的标题、内容、tag、用户名匹配；支持多关键词，按逻辑与进行匹配，关键词之间用空格分隔
        * tag: 标签
        * type: 帖子类型 //hot 热门  follow 关注的人发的帖子 all 所有帖子
        * sort_by: 排序依据 //"time" 时间倒序排序 "like" 点赞数降序排序 "comment" 评论数降序排序
        * if_follow: 是否只返回关注的人发布的帖子
        * 输出： List<Post> 所有帖子
        * */

        try{
            int hot_post_num=10;

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
                System.out.println("search key is not null, is: " + search_key);
                List<String> key_list=Arrays.asList(search_key.split(" "));
                List<Post> tmp=new ArrayList<>();
                for(Post post:rv){
                    if(if_contain_serchkey_list(post,key_list)){
                        System.out.println("add post: " + post.getTitle());
                        tmp.add(post);
                    }
                }
                rv=tmp;
                System.out.println("rv: " + rv.get(0).getTitle());
            }

            //根据用户的屏蔽名单block_list筛选帖子,若作者id在block_list中则不保留
            //获取用户屏蔽名单
            Query query = new Query();
            query.addCriteria(Criteria.where("userid").is(userid));
            User user = mongoTemplate.findOne(query, User.class);
            //确认用户存在
            if(user==null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            List<String> block_list = user.getBlock_list();
            //根据用户屏蔽名单筛选帖子
            if(block_list!=null){
                List<Post> tmp=new ArrayList<>();
                for(Post post:rv){
                    if(!block_list.contains(post.getAuthor_id())){
                        tmp.add(post);
                    }
                }
                rv=tmp;
            }
            //根据type筛选帖子，若为follow,从用户的关注列表筛选帖子,若为hot,选择热度高的，目前热度高定义为点赞量+评论量>=hot_post_num
            if(type.equals("follow")){
                List<String> follow_list = user.getFollow_list();
                if(follow_list!=null){
                    List<Post> tmp=new ArrayList<>();
                    for(Post post:rv){
                        if(follow_list.contains(post.getAuthor_id())){
                            tmp.add(post);
                        }
                    }
                    rv=tmp;
                }
            }
            else if(type.equals("hot")){
                List<Post> tmp=new ArrayList<>();
                for(Post post:rv){
                    if(post.getLike_count()+post.getComment_count()>=hot_post_num){
                        tmp.add(post);
                    }
                }
                rv=tmp;
                rv.sort((o1, o2) -> (o2.getLike_count() + o2.getComment_count()) - (o1.getLike_count() + o1.getComment_count()));
            }
            else if(type.equals("all")){
                //do nothing
            }
            else{
                System.out.println("type is wrong");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }


            //根据sortby排序帖子
            if(sort_by.equals("time")){
            //将create_time转化为Date类型进行倒序排序
                rv.sort((o1, o2) -> {
                    try{
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date1 = dateFormat.parse(o1.getCreate_time());
                        Date date2 = dateFormat.parse(o2.getCreate_time());
                        return date2.compareTo(date1);
                    }
                    catch (Exception e){
                        return 0;
                    }
                });

            }
            else if(sort_by.equals("like")){
                rv.sort((o1, o2) -> (o2.getLike_count()  - o1.getLike_count() ));
            }
            else if(sort_by.equals("comment")){
                rv.sort((o1, o2) -> (o2.getComment_count()  - o1.getComment_count() ));
            }
            else{
                System.out.println("sort by is wrong");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }



//            ///////////// 此处开始测试代码////////////
//            List<String> tmp=new ArrayList<>();
//            List<Comment> tmpComment = new ArrayList<>();
//            String timeString = "2023-05-17 10:30:00"; // 时间字符串
//            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date date = dateFormat.parse(timeString);
//            Comment comment = new Comment(userid,date,"haha","name","giao.jpg");
//            tmpComment.add(comment);
//            int resource_num = 1;
//            String[] resource_list=new String[resource_num];
//            resource_list[0]="1.mp4";
//            Post post_new = new Post("1",userid,"name","giao.jpg","2020-01-01 07:31:00","title","content","tag",resource_num,"mp4",
//                    0,0,tmp,tmp,tmpComment.size(),resource_list,tmpComment);
//            rv.add(post_new);

//            ///////////// 此处结束测试代码////////////

//            //根据用户id 设置用户名称及头像 若修改用户名、头像没有bug，可删
//            for(Post post:rv){
//                query = new Query();
//                query.addCriteria(Criteria.where("userid").is(post.getAuthor_id()));
//                User user_author = mongoTemplate.findOne(query, User.class);
//                if(user_author==null){
//                    System.out.println("Error: user not found");
//                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//                }
//                post.setAuthor_name(user_author.getUsername());
//                post.setAuthor_head(user_author.getUser_head());
//            }
            
            //根据用户id设置Post的点赞、收藏状态，若userid在点赞列表中则设置为true，反之为false
            System.out.println("Start set like and star");
            //输出Post的like_userid_list和star_userid_list
            System.out.println("Post like_userid_list: "+rv.get(0).getLike_userid_list());
            System.out.println("Post star_userid_list: "+rv.get(0).getStar_userid_list());
            System.out.println("userid: "+userid);
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
            System.out.println(rv.get(0).getIf_like());


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
            Boolean not_contain=false;
            for(String key:key_list){
                if(post.getTitle().contains(key)){
                    continue;
                }
                else{
                    not_contain = true;
                    break;
                }
            }
            if(!not_contain){
                return true;
            }
        }
        if(post.getContent()!=null){
            Boolean not_contain=false;
            for(String key:key_list){
                if(post.getContent().contains(key)){
                    continue;
                }
                else{
                    not_contain = true;
                    break;
                }
            }
            if(!not_contain){
                return true;
            }
        }
        if(post.getTag()!=null){
            Boolean not_contain=false;
            for(String key:key_list){
                if(post.getTag().contains(key)){
                    continue;
                }
                else{
                    not_contain = true;
                    break;
                }
            }
            if(!not_contain){
                return true;
            }
        }
        if(post.getAuthor_name()!=null){
            Boolean not_contain=false;
            for(String key:key_list){
                if(post.getAuthor_name().contains(key)){
                    continue;
                }
                else{
                    not_contain = true;
                    break;
                }
            }
            if(!not_contain){
                return true;
            }
        }
        return false;
    }

    @PostMapping("/new_post")
    public ResponseEntity<String[]> new_post(@RequestParam String userid,//用户唯一
                           @RequestParam String createAt,
                           @RequestParam String title,
                           @RequestParam String content,
                           @RequestParam String tag,
                           @RequestParam int resource_num,//资源的数量
                           @RequestParam String resource_type, //资源类型 图片为jpg，视频为mp4
                           @RequestParam String location//地点
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
            成功： List<String> img_id A
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
            Post post_new = new Post(postid,userid,user_name,user_head,createAt,title,content,tag,location,resource_num,resource_type,
                    0,0,tmp,tmp,0,resource_list,tmp_comment);

            //将post加入user的my_post_list
            user.addMyPost(postid);
            //将post加入数据库，返回图片id
            mongoTemplate.insert(post_new);
            mongoTemplate.save(user);
            //给作者每一个粉丝发一条通知，通知他们关注的人发了新的动态
            String msg_title="关注的人发了新的动态";
            String msg_content="你关注的用户"+user_name+"发了新的动态："+title+"，快去看看吧！";
            String msg_type="post";
            Date date = new Date();
            List<String> fans_list=user.getFans_list();
            for(String fans_id:fans_list){
                Query query1 = new Query();
                query1.addCriteria(Criteria.where("userid").is(fans_id));
                User fans = mongoTemplate.findOne(query1, User.class);
                if(fans!=null){
                    Message message=new Message(msg_title,msg_content,date,msg_type,fans_id);
                    fans.addMessage(message);
                    mongoTemplate.save(fans);
                }
            }
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
                    //给帖子的作者发一个通知，通知他有人点赞了他的帖子
                    String authorid=post.getAuthor_id();
                    String msg_type="like";
                    String msg_title="有人点赞了你的帖子";
                    String msg_content= user.getUsername()+"点赞了你的帖子 "+post.getTitle();
                    //获取date类型的当前时间
                    Date date = new Date();
                    System.out.println("msg_follow_new_post_date: "+date);
                    Message msg=new Message(msg_title,msg_content,date,msg_type,userid);
                    user.addMessage(msg);
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
            System.out.println("postlist: " + user.getStar_post_list());
            mongoTemplate.save(post);
            mongoTemplate.save(user);
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
            @RequestParam String createAt,
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
            System.out.println("received comment: " + content);
            String username=user.getUsername();
            String userhead=user.getUser_head();
            // convert string to date
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date tmp = format.parse(createAt);
            Comment comment = new Comment(userid,tmp,content,username,userhead);
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

            //给作者发送一条通知，提醒该用户评论了他的帖子
            String authorid = post.getAuthor_id();
            Query query3 = new Query();
            query3.addCriteria(Criteria.where("userid").is(authorid));
            User author = mongoTemplate.findOne(query3, User.class);
            if(author==null){
                System.out.println("Error: author not found");
                return ResponseEntity.badRequest().body("author not found");
            }
            String msg_title = "您的帖子有新的评论";
            String msg_content=username+"评论了您的帖子: "+content;
            String msg_type = "comment";
            Message msg = new Message(msg_title,msg_content, tmp,msg_type,userid);
            author.addMessage(msg);
            mongoTemplate.save(author);
            System.out.println("Send message success");
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
            System.out.println("Start get pos， postid: " + postid);
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


