package com.adeleon.sport.nbascoreboard.app.data;

/**
 * Created by theade on 4/11/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.adeleon.sport.nbascoreboard.app.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your ScoreboardContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }


    static ContentValues createEventValues(long teamAwayRowId, long teamHomeRowId) {
        ContentValues eventValues = new ContentValues();
        eventValues.put(ScoreboardContract.EventEntry.COLUMN_AWAY_TEAM_ID_KEY , teamAwayRowId);
        eventValues.put(ScoreboardContract.EventEntry.COLUMN_HOME_TEAM_ID_KEY, teamHomeRowId);
        eventValues.put(ScoreboardContract.EventEntry.COLUMN_EVENT_ID , "20150327-charlotte-hornets-at-washington-wizards");
        eventValues.put(ScoreboardContract.EventEntry.COLUMN_EVENT_STATUS , "completed");
        eventValues.put(ScoreboardContract.EventEntry.COLUMN_START_DATE_TIME , "2015-03-27T19:00:00-04:00");
        eventValues.put(ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_FIRTS , 30);
        eventValues.put(ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_SECOND, 24);
        eventValues.put(ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_THIRD, 33);
        eventValues.put(ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_FOURTH , 24 );
        eventValues.put(ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_FIRTS , 27);
        eventValues.put(ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_SECOND , 26);
        eventValues.put(ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_THIRD , 23);
        eventValues.put(ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_FOURTH , 21);

        return eventValues;
    }

    /*
        Students: You can uncomment this helper function once you have finished creating the
        TeamEntry part of the ScoreboardContract.
     */
    static ContentValues createAwayTeamValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testAwayValues = new ContentValues();
        testAwayValues.put(ScoreboardContract.TeamEntry.COLUMN_TEAM_ID , "charlotte-hornets");
        testAwayValues.put(ScoreboardContract.TeamEntry.COLUMN_FIRST_NAME_TEAM, "Charlotte");
        testAwayValues.put(ScoreboardContract.TeamEntry.COLUMN_LAST_NAME_TEAM ,"Hornets");
        testAwayValues.put(ScoreboardContract.TeamEntry.COLUMN_ABBREVIATION, "CHA");
        testAwayValues.put(ScoreboardContract.TeamEntry.COLUMN_CITY , "Charlotte");
        testAwayValues.put(ScoreboardContract.TeamEntry.COLUMN_STATE , "North Carolina");
        testAwayValues.put(ScoreboardContract.TeamEntry.COLUMN_SITE_NAME , "Time Warner Cable Arena");

        return testAwayValues;
    }

    static ContentValues createHomeTeamValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testHomeValues = new ContentValues();
        testHomeValues.put(ScoreboardContract.TeamEntry.COLUMN_TEAM_ID , "washington-wizards");
        testHomeValues.put(ScoreboardContract.TeamEntry.COLUMN_FIRST_NAME_TEAM, "Washington");
        testHomeValues.put(ScoreboardContract.TeamEntry.COLUMN_LAST_NAME_TEAM , "Wizards");
        testHomeValues.put(ScoreboardContract.TeamEntry.COLUMN_ABBREVIATION, "WAS");
        testHomeValues.put(ScoreboardContract.TeamEntry.COLUMN_CITY , "Washington");
        testHomeValues.put(ScoreboardContract.TeamEntry.COLUMN_STATE , "District of Columbia");
        testHomeValues.put(ScoreboardContract.TeamEntry.COLUMN_SITE_NAME ,"Verizon Center");

        return testHomeValues;
    }

    /*
        Students: You can uncomment this function once you have finished creating the
        TeamEntry part of the ScoreboardContract as well as the ScoreboardDbHelper.
     */
    static long insertTeamAwayValues(Context context) {
        // insert our test records into the database
        ScoreboardDbHelper dbHelper = new ScoreboardDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testAwayValues = TestUtilities.createAwayTeamValues();

        long teamAwayRowId;
        teamAwayRowId = db.insert(ScoreboardContract.TeamEntry.TABLE_NAME, null, testAwayValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", teamAwayRowId != -1);

        return teamAwayRowId;
    }

    static long insertTeamHomeValues(Context context) {
        // insert our test records into the database
        ScoreboardDbHelper dbHelper = new ScoreboardDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testHomeValues = TestUtilities.createHomeTeamValues();

        long teamHomeRowId;
        teamHomeRowId = db.insert(ScoreboardContract.TeamEntry.TABLE_NAME, null, testHomeValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", teamHomeRowId != -1);

        return teamHomeRowId;
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.
        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}