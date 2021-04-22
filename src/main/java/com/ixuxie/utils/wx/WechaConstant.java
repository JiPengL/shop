package com.ixuxie.utils.wx;

public class WechaConstant {


    public static final String G_W_UID = "shop_uid_";

    public static final String A_W_UID = "auth_code_";

    public static final String S_C_UID = "show_coupon_uid_";


    /**
     * 支付常量
     */
    public static String FEETYPE = "CNY";


    public static String JSAPI = "JSAPI";


    public static String BODY = "商城商品支付";


    public static String ORDER_PAY_LOCK_KEY = "order_pay_lock_key_%s";

    public static String ORDER_CREATE_LOCK_KEY = "order_create_lock_key_%s";


    /**
     * 支付结果
     */
    public static class ResultCode {

        public static String SUCCESS = "SUCCESS";

        public static String FAIL = "SUCCESS";

    }


    /**
     * 优惠券失败原因
     */
    public static class CouponReason {


        public static String COUPON_REASON = "优惠券不合理，优惠金额不能大于满减金额";

        public static String AMOUNT_MAX_REASON = "优惠金额不能大于等于商品金额";

        public static String AMOUNT_REASON = "商品金额还差%s元可用优惠券";

        public static String TIME_REASON = "使用时间超出优惠券有效期%s至%s";

    }

}