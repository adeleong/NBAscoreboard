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
    private static final String TEST_SCORE_DATE = "2015-03-27T19:00:00-04:00";

    /*
        Students: Uncomment this out to test your weather location function.
     */
    public void testBuildEventTeam() {
        Uri teamUri = ScoreboardContract.EventEntry.buildEventTeam(TEST_SCORE_TEAM);
        assertNotNull("Error: Null Uri returned.  You must fill-in BuildEventTeam in " +
                        "ScoreboardContract.",
                teamUri);
        assertEquals("Error: Event team not properly appended to the end of the Uri",
                TEST_SCORE_TEAM, teamUri.getLastPathSegment());
        assertEquals("Error: Event team Uri doesn't match our expected result",
                teamUri.toString(),
                "content://com.adeleon.sport.nbascoreboard.app/event/%2Fcharlotte-hornets");
    }
}
