package com.sbt.util.blowfish;

import com.sbt.util.SessionUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lww
 * @date 2019-06-30 1:31 AM
 */
public class Blowfish {

    /**
     * 默认密钥，当提供的密钥为null时使用 随意设置，但不能超过16位
     */
    private static BlowfishCipher cipher = new BlowfishCipher();

    /**
     * 加密串source
     *
     * @param src 要加密的内容
     * @return 加密后的内容
     */
    public static String encrypt(String src) {
        return encrypt(src, null);
    }

    /**
     * 解密串source
     *
     * @param src 要解密的内容
     * @return 解密后的内容
     */
    public static String decrypt(String src) {
        return decrypt(src, null);
    }

    /**
     * 加密串src,使用的密钥为key
     *
     * @param src 要加密的内容
     * @param key 加密所使用的密钥
     */
    public static String encrypt(String src, String key) {
        byte[] srcBytes = CodecUtil.toBytes(src);
        byte[] keyBytes = (StringUtils.isBlank(key) ? CodecUtil.toBytes(SessionUtil.ENCRYPT_KEY) : CodecUtil.toBytes(key.hashCode() + ""));
        byte[] encryptedBytes = cipher.encrypt(srcBytes, keyBytes);
        return Base64Encoder.encode(encryptedBytes);
    }

    /**
     * 解密串src,使用的密钥为key
     *
     * @param src 要解密的内容
     * @param key 解密时所使用的密钥
     */
    public static String decrypt(String src, String key) {
        byte[] srcBytes = Base64Encoder.decode(src.getBytes());
        byte[] keyBytes = (StringUtils.isBlank(key) ? CodecUtil.toBytes(SessionUtil.ENCRYPT_KEY) : CodecUtil.toBytes(key.hashCode() + ""));
        byte[] bytes = cipher.decrypt(srcBytes, keyBytes);
        return CodecUtil.toString(bytes);
    }

    /**
     * 加密文本文件
     *
     * @param src 源文件
     * @param dst 加密后的文件，若不存在此文件将会自动生成
     */
    public static void encrypt(File src, File dst) throws IOException {
        encrypt(src, dst, null, null);
    }

    public static void encrypt(File src, File dst, String encoding) throws IOException {
        encrypt(src, dst, null, encoding);
    }

    /**
     * 解密文本文件
     *
     * @param src 待解密的文件
     * @param dst 解密后的文件，若不存在此文件将会自动生成
     */
    public static void decrypt(File src, File dst) throws IOException {
        decrypt(src, dst, null, null);
    }

    public static void decrypt(File src, File dst, String encoding) throws IOException {
        decrypt(src, dst, null, encoding);
    }

