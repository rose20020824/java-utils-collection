package com.aiadtech.collection.util;


import java.util.ArrayList;
import java.util.List;

/**
 *  文本切割工具类
 */
public class TextSplitter {
    // 定义常见的句子结束符号
    private static final String PUNCTUATIONS = "。！？；.,!?;︰׃‽⁈⁉¿¡।॥។ฯ";

    public  List<String> split(String text, int maxByteLength) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return result;
        }

        while (getByteLength(text) > maxByteLength) {
            // 截取前 maxByteLength 个字节的字符串
            String substring = truncateToByteLength(text, maxByteLength);
            // 查找最后一个标点符号的位置
            int lastPunctuationIndex = -1;
            for (int i = substring.length() - 1; i >= 0; i--) {
                if (PUNCTUATIONS.indexOf(substring.charAt(i)) != -1) {
                    lastPunctuationIndex = i;
                    break;
                }
            }

            // 如果找到标点符号，则按标点切割；否则直接按 maxByteLength 切割
            int splitIndex = (lastPunctuationIndex != -1) ? lastPunctuationIndex + 1 :
                findSplitIndexByByteLength(text, maxByteLength);
            String part = text.substring(0, splitIndex);
            result.add(part);
            // 剩余部分继续处理
            text = text.substring(splitIndex);
        }

        // 添加剩余部分
        if (!text.isEmpty()) {
            result.add(text);
        }

        return result;
    }

    // 计算字符串的字节长度
    private static int getByteLength(String str) {
        return str.getBytes().length;
    }

    // 截取字符串到指定字节长度
    private static String truncateToByteLength(String str, int maxByteLength) {
        byte[] bytes = str.getBytes();
        if (bytes.length <= maxByteLength) {
            return str;
        }
        return new String(bytes, 0, maxByteLength);
    }

    // 找到合适的切割点以确保不超过最大字节长度
    private static int findSplitIndexByByteLength(String str, int maxByteLength) {
        byte[] bytes = str.getBytes();
        if (bytes.length <= maxByteLength) {
            return str.length();
        }

        // 确保不截断多字节字符
        int splitIndex = maxByteLength;
        while (splitIndex > 0 && (bytes[splitIndex] & 0xC0) == 0x80) {
            splitIndex--;
        }
        return splitIndex;
    }


}
