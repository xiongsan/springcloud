package com.example.demo.controller;

import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @Author : HARRY
 * @Description :
 * @Date : created in 2025/9/9 17:02
 */
@RestController
@RefreshScope
public class TestController {

    @Value("${server.port}")
    String port;

    @Value("${pattern.dateformat}")
    private String dateformat;

    @GetMapping("now")
    public String now(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateformat));
    }

    @RequestMapping("/hi")
    public String home(@RequestParam String name) {
        return "hi "+name+",i am from port:" +port;
    }

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable String id) {
        User user = new User();
        user.setId(id);
        user.setName(port + "_" + UUID.randomUUID().toString());
        return user;
    }
}
