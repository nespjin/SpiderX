/*
 *
 *   Copyright (c) 2021  NESP Technology Corporation. All rights reserved.
 *
 *   This program is not free software; you can't redistribute it and/or modify it
 *   without the permit of team manager.
 *
 *   Unless required by applicable law or agreed to in writing.
 *
 *   If you have any questions or if you find a bug,
 *   please contact the author by email or ask for Issues.
 *
 *   Author:JinZhaolu <1756404649@qq.com>
 */

package com.nesp.sdk.java.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * Time: Created 2021/7/27 15:17
 **/
public final class DateTimeUtil {

    private DateTimeUtil() {
        //no instance
    }

    public static final String DATE_FORMAT_PATTERN_FULL_TYPE_ONE = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_PATTERN_DATE_TYPE_ONE = "yyyy-MM-dd";
    public static final String DATE_FORMAT_PATTERN_FULL_TIME_TYPE_ONE = "HH:mm:ss";
    public static final String DATE_FORMAT_PATTERN_TIME_TYPE_ONE = "HH:mm";

    public static final SimpleDateFormat DATE_FORMAT_FULL_TYPE_ONE =
            newSimpleDateFormat(DATE_FORMAT_PATTERN_FULL_TYPE_ONE);

    public static final SimpleDateFormat DATE_FORMAT_DATE_TYPE_ONE =
            newSimpleDateFormat(DATE_FORMAT_PATTERN_DATE_TYPE_ONE);

    public static final SimpleDateFormat DATE_FORMAT_FULL_TIME_TYPE_ONE =
            newSimpleDateFormat(DATE_FORMAT_PATTERN_FULL_TIME_TYPE_ONE);

    public static final SimpleDateFormat DATE_FORMAT_TIME_TYPE_ONE =
            newSimpleDateFormat(DATE_FORMAT_PATTERN_TIME_TYPE_ONE);

    public static String tryFormatDate(SimpleDateFormat simpleDateFormat, String dateSource) {
        try {
            return formatDate(simpleDateFormat, DateFormat.getInstance().parse(dateSource));
        } catch (ParseException e) {
            return dateSource;
        }
    }

    public static String formatDate(SimpleDateFormat simpleDateFormat, String date)
            throws ParseException {
        return formatDate(simpleDateFormat, DateFormat.getInstance().parse(date));
    }

    public static String formatDate(SimpleDateFormat simpleDateFormat, Date date) {
        return simpleDateFormat.format(date);
    }

