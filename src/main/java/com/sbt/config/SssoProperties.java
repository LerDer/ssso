package com.sbt.config;

import com.sbt.util.SpringBeanFactoryUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lww
 */
@ConfigurationProperties(prefix = "sbt.sso")
public class SssoProperties {

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

    private SssoConfig config;

    public SssoConfig getConfig() {
        return config;
    }

    public void setConfig(SssoConfig config) {
        this.config = config;
    }

    public Boolean getAutoRenewal() {
        return autoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }

    public Boolean getIpCheck() {
        return ipCheck;
    }

    public void setIpCheck(Boolean ipCheck) {
        this.ipCheck = ipCheck;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Boolean getMulBlack() {
        return mulBlack;
    }

    public void setMulBlack(Boolean mulBlack) {
        this.mulBlack = mulBlack;
    }

    public Boolean getAllHandle() {
        return allHandle;
    }

    public void setAllHandle(Boolean allHandle) {
        this.allHandle = allHandle;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getNotPattern() {
        return notPattern;
    }

    public void setNotPattern(String notPattern) {
        this.notPattern = notPattern;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public SssoProperties() {
    }

    public Boolean getSingleLogin() {
        return singleLogin;
    }

    public void setSingleLogin(Boolean singleLogin) {
        this.singleLogin = singleLogin;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public boolean isLocal() {
        return "local".equals(env);
    }

    public boolean isDaily() {
        return "daily".equals(env);
    }

    public boolean isDailyOrLocal() {
        return isDaily() || isLocal();
    }

    public boolean isGray() {
        return "gray".equals(env);
    }

    public boolean isOnline() {
        return "online".equals(env);
    }

    public int getEnvId() {
        if (isLocal()) {
            return 0;
        } else if (isDaily()) {
            return 1;
        } else if (isGray()) {
            return 2;
        } else if (isOnline()) {
            return 3;
        } else {
            throw new RuntimeException("impossible");
        }
    }

    public void init() {
        SssoConfig config = SpringBeanFactoryUtils.getApplicationContext().getBean(SssoConfig.class);
        if (config == null) {
            return;
        }
        if (config.getAutoRenewal() != null) {
            this.setAutoRenewal(config.getAutoRenewal());
        }
        if (config.getIpCheck() != null) {
            this.setIpCheck(config.getIpCheck());
        }
        if (config.getEncryptKey() != null) {
            this.setEncryptKey(config.getEncryptKey());
        }
        if (config.getDomainName() != null) {
            this.setDomainName(config.getDomainName());
        }
        if (config.getMulBlack() != null) {
            this.setMulBlack(config.getMulBlack());
        }
        if (config.getAllHandle() != null) {
            this.setAllHandle(config.getAllHandle());
        }
        if (config.getUrlPattern() != null) {
            this.setUrlPattern(config.getUrlPattern());
        }
        if (config.getNotPattern() != null) {
            this.setNotPattern(config.getNotPattern());
        }
        if (config.getSystemName() != null) {
            this.setSystemName(config.getSystemName());
        }
        if (config.getSingleLogin() != null) {
            this.setSingleLogin(config.getSingleLogin());
        }
        if (config.getEnv() != null) {
            this.setEnv(config.getEnv());
        }
    }
}
