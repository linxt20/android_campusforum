package com.example.backend.Controller;

import com.example.backend.Base.Message;
import com.example.backend.Base.User;
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
        User user_tmp = new User(username, password);
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

}
