package com.ixuxie.utils;

import com.alibaba.fastjson.JSON;
import com.ixuxie.utils.wx.WechatUtil;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

public class FormatAmountUtil {

    private static BigDecimal rate = new BigDecimal(100);

    /**
     *
     */
    public static String format(int amount){
        BigDecimal a =  BigDecimal.valueOf(amount).divide(rate , 2,BigDecimal.ROUND_HALF_UP);
        return a.toPlainString();
    }

    public static String formatToY(Integer amount){
        if(amount == null){
            return "0";
        }
        BigDecimal a =  BigDecimal.valueOf(amount).divide(rate , 2,BigDecimal.ROUND_HALF_UP);
        return a.stripTrailingZeros().toPlainString();
    }

    public static void main(String[] args) {

        Map<String, Object> params = new TreeMap<>();
        //小程序appid
        params.put("appid", "wxd929d33574685e62");
        //商户号
        params.put("mch_id", "1604843849");
        //商户订单号,以支付ID为准
        params.put("out_trade_no", "788063784166182912");
        //32位的随机字符串
        Long nonceStr = SnowflakeIdWorker.nextId();
        params.put("nonce_str", nonceStr);
        //支付金额（单位是分）
        params.put("total_fee", 1);
        //退款金额（单位是分）
        params.put("refund_fee", 1);
        //设置签名
        params.put("sign", WechatUtil.sign(params, "92bedc1b6ee181c8424922f1afd42c6f"));
        String xml = MapUtils.convertMap2Xml(params);
        try {
            String res = WechatUtil.requestOnce("https://api.mch.weixin.qq.com/secapi/pay/refund", xml);
            Map<String, Object> resMap = XmlUtil.xmlStrToMap(res);
            System.out.println(JSON.toJSONString(resMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
