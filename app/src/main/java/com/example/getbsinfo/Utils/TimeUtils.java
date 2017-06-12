package com.example.getbsinfo.Utils;


import java.util.Calendar;

/**
 * Created by DK on 2017/6/12.
 */

public class TimeUtils {
    public static String getCurrentTime(){

        Calendar mCalendar = Calendar.getInstance();

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH)+1;
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);
        int second = mCalendar.get(Calendar.SECOND);
        return (year+"."+month+"."+day+"."+hour+":"+minute+"'"+second+"\"");

    }
}
