package com.sbt.domain;

import java.util.Date;
import lombok.Data;

/**
 * @author lww
 * @date 2020-08-19 10:01
 */
@Data
public class SsoUser {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * EC的memberId
     */
    private Long memberId;

    /**
     * EC  member_user_id
     */
    private Long ecUserId;

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
     * ip 地址
     */
    private String ipAddress;

    /**
     * 当前登录版本号，用来单点登录
     */
    private String version;

    private String env;

}