    public static SimpleDateFormat newSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern, Locale.CHINA);
    }

    /**
     * 获取一定天数后的日期
     *
     * @param begin 开始的日期
     * @param days  天数
     * @return 几天后的日期
     */
    public static Date getDateAfter(final Date begin, final int days) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(begin);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    /**
     * 获取两个日期之间的天数
     *
     * @param begin 开始的日期
     * @param end   结束的日期
     * @return 两个日期间的天数
     */
    public static int getDaysBetween(final Date begin, final Date end) {
        return (int) (getTimeBetween(begin, end) / (1000 * 60 * 60 * 24 /* 24h */));
    }

    /**
     * 获取两个日期之间的毫秒时间
     *
     * @param begin 开始的日期
     * @param end   结束的日期
     * @return 两个日期间隔时间
     */
    public static long getTimeBetween(final Date begin, final Date end) {
        final Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(begin);
        beginCalendar.set(Calendar.HOUR_OF_DAY, 0);
        beginCalendar.set(Calendar.MINUTE, 0);
        beginCalendar.set(Calendar.SECOND, 0);
        beginCalendar.set(Calendar.MILLISECOND, 0);

        final Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(end);
        endCalendar.set(Calendar.HOUR_OF_DAY, 0);
        endCalendar.set(Calendar.MINUTE, 0);
        endCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.MILLISECOND, 0);

        return endCalendar.getTime().getTime() - beginCalendar.getTime().getTime();
    }

    /**
     * @return 某一天开始的时间
     */
    public static Date beginOfSomeDay(final Date someDay) {
        if (someDay == null) return null;
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(someDay);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * @return 某一天结束的时间
     */
    public static Date endOfSomeDay(final Date someDay) {
        if (someDay == null) return null;
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(someDay);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * @return 今天的开始时间
     */
    public static Date beginOfToday() {
        return beginOfSomeDay(new Date());
    }

    /**
     * @return 今天的结束时间
     */
    public static Date endOfToday() {
        return endOfSomeDay(new Date());
    }

    /**
     * @param amount 相对与今天的天数 1:明天 0:今天 -1:昨天 ...
     * @return 目标日期的一天之中的开始时间
     */
    public static Date beginOfRelativeToday(final int amount) {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(beginOfToday());
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, amount);
        return gregorianCalendar.getTime();
    }

    /**
     * @param amount 相对与今天的天数 1:明天 0:今天 -1:昨天 ...
     * @return 目标日期的一天之中的结束时间
     */
    public static Date endOfRelativeToday(final int amount) {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(endOfToday());
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, amount);
        return gregorianCalendar.getTime();
    }

    /**
     * @return 昨天的开始时间
     */
    public static Date beginOfYesterday() {
        return beginOfRelativeToday(-1);
    }

    /**
     * @return 昨天的结束时间
     */
    public static Date endOfYesterday() {
        return endOfRelativeToday(-1);
    }

    /**
     * @return 明天的开始时间
     */
    public static Date beginOfTomorrow() {
        return beginOfRelativeToday(1);
    }

    /**
     * @return 明天的结束时间
     */
    public static Date endOfTomorrow() {
        return endOfRelativeToday(1);
    }

    /**
     * 距离指定日期还剩多少天
     *
     * @param end 结束时间
     * @return 剩余天数
     */
    public static int remainingDays(final Date end) {
        if (end == null) {
            throw new IllegalArgumentException("End date must not be null");
        }

        final long nowMilliseconds = System.currentTimeMillis();
        final long endMilliseconds = end.getTime();

        long distanceMilliseconds = endMilliseconds - nowMilliseconds;
        if (distanceMilliseconds <= 0) return 0;

        final double rate = distanceMilliseconds / (1.0F * 24 * 3600 * 1000);
        return (int) (rate + 0.5F);
    }

    /**
     * 判断两个日期是否在同一周
     *
     * @param date1 date1
     * @param date2 date2
     * @return 判断两个日期是否在同一周
     */
    public static boolean isSameWeek(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        int subYear = calendar1.get(Calendar.YEAR) - calendar2.get(Calendar.YEAR);
        if (0 == subYear) {
            return calendar1.get(Calendar.WEEK_OF_YEAR) == calendar2.get(Calendar.WEEK_OF_YEAR);
        } else if (1 == subYear && Calendar.DECEMBER == calendar2.get(Calendar.MONTH)) {
            // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            return calendar1.get(Calendar.WEEK_OF_YEAR) == calendar2.get(Calendar.WEEK_OF_YEAR);
        } else if (-1 == subYear && Calendar.DECEMBER == calendar1.get(Calendar.MONTH)) {
            return calendar1.get(Calendar.WEEK_OF_YEAR) == calendar2.get(Calendar.WEEK_OF_YEAR);
        }
        return false;
    }

    /**
     * @return 获取短时间格式
     */
    public static String formatTimeShortestLength(long milliseconds) {
        Date date = new Date(milliseconds);
        Date curDate = new Date();

        String str;
        long durTime = curDate.getTime() - date.getTime();

        int dayStatus = relativeTodayRecentDay(date);

        if (durTime <= 10 * 60 * 1000) {
            str = "刚刚";
        } else if (durTime < 60 * 60 * 1000) {
            str = durTime / 60 * 1000 + "分钟前";
        } else if (dayStatus == 0) {
            str = durTime / 6 * 60 * 1000 + "小时前";
        } else if (dayStatus == -1) {
            str = "昨天" + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
        } else if (isSameYear(date, curDate) && dayStatus < -1) {
            str = new SimpleDateFormat("MM:dd", Locale.getDefault()).format(date);
        } else {
            str = new SimpleDateFormat("yyyy:MM", Locale.getDefault()).format(date);
        }
        return str;
    }

    /**
     * 判断是否是同一年
     *
     * @param date1 date1
     * @param date2 date2
     * @return 判断是否是同一年
     */
    public static boolean isSameYear(Date date1, Date date2) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(date1);
        int tarYear = tarCalendar.get(Calendar.YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(date2);
        int comYear = compareCalendar.get(Calendar.YEAR);
        return tarYear == comYear;
    }

    /**
     * date1 相对于 今天 是 昨天 还是以前 还是今天
     * 1表示明天 0表示今天，-1表示昨天，小于-1则是昨天以前 ....
     *
     * @param date1 date1
     * @return date1 相对于 date1 是 昨天 还是以前 还是今天
     */
    public static int relativeTodayRecentDay(Date date1) {
        return relativeRecentDay(date1, new Date());
    }

    /**
     * date1 相对于 date2 是 昨天 还是以前 还是今天
     * 1表示明天 0表示今天，-1表示昨天，小于-1则是昨天以前 ....
     *
     * @param date1 date1
     * @param date2 date2
     * @return date1 相对于 date1 是 昨天 还是以前 还是今天
     */
    public static int relativeRecentDay(Date date1, Date date2) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(date1);
        int tarDayOfYear = tarCalendar.get(Calendar.DAY_OF_YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(date2);
        int comDayOfYear = compareCalendar.get(Calendar.DAY_OF_YEAR);

        return tarDayOfYear - comDayOfYear;
    }

    /**
     * @param milliseconds 时间值
     * @param isDetail     是否需要显示具体时间段 + 时分和星期 + 时分
     * @return 格式化后的时间
     */
    public static String formatDateBucket(long milliseconds, boolean isDetail) {
        String dataString = "";
        String timeStringBy24 = "";

        Date currentTime = new Date(milliseconds);
        Date today = new Date();
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        Date todaybegin = todayStart.getTime();
        Date yesterdaybegin = new Date(todaybegin.getTime() - 3600 * 24 * 1000);
        Date preyesterday = new Date(yesterdaybegin.getTime() - 3600 * 24 * 1000);

        if (!currentTime.before(todaybegin)) {
            dataString = "今天";
        } else if (!currentTime.before(yesterdaybegin)) {
            dataString = "昨天";
        } else if (!currentTime.before(preyesterday)) {
            dataString = "前天";
        } else if (isSameWeek(currentTime, today)) {
            dataString = weekDayName(currentTime);
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dataString = dateFormat.format(currentTime);
        }

        SimpleDateFormat timeformatter24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeStringBy24 = timeformatter24.format(currentTime);

        if (isDetail) {
            //显示具体的时间
            //在聊天界面显示时间，如果是今天则显示当前时间段加上时和分  如上午 9:58
            if (!currentTime.before(todaybegin)) {
                //如果是今天
                return formatDayDateBucket(currentTime);//根据时间段分为凌晨 上午 下午等
            } else {
                return dataString + " " + timeStringBy24;//如果是昨天 则是 昨天 9：58 如果是同在一个星期，前天之前的时间则显示 星期一 9：58
            }
        } else {
            //在会话记录界面不需要展示很具体的时间
            if (!currentTime.before(todaybegin)) {//如果是今天
                return timeStringBy24;//直接返回时和分 如 9:58
            } else {
                return dataString;//如果不是今天，不需要展示时和分 如 昨天 前天 星期一
            }
        }
    }

    /**
     * 根据日期获得星期的名称
     *
     * @param date date
     * @return 根据日期获得星期的名称
     */
    public static String weekDayName(Date date) {
        final String[] weekDayName = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return weekDayName[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    /**
     * 根据不同时间段，显示不同时间
     *
     * @param date 日期
     * @return 根据不同时间段，显示不同时间
     */
    public static String formatDayDateBucket(final Date date) {
        if (date == null) {
            return "";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat simpleDateFormat0to11 = new SimpleDateFormat("KK:mm",
                Locale.getDefault());
        SimpleDateFormat timeformatter1to12 = new SimpleDateFormat("hh:mm",
                Locale.getDefault());
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 5) {
            return "凌晨 " + simpleDateFormat0to11.format(date);
        } else if (hour < 12) {
            return "上午 " + simpleDateFormat0to11.format(date);
        } else if (hour < 18) {
            return "下午 " + timeformatter1to12.format(date);
        } else {
            return "晚上 " + timeformatter1to12.format(date);
        }
    }

    /**
     * @param timeLengthMilliseconds 时间长度毫秒
     * @return 格式化后的时间长度
     */
    public static String formatTimeLength(long timeLengthMilliseconds) {
        return formatTimeLength(timeLengthMilliseconds, new String[]{"h", "m", "s"});
    }

    /**
     * @param timeLengthMilliseconds 时间长度毫秒
     * @param units                  时间长度单位
     * @return 格式化后的时间长度
     */
    public static String formatTimeLength(long timeLengthMilliseconds, String[] units) {
        int hour = 0;
        int min;
        int sec;
        if (timeLengthMilliseconds < 60 * 1000) {
            min = 0;
            sec = (int) (timeLengthMilliseconds / 1000);
        } else if (timeLengthMilliseconds < 60 * 60 * 1000) {
            min = (int) (timeLengthMilliseconds / (60 * 1000));
            sec = (int) ((timeLengthMilliseconds % (60 * 1000)) / 1000);
        } else {
            hour = (int) (timeLengthMilliseconds / (60 * 60 * 1000));
            min = (int) ((timeLengthMilliseconds % (60 * 60 * 1000)) / (60 * 1000));
            sec = (int) ((timeLengthMilliseconds % (60 * 1000)) / (1000));
        }
        if (units.length < 1) {
            return hour + "h" + min + "m" + sec + "s";
        } else if (units.length == 1) {
            return hour + units[0];
        } else if (units.length == 2) {
            return hour + units[0] + min + units[1];
        } else if (units.length == 3) {
            return hour + units[0] + min + units[1] + sec + units[2];
        } else {
            return hour + units[0] + min + units[1] + sec + units[2];
        }
    }

    /**
     * @param timeMilliseconds 要转换的毫秒数
     * @return 该毫秒数转换为 ss 或 mm:ss 或 HH:mm:ss 后的格式
     */
    public static String formatTime(long timeMilliseconds) {
        SimpleDateFormat simpleDateFormat;
        long days = timeMilliseconds / (1000 * 60 * 60 * 24);
        long hours = (timeMilliseconds % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (timeMilliseconds % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (timeMilliseconds % (1000 * 60)) / 1000;
        if (hours == 0) {
            if (minutes == 0) {
                simpleDateFormat = new SimpleDateFormat("ss", Locale.getDefault());
            } else {
                simpleDateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
            }
        } else {
            simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        }

        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return simpleDateFormat.format(timeMilliseconds);
    }

    /**
     * @param begin 时间段的开始
     * @param end   时间段的结束
     * @return 该毫秒数转换为 ss 或 mm:ss 或 HH:mm:ss 后的格式
     */
    public static String formatTime(Date begin, Date end) {
        return formatTime(end.getTime() - begin.getTime());
    }


    /**
     * 是否晚上九点到早上六点之间
     *
     * @param date 日期
     * @return 是否为晚上
     */
    public static boolean isNight(Date date) {
        if (null == date) return false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY) >= 19 || calendar.get(Calendar.HOUR_OF_DAY) < 6;
    }


    public static String formatMilliseconds(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.CHINA, "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.CHINA, "%02d:%02d", minutes, seconds);
        }
    }

    public static String formatMilliseconds2(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.CHINA, "%d小时%02d分钟%02d秒", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format(Locale.CHINA, "%02d分%02d秒", minutes, seconds);
        } else {
            return String.format(Locale.CHINA, "%02d秒", seconds);
        }
    }
}