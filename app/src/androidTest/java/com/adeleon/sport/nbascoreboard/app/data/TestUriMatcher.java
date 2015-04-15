package com.adeleon.sport.nbascoreboard.app.data;

/**
 * Created by theade on 4/13/2015.
 */

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/*
    Uncomment this class when you are ready to test your UriMatcher.  Note that this class utilizes
    constants that are declared with package protection inside of the UriMatcher, which is why
    the test must be in the same data package as the Android app code.  Doing the test this way is
    a nice compromise between data hiding and testability.
 */
public class TestUriMatcher extends AndroidTestCase {
    private static final String TEAM_QUERY = "charlotte-hornets";
    private static final String TEST_DATE = "2015-03-27T19:00:00-04:00";
    //private static final long TEST_LOCATION_ID = 10L;

    // content://com.example.android.sunshine.app/weather"
    private static final Uri TEST_EVENT_DIR = ScoreboardContract.EventEntry.CONTENT_URI;
    private static final Uri TEST_EVENT_WITH_TEAM_DIR = ScoreboardContract.EventEntry.buildEventTeam(TEAM_QUERY);
    private static final Uri TEST_EVENT_WITH_TEAM_AND_DATE_DIR = ScoreboardContract.EventEntry.buildEventTeamWithStartDate(TEAM_QUERY, TEST_DATE);
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
        assertEquals("Error: The EVENT WITH TEAM URI was matched incorrectly.",
                testMatcher.match(TEST_EVENT_WITH_TEAM_DIR), ScoreboardProvider.EVENT_WITH_TEAM);
        assertEquals("Error: The EVENT WITH TEAM AND DATE URI was matched incorrectly.",
                testMatcher.match(TEST_EVENT_WITH_TEAM_AND_DATE_DIR), ScoreboardProvider.EVENT_WITH_TEAM_AND_DATE);
        assertEquals("Error: The TEAM URI was matched incorrectly.",
                testMatcher.match(TEST_TEAM_DIR), ScoreboardProvider.TEAM);
    }
}