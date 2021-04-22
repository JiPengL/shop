package com.ixuxie.utils;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(OkHttpUtil.class);

    private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    private static OkHttpUtil INSTANCE;

    private static OkHttpClient okHttpClient;
    private static class OkHttpUtilHolder {
        private static final OkHttpUtil INSTANCE = new OkHttpUtil();
    }

    public static OkHttpUtil getIntance() {
        return OkHttpUtilHolder.INSTANCE;
    }


    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";

    private OkHttpUtil(){
        okHttpClient = new OkHttpClient().newBuilder().
                connectTimeout(10, TimeUnit.SECONDS).
                readTimeout(20, TimeUnit.SECONDS).
                connectionPool(new ConnectionPool(Runtime.getRuntime().availableProcessors()*4,5L, TimeUnit.MINUTES)).build();
    }



    public static void main(String [] ary){
        Map<String,Object> map = new HashMap<>();
        map.put("id",1);
        String diff = OkHttpUtil.getIntance().get("http://192.168.12.152:9090/api/v1/product/detail",map);
        System.out.println(diff);
    }


    public String postJson(String url, String json) {
        return this.postJson(url ,json ,null);
    }

    public String post(String url, Map<String, ?> params) {
        return this.post(url,params,null);
    }


    public String get(String url) {
        return this.get(url,null,null);
    }

    public String get(String url, Map<String, Object> params) {
        return this.get(url,params,null);
    }

    public ResponseBody getBody(String url) {
        return this.getResponseBody(url,null,null);
    }

    public ResponseBody getBody(String url, Map<String, Object> params) {
        return this.getResponseBody(url,params,null);
    }


    public String post(String url, Map<String, ?> params, Map<String, String> headers) {
        String res = null;
        Response response = null;
        try {
            FormBody.Builder builder = new FormBody.Builder();
            if (params != null) {
                for (Map.Entry<String, ?> entry : params.entrySet()) {
                    if (entry != null) {
                        builder.add(entry.getKey(), entry.getValue().toString());
                    }
                }
            }
            Request.Builder header = builderHeader(headers);
            Request request = header.url(url).post(builder.build()).build();
            response = okHttpClient.newCall(request).execute();
            if(response !=null && response.body()!= null){
                res = response.body().string();
            }
        }catch (Exception e){
            logger.error("OKhttp请求异常",e);
        }finally {
            if(response != null){
                response.close();
            }
        }
        return res;
    }



    public String postJson(String url, String json , Map<String, String> headers) {
        String res = null;
        Response response = null;
        try {
            Request.Builder header = builderHeader(headers);
            Request request = header.url(url).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json)).build();
            response = okHttpClient.newCall(request).execute();
            if(response !=null && response.body()!= null){
                res = response.body().string();
            }
        }catch (Exception e){
            logger.error("OKhttp请求异常",e);
        }finally {
            if(response != null){
                response.close();
            }
        }
        return res;
    }



    public String get(String url, Map<String, Object> params ,Map<String, String> headers) {
        String res = null;
        Response response = null;
        try {
            if (params != null) {
                String paramStr = "";
                for (String key : params.keySet()) {
                    if (!paramStr.isEmpty()) {
                        paramStr += '&';
                    }
                    if (null == params.get(key)) {
                        paramStr += key + '=';
                    } else {
                        try {
                            paramStr += key + '=' + URLEncoder.encode(params.get(key) + "", CHARSET_UTF8.toString());
                        } catch (UnsupportedEncodingException e) {
                            logger.error("key=" + key + "," + params.get(key) + " 错误", e);
                        }
                    }
                }
                if (url.indexOf('?') > 0) {
                    url += '&' + paramStr;
                } else {
                    url += '?' + paramStr;
                }
            }
            Request.Builder header = builderHeader(headers);
            Request request = header.url(url).build();
            response = okHttpClient.newCall(request).execute();
            if(response !=null && response.body()!= null){
                res = response.body().string();
            }
        }catch (Exception e){
            logger.error("OKhttp请求异常",e);
        }finally {
            if(response != null){
                response.close();
            }
        }
        return res;
    }

    public ResponseBody getResponseBody(String url, Map<String, Object> params ,Map<String, String> headers) {
        ResponseBody res = null;
        Response response = null;
        try {
            if (params != null) {
                String paramStr = "";
                for (String key : params.keySet()) {
                    if (!paramStr.isEmpty()) {
                        paramStr += '&';
                    }
                    if (null == params.get(key)) {
                        paramStr += key + '=';
                    } else {
                        try {
                            paramStr += key + '=' + URLEncoder.encode(params.get(key) + "", CHARSET_UTF8.toString());
                        } catch (UnsupportedEncodingException e) {
                            logger.error("key=" + key + "," + params.get(key) + " 错误", e);
                        }
                    }
                }
                if (url.indexOf('?') > 0) {
                    url += '&' + paramStr;
                } else {
                    url += '?' + paramStr;
                }
            }
            Request.Builder header = builderHeader(headers);
            Request request = header.url(url).build();
            response = okHttpClient.newCall(request).execute();
            if(response !=null && response.body()!= null){
                res = response.body();
            }
        }catch (Exception e){
            logger.error("OKhttp请求异常",e);
        }finally {
            if(response != null){
                response.close();
            }
        }
        return res;
    }


    /**
     * 组装header
     * @param headers
     * @return
     */
    private Request.Builder builderHeader(Map<String, String> headers){
        Request.Builder header = new Request.Builder();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (entry != null) {
                    header.addHeader(entry.getKey(), entry.getValue());
                }
            }
        }
        return header;
    }




}
