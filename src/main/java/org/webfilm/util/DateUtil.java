package org.webfilm.util;

import org.webfilm.entity.ParsedConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private static final String DATE_FORMAT = "dd-M-yyyy HH:mm:ss";

    public static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    public static String convertStringDate(long millisecond) {
        return sdf.format(new Date(millisecond));
    }

    public static long convertMillisecond(String time) {
        try {
            return sdf.parse(time).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    public static boolean isChannelUpToDate(long crawlerTime, String time) {
        if (time == null || time.isEmpty()) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        long updatedTime = DateUtil.convertMillisecond(time);
        return currentTime - updatedTime < crawlerTime;
    }
}
