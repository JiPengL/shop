package com.ixuxie.service;


import com.ixuxie.dto.UserDto;
import com.ixuxie.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    public User findById(Long id);

    User findByName(String name);

    User findByEmail(String Email);

    User registerByEmail(String email);

    User registerByName(String name,String pwd);

    boolean reportByName(String name);


    UserDto findByOpenId(String openId);

    UserDto register(String nickName, Integer gender, String avatarUrl, String inviteCode, String openId);
}
