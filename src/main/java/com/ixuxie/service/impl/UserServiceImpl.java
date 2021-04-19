package com.ixuxie.service.impl;

import com.ixuxie.dto.UserDto;
import com.ixuxie.entity.User;
import com.ixuxie.mappper.UserMapper;
import com.ixuxie.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Date: 2020/11/18 15:23
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDto findById(Long uid) {
        User user = userMapper.findById(uid);
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(user,dto);
        return dto;
    }

    @Override
    public UserDto findByPhone(String phone) {
        return null;
    }

    @Override
    public UserDto findByEmail(String email) {
        return null;
    }

    @Override
    public UserDto findByOpenId(String openId) {
        return null;
    }

    @Override
    public UserDto register(String nickName, Integer gender, String avatarUrl, String inviteCode, String openId) {
        User us = new User();
        us.setNickName(nickName);
        us.setGender(gender);
        us.setAvatarUrl(avatarUrl);
        us.setInviteCode(inviteCode);
        userMapper.insert(us);
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(us,userDto);
        return userDto;
    }

    @Override
    public boolean bindPhone(Long uid, String phone) {
        return false;
    }

    @Override
    public boolean bindEmail(Long uid, String email) {
        return false;
    }

    @Override
    public int refreshByOpenId(String nickName, Integer gender, String avatarUrl, String birthday, String openId) {
        return 0;
    }

    @Override
    public List<UserDto> findByInviteCode(String inviteCode) {
        return null;
    }

    @Override
    public Object page(Integer pageNo, Integer pageSize, Map<String, Object> params) {
        return null;
    }

    @Override
    public String findOpenId(Long uid) {
        return null;
    }

    @Override
    public List<UserDto> findByIds(List<Long> ids) {
        return null;
    }


/*
    @Autowired
    private UserDao userDao;

    @Autowired
    private LoginInfoDao loginInfoDao;

    @Autowired
    private EventBusDispatcher eventBusDispatcher;

    @Autowired
    private UserBridgeService userBridgeService;

    @Override
    public UserDto findById(Long uid) {
        return BeanMapper.map(userDao.findById(uid) ,UserDto.class);
    }

    @Override
    public UserDto findByPhone(String phone) {
        return BeanMapper.map(userDao.findByPhone(phone) ,UserDto.class);
    }

    @Override
    public UserDto findByEmail(String email) {
        return BeanMapper.map(userDao.findByEmail(email) ,UserDto.class);
    }

    @Override
    public UserDto findByOpenId(String openId) {
        LoginInfo info = loginInfoDao.findByOpenId(openId);
        if(info == null){
            return null;
        }
        return BeanMapper.map(userDao.findById(info.getUid()) ,UserDto.class);
    }

    @Override
    public UserDto register(String nickName, Integer gender, String avatarUrl, String inviteCode ,String openId) {
        User user = userBridgeService.register(nickName,gender,avatarUrl,inviteCode,openId);
        if(user == null){
            throw new ServiceException(ServiceCode.FAILD ,"注册失败");
        }
        *//**
         * 注册通知
         *//*
        eventBusDispatcher.dispatcher(UserEvent.builder().uid(user.getId()).build());
        return BeanMapper.map(user , UserDto.class);
    }

    @Override
    public boolean bindPhone(Long uid, String phone) {
        return false;
    }

    @Override
    public boolean bindEmail(Long uid, String email) {
        return false;
    }

    @Override
    public int refreshByOpenId(String nickName, Integer gender, String avatarUrl, String birthday, String openId) {
        LoginInfo info = loginInfoDao.findByOpenId(openId);
        if(info == null){
            return 0;
        }
        User user = userDao.findById(info.getUid());
        user.setNickName(nickName);
        user.setGender(gender);
        user.setAvatarUrl(avatarUrl);
        return userDao.update(user);
    }

    @Override
    public List<UserDto> findByInviteCode(String inviteCode) {
        return BeanMapper.mapList(userDao.findByInviteCode(inviteCode) ,UserDto.class);
    }

    @Override
    public Page page(Integer pageNo, Integer pageSize, Map<String, Object> params) {
        if (null == params) {
            params = Maps.newHashMap();
        }
        params.put("start", (pageNo -1)*pageSize);
        params.put("ends", pageSize);
        Integer count = userDao.countByParams(params);
        List<User> list = userDao.findPageByParams(params);
        return new Page(count,pageSize,pageNo, BeanMapper.mapList(list, UserDto.class));
    }

    @Override
    public String findOpenId(Long uid) {
        LoginInfo loginInfo = loginInfoDao.selectById(DigestUtils.md5Hex(uid + "_" + 3));
        if(loginInfo != null){
            return loginInfo.getLoginToken();
        }
        return null;
    }

    @Override
    public List<UserDto> findByIds(List<Long> ids) {
        return BeanMapper.mapList(userDao.findByIds(ids) ,UserDto.class);
    }

    */
}
