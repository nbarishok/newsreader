package com.test.news.utilities;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nikita on 07.10.2014.
 */
public class DateTimeUtilities {
    public static String getDateTimeNowString(){
        SimpleDateFormat date = new SimpleDateFormat("HH:mm");
        String formattedDate = date.format(Calendar.getInstance().getTime());
        return formattedDate;
    }

    public static String parseTimestamp(int timestamp) {
        String format = DateUtils.isToday(timestamp) ? "HH:mm" : "E";
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date(timestamp));
    }

    private static long getDaysDifference(Date date1, Date date2){
        long diff = date1.getTime() - date2.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return days;
    }

}
