package com.ixuxie.utils;


public enum InfoCode {

    SUCCESS(0,"成功"),
    FAIL(1000,"失败"),

    NOT_LOGIN(1001,"用户登陆过期！"),
    AUTH_FAIL(1002,"用户授权失败！"),
    AUTH_PHONE_FAIL(1003,"用户授权手机号失败！"),

    EMAIL_SEND_FAIL(1004,"邮件发送失败！"),
    EMAIL_HAS_REGIEST(1005,"邮箱已注册！"),
    NAME_HAS_REGIEST(1006,"账号名称已注册！"),
    PWD_ERROR(1007,"密码不正确！"),

    NOT_USER(1001,"用户不存在！"),

    ERROR(1099,"非法操作！"),









    PARAM_ERROR(2000,"请求参数缺失"),

    PRODUCT_NOT_EXISTS(2004,"商品不存在!"),
    PRODUCT_NOT_ENOUGH(2005,"商品库存不足!"),

    CHECK_POOL(2009,"当前观察者连接不支持，请联系管理员"),

    POOL_ERROR(2010,"未采集到数据，请检查观察者连接是否正确"),

    CREATE_ORDER_ERROR(3000,"请勿重复创建订单"),

    PAY_ERROR(3001,"请勿重复支付订单"),

    RESULT_ERROR(3002,"未查询到结果"),

    DATA_ERROR(3003,"数据异常"),

    CART_REFULS(3009,"购物车下单商品库存不足"),

    APPLY_CANCEL_ERROR(3003,"当前状态不允许撤销"),

    URL_NOT_EXIST(4000,"请求URL不存在"),

    UNSUPPORTED(4001,"不支持的请求方式"),

    SERVICE_UNAVAILABLE(5000,"抱歉，服务器开小差了请稍后重试~");


    private int status;

    private String msg;

    InfoCode(int status, String msg){
        this.status = status;
        this.msg = msg;
    }

    public static InfoCode turnInfoCode(int status){
        for (InfoCode e : values()) {
            if (e.getStatus() == status) {
                return e;
            }
        }
        return InfoCode.FAIL;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
