/*
 * Copyright (c) 2021-2022.  NESP Technology.
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

package com.nesp.sdk.java.util;

import java.util.Date;

/**
 * Team: NESP Technology
 *
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * @version 1.0
 * Time: Created 2021/12/11 1:22
 * Description:
 **/
public final class DateRange {

    private final Date mStartDate;
    private final Date mEndDate;

    public DateRange(final Date startDate, final Date endDate) {
        mStartDate = startDate;
        mEndDate = endDate;
        if (mStartDate.after(mEndDate)) {
            throw new IllegalArgumentException("start date must > end date");
        }
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public static DateRange someDayDateRange(final Date someDay) {
        return new DateRange(DateTimeUtil.beginOfSomeDay(someDay), DateTimeUtil.endOfSomeDay(someDay));
    }

    public static DateRange todayDateRange() {
        return new DateRange(DateTimeUtil.beginOfToday(), DateTimeUtil.endOfToday());
    }

    public static DateRange dateRangeOfRelativeToday(final int amount) {
        return new DateRange(DateTimeUtil.beginOfRelativeToday(amount),
                DateTimeUtil.beginOfRelativeToday(amount));
    }

    public static DateRange yesterdayDateRange() {
        return new DateRange(DateTimeUtil.beginOfYesterday(), DateTimeUtil.endOfYesterday());
    }

    public static DateRange tomorrowDateRange() {
        return new DateRange(DateTimeUtil.beginOfTomorrow(), DateTimeUtil.endOfTomorrow());
    }

    /**
     * @param amount 最近几天 正数
     * @return 最近几天的日期范围
     */
    public static DateRange dateRangeOfRecentSomeDays(final int amount) {
        return new DateRange(DateTimeUtil.beginOfRelativeToday(-Math.abs(amount)),
                DateTimeUtil.endOfYesterday());
    }

    /**
     * @return 最近7天
     */
    public static DateRange dateRangeOfRecentSevenDays() {
        return dateRangeOfRecentSomeDays(7);
    }

    /**
     * @return 最近三十天
     */
    public static DateRange dateRangeOfRecentThirtyDays() {
        return dateRangeOfRecentSomeDays(30);
    }

}
