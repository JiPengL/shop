package com.ixuxie.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.ixuxie.config.Constant;
import com.ixuxie.config.cache.GuavaCache;
import com.ixuxie.config.email.EmailService;
import com.ixuxie.dto.UserDto;
import com.ixuxie.entity.User;
import com.ixuxie.exception.ApiRuntimeException;
import com.ixuxie.service.UserService;
import com.ixuxie.utils.*;
import com.ixuxie.utils.wx.WechaConstant;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.patchca.background.BackgroundFactory;
import org.patchca.color.SingleColorFactory;
import org.patchca.filter.predefined.CurvesRippleFilterFactory;
import org.patchca.service.Captcha;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.word.RandomWordFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    @Autowired
    private EmailService emailService;

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

    @NacosValue(value = "${isTest:true}",autoRefreshed = true)
    private boolean isTest;

    @Autowired
    private GuavaCache guavaCache;



    private static final ConfigurableCaptchaService cs = new ConfigurableCaptchaService();



    static {
        //设置字体颜色
        cs.setColorFactory(new SingleColorFactory(new Color(139, 83, 246)));
        cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory()));
        //设置字体颜色
        cs.setWidth(130);
        cs.setHeight(60);
        RandomWordFactory randomWordFactory = new RandomWordFactory();
        randomWordFactory.setMaxLength(4);
        randomWordFactory.setMinLength(4);
        cs.setWordFactory(randomWordFactory);
        //设置背景颜色
        cs.setBackgroundFactory( new BackgroundFactory() {
            @Override
            public void fillBackground(BufferedImage image) {
                Graphics graphics = image.getGraphics();
                int imgWidth = image.getWidth();
                int imgHeight = image.getHeight();
                graphics.setColor(new Color(177, 100, 15));
                graphics.fillRect(0, 0, imgWidth, imgHeight);
            }
        });
    }


    @GetMapping("/getIdentifyingCode")
    public Object getIdentifyingCode() throws IOException {
        Captcha captcha = cs.getCaptcha();
        ByteArrayOutputStream outputStream = null;
        outputStream = new ByteArrayOutputStream();
        String code = captcha.getChallenge();
        ImageIO.write(captcha.getImage(), "png", outputStream);
        String images = "data:image/jpg;base64," + Base64.encodeBase64String(outputStream.toByteArray());
        String msgId = UUID.randomUUID().toString().replaceAll("-","");
        guavaCache.valueSetAndExpire(msgId , code ,360 ,TimeUnit.SECONDS);
        System.out.println("code-->"+code);
        return new HashMap<String, Object>(2) {
            {
                put("msgId", msgId);
                put("images", images);
            }
        };
    }


    @PostMapping("/wx")
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
        String key = WechaConstant.A_W_UID +  code;
        try {
            String have = guavaCache.valueGet(key).toString();
            if (StringUtils.isNotBlank(have)){
                throw new ApiRuntimeException(InfoCode.AUTH_FAIL ,"禁止重复提交");
            }
            guavaCache.valueSetAndExpire(key , "NX" ,5 ,TimeUnit.SECONDS);
            String openId = jsonObject.getString("openid");
            UserDto user = userService.findByOpenId(openId);
            if(user == null){
                user = userService.register(nickName ,gender, avatarUrl ,inviteCode ,openId);
            }
            if(user == null){
                throw new ApiRuntimeException(InfoCode.AUTH_FAIL ,"授权异常");
            }
            String k = WechaConstant.G_W_UID +  user.getId();
            String token = UUID.randomUUID().toString();
            guavaCache.valueSetAndExpire(k , token ,tokenExpire ,TimeUnit.SECONDS);

            String ck = WechaConstant.S_C_UID +  user.getId();
            guavaCache.valueSetAndExpire(ck , "true" ,1 ,TimeUnit.HOURS);
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
            if(guavaCache.hasKey(key)){
                guavaCache.delete(key);
            }
        }
    }



    @PostMapping("/registerByName")
    public Object registerByEmail(@RequestParam(value = "code") String code,
                                  @RequestParam(value = "msgId") String msgId,
                                  @RequestParam(value = "name") String name,
                                  @RequestParam(value = "pwd") String pwd){

        try {
            valiCode(msgId, code);
            valiName(name);
            boolean flag = userService.reportByName(name);
            if(flag){
                throw new ApiRuntimeException(InfoCode.EMAIL_HAS_REGIEST);
            }
            String passwd = StringUtil.getPwd(name,pwd);
            User user = userService.registerByName(name, passwd);

            String k = Constant.G_W_UID +  user.getId();
            String token =  MD5.getMessageDigest(k);
            guavaCache.valueSetAndExpire(k , token ,tokenExpire ,TimeUnit.SECONDS);
            return new HashMap<String, Object>(3) {
                {
                    put("uid", user.getId());
                    put("token", token);
                    put("uname", user.getName());
                }
            };
        } catch (Exception e) {
            logger.error("用户注册失败 e =" ,e);
            throw new ApiRuntimeException(InfoCode.FAIL, e.getMessage());
        } finally {
            if(guavaCache.hasKey(msgId)&&!isTest){
                guavaCache.delete(msgId);
            }
        }
    }

    @PostMapping("/loginByName")
    public Object login(@RequestParam(value = "code" ) String code,
                        @RequestParam(value = "msgId" ) String msgId,
                        @RequestParam(value = "name" ) String name,
                        @RequestParam(value = "pwd" ) String pwd){

        try {
            valiCode( msgId, code);
            valiName( name);
            User user = userService.findByName(name);
            if(null == user){
                throw new ApiRuntimeException(InfoCode.NOT_USER);
            }
            String passwd = StringUtil.getPwd(name,pwd);
            if(!passwd.equals(user.getPwd())){
                throw new ApiRuntimeException(InfoCode.AUTH_FAIL ,"密码不正确");
            }
            String k = Constant.G_W_UID +  user.getId();
            String token =  MD5.getMessageDigest(k);
            guavaCache.valueSetAndExpire(k , token ,tokenExpire ,TimeUnit.SECONDS);
            return new HashMap<String, Object>(3) {
                {
                    put("uid", user.getId());
                    put("token", token);
                    put("uname", user.getName());
                }
            };
        } catch (Exception e) {
            logger.error("用户登陆失败 e =" ,e);
            throw new ApiRuntimeException(InfoCode.FAIL, e.getMessage());
        } finally {
            if(guavaCache.hasKey(msgId)&&!isTest){
                guavaCache.delete(msgId);
            }
        }
    }


    @PostMapping("/registerByEmail")
    public Object registerByEmail(@RequestParam(value = "code" ) String code,
                                  @RequestParam(value = "msgId" ) String msgId,
                                  @RequestParam(value = "email" ) String email){

        try {
            valiCode( msgId, code);

            String email1Code = "email";//sendEmail(email);
            String token =  MD5.getMessageDigest(Constant.G_W_UID +  email1Code);
            guavaCache.valueSetAndExpire(token , email1Code+"|"+email ,360 ,TimeUnit.SECONDS);
            return new HashMap<String, Object>(2) {
                {
                    put("emailId", token);
                    put("email1Code", email1Code);
                }
            };
        } catch (Exception e) {
            logger.error("用户注册失败 e =" ,e);
            throw new ApiRuntimeException(InfoCode.FAIL, e.getMessage());
        } finally {
            if(guavaCache.hasKey(msgId)&&!isTest){
                guavaCache.delete(msgId);
            }
        }
    }


    @PostMapping("/loginByEmail")
    public Object loginByEmail(@RequestParam(value = "emailId" ) String emailId,
                               @RequestParam(value = "emailCode" ) String emailCode){

        try {
            Object email = guavaCache.valueGet(emailId);
            if(null == email || StringUtils.isBlank(email.toString())){
                throw new ApiRuntimeException(InfoCode.AUTH_FAIL ,"邮箱验证码已过期");
            }
            String[] split = email.toString().split("\\|");
            if(!split[0].equals(emailCode)){
                throw new ApiRuntimeException(InfoCode.AUTH_FAIL ,"邮箱验证码不正确");
            }
            User user = userService.findByEmail(split[1]);
            if(null == user){
                user = userService.registerByEmail(split[1]);
            }
            String k = Constant.G_W_UID +  user.getId();
            String token =  MD5.getMessageDigest(k);
            guavaCache.valueSetAndExpire(k , token ,360 ,TimeUnit.SECONDS);

            HashMap<String, Object> map = new HashMap<>();
            map.put("uid", user.getId());
            map.put("token", token);
            map.put("uname", user.getName());
            return map;
        } catch (Exception e) {
            logger.error("用户注册失败 e =" ,e);
            throw new ApiRuntimeException(InfoCode.FAIL, e.getMessage());
        } finally {
            if(guavaCache.hasKey(emailId)&&!isTest){
                guavaCache.delete(emailId);
            }
        }
    }

    String sendEmail(String from){
        String subject = "IXUXIE";
        String rundomNum = StringUtil.getRundomNum();
        String content = "您好，您的注册码是"+rundomNum+"。";
        emailService.send(from,subject,content);
        return rundomNum;
    }

    void valiCode(String msgId,String code){
        Object MSG = guavaCache.valueGet(msgId);
        if (null==MSG||StringUtils.isBlank(MSG.toString())){
            throw new ApiRuntimeException(InfoCode.AUTH_FAIL ,"验证码已过期");
        }
        if(!MSG.toString().equals(code)){
            throw new ApiRuntimeException(InfoCode.AUTH_FAIL ,"验证码不准确");
        }
    }

    void valiName(String name){
        if(StringUtils.isEmpty(name) || "大哥".equals(name)){
            throw new ApiRuntimeException(InfoCode.NOT_USER);
        }
    }

}
