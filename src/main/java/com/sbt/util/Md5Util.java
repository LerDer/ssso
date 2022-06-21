package com.sbt.util;

import org.springframework.util.DigestUtils;

/**
 * MD5工具类
 *
 * @author lww
 */
public class Md5Util {

    public static String encript(String input) {
        input = input + "slat#user_of_mconfig_password%~^678";
        return DigestUtils.md5DigestAsHex(input.getBytes()).toLowerCase();
    }

    public static Boolean checkPassword(String input, String password) {
        return encript(input).equalsIgnoreCase(password);
    }

}
