package com.ixuxie.utils.wx;

import com.ixuxie.utils.MD5;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * <p>Title: 微信退款工具类</p>
 * <p>Description: 微信退款工具类，通过充值客户端的不同初始化不同的工具类，得到相应微信退款相关的appid和muchid</p>
 */
public class WechatUtil {
    private static final Logger logger = LoggerFactory.getLogger(WechatUtil.class);







    private static HttpPost buildPostParam(String mapToXml ,String url) {
        HttpPost httPost = new HttpPost(url);
        httPost.addHeader("Connection", "keep-alive");
        httPost.addHeader("Accept", "*/*");
        httPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httPost.addHeader("Host", "api.mch.weixin.qq.com");
        httPost.addHeader("X-Requested-With", "XMLHttpRequest");
        httPost.addHeader("Cache-Control", "max-age=0");
        httPost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
        httPost.setEntity(new StringEntity(mapToXml, "UTF-8"));
        return httPost;
    }

    /**
     * 支付交易ID
     */
    public static String getBundleId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String tradeno = dateFormat.format(new Date());
        String str = "000000" + (int) (Math.random() * 1000000);
        tradeno = tradeno + str.substring(str.length() - 6);
        return tradeno;
    }


    /**
     * 校验返回签名
     * @param params
     * @return
     */
    public static boolean verifySign(Map<String, Object> params, String sign){
        String notifySign = null;
        if(!params.isEmpty() && params.containsKey("sign")){
            notifySign = params.get("sign")+"";
            params.remove("sign");
        }
        String newSign = sign(params,sign);
        if(newSign.equalsIgnoreCase(notifySign)){
            return true;
        }
        return false;
    }

    /**
     * 方法描述：根据签名加密请求参数
     * 创建时间：2017年6月8日  上午11:28:52
     * 作者： xubo
     *
     * @param
     * @return
     */
    public static String sign(Map<String, Object> params, String paySignKey) {
        boolean encode = false;
        Set<String> keysSet = params.keySet();
        Object[] keys = keysSet.toArray();
        Arrays.sort(keys);
        StringBuffer temp = new StringBuffer();
        boolean first = true;
        for (Object key : keys) {
            if (first) {
                first = false;
            } else {
                temp.append("&");
            }
            temp.append(key).append("=");
            Object value = params.get(key);
            String valueString = "";
            if (null != value) {
                valueString = value.toString();
            }
            if (encode) {
                try {
                    temp.append(URLEncoder.encode(valueString, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                temp.append(valueString);
            }
        }
        temp.append("&key=");
        temp.append(paySignKey);
        String packageSign = MD5.getMessageDigest(temp.toString());
        return packageSign;
    }


    /**
     * 请求，只请求一次，不做重试
     *
     * @param url
     * @param data
     * @return
     * @throws Exception
     */
    public static String requestOnce(final String url, String data) throws Exception {

        logger.info("微信支付，请求地址【{}】，请求参数【{}】",url,data);
        BasicHttpClientConnectionManager connManager;
        connManager = new BasicHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", SSLConnectionSocketFactory.getSocketFactory())
                        .build(),
                null,
                null,
                null
        );

        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connManager)
                .build();

        HttpPost httpPost = new HttpPost(url);

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(10000).build();

        httpPost.setConfig(requestConfig);
        StringEntity postEntity = new StringEntity(data, "UTF-8");
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.addHeader("User-Agent", "wxpay sdk java v1.0 ");
        httpPost.setEntity(postEntity);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        String reusltObj = EntityUtils.toString(httpEntity, "UTF-8");
        logger.info("请求结果:" + reusltObj);
        return reusltObj;

    }
}
