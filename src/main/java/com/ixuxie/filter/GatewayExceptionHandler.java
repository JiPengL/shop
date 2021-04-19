package com.ixuxie.filter;

import com.ixuxie.exception.ApiRuntimeException;
import com.ixuxie.utils.InfoCode;
import com.ixuxie.utils.RestResp;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * @author wuhui
 * @date 2019/6/3 17:21
 */
@ControllerAdvice
public class GatewayExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GatewayExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(ApiRuntimeException.class)
    public Object apiException(ApiRuntimeException e){
        logger.error("",e);
        if (StringUtils.isNotBlank(e.getMsg())){
            return RestResp.build(e.getInfoCode().getStatus(),e.getMsg());
        }
        return RestResp.build(e.getInfoCode());
    }


    @ResponseBody
    @ExceptionHandler(NoHandlerFoundException.class)
    public Object excetion(NoHandlerFoundException e){
        logger.error("",e);
        return new RestResp(InfoCode.URL_NOT_EXIST,null);
    }
    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object excetion(HttpRequestMethodNotSupportedException e){
        logger.error("",e);
        return new RestResp(InfoCode.UNSUPPORTED,null);
    }

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public Object throwable(Throwable e){
        logger.error("",e);
        return new RestResp(InfoCode.SERVICE_UNAVAILABLE,null);
    }

    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object throwable(MissingServletRequestParameterException e){
        logger.error("",e);
        return new RestResp(InfoCode.PARAM_ERROR,e.getParameterName());
    }
}
