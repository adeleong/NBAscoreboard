package com.adeleon.sport.nbascoreboard.app.data;

/**
 * Created by theade on 4/13/2015.
 */

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.adeleon.sport.nbascoreboard.app.data.ScoreboardContract.EventEntry;
import com.adeleon.sport.nbascoreboard.app.data.ScoreboardContract.TeamEntry;
/*
    Note: This is not a complete set of tests of the Sunshine ContentProvider, but it does test
    that at least the basic functionality has been implemented correctly.
    Students: Uncomment the tests in this class as you implement the functionality in your
    ContentProvider to make sure that you've implemented things reasonably correctly.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.
       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                EventEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                TeamEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                EventEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                TeamEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Location table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // ScoreboardProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                ScoreboardProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: ScoreboardProvider registered with authority: " + providerInfo.authority +
                    " instead of authority: " + ScoreboardContract.CONTENT_AUTHORITY,
                    providerInfo.authority, ScoreboardContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: ScoreboardProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
            Students: Uncomment this test to verify that your implementation of GetType is
            functioning correctly.
         */
    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(EventEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the EventEntry CONTENT_URI should return EventEntry.CONTENT_TYPE",
                EventEntry.CONTENT_TYPE, type);

        String eventDate = "2015-03-27T00:00:00-04:00";
        // content://com.example.android.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(
                EventEntry.buildEventDate(eventDate));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the EventEntry CONTENT_URI with team should return EventEntry.CONTENT_TYPE",
                EventEntry.CONTENT_TYPE, type);

        String EventDate = "2015-03-27T19:00:00-04:00"; // December 21st, 2014
        long eventId = 1;
        // content://com.example.android.sunshine.app/weather/94074/20140612
        type = mContext.getContentResolver().getType(
                EventEntry.buildEvetIdAndDateUri(eventId,EventDate));
        // vnd.android.cursor.item/com.example.android.sunshine.app/weather/1419120000
        assertEquals("Error: the EventEntry CONTENT_URI with team and date should return EventEntry.CONTENT_ITEM_TYPE",
                EventEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(TeamEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals("Error: the TeamEntry CONTENT_URI should return TeamEntry.CONTENT_TYPE",
                TeamEntry.CONTENT_TYPE, type);
    }


    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if the basic weather query functionality
        given in the ContentProvider is working correctly.
     */
    public void testBasicEventQuery() {
        // insert our test records into the database
        ScoreboardDbHelper dbHelper = new ScoreboardDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testTeamAwayValues = TestUtilities.createAwayTeamValues();
        ContentValues testTeamHomeValues = TestUtilities.createHomeTeamValues();

        long teamAwayRowId = TestUtilities.insertTeamAwayValues(mContext);
        long teamHomeRowId = TestUtilities.insertTeamHomeValues(mContext);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues eventValues = TestUtilities.createEventValues(teamAwayRowId,teamHomeRowId);

        long eventRowId = db.insert(EventEntry.TABLE_NAME, null, eventValues);
        assertTrue("Unable to Insert EventEntry into the Database", eventRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor eventCursor = mContext.getContentResolver().query(
                EventEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicEventQuery", eventCursor, eventValues);
    }

    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if your location queries are
        performing correctly.
     */
    public void testBasicLocationQueries() {
        // insert our test records into the database
        ScoreboardDbHelper dbHelper = new ScoreboardDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createAwayTeamValues();
        long teamRowId = TestUtilities.insertTeamAwayValues(mContext);

        // Test the basic content provider query
        Cursor teamCursor = mContext.getContentResolver().query(
                TeamEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicLocationQueries, location query", teamCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Location Query did not properly set NotificationUri",
                    teamCursor.getNotificationUri(), TeamEntry.CONTENT_URI);
        }
    }

    /*
        This test uses the provider to insert and then update the data. Uncomment this test to
        see if your update location is functioning correctly.
     */
    public void testUpdateTeam() {
        // Create a new map of values, where column names are the keys
        ContentValues awayValues = TestUtilities.createAwayTeamValues();

        Uri teamUri = mContext.getContentResolver().
                insert(TeamEntry.CONTENT_URI, awayValues);
        long teamRowId = ContentUris.parseId(teamUri);
       // String TeamRowId = "charlotte-hornets";

        // Verify we got a row back.
        assertTrue(teamRowId != -1);
        Log.d(LOG_TAG, "New row id: " + teamRowId);

        ContentValues updatedValues = new ContentValues(awayValues);
        updatedValues.put(TeamEntry._ID, teamRowId);
        updatedValues.put(TeamEntry.COLUMN_CITY, "Santa's Village");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor locationCursor = mContext.getContentResolver().query(TeamEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                TeamEntry.CONTENT_URI, updatedValues, TeamEntry._ID + "= ?",
                new String[] { Long.toString(teamRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        ///String[] selectionArgs = new String[]{TeamRowId};
        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                TeamEntry.CONTENT_URI,
                null,   // projection
                TeamEntry._ID + " = "+teamRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateTeam.  Error validating team entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    public void testInsertReadProvider() {
        ContentValues testAwayValues = TestUtilities.createAwayTeamValues();
        ContentValues testHomeValues = TestUtilities.createHomeTeamValues();

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(TeamEntry.CONTENT_URI, true, tco);

        Uri TeamAwayUri = mContext.getContentResolver().insert(TeamEntry.CONTENT_URI, testAwayValues);
        Uri TeamHomeUri = mContext.getContentResolver().insert(TeamEntry.CONTENT_URI, testHomeValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long teamAwayRowId = ContentUris.parseId(TeamAwayUri);
        long teamHomeRowId = ContentUris.parseId(TeamHomeUri);

        assertTrue(teamAwayRowId != -1);
        assertTrue(teamHomeRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                TeamEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating TeamEntry.",
                cursor, testAwayValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues eventValues = TestUtilities.createEventValues(teamAwayRowId, teamHomeRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(EventEntry.CONTENT_URI, true, tco);

        Uri eventInsertUri = mContext.getContentResolver()
                .insert(EventEntry.CONTENT_URI, eventValues);
        assertTrue(eventInsertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor eventCursor = mContext.getContentResolver().query(
                EventEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating EventEntry insert.",
                eventCursor, eventValues);

        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        eventValues.putAll(testAwayValues);

        // Get the joined Weather and Location data
        eventCursor = mContext.getContentResolver().query(
                EventEntry.buildEventDate(TestUtilities.TEST_EVENT_DATE),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
       /* TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined EVENT and team Data.",
                eventCursor, eventValues);  pendiente disenar prueba*/
        int idx = eventCursor.getColumnIndex(EventEntry.COLUMN_EVENT_ID);

        // Get the joined Event Id
        eventCursor = mContext.getContentResolver().query(
                EventEntry.buildEvetIdAndDateUri ( TestUtilities.TEST_EVENT_ID, TestUtilities.TEST_EVENT_DATE),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        assertTrue(eventCursor.getCount() != -1);
        /*TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location Data with start date.",
                eventCursor, eventValues);*/

    }

    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the delete functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our location delete.
        TestUtilities.TestContentObserver teamObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TeamEntry.CONTENT_URI, true, teamObserver);

        // Register a content observer for our weather delete.
        TestUtilities.TestContentObserver eventObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(EventEntry.CONTENT_URI, true, eventObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        teamObserver.waitForNotificationOrFail();
        eventObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(teamObserver);
        mContext.getContentResolver().unregisterContentObserver(eventObserver);
    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertEventValues(long awayTeam,  long homeTeam) {

        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++ ) {
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(EventEntry.COLUMN_EVENT_ID, "20150327-charlotte-hornets-at-washington-wizards"+i);
            weatherValues.put(EventEntry.COLUMN_EVENT_DATE, "2015-03-27T00:00:00-04:00");
            weatherValues.put(EventEntry.COLUMN_EVENT_STATUS, "completed");
            weatherValues.put(EventEntry.COLUMN_START_DATE_TIME, "2015-03-27T19:00:00-04:00");
            weatherValues.put(EventEntry.COLUMN_AWAY_TEAM_ID_KEY, awayTeam);
            weatherValues.put(EventEntry.COLUMN_HOME_TEAM_ID_KEY, homeTeam);
            weatherValues.put(EventEntry.COLUMN_AWAY_PERIOD_FIRTS, 25 + i);
            weatherValues.put(EventEntry.COLUMN_AWAY_PERIOD_SECOND, 30 + i);
            weatherValues.put(EventEntry.COLUMN_AWAY_PERIOD_THIRD, 27 + i);
            weatherValues.put(EventEntry.COLUMN_AWAY_PERIOD_FOURTH, 32+ i);
            weatherValues.put(EventEntry.COLUMN_HOME_PERIOD_FIRTS, 29+ i);
            weatherValues.put(EventEntry.COLUMN_HOME_PERIOD_SECOND, 16+ i);
            weatherValues.put(EventEntry.COLUMN_HOME_PERIOD_THIRD, 13 + i);
            weatherValues.put(EventEntry.COLUMN_HOME_PERIOD_FOURTH, 40+ i);
            weatherValues.put(EventEntry.COLUMN_AWAY_PERIOD_SCORES, 105+ i);
            weatherValues.put(EventEntry.COLUMN_HOME_PERIOD_SCORES, 107+ i);
            returnContentValues[i] = weatherValues;
        }
        return returnContentValues;
    }

    public void testBulkInsert() {
        // first, let's create a location value
        ContentValues testAwayValues = TestUtilities.createAwayTeamValues();
        ContentValues testHomeValues = TestUtilities.createHomeTeamValues();

        Uri awayTeamUri = mContext.getContentResolver().insert(TeamEntry.CONTENT_URI, testAwayValues);
        Uri homeTeamUri =  mContext.getContentResolver().insert(TeamEntry.CONTENT_URI, testHomeValues);

        long awayTeamRowId = ContentUris.parseId(awayTeamUri);
        long homeTeamRowId = ContentUris.parseId(homeTeamUri);
        String teamRowId = "charlotte-hornets";  //ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(teamRowId != null);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                TeamEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating TeamEntry.",
                cursor, testAwayValues);

        // Now we can bulkInsert some weather.  In fact, we only implement BulkInsert for weather
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertEventValues(awayTeamRowId, homeTeamRowId);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver weatherObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(EventEntry.CONTENT_URI, true, weatherObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(EventEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        weatherObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(weatherObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                EventEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                EventEntry.COLUMN_START_DATE_TIME + " ASC"  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating EventEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}
