package com.adeleon.sport.nbascoreboard.app.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.adeleon.sport.nbascoreboard.app.data.ScoreboardContract.TeamEntry;
import com.adeleon.sport.nbascoreboard.app.data.ScoreboardContract.EventEntry;
import com.adeleon.sport.nbascoreboard.app.data.ScoreboardContract.EventPlayerEntry;

/**
 * Created by theade on 3/31/2015.
 */

/**
 * Manages a local database for weather data.
 */
public class ScoreboardDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "scoreboard.db";

    public ScoreboardDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TEAM_TABLE = "CREATE TABLE " + TeamEntry.TABLE_NAME + " (" +
                TeamEntry.COLUMN_TEAM_ID + " TEXT PRIMARY KEY," +
                TeamEntry.COLUMN_FIRST_NAME_TEAM +  " TEXT NOT NULL," +
                TeamEntry.COLUMN_LAST_NAME_TEAM + " TEXT NOT NULL, " +
                TeamEntry.COLUMN_ABBREVIATION + " TEXT NOT NULL, " +
                TeamEntry.COLUMN_SITE_NAME + " TEXT, " +
                TeamEntry.COLUMN_CITY + "TEXT, " +
                TeamEntry.COLUMN_STATE + "TEXT "+
                " );";


        final String SQL_CREATE_EVENT_TABLE = "CREATE TABLE " + EventEntry.TABLE_NAME + " (" +
                EventEntry.COLUMN_EVENT_ID + " TEXT PRIMARY KEY," +
                EventEntry.COLUMN_START_DATE_TIME +  " TEXT  NOT NULL," +
                EventEntry.COLUMN_EVENT_STATUS + " TEXT NOT NULL, " +
                EventEntry.COLUMN_AWAY_TEAM_ID_KEY + " TEXT NOT NULL, " +
                EventEntry.COLUMN_HOME_TEAM_ID_KEY + " TEXT NOT NULL, " +
                EventEntry.COLUMN_AWAY_PERIOD_FIRTS + " INTEGER NOT NULL " +
                EventEntry.COLUMN_AWAY_PERIOD_SECOND + " INTEGER NOT NULL " +
                EventEntry.COLUMN_AWAY_PERIOD_THIRD + " INTEGER NOT NULL " +
                EventEntry.COLUMN_AWAY_PERIOD_FOURTH + " INTEGER NOT NULL " +
                EventEntry.COLUMN_HOME_PERIOD_FIRTS + " INTEGER NOT NULL " +
                EventEntry.COLUMN_HOME_PERIOD_SECOND + " INTEGER NOT NULL " +
                EventEntry.COLUMN_HOME_PERIOD_THIRD + " INTEGER NOT NULL " +
                EventEntry.COLUMN_HOME_PERIOD_FOURTH + " INTEGER NOT NULL " +
                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + EventEntry.COLUMN_AWAY_TEAM_ID_KEY + ") REFERENCES " +
                TeamEntry.TABLE_NAME + " (" + TeamEntry.COLUMN_TEAM_ID + "), " +
                " FOREIGN KEY (" + EventEntry.COLUMN_HOME_TEAM_ID_KEY + ") REFERENCES " +
                TeamEntry.TABLE_NAME + " (" + TeamEntry.COLUMN_TEAM_ID + "), " +
                " UNIQUE (" + EventEntry.COLUMN_START_DATE_TIME + ", " +
                EventEntry.COLUMN_EVENT_ID + ", "+EventEntry.COLUMN_EVENT_STATUS +" ) ON CONFLICT REPLACE);";


        final String SQL_CREATE_EVENT_PLAYER_TABLE = "CREATE TABLE " + EventPlayerEntry.TABLE_NAME + " (" +

                EventPlayerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                //EventPlayerEntry.COLUMN_EVENT_PLAYER_ID + " INTEGER NOT NULL, " +
                EventPlayerEntry.COLUMN_EVENT_ID_KEY + " TEXT NOT NULL, " +
                EventPlayerEntry.COLUMN_TEAM_ID_KEY + " TEXT NOT NULL," +
                EventPlayerEntry.COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
                EventPlayerEntry.COLUMN_LAST_NAME + " TEXT NOT NULL, " +
                EventPlayerEntry.COLUMN_POSITION + " TEXT NOT NULL, " +
                EventPlayerEntry.COLUMN_MINUTES + " INTEGER NOT NULL, " +
                EventPlayerEntry.COLUMN_POINTS + " INTEGER NOT NULL, " +
                EventPlayerEntry.COLUMN_ASSISTS + " INTEGER NOT NULL, " +
                EventPlayerEntry.COLUMN_BLOCKS + " INTEGER NOT NULL, " +
                EventPlayerEntry.COLUMN_REBOUNDS + " INTEGER NOT NULL, " +
                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + EventPlayerEntry.COLUMN_EVENT_ID_KEY + ") REFERENCES " +
                EventEntry.TABLE_NAME + " (" + EventEntry._ID + "), " +
                " FOREIGN KEY (" + EventPlayerEntry.COLUMN_TEAM_ID_KEY + ") REFERENCES " +
                TeamEntry.TABLE_NAME + " (" + TeamEntry._ID + "), " +

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + EventPlayerEntry.COLUMN_EVENT_ID_KEY + ", "
                            +  EventPlayerEntry.COLUMN_TEAM_ID_KEY + ", "
                            + EventPlayerEntry.COLUMN_FIRST_NAME  + ", "
                            + EventPlayerEntry.COLUMN_LAST_NAME +
                ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_TEAM_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_EVENT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_EVENT_PLAYER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TeamEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EventPlayerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}