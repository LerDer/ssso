package com.sbt.util.blowfish;

import org.apache.commons.codec.binary.Base64;

/**
 * @author lww
 * @date 2020-12-18 7:53 PM
 */
public class Base64Encoder {

    /**
     * @param bytes
     * @return
     */
    public static byte[] decode(final byte[] bytes) {
        return Base64.decodeBase64(bytes);
    }

    /**
     * 二进制数据编码为BASE64字符串
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    public static String encode(final byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }

}
