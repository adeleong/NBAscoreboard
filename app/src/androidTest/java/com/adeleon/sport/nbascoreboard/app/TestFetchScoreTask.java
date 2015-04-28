package com.adeleon.sport.nbascoreboard.app;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.test.AndroidTestCase;
import com.adeleon.sport.nbascoreboard.app.data.ScoreboardContract;
import com.adeleon.sport.nbascoreboard.app.services.ScoreSyncAdapter;

/**
 * Created by theade on 4/13/2015.
 */

public class TestFetchScoreTask extends AndroidTestCase {

    static final String ADD_TEAM_ID = "orlando-magic";
    static final String ADD_TEAM_FIRST_NAME_TEAM = "Orlando";
    static final String ADD_TEAM_LAST_NAME_TEAM = "Magic";
    static final String ADD_TEAM_ABBREVIATION = "ORL";
    static final String ADD_TEAM_SITE_NAME = "Amway Center";
    static final String ADD_TEAM_CITY ="Orlando";
    static final String ADD_TEAM_STATE = "Florida";

    /*
        Students: uncomment testAddLocation after you have written the AddLocation function.
        This test will only run on API level 11 and higher because of a requirement in the
        content provider.
     */
    @TargetApi(11)
    public void testAddLocation() {
        // start from a clean state
        getContext().getContentResolver().delete(ScoreboardContract.TeamEntry.CONTENT_URI,
                ScoreboardContract.TeamEntry.COLUMN_TEAM_ID + " = ?",
                new String[]{ADD_TEAM_ID});

        ScoreSyncAdapter fst = new ScoreSyncAdapter(getContext(), false);
        long IdTeam = fst.addTeam(ADD_TEAM_ID, ADD_TEAM_FIRST_NAME_TEAM,
                ADD_TEAM_LAST_NAME_TEAM, ADD_TEAM_ABBREVIATION, ADD_TEAM_CITY, ADD_TEAM_STATE, ADD_TEAM_SITE_NAME);

        // does addLocation return a valid record ID?
        assertFalse("Error: addLocation returned an invalid ID on insert",
                IdTeam == -1);

        // test all this twice
        for ( int i = 0; i < 2; i++ ) {

            // does the ID point to our location?
            Cursor teamCursor = getContext().getContentResolver().query(
                    ScoreboardContract.TeamEntry.CONTENT_URI,
                    new String[]{
                            ScoreboardContract.TeamEntry._ID,
                            ScoreboardContract.TeamEntry.COLUMN_TEAM_ID,
                            ScoreboardContract.TeamEntry.COLUMN_FIRST_NAME_TEAM,
                            ScoreboardContract.TeamEntry.COLUMN_LAST_NAME_TEAM,
                            ScoreboardContract.TeamEntry.COLUMN_ABBREVIATION,
                            ScoreboardContract.TeamEntry.COLUMN_CITY,
                            ScoreboardContract.TeamEntry.COLUMN_STATE,
                            ScoreboardContract.TeamEntry.COLUMN_SITE_NAME,
                    },
                    ScoreboardContract.TeamEntry.COLUMN_TEAM_ID + " = ?",
                    new String[]{ADD_TEAM_ID},
                    null);

            // these match the indices of the projection
            if (teamCursor.moveToFirst()) {
                assertEquals("Error: the queried value of Id Team Long does not match the returned value" +
                        "from addLocation", teamCursor.getLong(0), IdTeam);
                assertEquals("Error: the queried value of Team Id string is incorrect",
                        teamCursor.getString(1), ADD_TEAM_ID);
                assertEquals("Error: the queried value of team first name is incorrect",
                        teamCursor.getString(2), ADD_TEAM_FIRST_NAME_TEAM);
                assertEquals("Error: the queried value of team last name is incorrect",
                        teamCursor.getString(3), ADD_TEAM_LAST_NAME_TEAM);
                assertEquals("Error: the queried value of abbreviation is incorrect",
                        teamCursor.getString(4), ADD_TEAM_ABBREVIATION);
                assertEquals("Error: the queried value of city is incorrect",
                        teamCursor.getString(5), ADD_TEAM_CITY);
                assertEquals("Error: the queried value of state is incorrect",
                        teamCursor.getString(6), ADD_TEAM_STATE);
                assertEquals("Error: the queried value of site name is incorrect",
                        teamCursor.getString(7), ADD_TEAM_SITE_NAME);
            } else {
                fail("Error: the id you used to query returned an empty cursor");
            }

            // there should be no more records
            assertFalse("Error: there should be only one record returned from a team query",
                    teamCursor.moveToNext());

            // add the team again
            long newIdTeam = fst.addTeam(ADD_TEAM_ID, ADD_TEAM_FIRST_NAME_TEAM,
                    ADD_TEAM_LAST_NAME_TEAM, ADD_TEAM_ABBREVIATION, ADD_TEAM_SITE_NAME, ADD_TEAM_CITY, ADD_TEAM_STATE);

            assertEquals("Error: inserting a team again should return the same ID",
                    IdTeam, newIdTeam);
        }
        // reset our state back to normal
        getContext().getContentResolver().delete(ScoreboardContract.TeamEntry.CONTENT_URI,
                ScoreboardContract.TeamEntry.COLUMN_TEAM_ID + " = ?",
                new String[]{ADD_TEAM_ID});

        // clean up the test so that other tests can use the content provider
        getContext().getContentResolver().
                acquireContentProviderClient(ScoreboardContract.TeamEntry.CONTENT_URI).
                getLocalContentProvider().shutdown();
    }
}