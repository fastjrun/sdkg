/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.helper;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;

public class LocalDateTimeHelper {
    public static final String YYYY                = "yyyy";
    public static final String YYYYMM              = "yyyyMM";
    public static final String YYYYMMDD            = "yyyyMMdd";
    public static final String YYYYMMDDHH          = "yyyyMMddHH";
    public static final String YYYYMMDDHHMM        = "yyyyMMddHHmm";
    public static final String YYYYMMDDHHMMSS      = "yyyyMMddHHmmss";
    public static final String YYYY_MM             = "yyyy-MM";
    public static final String YYYY_MM_DD          = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH       = "yyyy-MM-dd HH";
    public static final String YYYY_MM_DD_HH_MM    = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static final String BASE_TIME_FORMAT =
      "[yyyyMMddHHmmss][yyyyMMddHHmm][yyyyMMddHH][yyyyMMdd][yyyyMM][yyyy][[-][/][.]MM][[-][/]["
        + ".]dd][ ][HH][[:][.]mm][[:][.]ss][[:][.]SSS]";

    /**
     * 根据pattern格式化时间
     *
     * @param localDateTime localDateTime
     * @param pattern       pattern
     * @return String
     */
    public static String format(LocalDateTime localDateTime, String pattern) {
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 【推荐】解析常用时间字符串，支持,并不局限于以下形式：
     * [yyyy][yyyy-MM][yyyy-MM-dd][yyyy-MM-dd HH][yyyy-MM-dd HH:mm][yyyy-MM-dd
     * HH:mm:ss][yyyy-MM-dd HH:mm:ss:SSS]
     * [yyyy][yyyy/MM][yyyy/MM/dd][yyyy/MM/dd HH][yyyy/MM/dd HH:mm][yyyy/MM/dd
     * HH:mm:ss][yyyy/MM/dd HH:mm:ss:SSS]
     * [yyyy][yyyy.MM][yyyy.MM.dd][yyyy.MM.dd HH][yyyy.MM.dd HH.mm][yyyy.MM.dd HH.mm.ss][yyyy.MM
     * .dd HH.mm.ss.SSS]
     * [yyyy][yyyyMM][yyyyMMdd][yyyyMMddHH][yyyyMMddHHmm][yyyyMMddHHmmss]
     * [MM-dd]
     * 不支持yyyyMMddHHmmssSSS，因为本身DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")就不支持这个形式
     *
     * @param timeString timeString
     * @return LocalDateTime
     */
    public static LocalDateTime parse(String timeString) {
        return LocalDateTime.parse(timeString, getDateTimeFormatterByPattern(BASE_TIME_FORMAT));
    }

    /**
     * 根据传进来的pattern返回LocalDateTime，自动补齐
     *
     * @param timeString timeString
     * @param pattern    pattern
     * @return LocalDateTime
     */
    public static LocalDateTime parseByPattern(String timeString, String pattern) {
        return LocalDateTime.parse(timeString, getDateTimeFormatterByPattern(pattern));
    }

    /**
     * 自由解析时间的基础
     *
     * @param pattern pattern
     * @return DateTimeFormatter
     */
    private static DateTimeFormatter getDateTimeFormatterByPattern(String pattern) {
        return new DateTimeFormatterBuilder().appendPattern(pattern).parseDefaulting(
          ChronoField.YEAR_OF_ERA, LocalDateTime.now().getYear()).parseDefaulting(
          ChronoField.MONTH_OF_YEAR, 1).parseDefaulting(ChronoField.DAY_OF_MONTH,
          1).parseDefaulting(ChronoField.HOUR_OF_DAY, 0).parseDefaulting(ChronoField.MINUTE_OF_HOUR,
          0).parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0).parseDefaulting(
          ChronoField.NANO_OF_SECOND, 0).toFormatter();
    }

