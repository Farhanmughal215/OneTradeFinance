package com.xstocks.uc.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public class DateUtils {

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String format(String pattern, final Date date) {
        if (null == pattern)
            pattern = YYYY_MM_DD_HH_MM_SS;
        return parseDateToStr(pattern, date);
    }

    public static String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date formatDateToMin(final Date date) {
        String pattern = YYYY_MM_DD_HH_MM_SS;
        try {
            String s = format(YYYY_MM_DD, date) + " 00:00:00";
            return new SimpleDateFormat(pattern).parse(s);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date getWeekStartDay() {
        String pattern = YYYY_MM_DD_HH_MM_SS;
        LocalDate today = LocalDate.now(); // 获取当前日期
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)); // 调整日期为本周的星期一
        return Date.from(weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
