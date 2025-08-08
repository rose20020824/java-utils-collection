package com.aiadtech.collection.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;

/**
 * 字节工具类
 */
public class BytesUtil {

    private BytesUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static float[] toFloat32(byte[] bytes) {
        // 确保字节数组的长度是4的倍数（32位浮点数）
        if (bytes.length % 4 != 0) {
            throw new IllegalArgumentException("Invalid base64 string length for float array");
        }

        // 创建浮点数数组
        var floats = new float[bytes.length / 4];
        // 将每4个字节转换为一个浮点数
        for (var i = 0; i < bytes.length; i += 4) {
            // 读取4个字节，并将字节序转换为小端
            ByteBuffer buffer = ByteBuffer.wrap(bytes, i, 4).order(ByteOrder.LITTLE_ENDIAN);
            // 将字节转换为浮点数
            floats[i / 4] = buffer.getFloat();
        }

        return floats;
    }

    /**
     * 数字字母的识别会不准，因此bytes的原始字符串里面需要包含中文
     * @param buffer 字符串的byte数组
     * @return 返回编码名
     */
    public static Charset detectEncoding(byte[] buffer) {
        if (buffer != null && buffer.length > 0) {
            // 使用指定范围的编码格式检查是否可以解析
            var charsets = Arrays.asList(
                StandardCharsets.UTF_8,
                Charset.forName("GBK"),
                StandardCharsets.UTF_16LE,
                StandardCharsets.UTF_16BE,
                StandardCharsets.ISO_8859_1
            );
            for (var charset : charsets) {
                var str = new String(buffer, charset);
                byte[] convertedBytes = str.getBytes(charset);
                if (Arrays.equals(buffer, convertedBytes)) {
                    return charset;
                }
            }
        }
        throw new UnsupportedCharsetException("unknown");
    }

}
