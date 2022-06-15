package com.sbt.config;

import lombok.Data;

@Data
public class SssoConfig {

    /**
     * 当前环境 local,daily,gray,online
     */
    private String env = "local";

    /**
     * 是否单点登录 默认 true 是
     */
    private Boolean singleLogin = true;

    /**
     * 系统名称，用于多系统区分，不传 不区分多系统 如果多系统，黑名单也区分多系统(可配置是否区分)。用户在Redis中的存储也区分多系统
     */
    private String systemName = "";

    /**
     * 如果设置了多系统，黑名单 是否多系统，默认false
     */
    private Boolean mulBlack = false;

    /**
     * 带星号的当做正则编译处理，需要登陆才能访问 多个用 , 号分隔
     */
    private String urlPattern = "";

    /**
     * 不需要登陆就可以访问，支持正则，多个用 , 号分隔
     */
    private String notPattern = "";

    /**
     * Domain 名称，不配置，从浏览器url自动获取
     */
    private String domainName;

    /**
     * 所有请求都拦截 默认true 全部拦截，但是notPattern 中的不拦截，
     * 如果是false，则只拦截 urlPattern 中，notPattern 中的不拦截
     */
    private Boolean allHandle = true;

    /**
     * BlowFish 加密解密秘钥，不配使用默认值，各系统可单独配置
     */
    private String encryptKey;

    /**
     * IP检测，两次请求的IP不一致，下线用户
     */
    private Boolean ipCheck = false;

    /**
     * 是否自动续约，即自动续订Cookie有效时长，默认false
     */
    private Boolean autoRenewal = false;

}
