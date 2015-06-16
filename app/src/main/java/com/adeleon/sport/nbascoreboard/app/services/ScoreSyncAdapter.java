package com.adeleon.sport.nbascoreboard.app.services;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.adeleon.sport.nbascoreboard.app.R;
import com.adeleon.sport.nbascoreboard.app.data.ScoreboardContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by theade on 4/26/2015.
 */
public class ScoreSyncAdapter extends AbstractThreadedSyncAdapter {
    private final String LOG_TAG = ScoreSyncAdapter.class.getSimpleName();


    // Interval at which to sync, in seconds.
    public static final int SECONDS_IN_HOUR = 3600;

    public ScoreSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        callSportService();
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        ScoreSyncAdapter.configurePeriodicSync(context);
        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void configurePeriodicSync(Context context) {
        /*
         * Since we've created an account
         */
        int syncInterval = 3600; //Integer.parseInt(MovieUtils.getPreferredSyncInterval(context));
        int syncFlexTime = syncInterval / SECONDS_IN_HOUR;

        ScoreSyncAdapter.configurePeriodicSync(context, syncInterval, syncFlexTime);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private void callSportService(){
        try {
            insertSportDataFromJson(callApi());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p/>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void insertSportDataFromJson(String scoreJsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        final String EVENT_DATE = "events_date";

        final String OSB_AWAY_TEAM = "away_team";

        final String OSB_HOME_TEAM = "home_team";

        final String AWAY_PERIOD_SCORES = "away_period_scores";

        final String HOME_PERIOD_SCORES = "home_period_scores";

        //final String TABLE_NAME = "team";
        final String OSB_TEAM_ID = "team_id";
        final String OSB_FIRST_NAME_TEAM = "first_name";
        final String OSB_LAST_NAME_TEAM = "last_name";
        final String OSB_ABBREVIATION = "abbreviation";
        final String OSB_SITE_NAME = "site_name";
        final String OSB_CITY = "city";
        final String OSB_STATE = "state";

        final String OSB_EVENT = "event";
        final String OSB_EVENT_ID = "event_id";
        final String OSB_EVENT_START_DATE = "start_date_time";
        final String OSB_EVENT_STATUS = "event_status";
        final String STATUS_COMPLETED = "completed";


        final String OSB_FULL_NAME = "full_name";
        final String OSB_AWAY_POINT_SCORED = "away_points_scored";
        final String OSB_HOME_POINT_SCORED = "home_points_scored";

        try {
            JSONObject scoreJson = new JSONObject(scoreJsonStr);

            JSONArray scoreArray = scoreJson.getJSONArray(OSB_EVENT);

            String eventDate = scoreJson.getString(EVENT_DATE);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(scoreArray.length());

            //  String[] resultStrs = new String[scoreArray.length()];
            for (int i = 0; i < scoreArray.length(); i++) {

                String awayTeam;
                String homeTeam;

                String teamId;
                String firstName;
                String lastName;
                String abbreviation;
                String city;
                String state;
                String siteName;

                String eventId;
                String eventStartDate;
                String eventStatus;

                int[] awayPeriodsArray = new int[4];
                int[] homePeriodsArray = new int[4];

                int awayPointScored;
                int homePointScored;

                JSONObject dayScoreboard = scoreArray.getJSONObject(i);

                eventId = dayScoreboard.getString(OSB_EVENT_ID);
                eventStartDate = dayScoreboard.getString(OSB_EVENT_START_DATE);
                eventStatus = dayScoreboard.getString(OSB_EVENT_STATUS);

                //final score
                awayPointScored = dayScoreboard.getInt(OSB_AWAY_POINT_SCORED);
                homePointScored = dayScoreboard.getInt(OSB_HOME_POINT_SCORED);

                JSONObject awayTeamObject = dayScoreboard.getJSONObject(OSB_AWAY_TEAM);

                if (eventStatus.equals(STATUS_COMPLETED)) {
                    JSONArray awayScoresArray = dayScoreboard.getJSONArray(AWAY_PERIOD_SCORES);
                    for (int j = 0; j < awayScoresArray.length(); j++) {
                        if (j <= 3 ) {
                            awayPeriodsArray[j] = awayScoresArray.getInt(j);
                        }else{
                            awayPeriodsArray[3] = awayPeriodsArray[3] + awayScoresArray.getInt(j);
                        }
                    }

                    JSONArray homeScoresArray = dayScoreboard.getJSONArray(HOME_PERIOD_SCORES);
                    for (int k = 0; k < awayScoresArray.length(); k++) {
                        if (k <= 3 ) {
                            homePeriodsArray[k] = homeScoresArray.getInt(k);
                        }else{
                            homePeriodsArray[3] = homePeriodsArray[3] + homeScoresArray.getInt(k);
                        }
                    }
                }
                teamId = awayTeamObject.getString(OSB_TEAM_ID);
                firstName = awayTeamObject.getString(OSB_FIRST_NAME_TEAM);
                lastName = awayTeamObject.getString(OSB_LAST_NAME_TEAM);
                abbreviation = awayTeamObject.getString(OSB_ABBREVIATION);
                city = awayTeamObject.getString(OSB_CITY);
                state = awayTeamObject.getString(OSB_STATE);
                siteName = awayTeamObject.getString(OSB_SITE_NAME);
                long IdTeamAway = addTeam(teamId, firstName, lastName, abbreviation, city, state, siteName);
                awayTeam = awayTeamObject.getString(OSB_FULL_NAME);

                ///-----------------------------------------------------
                JSONObject homeTeamObject = dayScoreboard.getJSONObject(OSB_HOME_TEAM);
                teamId = homeTeamObject.getString(OSB_TEAM_ID);
                firstName = homeTeamObject.getString(OSB_FIRST_NAME_TEAM);
                lastName = homeTeamObject.getString(OSB_LAST_NAME_TEAM);
                abbreviation = homeTeamObject.getString(OSB_ABBREVIATION);
                city = homeTeamObject.getString(OSB_CITY);
                state = homeTeamObject.getString(OSB_STATE);
                siteName = homeTeamObject.getString(OSB_SITE_NAME);
                long IdTeamHome = addTeam(teamId, firstName, lastName, abbreviation, city, state, siteName);
                homeTeam = homeTeamObject.getString(OSB_FULL_NAME);

                ContentValues eventValues = new ContentValues();

                eventValues.put(ScoreboardContract.EventEntry.COLUMN_EVENT_ID, eventId);
                eventValues.put(ScoreboardContract.EventEntry.COLUMN_EVENT_DATE, " ");
                eventValues.put(ScoreboardContract.EventEntry.COLUMN_START_DATE_TIME, eventStartDate);
                eventValues.put(ScoreboardContract.EventEntry.COLUMN_AWAY_TEAM_ID_KEY, IdTeamAway);
                eventValues.put(ScoreboardContract.EventEntry.COLUMN_HOME_TEAM_ID_KEY, IdTeamHome);
                eventValues.put(ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_FIRTS, awayPeriodsArray[0]);
                eventValues.put(ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_SECOND, awayPeriodsArray[1]);
                eventValues.put(ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_THIRD, awayPeriodsArray[2]);
                eventValues.put(ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_FOURTH, awayPeriodsArray[3]);
                eventValues.put(ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_FIRTS, homePeriodsArray[0]);
                eventValues.put(ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_SECOND, homePeriodsArray[1]);
                eventValues.put(ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_THIRD, homePeriodsArray[2]);
                eventValues.put(ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_FOURTH, homePeriodsArray[3]);
                eventValues.put(ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_SCORES, awayPointScored);
                eventValues.put(ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_SCORES, homePointScored);


                cVVector.add(eventValues);
            }
            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);

                inserted = getContext().getContentResolver().bulkInsert(ScoreboardContract.EventEntry.CONTENT_URI, cvArray);
            }


            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");

        }catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     *
     * @param teamId
     * @param firstName
     * @param lastName
     * @param abbreviation
     * @param cityName
     * @param state
     * @param siteName
     * @return Id
     */
    public long addTeam(String teamId, String firstName, String lastName, String abbreviation,  String cityName, String state, String siteName) {
        long Id;

        // First, check if the team with this city name exists in the db
        Cursor teamCursor = getContext().getContentResolver().query(
                ScoreboardContract.TeamEntry.CONTENT_URI,
                new String[]{ScoreboardContract.TeamEntry._ID},
                ScoreboardContract.TeamEntry.COLUMN_TEAM_ID + " = ?",
                new String[]{teamId},
                null);

        if (teamCursor.moveToFirst()) {
            int teamIdIndex = teamCursor.getColumnIndex(ScoreboardContract.TeamEntry._ID);
            Id = teamCursor.getLong(teamIdIndex);
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues teamValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            teamValues.put(ScoreboardContract.TeamEntry.COLUMN_TEAM_ID, teamId);
            teamValues.put(ScoreboardContract.TeamEntry.COLUMN_FIRST_NAME_TEAM, firstName);
            teamValues.put(ScoreboardContract.TeamEntry.COLUMN_LAST_NAME_TEAM, lastName);
            teamValues.put(ScoreboardContract.TeamEntry.COLUMN_ABBREVIATION, abbreviation);
            teamValues.put(ScoreboardContract.TeamEntry.COLUMN_CITY, cityName);
            teamValues.put(ScoreboardContract.TeamEntry.COLUMN_STATE, state);
            teamValues.put(ScoreboardContract.TeamEntry.COLUMN_SITE_NAME, siteName);

            // Finally, insert team data into the database.
            Uri insertedUri = getContext().getContentResolver().insert(
                    ScoreboardContract.TeamEntry.CONTENT_URI,
                    teamValues
            );

            // The resulting URI contains the ID for the row.  Extract the teamId from the Uri.
            Id = ContentUris.parseId(insertedUri);
        }

        teamCursor.close();
        // Wait, that worked?  Yes!
        return Id;
    }

    public String callApi(){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String scoreboardJsonStr = null;
        String sport_type = "nba";
        String authotization_value = getContext().getString(R.string.token_api);

        try {

            final String SCOREBOARD_BASE_URL = "https://erikberg.com/events.json?";
            final String DAY_PARAM = "date";
            final String CATEGORY_PARAM = "sport";
            final String AUTHORIZATION_PARAM = "Authorization";

            Uri builtUri = Uri.parse(SCOREBOARD_BASE_URL).buildUpon()
                    .appendQueryParameter(DAY_PARAM, " ")
                    .appendQueryParameter(CATEGORY_PARAM, sport_type)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            // URL url = new URL("https://erikberg.com/events.json?date=20150325&sport=nba");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty(AUTHORIZATION_PARAM, authotization_value);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {

                return null;
            }
            scoreboardJsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return scoreboardJsonStr;
    }
}
