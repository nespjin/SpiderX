/*
 *
 *   Copyright (c) 2020  NESP Technology Corporation. All rights reserved.
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

package com.nesp.sdk.java.util.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-6-9 下午1:37
 * @deprecated 需要重构
 **/

//@TODO 需要重构
@Deprecated
public class Festival {

    /**
     * 农历部分假日
     */
    private final String[] mLunarFestivalInfos = new String[]{
            "一月初一 春节",
            "一月十五 元宵",
            "五月初五 端午",
            "七月初七 情人",
            "七月十五 中元",
            "八月十五 中秋",
            "九月初九 重阳",
            "十二月初八 腊八",
            "十二月二十四 小年",
            "十二月三十 除夕",
            //            "五月初七 建党",
    };

    /**
     * 公历部分节假日
     */
    private final String[] mSolarFestivalInfos = new String[]{ //
            "0101 元旦",
            "0214 情人",
            "0308 妇女",
            "0312 植树",
            "0315 消费者权益日",
            "0401 愚人",
            "0404 清明",
            "0501 劳动",
            "0504 青年",
            "0512 护士",
            "0601 儿童",

            "0606 高考",
            "0607 高考",
            "0608 高考",

            "0610 毕业",
            "0611 毕业",
            "0612 毕业",
            "0613 毕业",
            "0614 毕业",
            "0615 毕业",
            "0616 毕业",
            "0617 毕业",
            "0618 毕业",
            "0619 毕业",
            "0620 毕业",
            "0621 毕业",
            "0622 毕业",
            "0623 毕业",
            "0624 毕业",
            "0625 毕业",
            "0626 毕业",
            "0627 毕业",
            "0628 毕业",
            "0629 毕业",
            "0630 毕业",

            "0701 建党",
            "0801 建军",
            //            "0808 父亲",
            "0909 毛泽东逝世纪念",
            "0910 教师",
            "0928 孔子诞辰",
            "1001 国庆",
            "1006 老人",
            "1024 联合国日",
            "1112 孙中山诞辰纪念",
            "1220 澳门回归纪念",
            "1225 圣诞",
            "1226 毛泽东诞辰纪念",

            //            "0609 父亲",
    };

    private final String mFestivalDate = getSimpleDateFormat().format(new Date());

    //如果阳历和引力的节日都不为NULL，则随机获取一种节日。
    private final Boolean mIsShowSolar = new Random().nextBoolean();
    private final boolean mIsShowExtSolar = new Random().nextBoolean();

    public static Festival getInstance() {
        return new Festival();
    }

    public String getFestival() {
        String lunarFestival = getLunarFestival();
        String solarFestival = getSolarFestival();
        if (mIsShowExtSolar) {
            if (isFatherFestival()) {
                return "父亲";
            } else if (isMonthFestival()) {
                return "母亲";
            }
        }

        if (lunarFestival != null && solarFestival != null) {
            if (mIsShowSolar) {
                return solarFestival;
            } else {
                return lunarFestival;
            }
        } else if (lunarFestival != null) {
            return lunarFestival;
        } else {
            return solarFestival;
        }
    }

    public String getLunarFestival() {
        String lunarDate = new Lunar().toString();
        for (String s : mLunarFestivalInfos) {
            if (lunarDate.equals(s.split(" ")[0])) {
                return s.split(" ")[1];
            }
        }
        return null;
    }

    public String getSolarFestival() {
        for (String s : mSolarFestivalInfos) {
            if (mFestivalDate.equals(s.split(" ")[0])) {
                return s.split(" ")[1];
            }
        }
        return null;
    }

    private SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat("MMdd", Locale.getDefault());
    }

    private Boolean isFatherFestival() {
        return mFestivalDate.equals(getSimpleDateFormat().format(getSomeMonthWeekDate(Calendar.JUNE, 3, Calendar.SUNDAY)));
    }

    private Boolean isMonthFestival() {
        return mFestivalDate.equals(getSimpleDateFormat().format(getSomeMonthWeekDate(Calendar.MAY, 2, Calendar.SUNDAY)));
    }

    /**
     * 计算第几个月第几个周几的日期
     *
     * @param month     　某个月
     * @param indexWeek 　第几周
     * @param week      　周几
     * @return 日期
     */
    private static Date getSomeMonthWeekDate(int month, int indexWeek, int week) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(Calendar.YEAR, Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date())));
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.MONTH, month);
        int maxDateOfMonth = calendar.getActualMaximum(Calendar.DATE);
        print("maxOfMonth=" + maxDateOfMonth);
        int count = 0;
        for (int i = 1; i <= maxDateOfMonth; i++) {
            calendar.set(Calendar.DATE, i);
            print(Festival.getInstance().getSimpleDateFormat().format(calendar.getTime()));
            if (calendar.get(Calendar.DAY_OF_WEEK) == week) {
                count++;
                if (count == indexWeek) {
                    break;
                }
            }
        }
        return calendar.getTime();
    }

    private static void print(String s) {
        System.out.println(s);
    }

}
