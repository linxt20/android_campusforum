package com.example.backend;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @PostMapping("/message")
    public String receiveMessage(@RequestParam String message) {
        if ("hello".equalsIgnoreCase(message)) {
            return "hi";
        } else {
            return "unknown message";
        }
    }
}
