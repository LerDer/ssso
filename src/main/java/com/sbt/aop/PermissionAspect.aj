package com.sbt.aop;

import com.sbt.annotation.Permission;
import com.sbt.domain.SsoRole;
import com.sbt.domain.SsoUser;
import com.sbt.util.HttpContextUtils;
import com.sbt.util.SessionUtil;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 权限拦截
 *
 * @author lww
 */
@Aspect
@Slf4j
@Component
public class PermissionAspect {

    /**
     * 切面
     */
    @Pointcut("@annotation(com.sbt.annotation.Permission)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Permission permissionAnnotation = method.getAnnotation(Permission.class);
        String key = permissionAnnotation.value();
        String url = permissionAnnotation.url();

        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        //获取当前登陆用户
        SsoUser user = SessionUtil.getUser();
        if (user == null) {
            throw new IllegalArgumentException("获取登录用户失败");
        }
        SsoRole role = user.getRole();
        if (role == null) {
            throw new IllegalArgumentException("获取用户角色失败");
        }
        List<com.sbt.domain.Permission> permissions = role.getPermissions();
        if (permissions.size() <= 0) {
            throw new IllegalArgumentException("获取用户权限失败");
        }
        Set<String> keySet = permissions.stream().map(com.sbt.domain.Permission::getPerKey).collect(Collectors.toSet());
        Set<String> urlSet = permissions.stream().map(com.sbt.domain.Permission::getRquestUrl).collect(Collectors.toSet());
        if ((StringUtils.isNotBlank(key) && keySet.contains(key)) ||
                (StringUtils.isNotBlank(url) && urlSet.contains(url))) {
            return point.proceed();
        } else {
            throw new IllegalArgumentException("无此权限，请联系管理员...");
        }
    }

}
