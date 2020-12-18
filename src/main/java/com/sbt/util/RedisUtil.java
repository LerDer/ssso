package com.sbt.util;

import com.alibaba.fastjson.JSONObject;
import com.sbt.domain.SsoUser;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * @author lww
 * @since 2020-04-11
 */
@Component
public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public SsoUser getUser(String key) {
        return JSONObject.parseObject(stringRedisTemplate.opsForValue().get(key), SsoUser.class);
    }

    public void remove(String key) {
        stringRedisTemplate.opsForValue().set(key, "", 1, TimeUnit.MILLISECONDS);
    }

    public void setValue(String key, String value, Integer expireTime) {
        stringRedisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
    }

    public void setValue(String key, String value, Integer expireTime, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, expireTime, unit);
    }

    public String getValue(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public Long incr(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    public Long incr(String key, Long value) {
        return stringRedisTemplate.opsForValue().increment(key, value);
    }

    public Long decr(String key) {
        return stringRedisTemplate.opsForValue().decrement(key);
    }

    public Long decr(String key, Long value) {
        return stringRedisTemplate.opsForValue().decrement(key, value);
    }

}
