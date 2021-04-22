package com.ixuxie.filter;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.ixuxie.utils.InfoCode;
import com.ixuxie.utils.RestResp;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @date 2019/6/3 17:00
 */
@ControllerAdvice
public class GatewayResponseBodyAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (o instanceof RestResp){
            return o;
        }
        if ( o instanceof Boolean){
            Boolean value = (Boolean) o;
            Map<String,Integer> result = Maps.newHashMap();
            result.put("status",value ? 1:0);
            return new RestResp(InfoCode.SUCCESS,result);
        }
        if(isPathNot(o)){
            return new RestResp(InfoCode.URL_NOT_EXIST,"地址不存在");
        }
        return new RestResp(InfoCode.SUCCESS,o);
    }


    /**
     * 是否地址不存在
     * @param o
     * @return
     */
    private boolean isPathNot(Object o){
        try {

            if (o instanceof JSONObject){
                return false;
            }

            if (o instanceof Map){
                HashMap<String,Object> map = (HashMap<String,Object>)o;
                if(map !=null && map.containsKey("status") && "404".equals(map.get("status").toString())){
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
