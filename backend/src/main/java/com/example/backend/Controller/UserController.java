package com.example.backend.Controller;

import com.example.backend.Base.Message;
import com.example.backend.Base.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @描述 用户控制器，用于执行用户相关的业务
 **/
@RestController
public class UserController {
    @Autowired
    MongoTemplate mongoTemplate;

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password) {
        System.out.println("Received register message" + username + " " + password);
        if(username.equals("") || password.equals("")) {
            System.out.println("用户名或密码不能为空");
            return "用户名或密码不能为空";
        }
        // TODO 进行检查，如果用户名已经存在，返回错误
        Query query = new Query();
        query.addCriteria(Criteria.where("userID").is(username)); // userID查重
        if(mongoTemplate.findOne(query, User.class) != null) {
            System.out.println("用户名已存在");
            return "用户名已存在";
        }
        // 把字符串存到数据库里
        User user1 = new User(username, password);
        mongoTemplate.insert(user1);
        return "saved";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        System.out.println("Received login message" + username + " " + password);
        if(username.equals("") || password.equals("")) {
            System.out.println("用户名或密码不能为空");
            return "用户名或密码不能为空";
        }
        // TODO 进行检查，如果用户名不存在，返回错误
        Query query = new Query();
        query.addCriteria(Criteria.where("userID").is(username)); // userID查重
        User user = mongoTemplate.findOne(query, User.class);
        if(user == null) {
            System.out.println("用户名不存在");
            return "用户名不存在";
        }
        if(!user.getPassword().equals(password)) {
            System.out.println("密码错误");
            return "密码错误";
        }
        return "登录成功";
    }
}