    /**
     * 加密文本文件
     *
     * @param src 源文件
     * @param dst 加密后的文件，若不存在此文件将会自动生成
     * @param key 加密密钥
     */
    private static void encrypt(File src, File dst, String key, String encoding) throws IOException {
        String charset = (StringUtils.isNotBlank(encoding) ? encoding : Constants.PREFERED_ENCODING);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(src), charset));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append(Constants.LINE_SEPARATER);
        }
        reader.close();

        String encrypt = encrypt(content.toString(), key);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dst), charset));
        writer.write(encrypt);
        writer.flush();
        writer.close();
    }

    /**
     * 解密文本文件
     *
     * @param src 待解密的文件
     * @param dst 解密后的文件，若不存在此文件将会自动生成
     * @param key 解密密钥
     */
    private static void decrypt(File src, File dst, String key, String encoding) throws IOException {
        String charset = (StringUtils.isNotBlank(encoding) ? encoding : Constants.PREFERED_ENCODING);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(src), charset));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append(Constants.LINE_SEPARATER);
        }
        reader.close();

        String decrypt = decrypt(content.toString(), key);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dst), charset));
        writer.write(decrypt);
        writer.flush();
        writer.close();
    }

    //public static void main(String[] args) throws IOException {
    //
    //	String[] keys = new String[]{"xx", "xx", "xxx"};
    //
    //	String orginal = "！！！我是中国人！！！毛主席万岁！！！";
    //	for (String key : keys) {
    //		System.out.println("原文 ＝> " + orginal);
    //		String encrypted = Blowfish.encrypt(orginal, key);
    //		System.out.println("(key=" + key + ")加密后 ＝> " + encrypted);
    //		String decrypted = Blowfish.decrypt(encrypted, key);
    //		System.out.println("解密后 ＝> " + decrypted);
    //
    //		System.err.println("================================================");
    //	}
    //
    //	String mykey = "xxx";
    //	List<String> strings = Arrays.asList(Constants.DEF_ENCRYPT_TEXT);
    //	System.out.println(strings.contains(Blowfish.encrypt(Constants.DEF_ORGINAL_TEXT, mykey)));
    //}

    static class BlowfishCipher {

        /**
         * The JDK Crypto Cipher algorithm to use for this class, equal to
         * &quot;Blowfish&quot;.
         */
        private static final String ALGORITHM = "Blowfish";

        /**
         * The JDK Crypto Transformation string to use for this class, equal to
         * {@link #ALGORITHM ALGORITHM} + &quot;/ECB/PKCS5Padding&quot;;
         */
        private static final String TRANSFORMATION_STRING = ALGORITHM + "/ECB/PKCS5Padding";

        private static final byte[] KEY_BYTES = Base64Encoder.decode("jJ9Kg1BAevbvhSg3vBfwfQ==".getBytes());
        private static final Key DEFAULT_CIPHER_KEY = new SecretKeySpec(KEY_BYTES, ALGORITHM);

        /**
         * The key to use by default, can be overridden by calling
         * {@link #setKey(java.security.Key)}.
         */
        private Key key = DEFAULT_CIPHER_KEY;

        BlowfishCipher() {
        }

        Key getKey() {
            return key;
        }

        void setKey(Key key) {
            this.key = key;
        }

        byte[] encrypt(byte[] raw, byte[] key) {
            byte[] encrypted = crypt(raw, javax.crypto.Cipher.ENCRYPT_MODE, key);
            return encrypted;
        }

        byte[] decrypt(byte[] encrypted, byte[] key) {
            return crypt(encrypted, javax.crypto.Cipher.DECRYPT_MODE, key);
        }

        javax.crypto.Cipher newCipherInstance() throws IllegalStateException {
            try {
                return javax.crypto.Cipher.getInstance(TRANSFORMATION_STRING);
            } catch (Exception e) {
                String msg = "Unable to acquire a Java JCE Cipher instance using " + javax.crypto.Cipher.class.getName()
                        + ".getInstance( \"" + TRANSFORMATION_STRING + "\" ). "
                        + "Blowfish under this configuration is required for the " + getClass().getName()
                        + " instance to function.";
                throw new IllegalStateException(msg, e);
            }
        }

        void init(javax.crypto.Cipher cipher, int mode, Key key) {
            try {
                cipher.init(mode, key);
            } catch (InvalidKeyException e) {
                String msg = "Unable to init cipher.";
                throw new IllegalStateException(msg, e);
            }
        }

        byte[] crypt(javax.crypto.Cipher cipher, byte[] bytes) {
            try {
                return cipher.doFinal(bytes);
            } catch (Exception e) {
                String msg = "Unable to crypt bytes with cipher [" + cipher + "].";
                throw new IllegalStateException(msg, e);
            }
        }

        byte[] crypt(byte[] bytes, int mode, byte[] key) {
            javax.crypto.Cipher cipher = newCipherInstance();

            Key jdkKey;
            if (key == null) {
                jdkKey = getKey();
            } else {
                jdkKey = new SecretKeySpec(key, ALGORITHM);
            }

            init(cipher, mode, jdkKey);
            return crypt(cipher, bytes);
        }

        public static Key generateNewKey() {
            return generateNewKey(128);
        }

        static Key generateNewKey(int keyBitSize) {
            KeyGenerator kg;
            try {
                kg = KeyGenerator.getInstance(ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                String msg = "Unable to acquire " + ALGORITHM + " algorithm.  This is required to function.";
                throw new IllegalStateException(msg, e);
            }
            kg.init(keyBitSize);
            return kg.generateKey();
        }

    }

}