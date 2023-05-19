package com.example.backend.Controller;

import com.example.backend.Base.Message;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api")
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

    @PostMapping("/test_image")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) {
        //++++端给后端传文件
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No image file provided");
        }
        try {
            System.out.println("Start to upload");
            // 指定存储文件的目录
            String uploadDirectory = "/Users/janet/Desktop/android_campusforum/backend/src/main/resources/static/images";
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 构建存储文件的路径
            String filePath = uploadDirectory + File.separator + fileName;
            // 将文件保存到本地
            file.transferTo(new File(filePath));
            // 返回成功状态码和文件保存的URL
            return ResponseEntity.ok("Image uploaded successfully. URL: http://127.0.0.1/images/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            // 返回错误状态码和错误信息
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        }
    }



//    @PostMapping("/upload-image")
//    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) {
//        try {
//            // 保存文件到本地或数据库
//            // 返回成功状态码和文件保存的URL
//            return ResponseEntity.ok("http://your-server.com/image/" + file.getOriginalFilename());
//        } catch (Exception e) {
//            e.printStackTrace();
//            // 返回错误状态码和错误信息
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Failed to upload image: " + e.getMessage());
//        }
//    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<ClassPathResource> getImage(@PathVariable String imageName) throws IOException {
        System.out.println("Received get image request");
        String haha= new String("you.jpg");
        ClassPathResource imageResource = new ClassPathResource("static/images/" + imageName);

        if (imageResource.exists() && imageResource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageResource);
        } else {
            return ResponseEntity.notFound().build();
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
