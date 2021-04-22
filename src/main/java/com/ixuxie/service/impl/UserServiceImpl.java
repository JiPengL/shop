package com.ixuxie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ixuxie.dto.UserDto;
import com.ixuxie.entity.User;
import com.ixuxie.mappper.UserMapper;
import com.ixuxie.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Date: 2020/11/18 15:23
 */
@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserMapper userMapper;

    public User findById(Long id){
        User user = userMapper.selectById(id);
        return user;
    }

    @Override
    public User findByName(String name) {

        QueryWrapper<User> wa = new QueryWrapper<>();
        wa.select("id","name","gender");
        wa.eq("name", name);
        User user = userMapper.selectOne(wa);
        return user;
    }

    @Override
    public User findByEmail(String Email) {
        return userMapper.findByEmail(Email);
    }

    @Override
    public User registerByEmail(String email) {
        User user = new User();
        user.setEamil(email);
        user.setName("大哥");
        userMapper.insert(user);
        return user;
    }

    @Override
    public User registerByName(String name, String pwd) {
        User user = new User();
        user.setName(name);
        user.setPwd(pwd);
        userMapper.insert(user);
        return user;
    }

    @Override
    public boolean reportByName(String name){
        QueryWrapper wa= new QueryWrapper();
        wa.select("id");
        wa.eq("name", name);
        User user = userMapper.selectOne(wa);
        if(null == user){
            return false;
        }
        return true;
    }

    @Override
    public UserDto findByOpenId(String openId) {
        QueryWrapper wa= new QueryWrapper();
        wa.eq("openId", openId);
        User user = userMapper.selectOne(wa);
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(user,dto);
        return dto;
    }

    @Override
    public UserDto register(String nickName, Integer gender, String avatarUrl, String inviteCode, String openId) {
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
