package com.adeleon.sport.nbascoreboard.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import com.adeleon.sport.nbascoreboard.app.data.ScoreboardContract.*;
import java.util.HashMap;

/**
 * Created by theade on 4/13/2015.
 */

public class ScoreboardProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ScoreboardDbHelper mOpenHelper;

    static final int EVENT = 100;
    static final int EVENT_WITH_EVENT_ID_AND_EVENT_DATE = 101;
    static final int EVENT_WITH_EVENT_DATE = 102;
    static final int TEAM = 300;

    static final String AWAY_TEAM = "away_team";
    static final String HOME_TEAM = "home_team";
    private static  HashMap<String, String> sEventTeamProjectionMap;

    private static final SQLiteQueryBuilder sEventByDateQueryBuilder;

    static{
        sEventByDateQueryBuilder = new SQLiteQueryBuilder();

        sEventTeamProjectionMap = new HashMap<String, String>();
        sEventTeamProjectionMap.put(EventEntry.COLUMN_EVENT_ID, EventEntry.COLUMN_EVENT_ID);
        sEventTeamProjectionMap.put(EventEntry.COLUMN_EVENT_DATE, EventEntry.COLUMN_EVENT_DATE);
        sEventTeamProjectionMap.put(EventEntry.COLUMN_START_DATE_TIME, EventEntry.COLUMN_START_DATE_TIME);
        sEventTeamProjectionMap.put(EventEntry.COLUMN_EVENT_STATUS, EventEntry.COLUMN_EVENT_STATUS);
        sEventTeamProjectionMap.put(EventEntry.COLUMN_AWAY_TEAM_ID_KEY, EventEntry.COLUMN_AWAY_TEAM_ID_KEY);
        sEventTeamProjectionMap.put(EventEntry.COLUMN_HOME_TEAM_ID_KEY, EventEntry.COLUMN_HOME_TEAM_ID_KEY);
        sEventTeamProjectionMap.put(EventEntry.COLUMN_AWAY_PERIOD_FIRTS, EventEntry.COLUMN_AWAY_PERIOD_FIRTS);
        sEventTeamProjectionMap.put(EventEntry.COLUMN_AWAY_PERIOD_SECOND, EventEntry.COLUMN_AWAY_PERIOD_SECOND);
        sEventTeamProjectionMap.put(EventEntry.COLUMN_AWAY_PERIOD_THIRD, EventEntry.COLUMN_AWAY_PERIOD_THIRD);
        sEventTeamProjectionMap.put(EventEntry.COLUMN_AWAY_PERIOD_FOURTH, EventEntry.COLUMN_AWAY_PERIOD_FOURTH);
        sEventTeamProjectionMap.put(EventEntry.COLUMN_HOME_PERIOD_FIRTS, EventEntry.COLUMN_HOME_PERIOD_FIRTS);
        sEventTeamProjectionMap.put(EventEntry.COLUMN_HOME_PERIOD_SECOND, EventEntry.COLUMN_HOME_PERIOD_SECOND);
        sEventTeamProjectionMap.put(EventEntry.COLUMN_HOME_PERIOD_THIRD, EventEntry.COLUMN_HOME_PERIOD_THIRD);
        sEventTeamProjectionMap.put(EventEntry.COLUMN_HOME_PERIOD_FOURTH, EventEntry.COLUMN_HOME_PERIOD_FOURTH);

        sEventTeamProjectionMap.put(AWAY_TEAM+"."+TeamEntry.COLUMN_FIRST_NAME_TEAM , AWAY_TEAM+"."+TeamEntry.COLUMN_FIRST_NAME_TEAM );
        sEventTeamProjectionMap.put(AWAY_TEAM+"."+TeamEntry.COLUMN_LAST_NAME_TEAM , AWAY_TEAM+"."+TeamEntry.COLUMN_LAST_NAME_TEAM );
        sEventTeamProjectionMap.put(AWAY_TEAM+"."+TeamEntry.COLUMN_ABBREVIATION , AWAY_TEAM+"."+TeamEntry.COLUMN_ABBREVIATION );

        sEventTeamProjectionMap.put(HOME_TEAM+"."+TeamEntry.COLUMN_FIRST_NAME_TEAM , HOME_TEAM+"."+TeamEntry.COLUMN_FIRST_NAME_TEAM );
        sEventTeamProjectionMap.put(HOME_TEAM+"."+TeamEntry.COLUMN_LAST_NAME_TEAM , HOME_TEAM+"."+TeamEntry.COLUMN_LAST_NAME_TEAM );
        sEventTeamProjectionMap.put(HOME_TEAM+"."+TeamEntry.COLUMN_ABBREVIATION , HOME_TEAM+"."+TeamEntry.COLUMN_ABBREVIATION );

        //This is an inner join which looks like

        sEventByDateQueryBuilder.setTables(
                ScoreboardContract.EventEntry.TABLE_NAME + " INNER JOIN " +
                        ScoreboardContract.TeamEntry.TABLE_NAME +" "+AWAY_TEAM +
                        " ON " + ScoreboardContract.EventEntry.TABLE_NAME +
                        "." + ScoreboardContract.EventEntry.COLUMN_AWAY_TEAM_ID_KEY +
                        " = " +AWAY_TEAM +
                        "." + ScoreboardContract.TeamEntry.COLUMN_TEAM_ID
                        + " INNER JOIN " + ScoreboardContract.TeamEntry.TABLE_NAME
                        +" "+HOME_TEAM  +" ON "+HOME_TEAM+"."+ScoreboardContract.TeamEntry.COLUMN_TEAM_ID+
                         " = "+ ScoreboardContract.EventEntry.TABLE_NAME +
                         "."+ScoreboardContract.EventEntry.COLUMN_HOME_TEAM_ID_KEY
                           );
        sEventByDateQueryBuilder.setProjectionMap(sEventTeamProjectionMap);
    }

    //Event.Event_date = ?
    private static final String sEventListByEventDateSelection =
            ScoreboardContract.EventEntry.TABLE_NAME+
                    "." + EventEntry.COLUMN_EVENT_DATE + " = ? ";

    private static final String sEventRowByEventIdAndDateSelection =
            EventEntry.TABLE_NAME+
                    "." + EventEntry.COLUMN_EVENT_ID + " = ? AND "+
                     EventEntry.COLUMN_EVENT_DATE +" = ? ";

    private Cursor getEventByEventDate(Uri uri, String[] projection, String sortOrder) {
        //String TeamSetting = ScoreboardContract.EventEntry.getTeamSettingFromUri(uri);
        String EventDate = ScoreboardContract.EventEntry.getEventDateFromUri(uri);

        String[] selectionArgs = new String[]{EventDate};
        String selection = sEventListByEventDateSelection;

        return sEventByDateQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getEventByEventIdAndDate(
            Uri uri, String[] projection, String sortOrder) {
        String eventId = ScoreboardContract.EventEntry.getEventIdFromUri(uri);
        String eventDate = ScoreboardContract.EventEntry.getEventIdDateFromUri(uri);
        return sEventByDateQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEventRowByEventIdAndDateSelection,
                new String[]{eventId,eventDate},
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ScoreboardContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, ScoreboardContract.PATH_EVENT, EVENT);
        matcher.addURI(authority, ScoreboardContract.PATH_EVENT + "/*/*", EVENT_WITH_EVENT_ID_AND_EVENT_DATE);
        matcher.addURI(authority, ScoreboardContract.PATH_EVENT + "/*", EVENT_WITH_EVENT_DATE);

        matcher.addURI(authority, ScoreboardContract.PATH_TEAM, TEAM);
        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new ScoreboardDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new ScoreboardDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.
     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case EVENT_WITH_EVENT_DATE:
                return EventEntry.CONTENT_TYPE;
            case EVENT_WITH_EVENT_ID_AND_EVENT_DATE:
                return EventEntry.CONTENT_ITEM_TYPE;
            case EVENT:
                return EventEntry.CONTENT_TYPE;
            case TEAM:
                return TeamEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "event/*/*"
            case EVENT_WITH_EVENT_DATE:
            {
                retCursor = getEventByEventDate(uri, projection, sortOrder);
                break;
            }
            // "event/*"
            case EVENT_WITH_EVENT_ID_AND_EVENT_DATE: {
                retCursor = getEventByEventIdAndDate(uri, projection, sortOrder);
                break;
            }
            // "event"
            case EVENT: {
                retCursor  = mOpenHelper.getReadableDatabase().query(
                        ScoreboardContract.EventEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "Team"
            case TEAM: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScoreboardContract.TeamEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Teams to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case EVENT: {
                //normalizeDate(values);ade
                long _id = db.insert(ScoreboardContract.EventEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ScoreboardContract.EventEntry.buildEvetIdAndDateUri("20150327-charlotte-hornets-at-washington-wizards", "2015-03-27T00:00:00-04:00"); //temporar
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TEAM: {
                long _id = db.insert(ScoreboardContract.TeamEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ScoreboardContract.TeamEntry.buildTeamUri("charlotte-hornets"); //temporal
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case EVENT:
                rowsDeleted = db.delete(
                        EventEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TEAM:
                rowsDeleted = db.delete(
                        TeamEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

  /*ade  private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(ScoreboardContract.EventEntry.COLUMN_DATE)) {
            long dateValue = values.getAsLong(ScoreboardContract.EventEntry.COLUMN_DATE);
            values.put(ScoreboardContract.EventEntry.COLUMN_DATE, ScoreboardContract.normalizeDate(dateValue));
        }
    }*/

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case EVENT:
                //normalizeDate(values);
                rowsUpdated = db.update(EventEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TEAM:
                rowsUpdated = db.update(TeamEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EVENT:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        //normalizeDate(value); ade
                        long _id = db.insert(ScoreboardContract.EventEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}