package com.ixuxie.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
@TableName("sh_login_info")
public class LoginInfo implements Serializable {
    private static final long serialVersionUID = 2982327481637544333L;
    /**
     * id：md5生成 （uid + login_type）
     */
    private String id;

    /**
     * uid
     */
    private Long uid;

    /**
     * 登陆账号
     */
    private String loginName;

    /**
     * 1手机号，2邮箱,3小程序
     */
    private Integer loginType;

    /**
     * 密码;open_id
     */
    private String loginToken;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;
}