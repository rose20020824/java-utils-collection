package com.aiadtech.collection.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 字符串工具类
 */
public class StringUtil {

    static final String ALPHABET_NUMBER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String random(int length) {
        Random random = new SecureRandom();
        var sb = new StringBuilder();
        for (var i = 0; i < length; i++) {
            // 0~61
            var number = random.nextInt(ALPHABET_NUMBER.length() - 1);
            sb.append(ALPHABET_NUMBER.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 驼峰转下划线
     */
    public static String camelToUnderline(String str) {
        var sb = new StringBuilder();
        for (var i = 0; i < str.length(); i++) {
            var c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append("_");
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private StringUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 过滤两端指定字符
     * @param string string
     * @param trimChars trimChars
     * @return string
     */
    public static String trim(String string, String trimChars) {
        List<Character> collect = trimChars.chars().mapToObj(i -> (char) i).toList();
        return trim(string, collect);
    }

    /**
     * 过滤两端指定字符
     * @param string string
     * @param trimChars trimChars
     * @return string
     */
    public static String trim(String string, List<Character> trimChars) {
        return rightTrim(leftTrim(string, trimChars), trimChars);
    }

    /**
     * 过滤左侧指定字符
     * @param string string
     * @param trimChars trimChars
     * @return string
     */
    public static String leftTrim(String string, List<Character> trimChars) {
        for (var i = 0; i < string.length(); i++) {
            var c = string.charAt(i);
            if (!trimChars.contains(c)) {
                break;
            }

            string = string.substring(0, i+1).replace(c, ' ') + string.substring(i+1);
        }
        return string.trim();
    }

    /**
     * 过滤右侧指定字符
     * @param string string
     * @param trimChars trimChars
     * @return string
     */
    public static String rightTrim(String string, List<Character> trimChars) {
        for (int i = (string.length() - 1); i > 0; i--) {
            var c = string.charAt(i);
            if (!trimChars.contains(c)) {
                break;
            }

            string = string.substring(0, i) + string.substring(i, i+1).replace(c, ' ');
        }
        return string.trim();
    }

    public static List<String> getUUIDs(int size) {
        List<String> ids = new ArrayList<>();
        for (var i = 0; i < size; i++) {
            ids.add(String.valueOf(UUID.randomUUID()));
        }
        return ids;
    }

    /**
     * 生成16位短UUID
     */
    public static String shortUUID() {
        return UUID.randomUUID().toString().replace("-", "").substring(8, 24);
    }

    public static String quoted(String str) {
        if (str == null) {
            return "null";
        }
        return "\"" + str + "\"";
    }

    /**
     * 去掉各种看不见的符号
     * @param input 字符串
     * @return 清理后的字符串
     */
    public static String removeInvisibleChars(String input) {
        if (input == null) {
            return null;
        }
        // 使用正则表达式匹配空格、制表符、换行符等，并将它们替换为空字符串
        return input.replaceAll("\\s+", "");
    }

    /**
     * 字符串中间加*
     * @param input
     * @return
     */
    public static String replaceMiddle(String input) {
        // 获取输入字符串的长度
        int length = input.length();

        // 如果长度不足9位，全部替换为 '*'
        if (length <= 8) {
            return "*".repeat(length);
        }
        // 如果长度超过或等于8位，进行替换
        else {
            // 获取前四位和后四位
            String firstFour = input.substring(0, 4);
            String lastFour = input.substring(length - 4);

            // 中间部分用 '*' 替换
            int middleLength = length - 8 + 4; // 实际上是 length - 4（前四位+后四位=8），但因为substring原因加4修正
            String middleStars = "*".repeat(middleLength);

            // 拼接结果字符串
            return firstFour + middleStars + lastFour;
        }
    }

}
