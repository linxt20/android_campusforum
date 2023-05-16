package com.example.backend.Controller;

import com.example.backend.Base.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            // 保存文件到本地或数据库
            // 返回成功状态码和文件保存的URL
            return ResponseEntity.ok("http://your-server.com/image/" + file.getOriginalFilename());
        } catch (Exception e) {
            e.printStackTrace();
            // 返回错误状态码和错误信息
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        }
    }


    @PostMapping("/test_data")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        System.out.println("Received data message" + username);
        Map<String, Object> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", 1);
        // 添加更多的键值对到Map中

        return data;
    }

}
