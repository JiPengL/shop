package com.ixuxie.filter;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.ixuxie.annotation.Auth;
import com.ixuxie.config.cache.GuavaCache;
import com.ixuxie.exception.ApiRuntimeException;
import com.ixuxie.utils.InfoCode;
import com.ixuxie.utils.wx.WechaConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @date 2019/6/4 9:42
 */
@Component
public class AuthHandlerInterceptor implements HandlerInterceptor {


    @NacosValue(value = "${abacus.token.expire:604800}")
    private Long tokenExpire ;

    private static final String TOKEN = "token";

    private static final String UID = "uid";


    @Autowired
    private GuavaCache guavaCache;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        if (handler instanceof HandlerMethod){
            HandlerMethod hm = (HandlerMethod) handler;
            Cookie[] cookies = request.getCookies();
            String uid = "";
            String token = "";
            if (cookies != null && cookies.length >0) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equalsIgnoreCase(TOKEN)) {
                        token = cookie.getValue();
                        continue;
                    }
                    if (cookie.getName().equalsIgnoreCase(UID)) {
                        uid = cookie.getValue();
                        continue;
                    }
                }
            }
            Auth auth = AnnotationUtils.findAnnotation(hm.getMethod(), Auth.class);
            if (auth != null && auth.login()){
                if (!isLogin(uid,token)){
                    throw new ApiRuntimeException(InfoCode.NOT_LOGIN);
                }
            }
            auth = AnnotationUtils.findAnnotation(hm.getBeanType(), Auth.class);
            if (auth != null && auth.login()){
                if (!isLogin(uid,token)){
                    throw new ApiRuntimeException(InfoCode.NOT_LOGIN);
                }
            }
        }
        return true;
    }

    private boolean isLogin(String uid,String token){
        if (StringUtils.isBlank(uid) || StringUtils.isBlank(token)){
            return false;
        }
        String k = WechaConstant.G_W_UID + uid;
        String ctoken = guavaCache.valueGet(k).toString();
        if (StringUtils.isBlank(ctoken)){
            return false;
        }
        if (ctoken.equalsIgnoreCase(token)){
            guavaCache.valueSetAndExpire(k,ctoken,tokenExpire, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }
}
