package com.aiadtech.collection.util;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * URl处理工具类
 */
public class UrlUtil {
    private UrlUtil() {
        // 不做处理
    }

    /**
     * 解析连接的文件拓展名
     * @param url 连接
     * @return 文件拓展名，没有的话返回空字符串
     */
    public static String getFileExtension(String url) {

        // 提取路径部分
        String path = getPath(url);

        if (path == null) {
            return "";
        }

        // 查找最后一个 '/' 后面的内容作为文件名
        int lastSlashIndex = path.lastIndexOf('/');
        String filename = lastSlashIndex >= 0 ? path.substring(lastSlashIndex + 1) : path;

        // 查找最后一个 '.' 后面的内容作为扩展名
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex >= 0 ? filename.substring(lastDotIndex + 1) : "";
    }

    public static String getPath(String url) {
        String path;
        try {
            path = new URI(url).getPath();
        } catch (Exception e) {
            // 如果 URL 解析失败，使用原始 URL
            path = url;
        }

        return path == null ? url : path;
    }

    public static Map<String, String> getQueryParam(String query) {

        // 将查询字符串分解为键值对
        Map<String, String> queryPairs = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            queryPairs.put(pair.substring(0, idx), pair.substring(idx + 1));
        }

        // 返回指定参数名的值
        return queryPairs;
    }
}
