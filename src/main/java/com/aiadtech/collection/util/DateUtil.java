package com.aiadtech.collection.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DateUtil {

    private DateUtil() {
    }

    public static final String NORMAL_DATETIME_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";

    public static final String NORMAL_DATE_FORMAT_STR = "yyyyMMdd";

    public static final String EXPORT_DATE_FORMAT_STR = "yyyyMMddHHmmss";

    public static final String EXPORT_TIME_FORMAT_STR = "HHmmss";

    public static final int SECONDS_30M = 30 * 60;

    public static final int SECONDS_15M = 15 * 60;

    public static final int SECONDS_10M = 10 * 60;

    public static final int SECONDS_5M = 5 * 60;

    public static final int SECONDS_1M = 60;

    public static final int SECONDS_1H = 3600;

    public static final int SECONDS_2H = 2 * 3600;

    public static final int SECONDS_30S = 30;

    public static final int SECONDS_24H = 24 * 3600;

    public static final int SECONDS_30D = 30 * 24 * 3600;

    public static final int MILLIS_1W = 7 * 24 * 3600 * 1000;

    public static final int MILLIS_1H = 3600 * 1000;

    public static final int SECONDS_1W = 7 * 24 * 3600;

    public static final int SECONDS_1Y = 365 * 24 * 3600;

    public static final int DAY_7D = 7;

    public static final int DAY_30D = 30;

    public static String format() {
        return format(NORMAL_DATETIME_FORMAT_STR);
    }

    public static String format(String formatStr) {
        return format(formatStr, new Date());
    }

    public static String format(String formatStr, Object date) {
        if (Objects.isNull(date)) {
            return "";
        } else if (date instanceof Date d) {
            return format(formatStr, d);
        } else if (date instanceof LocalDateTime ldt) {
            return format(formatStr, ldt);
        } else if (date instanceof Integer i) {
            return format(formatStr, new Date(i));
        } else if (date instanceof Long l) {
            return format(formatStr, new Date(l));
        } else {
            return date.toString();
        }
    }

    public static String format(String formatStr, LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern(formatStr));
    }

    public static String format(String formatStr, Date date) {
        var dateFormat = new SimpleDateFormat(formatStr);
        if (date == null) {
            date = new Date();
        }
        return dateFormat.format(date);
    }


    public static Date parse(String date) throws ParseException {
        var dateFormat = new SimpleDateFormat(NORMAL_DATETIME_FORMAT_STR);
        return dateFormat.parse(date);
    }

    public static long getDurationSeconds(Date start, Date end) {
        return Duration.between(LocalDateTime.ofInstant(start.toInstant(), ZoneOffset.UTC), LocalDateTime.ofInstant(end.toInstant(), ZoneOffset.UTC)).getSeconds();
    }

    public static long getDurationSeconds(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).getSeconds();
    }

    /**
     * 近几天的开始时间
     * @return Date
     */
    public static Date getDaysAgoBegin(Integer days) {
        var calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        return calendar.getTime();
    }

    public static LocalDateTime getNowAfterSecond(int second) {
        LocalDateTime now = LocalDateTime.now();
        return now.plusSeconds(second);
    }
}
