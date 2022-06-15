package com.sbt.util;

import com.alibaba.fastjson.JSONObject;
import com.sbt.config.SssoProperties;
import com.sbt.domain.SsoUser;
import com.sbt.util.blowfish.Blowfish;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lww
 * @date 2020-08-19 10:18
 */
@Configuration
@EnableConfigurationProperties(SssoProperties.class)
public class SessionUtil implements InitializingBean {

    private static final String PRE = "SSO";

    /**
     * 黑名单 ip
     */
    private static final String BLACK_IP = "BLACK_IP";

    /**
     * 黑名单 userId
     */
    private static final String BLACK_ID = "BLACK_ID";

    private static String USER_IN_SESSION;

    private static String systemName;

    private static Boolean mulBlack = false;

    private static String domainName;

    private static Pattern pb = Pattern.compile("(?<=://).+(?=/)");

    private static final Integer DOT_SPLIT_NUM = 3;

    private static Boolean single = false;

    public static String ENCRYPT_KEY = "jl&*^$6#sad$%0)";

    /**
     * 当前系统在线用户，基于 redis 所以不是很准确，如果不直接调登出接口，人数可能出现多的
     */
    private static String ALL_USER_COUNT = "_ALL_USER_COUNT";

    private static Boolean ipCheck;

    public static Boolean autoRenewal;

    @Resource
    private SssoProperties confEnv;

    @Override
    public void afterPropertiesSet() {
        systemName = confEnv.getSystemName();
        mulBlack = confEnv.getMulBlack();
        domainName = confEnv.getDomainName();
        ipCheck = confEnv.getIpCheck();
        autoRenewal = confEnv.getAutoRenewal();
        ALL_USER_COUNT = systemName + ALL_USER_COUNT;
        if (confEnv.isDaily()) {
            USER_IN_SESSION = systemName + "_dk";
        } else if (confEnv.isGray()) {
            USER_IN_SESSION = systemName + "_gk";
        } else if (confEnv.isOnline()) {
            USER_IN_SESSION = systemName + "_ok";
        } else {
            USER_IN_SESSION = systemName + "_dk";
        }
        if (confEnv.getSingleLogin()) {
            single = true;
        }
        if (StringUtils.isNotBlank(confEnv.getEncryptKey())) {
            ENCRYPT_KEY = confEnv.getEncryptKey();
        }
    }

