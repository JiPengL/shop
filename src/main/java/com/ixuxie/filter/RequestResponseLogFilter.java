package com.ixuxie.filter;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @date 2019/12/20 15:42
 */
@Component
public class RequestResponseLogFilter extends OncePerRequestFilter implements Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLogFilter.class);

    private int order = Ordered.LOWEST_PRECEDENCE - 8;

    public static final String SPLIT_STRING_M = "=";

    public static final String SPLIT_STRING_DOT = ",";

    private static final AtomicLong atomicLong = new AtomicLong(1);

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        Long traceId = atomicLong.getAndIncrement();
     //   RpcContext.getContext().setAttachment("traceId", traceId);
        ContentCachingRequestWrapper wrapperRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrapperResponse = new ContentCachingResponseWrapper(response);
        String responseBodyStr = null;
        try {
            getRequestInfo(wrapperRequest, traceId);
            filterChain.doFilter(wrapperRequest, wrapperResponse);
            responseBodyStr = getResponseBody(wrapperResponse);
            wrapperResponse.copyBodyToResponse();
        }catch (Exception e){
            logger.error("",e);
        }
        logger.info("response[traceId={}, url={}, time={},status={},body={}]",traceId,request.getRequestURL(), (System.currentTimeMillis()-start), wrapperResponse.getStatus(), responseBodyStr);
    }

    /**
     * 打印请求信息
     * @param requestWrapper
     */
    private void getRequestInfo(ContentCachingRequestWrapper requestWrapper, Long traceId) {
        try {
            Map<String,Object> rootNode = Maps.newHashMap();

            rootNode.put("traceId", traceId);
            rootNode.put("method", requestWrapper.getMethod());
            rootNode.put("uri", requestWrapper.getRequestURI());
            rootNode.put("clientIp", requestWrapper.getRemoteAddr());
            rootNode.put("body", requestWrapper.getParameterMap());
            logger.info("request[{}]", JSON.toJSONString(rootNode));
        } catch (Exception e) {
            logger.error("输出请求日志异常.", e);
        }
    }

    /**
     * 打印响应参数
     * @param response
     */
    private String getResponseBody(ContentCachingResponseWrapper response) {
        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if(wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if(buf.length > 0) {
                String responseStr;
                try {
                    responseStr = new String(buf, "utf-8").replaceAll("[\n|\r|\t]", "");
                } catch (UnsupportedEncodingException e) {
                    responseStr = "[unknown]";
                }
                return responseStr;
            }
        }
        return "";
    }

    /**
     * 获取请求头信息
     * @param request
     * @return
     */
    private Map<String, Object> getRequestHeaders(HttpServletRequest request) {
        Map<String, Object> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;
    }

    /**
     * 获取响应头信息
     * @param response
     * @return
     */
    private Map<String, Object> getResponseHeaders(ContentCachingResponseWrapper response) {
        Map<String, Object> headers = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            headers.put(headerName, response.getHeader(headerName));
        }
        return headers;
    }

    /**
     * 打印请求参数
     * @param request
     */
    private String getRequestBody(ContentCachingRequestWrapper request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if(wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if(buf.length > 0) {
                String requestStr;
                try {
                    requestStr = new String(buf, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    requestStr = "[unknown]";
                }
                return requestStr.replaceAll("\\n","");
            }
        }
        return "";
    }

    /**
     * 获取请求地址上的参数
     * @param request
     * @return
     */
    public static String getRequestParams(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        Enumeration<String> enu = request.getParameterNames();
        //获取请求参数
        while (enu.hasMoreElements()) {
            String name = enu.nextElement();
            sb.append(name + SPLIT_STRING_M).append(request.getParameter(name));
            if(enu.hasMoreElements()) {
                sb.append(SPLIT_STRING_DOT);
            }
        }
        return sb.toString();
    }
}
