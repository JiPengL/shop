package com.ixuxie.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sh_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1136926278128056705L;

    private Long id;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 性别，0男，1女
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String eamil;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 安全等级（二进制），1默认，2，授权手机号，4绑定邮箱
     */
    private Integer safetyLevel;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 1正常，2禁用
     */
    private Integer status;

    /**
     * 累计积分
     */
    private Integer totalIntegration;

    /**
     * 被邀请码
     */
    private String inviteCode;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;

}