package com.aiadtech.collection.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 获取Spark API的认证参数
 */
public class SparkUtil {
    public static final String METHOD_GET = "GET";

    public static final String METHOD_POST = "POST";

    private SparkUtil() {
    }

    public static String getDate() {
        var sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(new Date());
    }

    public static String getAuthUri(String host, String uri, String apiKey, String apiSecret) throws NoSuchAlgorithmException, InvalidKeyException {
        return getAuthUri(host, uri, apiKey, apiSecret, METHOD_GET);
    }

    public static String getAuthUri(String host, String uri, String apiKey, String apiSecret, String method) throws NoSuchAlgorithmException, InvalidKeyException {
        var algorithm = "HmacSHA256";
        var dateNow = getDate();

        var tmp = "host: " + host + "\n";
        tmp += "date: " + dateNow + "\n";
        tmp += method + " " + UrlUtil.getPath(uri) + " HTTP/1.1";

        var sha256HMAC = Mac.getInstance(algorithm);
        var secretKey = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), algorithm);
        sha256HMAC.init(secretKey);
        var bytes = sha256HMAC.doFinal(tmp.getBytes(StandardCharsets.UTF_8));

        var encoder = Base64.getEncoder();
        var signature = encoder.encodeToString(bytes);
        var authorizationFormat = "api_key=\"{0}\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"{1}\"";
        var authorization = encoder.encodeToString(MessageFormat.format(authorizationFormat, apiKey, signature).getBytes(StandardCharsets.UTF_8));
        return URLEncoder.encode("authorization", StandardCharsets.UTF_8) +
                "=" +
                URLEncoder.encode(authorization, StandardCharsets.UTF_8) +
                "&" +
                URLEncoder.encode("date", StandardCharsets.UTF_8) +
                "=" +
                URLEncoder.encode(dateNow, StandardCharsets.UTF_8) +
                "&" +
                URLEncoder.encode("host", StandardCharsets.UTF_8) +
                "=" +
                URLEncoder.encode(host, StandardCharsets.UTF_8);
    }
}
