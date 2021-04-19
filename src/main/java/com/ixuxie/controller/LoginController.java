package com.ixuxie.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.ixuxie.config.cache.CacheTemplate;
import com.ixuxie.constant.Constant;
import com.ixuxie.dto.UserDto;
import com.ixuxie.exception.ApiRuntimeException;
import com.ixuxie.service.UserService;
import com.ixuxie.utils.InfoCode;
import com.ixuxie.utils.OkHttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author
 */
@RestController
@RequestMapping("/api/v1/login")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;


    @NacosValue(value = "${abacus.token.expire:604800}")
    private Long tokenExpire ;

    @NacosValue(value = "${wx.appid:35423414325323}",autoRefreshed = true)
    private String appId;

    @NacosValue(value = "${wx.secret:1235342324}" ,autoRefreshed = true)
    private String secret;

    @NacosValue(value = "${wx.authorize:1234}",autoRefreshed = true)
    private String authorize;

    @NacosValue(value = "${wx.jscode2session:1234}",autoRefreshed = true)
    private String jscode2session;
/*
    @Autowired
    @Qualifier("stringRedisTemplate")
    private RedisTemplate<String, String> stringRedisTemplate;*/

    @Autowired
    private CacheTemplate cacheTemplate;



    @PostMapping("/login")
    public Object login(@RequestParam(value = "code" ,required = false) String code,
                            @RequestParam(value = "encryptedData" ,required = false) String encryptedData,
                            @RequestParam(value = "iv" ,required = false) String iv,
                            @RequestParam(value = "avatarUrl" ,required = false) String avatarUrl,
                            @RequestParam(value = "nickName" ,required = false) String nickName,
                            @RequestParam(value = "inviteCode" ,required = false) String inviteCode,
                            @RequestParam(value = "gender" ,defaultValue = "1") Integer gender){
        String cUrl = String.format(jscode2session, appId, secret, code);
        String res = OkHttpUtil.getIntance().get(cUrl);
        if(res == null){
            throw new ApiRuntimeException(InfoCode.AUTH_FAIL);
        }
        JSONObject jsonObject = JSON.parseObject(res);
        if (jsonObject == null || !jsonObject.containsKey("openid")) {
            throw new ApiRuntimeException(InfoCode.AUTH_FAIL);
        }
        String key = Constant.A_W_UID +  code;
        try {
            String have = cacheTemplate.valueGet(key).toString();
            if (StringUtils.isNotBlank(have)){
                throw new ApiRuntimeException(InfoCode.AUTH_FAIL ,"禁止重复提交");
            }
            cacheTemplate.valueSetAndExpire(key , "NX" ,5 ,TimeUnit.SECONDS);
            String openId = jsonObject.getString("openid");
            UserDto user = userService.findByOpenId(openId);
            if(user == null){
                user = userService.register(nickName ,gender, avatarUrl ,inviteCode ,openId);
            }
            if(user == null){
                throw new ApiRuntimeException(InfoCode.AUTH_FAIL ,"授权异常");
            }
            String k = Constant.G_W_UID +  user.getId();
            String token = UUID.randomUUID().toString();
            cacheTemplate.valueSetAndExpire(k , token ,tokenExpire ,TimeUnit.SECONDS);

            String ck = Constant.S_C_UID +  user.getId();
            cacheTemplate.valueSetAndExpire(ck , "true" ,1 ,TimeUnit.HOURS);
            UserDto finalUser = user;
            return new HashMap<String, Object>(5) {
                {
                    put("uid", finalUser.getId());
                    put("token", token);
                    put("nickName", nickName);
                    put("gender", gender);
                    put("avatarUrl", avatarUrl);
                }
            };
        } catch (Exception e) {
            logger.error("注册用户失败 e =" ,e);
            throw new ApiRuntimeException(InfoCode.FAIL, e.getMessage());
        } finally {
            if(cacheTemplate.hasKey(key)){
                cacheTemplate.delete(key);
            }
        }
    }
}
