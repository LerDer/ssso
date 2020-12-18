package com.sbt.util;

import com.sbt.exception.UserFriendlyException;

/**
 * @author lww
 * @date 2020-08-19 10:12
 */
public class CommonUtil {

    public static void isTrue(Boolean ex, String msg) {
        if (!ex) {
            throw new UserFriendlyException(msg);
        }
    }
}
