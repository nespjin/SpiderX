/*
 * Copyright (c) 2020-2022.  NESP Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesp.sdk.java.util.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Author: <a href="mailto:jinzhaolu@numob.com">Jack Email:jinzhaolu@numob.com</a>
 * Time: Created 2021/12/9 17:07
 * Description:
 * <p>
 * 阴历
 **/
// @TODO: 需要重构
@Deprecated
public class Lunar {

    private final int mYear;
    private final int mMonth;
    private final int mDay;

    private boolean mLeap;

    private final static String[] CHINESE_NUMBER = {
            "一", "二", "三", "四", "五", "六",
            "七", "八", "九", "十", "十一", "十二"
    };

    private static final SimpleDateFormat CHINESE_DATE_FORMAT =
            new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);

    final static long[] lunarInfo = new long[]{
            0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
            0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,
            0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
            0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
            0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,
            0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0,
            0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,
            0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6,
            0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,
            0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,
            0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,
            0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,
            0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,
            0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,
            0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0
    };


    /**
     * @param year 某一年
     * @return 农历某一年的总天数
     */
    private static int daysOfYear(int year) {
        int i, sum = 348;
        for (i = 0x8000; i > 0x8; i >>= 1) {
            if ((lunarInfo[year - 1900] & i) != 0) sum += 1;
        }
        return (sum + leapDaysOfYear(year));
    }


    /**
     * @param year 某一年
     * @return 某一年年闰月的天数
     */
    private static int leapDaysOfYear(int year) {
        if (leapMonth(year) != 0) {
            if ((lunarInfo[year - 1900] & 0x10000) != 0)
                return 30;
            else
                return 29;
        } else
            return 0;
    }

    /**
     * @param year 某一年
     * @return 某一年闰哪个月 1-12 , 没闰传回 0
     */
    private static int leapMonth(int year) {
        return (int) (lunarInfo[year - 1900] & 0xf);
    }

    /**
     * @param year  某一年
     * @param month 某个月
     * @return 某一年的某个月的总天数
     */
    private static int daysOfYearMonth(int year, int month) {
        if ((lunarInfo[year - 1900] & (0x10000 >> month)) == 0)
            return 29;
        else
            return 30;
    }


    //====== 传回农历 y年的生肖

    /**
     * @return 生肖
     */
    public final String animalsYear() {
        final String[] Animals = new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
        return Animals[(mYear - 4) % 12];
    }


    //====== 传入 月日的offset 传回干支, 0=甲子
    private static String cyclicalm(int num) {
        final String[] Gan = new String[]{"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
        final String[] Zhi = new String[]{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
        return (Gan[num % 10] + Zhi[num % 12]);
    }


    //====== 传入 offset 传回干支, 0=甲子
    public final String cyclical() {
        int num = mYear - 1900 + 36;
        return (cyclicalm(num));
    }

    public String getLunarMonthString() {
        return null;
    }


    public Lunar() {
        Calendar cal = Calendar.getInstance();
        @SuppressWarnings("unused") int yearCyl, monCyl, dayCyl;
        int leapMonth = 0;
        Date baseDate = null;
        try {
            baseDate = CHINESE_DATE_FORMAT.parse("1900年1月31日");
        } catch (ParseException e) {
            e.printStackTrace(); //To change body of catch statement use Options | File Templates.
        }


        //求出和1900年1月31日相差的天数
        int offset = (int) ((cal.getTime().getTime() - baseDate.getTime()) / 86400000L);
        dayCyl = offset + 40;
        monCyl = 14;


        //用offset减去每农历年的天数
        // 计算当天是农历第几天
        //i最终结果是农历的年份
        //offset是当年的第几天
        int iYear, daysOfYear = 0;
        for (iYear = 1900; iYear < 2050 && offset > 0; iYear++) {
            daysOfYear = daysOfYear(iYear);
            offset -= daysOfYear;
            monCyl += 12;
        }
        if (offset < 0) {
            offset += daysOfYear;
            iYear--;
            monCyl -= 12;
        }
        //农历年份
        mYear = iYear;

        yearCyl = iYear - 1864;
        leapMonth = leapMonth(iYear); //闰哪个月,1-12
        mLeap = false;


        //用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
        int iMonth, daysOfMonth = 0;
        for (iMonth = 1; iMonth < 13 && offset > 0; iMonth++) {
            //闰月
            if (leapMonth > 0 && iMonth == (leapMonth + 1) && !mLeap) {
                --iMonth;
                mLeap = true;
                daysOfMonth = leapDaysOfYear(mYear);
            } else
                daysOfMonth = daysOfYearMonth(mYear, iMonth);


            offset -= daysOfMonth;
            //解除闰月
            if (mLeap && iMonth == (leapMonth + 1)) mLeap = false;
            if (!mLeap) monCyl++;
        }

        //offset为0时，并且刚才计算的月份是闰月，要校正
        if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
            if (mLeap) {
                mLeap = false;
            } else {
                mLeap = true;
                --iMonth;
                --monCyl;
            }
        }

        //offset小于0时，也要校正
        if (offset < 0) {
            offset += daysOfMonth;
            --iMonth;
            --monCyl;
        }
        mMonth = iMonth;
        mDay = offset + 1;
    }


    public static String getChinaDayString(int day) {
        String[] chineseTen = {"初", "十", "廿", "三"};
        int n = day % 10 == 0 ? 9 : day % 10 - 1;
        if (day > 30)
            return "";
        if (day == 10)
            return "初十";
        else
            return chineseTen[day / 10] + CHINESE_NUMBER[n];
    }


    public String toString() {
        return (mLeap ? "闰" : "") + CHINESE_NUMBER[mMonth - 1] + "月" + getChinaDayString(mDay);
    }

}