    public static LocalDate toLocalDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }

    public static LocalDateTime fromLocalDate(LocalDate localDate) {
        return localDate.atStartOfDay();
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime fromDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static long toTimestamp(LocalDateTime localDateTime) {
        return toInstant(localDateTime).toEpochMilli();
    }

    public static LocalDateTime fromTimestamp(long timestamp) {
        return fromInstant(Instant.ofEpochMilli(timestamp));
    }

    public static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    public static LocalDateTime fromInstant(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime atStartOfSecond(LocalDateTime localDateTime) {
        return localDateTime.withNano(0);
    }

    public static LocalDateTime atStartOfMinute(LocalDateTime localDateTime) {
        return atStartOfSecond(localDateTime).withSecond(0);
    }

    public static LocalDateTime atStartOfHour(LocalDateTime localDateTime) {
        return atStartOfMinute(localDateTime).withMinute(0);
    }

    public static LocalDateTime atStartOfDay(LocalDateTime localDateTime) {
        return atStartOfHour(localDateTime).withHour(0);
    }

    public static LocalDateTime atStartOfWeek(LocalDateTime localDateTime) {
        return atStartOfDay(localDateTime).minusDays(localDateTime.getDayOfWeek().getValue() - 1);
    }

    public static LocalDateTime atStartOfMonth(LocalDateTime localDateTime) {
        return atStartOfDay(localDateTime).withDayOfMonth(1);
    }

    public static LocalDateTime atStartOfYear(LocalDateTime localDateTime) {
        return atStartOfMonth(localDateTime).withMonth(1);
    }

    /**
     * 获得形如：XXX 天 XXX 小时 XXX 分 XXX 秒 XXX 毫秒 的格式化后的时间间隔
     * 如只想获得统一单位的时间间隔，请直接用 Duration
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return String
     */
    public static String formatDuration(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.compareTo(endTime) > 0) {
            return "出错啦 ! 起始时间大于结束时间";
        }
        Duration duration = Duration.between(startTime, endTime);
        StringBuilder output = new StringBuilder();
        long day = duration.toDays();
        if (day > 0) {
            output.append(day).append(" 天 ");
            duration = duration.minusDays(day);
        }
        long hour = duration.toHours();
        if (hour > 0) {
            output.append(hour).append(" 小时 ");
            duration = duration.minusHours(hour);
        }
        long minute = duration.toMinutes();
        if (minute > 0) {
            output.append(minute).append(" 分 ");
            duration = duration.minusMinutes(minute);
        }
        long second = duration.getSeconds();
        if (second > 0) {
            output.append(second).append(" 秒 ");
            duration = duration.minusSeconds(second);
        }
        output.append(duration.toMillis()).append(" 毫秒");
        return output.toString();
    }

    public static class builder {

        private LocalDateTime builderTime;

        public builder() {
            builderTime = LocalDateTime.now();
        }

        public builder(LocalDateTime localDateTime) {
            builderTime = localDateTime;
        }

        public static builder now() {
            return new builder();
        }

        public static builder parse(String timeString) {
            return new builder(LocalDateTime.parse(timeString,
              LocalDateTimeHelper.getDateTimeFormatterByPattern(BASE_TIME_FORMAT)));
        }

        public static builder parseByPattern(String timeString, String pattern) {
            return new builder(LocalDateTime.parse(timeString,
              LocalDateTimeHelper.getDateTimeFormatterByPattern(pattern)));
        }

        public static builder fromLocalDate(LocalDate localDate) {
            return new builder(localDate.atStartOfDay());
        }

        public static builder fromDate(Date date) {
            return new builder(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }

        public static builder fromTimestamp(long timestamp) {
            return new builder(LocalDateTimeHelper.fromInstant(Instant.ofEpochMilli(timestamp)));
        }

        public static builder fromInstant(Instant instant) {
            return new builder(instant.atZone(ZoneId.systemDefault()).toLocalDateTime());
        }

        public builder withYear(int year) {
            builderTime = builderTime.withYear(year);
            return this;
        }

        public builder withMonth(int month) {
            builderTime = builderTime.withMonth(month);
            return this;
        }

        public builder withDayOfMonth(int dayOfMonth) {
            builderTime = builderTime.withDayOfMonth(dayOfMonth);
            return this;
        }

        public builder withDayOfYear(int dayOfYear) {
            builderTime = builderTime.withDayOfYear(dayOfYear);
            return this;
        }

        public builder withHour(int hour) {
            builderTime = builderTime.withHour(hour);
            return this;
        }

        public builder withMinute(int minute) {
            builderTime = builderTime.withMinute(minute);
            return this;
        }

        public builder withSecond(int second) {
            builderTime = builderTime.withSecond(second);
            return this;
        }

        public builder withNano(int nanoOfSecond) {
            builderTime = builderTime.withNano(nanoOfSecond);
            return this;
        }

        public builder plusYears(long years) {
            builderTime = builderTime.plusYears(years);
            return this;
        }

        public builder plusMonths(long months) {
            builderTime = builderTime.plusMonths(months);
            return this;
        }

        public builder plusWeeks(long weeks) {
            builderTime = builderTime.plusWeeks(weeks);
            return this;
        }

        public builder plusDays(long days) {
            builderTime = builderTime.plusDays(days);
            return this;
        }

        public builder plusHours(long hours) {
            builderTime = builderTime.plusHours(hours);
            return this;
        }

        public builder plusMinutes(long minutes) {
            builderTime = builderTime.plusMinutes(minutes);
            return this;
        }

        public builder plusSeconds(long seconds) {
            builderTime = builderTime.plusSeconds(seconds);
            return this;
        }

        public builder plusNanos(long nanos) {
            builderTime = builderTime.plusNanos(nanos);
            return this;
        }

        public builder minusYears(long years) {
            builderTime = builderTime.minusYears(years);
            return this;
        }

        public builder minusMonths(long months) {
            builderTime = builderTime.minusMonths(months);
            return this;
        }

        public builder minusWeeks(long weeks) {
            builderTime = builderTime.minusWeeks(weeks);
            return this;
        }

        public builder minusDays(long days) {
            builderTime = builderTime.minusDays(days);
            return this;
        }

        public builder minusHours(long hours) {
            builderTime = builderTime.minusHours(hours);
            return this;
        }

        public builder minusMinutes(long minutes) {
            builderTime = builderTime.minusMinutes(minutes);
            return this;
        }

        public builder minusSeconds(long seconds) {
            builderTime = builderTime.minusSeconds(seconds);
            return this;
        }

        public builder minusNanos(long nanos) {
            builderTime = builderTime.minusNanos(nanos);
            return this;
        }

        public builder atStartOfSecond() {
            builderTime = builderTime.withNano(0);
            return this;
        }

        public builder atStartOfMinute() {
            atStartOfSecond();
            builderTime = builderTime.withSecond(0);
            return this;
        }

        public builder atStartOfHour() {
            atStartOfMinute();
            builderTime = builderTime.withMinute(0);
            return this;
        }

        public builder atStartOfDay() {
            atStartOfHour();
            builderTime = builderTime.withHour(0);
            return this;
        }

        public builder atStartOfWeek() {
            atStartOfDay();
            builderTime = builderTime.minusDays(builderTime.getDayOfWeek().getValue() - 1);
            return this;
        }

        public builder atStartOfMonth() {
            atStartOfDay();
            builderTime = builderTime.withDayOfMonth(1);
            return this;
        }

        public builder atStartOfYear(LocalDateTime localDateTime) {
            atStartOfDay();
            builderTime = builderTime.withMonth(1);
            return this;
        }

        public String format(String pattern) {
            return LocalDateTimeHelper.format(builderTime, pattern);
        }

        public LocalDate toLocalDate() {
            return builderTime.toLocalDate();
        }


        public Date toDate() {
            return Date.from(builderTime.atZone(ZoneId.systemDefault()).toInstant());
        }


        public long toTimestamp() {
            return toInstant().toEpochMilli();
        }

        public Instant toInstant() {
            return builderTime.atZone(ZoneId.systemDefault()).toInstant();
        }

        public LocalDateTime build() {
            return builderTime;
        }
    }
}
