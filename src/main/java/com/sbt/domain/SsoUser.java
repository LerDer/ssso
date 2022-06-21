package com.sbt.domain;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author lww
 * @date 2020-08-19 10:01
 */
@Data
public class SsoUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 身份证号
     */
    private String idcardNo;

    /**
     * 头像 url
     */
    private String avatar;

    /**
     * 用户状态
     */
    private Integer status;

    /**
     * 最近登录时间
     */
    private Date recentLoginTime;

    /**
     * 实名认证状态
     */
    private String systemName;

    /**
     * 不用设置,框架自动获取, ip 地址
     */
    private String ipAddress;

    /**
     * 不用设置,框架自动获取,当前登录版本号，用来单点登录
     */
    private String version;

    /**
     * 不用设置,框架自动获取
     */
    private String env;

    private SsoRole role;

}
