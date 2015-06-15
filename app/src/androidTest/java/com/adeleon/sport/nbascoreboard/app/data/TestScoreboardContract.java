package com.adeleon.sport.nbascoreboard.app.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by theade on 4/13/2015.
 */

/*
    Students: This is NOT a complete test for the WeatherContract --- just for the functions
    that we expect you to write.
 */
public class TestScoreboardContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final String TEST_SCORE_TEAM = "/charlotte-hornets";
    private static final String TEST_EVENT_DATE = "2015-03-27T00:00:00-04:00";

    /*
        Students: Uncomment this out to test your weather location function.
     */
    public void testBuildEventTeam() {
        Uri eventUri = ScoreboardContract.EventEntry.buildEventDate(TEST_EVENT_DATE);
        assertNotNull("Error: Null Uri returned.  You must fill-in BuildEventTeam in " +
                        "ScoreboardContract.",
                eventUri);
        assertEquals("Error: Event team not properly appended to the end of the Uri",
                TEST_EVENT_DATE, eventUri.getLastPathSegment());
        assertEquals("Error: Event Id and Date Uri doesn't match our expected result",
                eventUri.toString(),
                "content://com.adeleon.sport.nbascoreboard.app/event/2015-03-27T00:00:00-04:00");
    }
}
