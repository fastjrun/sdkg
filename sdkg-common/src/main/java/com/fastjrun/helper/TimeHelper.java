package com.fastjrun.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeHelper {
    public final static String DF14 = "DF14";

    private final static String FORMAT_DF14 = "yyyyMMddHHmmss";

    public final static String DF17 = "DF17";

    private final static String FORMAT_DF17 = "yyyyMMddHHmmssSSS";

    public static String getCurrentTime(String format) {
        Calendar calendar = Calendar.getInstance();
        return getTimeInFormat(calendar, format);
    }

    public static String getFormatDate(Date date, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getTimeInFormat(calendar, format);
    }

    private static String getTimeInFormat(Calendar calendar, String format) {
        DateFormat df = new SimpleDateFormat(FORMAT_DF14);
        if (format.equals(DF14)) {
            df = new SimpleDateFormat(FORMAT_DF14);
        } else if (format.equals(DF17)) {
            df = new SimpleDateFormat(FORMAT_DF17);
        }
        return df.format(calendar.getTime());
    }

    public static Date getDateInFormat(String time, String format) {
        Calendar calendar = Calendar.getInstance();
        if (format.equals(DF14)) {
            calendar.set(Integer.parseInt(time.substring(0, 4)),
                    Integer.parseInt(time.substring(4, 6)),
                    Integer.parseInt(time.substring(6, 8)),
                    Integer.parseInt(time.substring(8, 10)),
                    Integer.parseInt(time.substring(10, 12)),
                    Integer.parseInt(time.substring(12)));
        } else if (format.equals(DF17)) {
            calendar.set(Integer.parseInt(time.substring(0, 4)),
                    Integer.parseInt(time.substring(4, 6)),
                    Integer.parseInt(time.substring(6, 8)),
                    Integer.parseInt(time.substring(8, 10)),
                    Integer.parseInt(time.substring(10, 12)),
                    Integer.parseInt(time.substring(12, 14)));
            calendar.set(Calendar.MILLISECOND,
                    Integer.parseInt(time.substring(14)));
        }
        return calendar.getTime();
    }

    public static Date getOffsetDate(Date date, int timeMeasurement, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int measurement = 0;
        if (timeMeasurement == Calendar.SECOND) {
            measurement = Calendar.SECOND;
        } else if (timeMeasurement == Calendar.MINUTE) {
            measurement = Calendar.MINUTE;
        } else if (timeMeasurement == Calendar.HOUR) {
            measurement = Calendar.HOUR;
        } else if (timeMeasurement == Calendar.DATE) {
            measurement = Calendar.DATE;
        } else if (timeMeasurement == Calendar.MONTH) {
            measurement = Calendar.MONTH;
        } else if (timeMeasurement == Calendar.YEAR) {
            measurement = Calendar.YEAR;
        }
        calendar.add(measurement, amount);
        return calendar.getTime();
    }

}
