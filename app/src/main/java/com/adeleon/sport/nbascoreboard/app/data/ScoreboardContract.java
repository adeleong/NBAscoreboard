package com.adeleon.sport.nbascoreboard.app.data;

import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by theade on 3/31/2015.
 */

/**
 * Defines table and column names for the weather database.
 */
public class ScoreboardContract {

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /*
        Inner class that defines the contents of the location table
     */
    public static final class EventEntry implements BaseColumns {

        public static final String TABLE_NAME = "event";

        public static final String COLUMN_EVENT_ID = "event_id";
        public static final String COLUMN_TEAM_ID = "team_id";
        public static final String COLUMN_START_DATE_TIME = "start_date_time";
        public static final String COLUMN_EVENT_STATUS = "event_status";
        public static final String COLUMN_ABBREVIATION = "abbreviation";
        public static final String COLUMN_FIRST_NAME_TEAM = "first_name_team";
        public static final String COLUMN_LAST_NAME_TEAM = "last_name_team";
        public static final String COLUMN_TEAM_HOME = "team_home";
        public static final String COLUMN_PERIOD_SCORE_FIRTS = "period_score_firts";
        public static final String COLUMN_PERIOD_SCORE_SECOND = "period_score_second";
        public static final String COLUMN_PERIOD_SCORE_THIRD = "period_score_third";
        public static final String COLUMN_PERIOD_SCORE_FOURTH = "period_score_fourth";

    }






    /* Inner class that defines the contents of the weather table */
    public static final class EventPlayerEntry implements BaseColumns {

        public static final String TABLE_NAME = "event_player";
        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed  mph
        public static final String COLUMN_WIND_SPEED = "wind";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";
    }
}