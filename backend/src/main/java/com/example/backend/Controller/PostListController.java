package com.example.backend.Controller;

import com.example.backend.Base.Post;
import com.example.backend.Base.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
public class PostListController {
    @Autowired
    MongoTemplate mongoTemplate;


    @PostMapping("/post/get_list")
    public List<Post> post_get() {
        //目前 返回所有帖子
        //之后会增加search key，返回固定的帖子
        System.out.println("Start get post list");
        List<Post> rv=mongoTemplate.findAll(Post.class);
        return rv;
    }

    @PostMapping("/post/new_post")
    public String new_post(@RequestParam String Username,//用户唯一
                           @RequestParam String createAt,
                           @RequestParam String title,
                           @RequestParam String content) {
        //新建一个post
        //TODO 目前不能传输图片
        try{
            System.out.println("Start new post");
            String[] tmp = new String[]{"s1","s2","s3"};
            Post post_new = new Post(Username,createAt,title,content,"123",tmp);
            mongoTemplate.insert(post_new);
            return "success";
        }catch (Exception e){
            return "fail";
        }
    }

}
