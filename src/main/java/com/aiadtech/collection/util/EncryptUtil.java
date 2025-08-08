package com.aiadtech.collection.util;


import com.aiadtech.collection.constant.ErrorCode;
import com.aiadtech.collection.exception.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 加密工具类
 */
public class EncryptUtil {

    private EncryptUtil() {
        // 不做处理
    }

    private static final Logger logger = LoggerFactory.getLogger(EncryptUtil.class);

    public static final String ALGORITHM_AES = "AES";

    public static final String ALGORITHM_RSA = "RSA";

    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    public static String md5Encode(String str) {
        try {
            var md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes());
            var result = new StringBuilder();
            for (byte b : bytes) {
                var temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    result.append("0");
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * Aes加密 AES-128-CBC
     *
     * @param content content
     * @param aesKey  aesKey
     * @return string
     */
    public static String aesEncode(String content, String aesKey) {
        if (aesKey.isEmpty()) {
            return null;
        }
        // 初始化向量iv取aes_key的1到16位
        var iv = aesKey.substring(0, 16);
        var key = aesKey.substring(16, 32);
        return encode(content, key, iv);
    }

    /**
     * Aes加密 AES-256-CBC
     *
     * @param content content
     * @param aesKey  aesKey
     * @return string
     */
    public static String aes256Encode(String content, String aesKey) {
        if (aesKey.isEmpty()) {
            return null;
        }
        // 初始化向量iv取aes_key的1到16位
        var iv = aesKey.substring(0, 16);
        var key = aesKey.substring(16, 48);
        return encode(content, key, iv);
    }

    /**
     * Aes加密
     *
     * @param content 加密内容
     * @param key     key
     * @param iv      iv
     * @return string
     */
    public static String encode(String content, String key, String iv) {
        var secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM_AES);
        try {
            var cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
            byte[] bytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            logger.warn("aes加密失败，加密内容：{}", content);
        }
        return null;
    }

    public static String aesDecrypt(String encryptedContent, String aesKey) {
        encryptedContent = encryptedContent.replace(" ", "+");
        // 初始化向量iv取aes_key的1到16位
        var iv = aesKey.substring(0, 16);
        var key = aesKey.substring(16, 32);
        var secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM_AES);
        try {
            var cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
            byte[] bytes = Base64.getDecoder().decode(encryptedContent); // 假设encryptedContent是加密后的密文
            byte[] decryptedBytes = cipher.doFinal(bytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.warn("error", e);
            logger.warn("aes解密失败 ，解密内容：{}", encryptedContent);
            throw new AppException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    /**
     * Aes解密 AES-256-CBC
     *
     * @param content content
     * @param aesKey  aesKey
     * @return string
     */
    public static String aes256Decode(String content, String aesKey) {
        if (aesKey.isEmpty()) {
            return null;
        }
        // 初始化向量iv取aes_key的1到16位
        var iv = aesKey.substring(0, 16);
        var key = aesKey.substring(16, 48);
        return aesDecode(content, key, iv);
    }

    /**
     * Aes解密
     *
     * @param content 密文
     * @param key     key
     * @param iv      iv
     * @return String
     */
    public static String aesDecode(String content, String key, String iv) {
        var secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM_AES);
        try {
            var cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));

            byte[] byteContent = Base64.getDecoder().decode(content);
            return new String(cipher.doFinal(byteContent), StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.warn("aes解密失败，加密内容：{}", content);
        }
        return null;
    }

    // 从Base64编码的字符串加载X.509公钥用于数据加密
    // base64PublicKey: 去掉 -----BEGIN PUBLIC KEY-----  -----END PUBLIC KEY----- 以及换行符的部分
    public static String rsaEncode(String content, String base64PublicKey, String charset) throws Exception {
        // 加载公钥
        byte[] data = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory factory = KeyFactory.getInstance(ALGORITHM_RSA);
        var publicKey = factory.generatePublic(spec);

        // RSA加密
        Cipher encryptCipher = Cipher.getInstance(ALGORITHM_RSA);
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherText = encryptCipher.doFinal(content.getBytes(charset));
        return Base64.getEncoder().encodeToString(cipherText);
    }

    // 从Base64编码的字符串加载X.509公钥用于数据解密
    // base64PublicKey: 去掉 -----BEGIN PRIVATE KEY-----  -----BEGIN PRIVATE KEY
    // ----- 以及换行符的部分
    public static String rsaDecrypt(String cipherText, String base64PrivateKey, String charset) throws Exception {
        // 加载私钥
        byte[] data = Base64.getDecoder().decode(base64PrivateKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory factory = KeyFactory.getInstance(ALGORITHM_RSA);
        var privateKey = factory.generatePublic(spec);

        // RSA解密
        byte[] bytes = Base64.getDecoder().decode(cipherText);
        Cipher decryptCipher = Cipher.getInstance(ALGORITHM_RSA);
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(decryptCipher.doFinal(bytes), charset);
    }
}
