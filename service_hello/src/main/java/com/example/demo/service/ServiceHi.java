package com.example.demo.service;

import com.example.demo.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author : HARRY
 * @Description : feign方式调用
 * @Date : created in 2025/9/11 14:16
 */

@FeignClient("service-hi")
public interface ServiceHi {

    @GetMapping("/user/{id}")
    User findById(@PathVariable("id") String id);

}
