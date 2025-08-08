package com.aiadtech.collection.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 */
public class Md5Util {

    private Md5Util() {
    }

    public static String encode(String str) {
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
}
