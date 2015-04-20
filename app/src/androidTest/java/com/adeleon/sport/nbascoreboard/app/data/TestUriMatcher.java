package com.adeleon.sport.nbascoreboard.app.data;

/**
 * Created by theade on 4/13/2015.
 */

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;


public class TestUriMatcher extends AndroidTestCase {
    private static final String EVENT_ID_QUERY = "20150327-charlotte-hornets-at-washington-wizards";
    private static final String EVENT_DATE_QUERY = "2015-03-27T19:00:00-04:00";

    // content://com.example.android.sunshine.app/weather"
    private static final Uri TEST_EVENT_DIR = ScoreboardContract.EventEntry.CONTENT_URI;
    private static final Uri TEST_EVENT_WITH_EVENT_ID_DIR = ScoreboardContract.EventEntry.buildEvetIdAndDateUri(EVENT_ID_QUERY,EVENT_DATE_QUERY);
    private static final Uri TEST_EVENT_WITH_EVENT_DATE_DIR = ScoreboardContract.EventEntry.buildEventDate(EVENT_DATE_QUERY);
    // content://com.example.android.sunshine.app/location"
    private static final Uri TEST_TEAM_DIR = ScoreboardContract.TeamEntry.CONTENT_URI;

    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = ScoreboardProvider.buildUriMatcher();

        assertEquals("Error: The EVENT URI was matched incorrectly.",
                testMatcher.match(TEST_EVENT_DIR), ScoreboardProvider.EVENT);
        assertEquals("Error: The EVENT WITH EVENT ID URI was matched incorrectly.",
                testMatcher.match(TEST_EVENT_WITH_EVENT_ID_DIR), ScoreboardProvider.EVENT_WITH_EVENT_ID_AND_EVENT_DATE);
        assertEquals("Error: The EVENT WITH EVENT DATE URI was matched incorrectly.",
                testMatcher.match(TEST_EVENT_WITH_EVENT_DATE_DIR), ScoreboardProvider.EVENT_WITH_EVENT_DATE);
        assertEquals("Error: The TEAM URI was matched incorrectly.",
                testMatcher.match(TEST_TEAM_DIR), ScoreboardProvider.TEAM);
    }
}