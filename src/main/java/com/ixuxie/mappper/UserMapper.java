package com.ixuxie.mappper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ixuxie.entity.User;

public interface UserMapper extends BaseMapper<User> {

    User findByEmail(String email);


}