package com.adeleon.sport.nbascoreboard.app.data;

/**
 * Created by theade on 3/31/2015.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(ScoreboardDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.
        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(ScoreboardContract.TeamEntry.TABLE_NAME);
        tableNameHashSet.add(ScoreboardContract.EventEntry.TABLE_NAME);
        tableNameHashSet.add(ScoreboardContract.EventPlayerEntry.TABLE_NAME);

        mContext.deleteDatabase(ScoreboardDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new ScoreboardDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the team entry, event entry and player entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + ScoreboardContract.TeamEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> teamColumnHashSet = new HashSet<String>();
        teamColumnHashSet.add(ScoreboardContract.TeamEntry.COLUMN_TEAM_ID);
        teamColumnHashSet.add(ScoreboardContract.TeamEntry.COLUMN_FIRST_NAME_TEAM);
        teamColumnHashSet.add(ScoreboardContract.TeamEntry.COLUMN_LAST_NAME_TEAM);
        teamColumnHashSet.add(ScoreboardContract.TeamEntry.COLUMN_ABBREVIATION);
        teamColumnHashSet.add(ScoreboardContract.TeamEntry.COLUMN_SITE_NAME);
        teamColumnHashSet.add(ScoreboardContract.TeamEntry.COLUMN_CITY);
        teamColumnHashSet.add(ScoreboardContract.TeamEntry.COLUMN_STATE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            teamColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required team entry columns",
                teamColumnHashSet.isEmpty());
        db.close();
    }

    public void testTeamTable() {
        insertAwayTeam();
        insertHomeTeam();
    }

    public void testEventTable() {
        long teamAwayRowId = insertAwayTeam();
        long teamHomeRowId = insertHomeTeam();

        // Make sure we have a valid row ID.
        assertFalse("Error: Team Away Not Inserted Correctly", teamAwayRowId == -1L);
        assertFalse("Error: Team Home Not Inserted Correctly", teamHomeRowId == -1L);

        ScoreboardDbHelper dbHelper = new ScoreboardDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues scoreValues = TestUtilities.createEventValues();

        long eventRowId = db.insert(ScoreboardContract.EventEntry.TABLE_NAME, null, scoreValues);
        assertTrue(eventRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor scoreCursor = db.query(
                ScoreboardContract.EventEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        assertTrue( "Error: No Records returned from location query", scoreCursor.moveToFirst() );

        TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate",
                scoreCursor, scoreValues);

        assertFalse( "Error: More than one record returned from weather query",
                scoreCursor.moveToNext() );

        scoreCursor.close();
        dbHelper.close();
    }

    public long insertAwayTeam() {
        ScoreboardDbHelper dbHelper = new ScoreboardDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createAwayTeamValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long TeamRowId;
        TeamRowId = db.insert(ScoreboardContract.TeamEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(TeamRowId != -1);

        Cursor cursor = db.query(
                ScoreboardContract.TeamEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from team query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: team Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from team query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return TeamRowId;
    }

    public long insertHomeTeam() {
        ScoreboardDbHelper dbHelper = new ScoreboardDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createHomeTeamValues();

        long TeamRowId;
        TeamRowId = db.insert(ScoreboardContract.TeamEntry.TABLE_NAME, null, testValues);

        assertTrue(TeamRowId != -1);

        String[] teamId =  {"washington-wizards"};
        Cursor cursor = db.query(
                ScoreboardContract.TeamEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                ScoreboardContract.TeamEntry.COLUMN_TEAM_ID +" = ? ", // Columns for the "where" clause
                teamId, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        assertTrue( "Error: No Records returned from team query", cursor.moveToFirst() );

        TestUtilities.validateCurrentRecord("Error: team Query Validation Failed",
                cursor, testValues);

        assertFalse( "Error: More than one record returned from team query",
                cursor.moveToNext() );

        cursor.close();
        db.close();
        return TeamRowId;
    }
}