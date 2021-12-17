package com.bixin.common.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

/**
 * @author zhangcheng
 * create 2021-07-21 5:00 下午
 */
public class LocalDateTimeUtil {

    public static String YYYY = "yyyy";
    public static String YYYY_MM_DD = "yyyy-MM-dd";
    public static String YYYYMMDD = "yyyyMMdd";
    public static String YYYYMMDD_DOT = "yyyy.MM.dd";
    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static String YYYY_MM_DD_HH_MM_SS_S = "yyyy-MM-dd HH:mm:ss.s";
    public static String YYYY_MM_DD_HH_MM_SS_SSS="yyyy-MM-dd HH:mm:ss.SSS";
    public static String YYYYMMDDHH = "yyyyMMddHH";
    public static String YYYYMMDDHHMM = "yyyyMMddHHmm";
    public static String YYMMDDHHMMSS = "yyMMddHHmmss";
    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static String YYYYMMDDHHMMSS_DOT = "yyyy.MM.dd.HH.mm.ss";
    public static String YYMMDDHHMM = "yyMMddHHmm";
    public static String MM_DD_HH_MM_SS = "MM-dd HH:mm:ss";
    public static String MM_DD_HH_MM = "MM-dd HH:mm";
    public static String MM_DD_HH_MM_CN = "MM月dd日 HH时mm分";
    public static String MM_DD_CN = "MM月dd日";
    public static String HH_MM_CN = "HH时mm分";
    public static String HH_MM_SS = "HH:mm:ss";
    public static String HHMM = "HHmm";
    public static String HH_MM = "HH:mm";


    /**
     * Date转换为LocalDateTime
     */
    public static LocalDateTime convertDateToLDT(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * LocalDateTime转换为Date
     */

    public static Date convertLDTToDate(LocalDateTime time) {
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取指定日期的毫秒
     */
    public static Long getMilliByTime(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 毫秒 转 指定日期
     */
    public static LocalDateTime getLocalDateTime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * 获取指定日期的秒
     */
    public static Long getSecondsByTime(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 获取指定时间的指定格式
     */
    public static String formatTime(LocalDateTime time, String pattern) {
        return time.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取当前时间的指定格式
     */
    public static String formatNow(String pattern) {
        return formatTime(LocalDateTime.now(), pattern);
    }

    /**
     * 获取当前时间的指定格式
     */
    public static String now() {
        return formatTime(LocalDateTime.now(), YYYY_MM_DD_HH_MM_SS);
    }

    /**
     *获取当前时间的指定格式
     */
    public static String dateToLong(LocalDateTime time) {
        return formatTime(time, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 日期加上一个数,根据field不同加不同值,field为ChronoUnit.*
     */

    public static LocalDateTime plus(LocalDateTime time, long number, TemporalUnit field) {
        return time.plus(number, field);
    }

    /**
     * 日期减去一个数,根据field不同减不同值,field参数为ChronoUnit.*
     */
    public static LocalDateTime minu(LocalDateTime time, long number, TemporalUnit field) {
        return time.minus(number, field);
    }

    /**
     * 获取两个日期的差  field参数为ChronoUnit.*
     *
     * @param startTime
     * @param endTime
     * @param field     单位(年月日时分秒)
     * @return
     */
    public static long betweenTwoTime(LocalDateTime startTime, LocalDateTime endTime, ChronoUnit field) {
        Period period = Period.between(LocalDate.from(startTime), LocalDate.from(endTime));
        if (field == ChronoUnit.YEARS) {
            return period.getYears();
        }
        if (field == ChronoUnit.MONTHS) {
            return period.getYears() * 12 + period.getMonths();
        }
        return field.between(startTime, endTime);
    }

    /**
     * 获取一天的开始时间，2017,7,22 00:00
     */

    public static LocalDateTime getDayStart(LocalDateTime time) {
        return time.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * 获取一天的结束时间，2017,7,22 23:59:59.999999999
     */

    public static LocalDateTime getDayEnd(LocalDateTime time) {
        return time.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }

    /**
     * 获取logkafka时间格式
     */
    public static String getLogKafkaTime(long timestamp){
        LocalDateTime localDateTime = getLocalDateTime(timestamp);
        return formatTime(localDateTime, YYYY_MM_DD_HH_MM_SS_SSS);
    }
    
}
