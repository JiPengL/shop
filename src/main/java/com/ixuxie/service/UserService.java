package com.ixuxie.service;


import com.ixuxie.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface UserService {

    public UserDto findById(Long uid);

    public UserDto findByPhone(String phone);

    public UserDto findByEmail(String email);

    public UserDto findByOpenId(String openId);

    public UserDto register(String nickName, Integer gender, String avatarUrl, String inviteCode, String openId);

    public boolean bindPhone(Long uid, String phone);

    public boolean bindEmail(Long uid, String email);

    public int refreshByOpenId(String nickName, Integer gender, String avatarUrl, String birthday, String openId);

    public List<UserDto> findByInviteCode(String inviteCode);

    public Object page(Integer pageNo, Integer pageSize, Map<String, Object> params);

    public String findOpenId(Long uid);

    public List<UserDto> findByIds(List<Long> ids);

}
