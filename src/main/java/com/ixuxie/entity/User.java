package com.ixuxie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sh_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1136926278128056705L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 昵称
     */
    private String name;

    /**
     * 昵称
     */
    private String pwd;

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
     * 被邀请码
     */
    private String inviteCode;


}