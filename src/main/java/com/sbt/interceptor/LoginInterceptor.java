package com.sbt.interceptor;

import com.sbt.config.SssoProperties;
import com.sbt.domain.SsoUser;
import com.sbt.util.CommonUtil;
import com.sbt.util.IPUtils;
import com.sbt.util.RedisUtil;
import com.sbt.util.SessionUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author lww
 * @date 2019-04-18 4:05 PM
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private SssoProperties sssoProperties;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 带星号的当做正则编译处理，需要登陆才能访问
        String[] urlPattern = sssoProperties.getUrlPattern().split(",");
        //不需要登陆就可以访问，支持正则
        List<String> notPattern = new ArrayList<>();
        notPattern.add(".+swagger.+");
        notPattern.add("/csrf");
        notPattern.add("/error");
        notPattern.add("/favicon.ico");
        notPattern.add("/");
        notPattern.addAll(Arrays.asList(sssoProperties.getNotPattern().split(",")));
        Boolean allHandle = sssoProperties.getAllHandle();

        String ipAddr = IPUtils.getIpAddr(request);
        String blackIp = redisUtil.getValue(SessionUtil.getBlackIpKey(ipAddr));
        CommonUtil.isTrue(StringUtils.isBlank(blackIp), "IP已被限制访问！");
        String servletPath = request.getServletPath();
        //log.info("LoginInterceptor_preHandle_servletPath:{}", servletPath);
        boolean handle = true;
        //全拦截模式,放过部分url
        if (allHandle) {
            for (String not : notPattern) {
                if (servletPath.matches(not)) {
                    handle = false;
                    break;
                }
            }
            if (handle) {
                check(ipAddr);
            }
            return true;
        }
        //不拦截模式,拦截指定url
        for (String pattern : urlPattern) {
            //正则里可能包含不需要登录就可以访问的接口，所以过滤是过滤掉在正则里但是可以不登录访问的
            if (servletPath.matches(pattern) && !notPattern.contains(servletPath)) {
                check(ipAddr);
            }
        }
        return true;
    }

    private void check(String ipAddr) {
        log.info("LoginInterceptor_check_ipAddr:{}", ipAddr);
        SsoUser user = SessionUtil.getUser();
        CommonUtil.isTrue(user != null, "登录失效，请重新登录！");
        String blackId = redisUtil.getValue(SessionUtil.getBlackIdKey(user.getUserId()));
        CommonUtil.isTrue(StringUtils.isBlank(blackId), "用户已被限制访问！");
        String ipAddress = user.getIpAddress();
        log.info("LoginInterceptor_check_ipAddress:{}", ipAddress);
        //刷新 Cookie 有效时长，设置自动续约才有
        SessionUtil.setUser(user, SessionUtil.autoRenewal);
    }

}
