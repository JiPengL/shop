package com.ixuxie.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.ixuxie.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


//@Auth
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @NacosValue(value = "${user_name}",autoRefreshed=true)
    private String user_name;

    @Autowired
    private UserService userService;


    @RequestMapping("/hello")
    public Object say(){
        userService.register("nickName", 27,  "avatarUrl",  "inviteCode",  "openId");
        Map <String,String>map = new HashMap<>();
        map.put("ss",user_name);
        return map;
    }

    @GetMapping("/findById")
    public Object getUserById(@RequestParam("id") Long id){
        return userService.findById(id);
    }

}