    public static Long getUserId() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        Cookie[] cookies = request.getCookies();
        CommonUtil.isTrue(cookies != null, "当前未登陆或登陆失效，获取cookie失败！");
        for (Cookie cookie : cookies) {
            if (USER_IN_SESSION.equalsIgnoreCase(cookie.getName())) {
                return getSsoUser(cookie).getUserId();
            }
        }
        return null;
    }

    public static Long getEcUserId() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        Cookie[] cookies = request.getCookies();
        CommonUtil.isTrue(cookies != null, "当前未登陆或登陆失效，获取cookie失败！");
        for (Cookie cookie : cookies) {
            if (USER_IN_SESSION.equalsIgnoreCase(cookie.getName())) {
                return getSsoUser(cookie).getEcUserId();
            }
        }
        return null;
    }

    public static Long getEcMemberId() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        Cookie[] cookies = request.getCookies();
        CommonUtil.isTrue(cookies != null, "当前未登陆或登陆失效，获取cookie失败！");
        for (Cookie cookie : cookies) {
            if (USER_IN_SESSION.equalsIgnoreCase(cookie.getName())) {
                return getSsoUser(cookie).getMemberId();
            }
        }
        return null;
    }

    public static String getName() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        Cookie[] cookies = request.getCookies();
        CommonUtil.isTrue(cookies != null, "当前未登陆或登陆失效，获取cookie失败！");
        for (Cookie cookie : cookies) {
            if (USER_IN_SESSION.equalsIgnoreCase(cookie.getName())) {
                return getSsoUser(cookie).getName();
            }
        }
        return null;
    }

    public static String getMobile() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        Cookie[] cookies = request.getCookies();
        CommonUtil.isTrue(cookies != null, "当前未登陆或登陆失效，获取cookie失败！");
        for (Cookie cookie : cookies) {
            if (USER_IN_SESSION.equalsIgnoreCase(cookie.getName())) {
                return getSsoUser(cookie).getMobile();
            }
        }
        return null;
    }

    public static Boolean isLogin() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        Cookie[] cookies = request.getCookies();
        CommonUtil.isTrue(cookies != null, "当前未登陆或登陆失效，获取cookie失败！");
        for (Cookie cookie : cookies) {
            if (USER_IN_SESSION.equalsIgnoreCase(cookie.getName())) {
                return getSsoUser(cookie) != null;
            }
        }
        return false;
    }

    public static SsoUser getUser() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        Cookie[] cookies = request.getCookies();
        CommonUtil.isTrue(cookies != null, "当前未登陆或登陆失效，获取cookie失败！");
        for (Cookie cookie : cookies) {
            if (USER_IN_SESSION.equalsIgnoreCase(cookie.getName())) {
                return getSsoUser(cookie);
            }
        }
        return null;
    }

    public static void setUser(SsoUser user, Boolean rememberMe) {
        setUser(user, rememberMe, false);
    }

    public static void setUser(SsoUser user, Boolean rememberMe, Boolean refreshVersion) {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        HttpServletResponse response = HttpContextUtils.getHttpServletResponse();
        user.setIpAddress(IPUtils.getIpAddr(request));
        user.setEnv(USER_IN_SESSION);
        String uuid = null;
        if (StringUtils.isBlank(user.getVersion())) {
            user.setRecentLoginTime(new Date());
        }
        if (refreshVersion || StringUtils.isBlank(user.getVersion())) {
            uuid = UUID.randomUUID().toString();
            user.setVersion(uuid);
        }
        user.setSystemName(systemName);
        RedisUtil redisUtil = SpringBeanFactoryUtils.getApplicationContext().getBean(RedisUtil.class);
        String key = PRE + user.getUserId() + systemName;
        String value = redisUtil.getValue(key);
        if (StringUtils.isBlank(value)) {
            redisUtil.incr(ALL_USER_COUNT);
            redisUtil.setValue(key, JSONObject.toJSONString(user), 7, TimeUnit.DAYS);
        } else {
            SsoUser ssoUser = JSONObject.parseObject(value, SsoUser.class);
            //小于1天重新写入
            if (ssoUser.getRecentLoginTime() == null || ssoUser.getRecentLoginTime().getTime() - System.currentTimeMillis() < (24 * 60 * 60 * 1000)) {
                redisUtil.setValue(key, JSONObject.toJSONString(user), 7, TimeUnit.DAYS);
            }
        }
        String encrypt = Blowfish.encrypt(key + "_" + System.currentTimeMillis() + "_" + uuid);
        //加密cookie
        Cookie cookie = new Cookie(USER_IN_SESSION, encrypt);
        //设置cookie的保存时间,=0删除,<0在浏览器关闭时删除
        if (rememberMe) {
            cookie.setMaxAge(60 * 60 * 24 * 7);
        } else {
            cookie.setMaxAge(-1);
        }
        if (StringUtils.isBlank(domainName)) {
            domainName = getDomainName(request);
        }
        //域名不同,但以baidu.com结尾,都可以共享cookie
        cookie.setDomain(domainName);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private static String getDomainName(HttpServletRequest request) {
        // http://localhost:8080/sso/user/login
        String url = request.getRequestURL().toString();
        Matcher matcher = pb.matcher(url);
        CommonUtil.isTrue(matcher.find(), "url未解析到域名！");
        String ym = matcher.group();
        ym = ym.substring(0, ym.indexOf("/"));
        String[] split = ym.split(".");

        StringBuilder sb = new StringBuilder();
        if (split.length >= DOT_SPLIT_NUM) {
            sb.append(".");
            for (int i = 0; i < split.length; i++) {
                if (i != 0) {
                    sb.append(split[i]).append(".");
                }
            }
            sb.substring(0, sb.length() - 1);
        } else {
            String[] split1 = ym.split(":");
            sb.append(split1[0]);
        }
        return sb.toString();
    }

    public static void loginOut() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        HttpServletResponse response = HttpContextUtils.getHttpServletResponse();
        RedisUtil redisUtil = SpringBeanFactoryUtils.getApplicationContext().getBean(RedisUtil.class);
        redisUtil.decr(ALL_USER_COUNT);
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (USER_IN_SESSION.equalsIgnoreCase(cookie.getName())) {
                redisUtil.remove(cookie.getValue());
            }
        }
        if (StringUtils.isBlank(domainName)) {
            domainName = getDomainName(request);
        }
        Cookie cookie = new Cookie(USER_IN_SESSION, "");
        //设置cookie的保存时间,=0删除,<0在浏览器关闭时删除
        cookie.setMaxAge(0);
        //域名不同,但以baidu.com结尾,都可以共享cookie
        cookie.setDomain(domainName);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private static SsoUser getSsoUser(Cookie cookie) {
        RedisUtil redisUtil = SpringBeanFactoryUtils.getApplicationContext().getBean(RedisUtil.class);
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        String value = cookie.getValue();
        String decrypt = Blowfish.decrypt(value);
        String[] s = decrypt.split("_");
        SsoUser user = redisUtil.getUser(s[0]);
        String ipAddr = IPUtils.getIpAddr(request);
        if (!user.getEnv().equalsIgnoreCase(cookie.getName())) {
            loginOut();
            CommonUtil.isTrue(false, "环境异常，强制下线！");
        }
        if (SessionUtil.ipCheck && !ipAddr.equalsIgnoreCase(user.getIpAddress())) {
            loginOut();
            CommonUtil.isTrue(false, "IP地址异常，强制下线！");
        }
        if (single && !user.getVersion().equalsIgnoreCase(s[2])) {
            loginOut();
            CommonUtil.isTrue(false, "您的账号已在其他地方登录，您已被下线！");
        }
        return user;
    }

    /**
     * 强制下线
     */
    public static void kickOut(Long userId) {
        String key = PRE + userId;
        RedisUtil redisUtil = SpringBeanFactoryUtils.getApplicationContext().getBean(RedisUtil.class);
        redisUtil.remove(key);
        redisUtil.decr(ALL_USER_COUNT);
    }

    public static void addBlackId(Long userId) {
        String key = getBlackIdKey(userId);
        RedisUtil redisUtil = SpringBeanFactoryUtils.getApplicationContext().getBean(RedisUtil.class);
        String value = redisUtil.getValue(key);
        if (value == null) {
            redisUtil.setValue(key, userId.toString(), 3, TimeUnit.DAYS);
        } else {
            redisUtil.setValue(key, userId.toString(), 7, TimeUnit.DAYS);
        }
        redisUtil.remove(PRE + userId);
    }

    public static void addBlackIp() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        RedisUtil redisUtil = SpringBeanFactoryUtils.getApplicationContext().getBean(RedisUtil.class);
        String ipAddr = IPUtils.getIpAddr(request);
        String key = getBlackIpKey(ipAddr);
        String blackIp = redisUtil.getValue(key);
        if (blackIp == null) {
            redisUtil.setValue(key, ipAddr, 3, TimeUnit.DAYS);
        } else {
            redisUtil.setValue(key, ipAddr, 7, TimeUnit.DAYS);
        }
        loginOut();
    }

    public static String getBlackIpKey(String ipAddr) {
        return PRE + "_" + (mulBlack ? systemName : "") + BLACK_IP + "_" + ipAddr;
    }

    public static String getBlackIdKey(Long userId) {
        return PRE + "_" + (mulBlack ? systemName : "") + BLACK_ID + "_" + userId;
    }

}
