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
        public static final String COLUMN_TEAM_ID = "city_name";
        public static final String COLUMN_START_DATE_TIME = "coord_lat";
        public static final String COLUMN_EVENT_STATUS = "coord_long";
        public static final String COLUMN_ABBREVIATION = "coord_long";
        public static final String COLUMN_FIRST_NAME_TEAM = "coord_long";
        public static final String COLUMN_LAST_NAME_TEAM = "coord_long";
        public static final String COLUMN_TEAM_HOME = "coord_long";
        public static final String COLUMN_PERIOD_SCORE_FIRTS = "coord_long";


        /*EVENT_ID: "20150325-brooklyn-nets-at-charlotte-hornets",
        EVENT_STATUS: "completed",
        START_DATE_TIME: "2015-03-25T19:00:00-04:00",
        TEAM_ID: "brooklyn-nets",
        ABBREVIATION: "BKN",
        FIRST_NAME_TEAM": "BROOKLYN",
        last_name_team": "Nets",
        POINTS_SCORED: 95
        PERIOD_SCORES:[0,1,2,3]
        TEAM_HOME {1 , 0}

        event_id: "20150325-brooklyn-nets-at-charlotte-hornets",
        event_status: "completed",
        start_date_time: "2015-03-25T19:00:00-04:00",
        team_id: "brooklyn-nets",
        abbreviation: "BKN",
        first_name_team": "Brooklyn",
        last_name_team": "Nets",
        points_scored: 95
        period_scores:[0,1,2,3]
        team_home {1 , 0}*/


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