package com.adeleon.sport.nbascoreboard.app.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by theade on 4/26/2015.
 */
public class ScoreUtil {

    private String getReadableDateString(long time) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    public static String getCurrentDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH,-1);
        return formatter.format( cal.getTime());
    }
}
