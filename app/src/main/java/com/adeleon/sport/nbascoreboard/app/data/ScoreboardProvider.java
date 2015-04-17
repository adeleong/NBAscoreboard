package com.adeleon.sport.nbascoreboard.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by theade on 4/13/2015.
 */
     

public class ScoreboardProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ScoreboardDbHelper mOpenHelper;

    static final int EVENT = 100;
    static final int EVENT_WITH_TEAM = 101;
    static final int EVENT_WITH_TEAM_AND_DATE = 102;
    static final int TEAM = 300;

    private static final SQLiteQueryBuilder sWeatherByTeamSettingQueryBuilder;

    static{
        sWeatherByTeamSettingQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN Team ON weather.Team_id = Team._id
        sWeatherByTeamSettingQueryBuilder.setTables(
                ScoreboardContract.EventEntry.TABLE_NAME + " INNER JOIN " +
                        ScoreboardContract.TeamEntry.TABLE_NAME +" AWAY_TEAM " +
                        " ON " + ScoreboardContract.EventEntry.TABLE_NAME +
                        "." + ScoreboardContract.EventEntry.COLUMN_AWAY_TEAM_ID_KEY +
                        " = " + " AWAY_TEAM" +
                        "." + ScoreboardContract.TeamEntry.COLUMN_TEAM_ID
                        + " INNER JOIN " + ScoreboardContract.TeamEntry.TABLE_NAME
                        +" HOME_TEAM " +" ON "+ " HOME_TEAM."+ScoreboardContract.TeamEntry.COLUMN_TEAM_ID+
                         " = "+ ScoreboardContract.EventEntry.COLUMN_HOME_TEAM_ID_KEY
                           );
    }

    //Team.Team_setting = ?
    private static final String sTeamSettingSelection =
            ScoreboardContract.TeamEntry.TABLE_NAME+
                    "." + ScoreboardContract.TeamEntry.COLUMN_TEAM_ID + " = ? ";

    //Team.Team_setting = ? AND date >= ?
    private static final String sTeamSettingWithStartDateSelection =
            ScoreboardContract.TeamEntry.TABLE_NAME+
                    "." + ScoreboardContract.TeamEntry.COLUMN_TEAM_ID + " = ? AND " +
                    ScoreboardContract.EventEntry.COLUMN_START_DATE_TIME + " >= ? ";

    //Team.Team_setting = ? AND date = ?
    private static final String sTeamSettingAndDaySelection =
            ScoreboardContract.TeamEntry.TABLE_NAME +
                    "." + ScoreboardContract.TeamEntry.COLUMN_TEAM_ID + " = ? AND " +
                    ScoreboardContract.EventEntry.COLUMN_START_DATE_TIME + " = ? ";

    private Cursor getEventByTeamSetting(Uri uri, String[] projection, String sortOrder) {
        String TeamSetting = ScoreboardContract.EventEntry.getTeamSettingFromUri(uri);
        String startDate = ScoreboardContract.EventEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate.equals("")) {
            selection = sTeamSettingSelection;
            selectionArgs = new String[]{TeamSetting};
        } else {
            selectionArgs = new String[]{TeamSetting, startDate};
            selection = sTeamSettingWithStartDateSelection;
        }

        return sWeatherByTeamSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getWeatherByTeamSettingAndDate(
            Uri uri, String[] projection, String sortOrder) {
        String TeamSetting = ScoreboardContract.EventEntry.getTeamSettingFromUri(uri);
        long date = ScoreboardContract.EventEntry.getDateFromUri(uri);

        return sWeatherByTeamSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sTeamSettingAndDaySelection,
                new String[]{TeamSetting, Long.toString(date)},
                null,
                null,
                sortOrder
        );
    }

    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, EVENT_WITH_TEAM, EVENT_WITH_TEAM_AND_DATE,
        and Team integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
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
        matcher.addURI(authority, ScoreboardContract.PATH_EVENT + "/*", EVENT_WITH_TEAM);
        matcher.addURI(authority, ScoreboardContract.PATH_EVENT + "/*/*", EVENT_WITH_TEAM_AND_DATE);

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
            case EVENT_WITH_TEAM_AND_DATE:
                return ScoreboardContract.EventEntry.CONTENT_ITEM_TYPE;
            case EVENT_WITH_TEAM:
                return ScoreboardContract.EventEntry.CONTENT_TYPE;
            case EVENT:
                return ScoreboardContract.EventEntry.CONTENT_TYPE;
            case TEAM:
                return ScoreboardContract.TeamEntry.CONTENT_TYPE;
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
            // "weather/*/*"
            case EVENT_WITH_TEAM_AND_DATE:
            {
                retCursor = getWeatherByTeamSettingAndDate(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case EVENT_WITH_TEAM: {
                retCursor = getEventByTeamSetting(uri, projection, sortOrder);
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
                    returnUri = ScoreboardContract.EventEntry.buildEvetUri("20150327-detroit-pistons-at-orlando-magic"); //temporar
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
        // Student: Start by getting a writable database

        // Student: Use the uriMatcher to match the WEATHER and Team URI's we are going to
        // handle.  If it doesn't match these, throw an UnsupportedOperationException.

        // Student: A null value deletes all rows.  In my implementation of this, I only notified
        // the uri listeners (using the content resolver) if the rowsDeleted != 0 or the selection
        // is null.
        // Oh, and you should notify the listeners here.

        // Student: return the actual rows deleted
        return 0;
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
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        return 0;
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