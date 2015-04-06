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


    /*
        Inner class that defines the contents of the Event table
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

    /* Inner class that defines the contents of the EventPlayer table */
    public static final class EventPlayerEntry implements BaseColumns {

        public static final String TABLE_NAME = "event_player";
        public static final String COLUMN_EVENT_ID_KEY = "event_id";
        public static final String COLUMN_EVENT_TEAM_ID_KEY = "event_team_id";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_POSITION = "position";
        public static final String COLUMN_MINUTES = "minutes";
        public static final String COLUMN_POINTS = "points";
        public static final String COLUMN_ASSISTS = "assists";
        public static final String COLUMN_BLOCKS = "blocks";
        public static final String COLUMN_REBOUNDS = "rebounds";

    }
}