package com.efit.sport.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtis {
    public final static int JUST_NOW_THRES_SEC = 2 * 60;

    /**
     * @return the number of seconds since Jan. 1, 1970, midnight GMT.
     */
    public static int getTimestampInSec(String hour, String min) {
        return getTimestampInSec(Integer.valueOf(hour), Integer.valueOf(min));
    }

    /**
     * @return the number of seconds since Jan. 1, 1970, midnight GMT.
     */
    public static int getTimestampInSec(int hour, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        int timestamp = (int) (calendar.getTimeInMillis() / 1000);
        return timestamp;
    }


    /**
     * @param timeInSecs
     *      the number of seconds since Jan. 1, 1970, midnight GMT.
     * @return
     */
    public static String getHourMin(long timeInSecs) {
        Calendar calendar = getCalendar(timeInSecs);
        return formatDate("HH:mm", calendar);
    }

    /**
     * @param timeInSecs
     *      the number of seconds since Jan. 1, 1970, midnight GMT.
     * @return
     */
    public static String getMinSec(long timeInSecs) {
        Calendar calendar = getCalendar(timeInSecs);
        return formatDate("mm:ss", calendar);
    }


    public static String formatDate(String format, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(calendar.getTime());
    }


    /**
     * 获取小时(24小时制)
     * @param timeInSecs
     *      the number of seconds since Jan. 1, 1970, midnight GMT.
     * @return
     */
    public static String getHour(long timeInSecs) {
        Calendar calendar = getCalendar(timeInSecs);
        return String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
    }

    /**
     * 获取月份( 1 -12)
     * @param timeInSecs
     *      the number of seconds since Jan. 1, 1970, midnight GMT.
     * @return
     */
    public static int getMonth(long timeInSecs) {
        Calendar calendar = getCalendar(timeInSecs);
        return calendar.get(Calendar.MONTH) +1;
    }


    /**
     * 获取日期(1-31)
     * @param timeInSecs
     *      the number of seconds since Jan. 1, 1970, midnight GMT.
     * @return
     */
    public static int getDayOfMonth(long timeInSecs) {
        Calendar calendar = getCalendar(timeInSecs);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取年份(2015)
     * @param timeInSecs
     *      the number of seconds since Jan. 1, 1970, midnight GMT.
     * @return
     */
    public static int getYear(long timeInSecs) {
        Calendar calendar = getCalendar(timeInSecs);
        return calendar.get(Calendar.YEAR);
    }

    public static String getHourMin(int hour, int min) {
        return String.format("%2d%2d", hour, min);
    }

    public static String showCanonicalTime(long timeInSecs) {
        long nowSecs = System.currentTimeMillis() / 1000;
        if (nowSecs - timeInSecs < JUST_NOW_THRES_SEC) {
            return "刚刚";
        } else if (nowSecs - timeInSecs < 60 * 60) {
            return String.format("%d分钟前", (nowSecs - timeInSecs) / 60);
        } else if (isInSameDay(nowSecs, timeInSecs)) {
            return getHourMin(timeInSecs);
        } else if (isYesterday(nowSecs, timeInSecs)) {
            return "昨天 " + getHourMin(timeInSecs);
        } else if (getYear(nowSecs) == getYear(timeInSecs)) {
            return getMonth(timeInSecs) + "月" + getDayOfMonth(timeInSecs) +"日";
        } else {
            return getYear(timeInSecs) +"年";
        }
    }


    public static boolean isSameMonth(long secsLater, long secsEarly) {
        long secsDiff = secsLater - secsEarly;
        if (secsDiff > (31 * 24 * 3600)) {
            return false;
        }
        return getMonth(secsLater) == getMonth(secsEarly);
    }

    public static boolean isSameYear(long secsLater, long secsEarly) {
        return new Date(secsLater * 1000).getYear() == new Date(secsEarly * 1000).getYear();
    }

    public static boolean isInSameDay(long secsLater, long secsEarly) {
        return isInSerervalDays(secsLater, secsEarly, 0);
    }

    public static boolean isYesterday(long secsLater, long secsEarly) {
        return isInSerervalDays(secsLater, secsEarly, 1);
    }

    /**
     * 判断两个时间点是否在一定天数内<br>
     * 返回true表示两个时间点相差n天以内
     *
     * @param secsLater 较新时间
     * @param secsEarly 较早时间
     * @param daysDiff  相差天数， 0：同一天, 1:相差一天
     * @return
     */
    public static boolean isInSerervalDays(long secsLater, long secsEarly, int daysDiff) {
        Date dateLater = new Date(secsLater * 1000);
        dateLater.getYear();
        int hour = dateLater.getHours();
        int min = dateLater.getMinutes();
        int sec = dateLater.getSeconds();
        long time = hour * 3600 + min * 60 + sec;
        if ((secsLater - time - secsEarly) < (24 * daysDiff * 3600)) {
            return true;
        }
        return false;
    }

    /**
     * @param timeInSecs the number of seconds since Jan. 1, 1970, midnight GMT.
     * @return
     */
    public static Calendar getCalendar(long timeInSecs) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInSecs * 1000);
        return c;
    }


    public static int getHours(long secs) {
        int hour = (int) (secs / 3600);
        if (hour > 24) {
            hour -= 24;
        }
        return hour;
    }

    public static int getMins(long secs) {
        return (int) ((secs % 3600) / 60);
    }

    public static String getHourMinSec(long timeInMills) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(timeInMills);
        return sdf.format(date);
    }

    public final static String[] WEEKDAYS = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};


    public static String getRepeatDays(boolean[] week) {

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        int count = 0;
        for (int i = 0; i < week.length; i++) {
            if (week[i]) {
                if (first) {
                    first = false;
                    sb.append(WEEKDAYS[i]);
                } else {
                    sb.append("  " + WEEKDAYS[i]);
                }
                count++;
            }
        }

        if (count == 0) {
            sb.append("从不");
        } else if (count == 7) {
            sb = new StringBuilder("每天");
        }
        return sb.toString();
    }

    /**
     * 设置间隔时间格式，如果超过一小时为 xx:xx:xx,小于一小时则为xx:xx
     * @param secs
     * @return
     */
    public static String formatIntervalTime(int secs) {
        if(secs > 3600) {
            int hour = secs / 3600;
            int min = (secs %3600) / 60;
            int sec = secs % 60;
            return hour + ":" + min+":"+sec;
        } else {
            int min = secs/ 60;
            int sec = secs % 60;
            return min+":"+sec;
        }
    }


    /**
     * 设置间隔时间格式，如果超过一小时为 xx:xx:xx,小于一小时则为xx:xx
     * @param secs
     * @return
     */
    public static String formatTimeDuration(int secs) {
        if(secs > 3600) {
            int hour = secs / 3600;
            int min = (secs %3600) / 60;
            int sec = secs % 60;
            return String.format("%02d:%02d:%02d",hour,min,sec);
        } else {
            int min = secs/ 60;
            int sec = secs % 60;
            return String.format("%02d:%02d",min,sec);
        }
    }
}
