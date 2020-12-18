package com.sbt.util.blowfish;

import com.alibaba.fastjson.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author lww
 * @date 2019-04-28 2:09 PM
 */
@Component
public class EncriptUtil {

    private static final String BOSS_TEST_KEY = "bv|NU>PT:#|&9oZ1Er";

    /**
     * 解密数据
     */
    public String getOrigin(String jsonString) {
        try {
            String s = new String(Base64Encoder.decode(jsonString.getBytes()), StandardCharsets.UTF_8);
            return Blowfish.decrypt(s, BOSS_TEST_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("加密信息解密错误！");
        }
    }

    /**
     * 获取加密后的数据，不传就获取当前时间戳加密，传了就获取传入数据的加密
     */
    public String getEncrypt(String jsonString) {
        String encrypt;
        if (StringUtils.isBlank(jsonString)) {
            encrypt = Blowfish.encrypt(System.currentTimeMillis() + "", BOSS_TEST_KEY);
        } else {
            encrypt = Blowfish.encrypt(jsonString, BOSS_TEST_KEY);
        }
        return Base64Encoder.encode(encrypt.getBytes()).trim().replaceAll("\n", "").replaceAll("\r", "");
    }

    /**
     * 检查授权
     */
    public void checkAuth(String auth) {
        String origin = getOrigin(auth);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 30);
        Long timeInMillis = calendar.getTimeInMillis();
        Long authLong = Long.valueOf(origin);
        Assert.isTrue(timeInMillis > authLong, "无效的请求！");
    }

    public String getPublicAuth(Long companyId) {
        Long timeMillis = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>(16);
        map.put("companyId", companyId);
        map.put("currentTimes", timeMillis);
        map.put("type", "PBT");
        map.put("env", "daily");
        return getEncrypt(JSONObject.toJSONString(map));
    }
}
