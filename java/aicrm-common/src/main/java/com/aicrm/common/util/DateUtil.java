package com.aicrm.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 日期工具：统一格式与解析（时区 Asia/Shanghai）
 */
public final class DateUtil {

    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final ZoneId ZONE_SHANGHAI = ZoneId.of("Asia/Shanghai");

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private DateUtil() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(ZONE_SHANGHAI);
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime == null ? null : DEFAULT_FORMATTER.format(dateTime);
    }

    public static String format(LocalDate date) {
        return date == null ? null : DATE_FORMATTER.format(date);
    }

    public static LocalDateTime parse(String text) {
        return text == null || text.isBlank() ? null : LocalDateTime.parse(text.trim(), DEFAULT_FORMATTER);
    }

    public static LocalDate parseDate(String text) {
        return text == null || text.isBlank() ? null : LocalDate.parse(text.trim(), DATE_FORMATTER);
    }

    /** 毫秒时间戳 → LocalDateTime */
    public static LocalDateTime ofEpochMilli(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZONE_SHANGHAI);
    }

    /** LocalDateTime → 毫秒时间戳 */
    public static long toEpochMilli(LocalDateTime dateTime) {
        return dateTime == null ? 0 : dateTime.atZone(ZONE_SHANGHAI).toInstant().toEpochMilli();
    }

    /** 当天 00:00:00 */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    /** 当天 23:59:59.999999999 */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date == null ? null : date.plusDays(1).atStartOfDay().minusNanos(1);
    }
}
