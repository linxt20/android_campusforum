package com.example.backend;

import com.example.backend.Base.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
    @Autowired
    MongoTemplate mongoTemplate;

    @PostMapping("/message")
    public String receiveMessage(@RequestParam String message) {
        if ("hello".equalsIgnoreCase(message)) {
            System.out.println("Received hello message");
            return "hi";
        } else {
            System.out.println("Received unknown message");
            // 把字符串存到数据库里
            Message message1 = new Message(message);
            mongoTemplate.insert(message1);
            return "saved";
        }
    }
}
