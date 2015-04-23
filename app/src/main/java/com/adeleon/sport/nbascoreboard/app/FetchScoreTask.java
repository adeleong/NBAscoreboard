package com.adeleon.sport.nbascoreboard.app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

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
import java.text.SimpleDateFormat;
import java.util.Vector;

/**
 * Created by theade on 4/13/2015.
 */
public class FetchScoreTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchScoreTask.class.getSimpleName();

    private ArrayAdapter<String> mScoreboardAdapter;
    private final Context mContext;

    public FetchScoreTask(Context context, ArrayAdapter<String> scoreboardAdapter) {
        mContext = context;
        mScoreboardAdapter = scoreboardAdapter;
    }

    private boolean DEBUG = true;

    private String getReadableDateString(long time) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
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
    long addTeam(String teamId, String firstName, String lastName, String abbreviation,  String cityName, String state, String siteName) {
        long Id;

        // First, check if the team with this city name exists in the db
        Cursor teamCursor = mContext.getContentResolver().query(
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
            Uri insertedUri = mContext.getContentResolver().insert(
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

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
        // return strings to keep UI functional for now
        String[] resultStrs = new String[cvv.size()];
        for ( int i = 0; i < cvv.size(); i++ ) {
            ContentValues eventValues = cvv.elementAt(i);
           /* String highAndLow = formatHighLows(
                    eventValues.getAsDouble(ScoreboardContract.EventEntry.COLUMN_MAX_TEMP),
                    eventValues.getAsDouble(ScoreboardContract.EventEntry.COLUMN_MIN_TEMP));*/
            resultStrs[i] = getReadableDateString(
                    eventValues.getAsLong(ScoreboardContract.EventEntry.COLUMN_DATE)) +
                    " - " + eventValues.getAsString(ScoreboardContract.EventEntry.COLUMN_SHORT_DESC) +
                    " - " + highAndLow;
        }
        return resultStrs;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p/>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getScoreDataFromJson(String scoreJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        final String EVENT_DATE = "events_date";

        final String OSB_AWAY_TEAM = "away_team";

        final String OSB_HOME_TEAM = "home_team";

         //final String TABLE_NAME = "team";
         final String OSB_TEAM_ID = "team_id";
         final String OSB_FIRST_NAME_TEAM = "first_name";
         final String OSB_LAST_NAME_TEAM = "last_name";
         final String OSB_ABBREVIATION = "abbreviation";
         final String OSB_SITE_NAME = "site_name";
         final String OSB_CITY = "city";
         final String OSB_STATE = "state";

        final String OSB_EVENT = "event";
        final String OSB_EVENT_STATUS = "event_status";


        final String OSB_FULL_NAME = "full_name";
        final String OSB_AWAY_POINT_SCORED = "away_points_scored";
        final String OSB_HOME_POINT_SCORED = "home_points_scored";

        JSONObject scoreJson = new JSONObject(scoreJsonStr);
        JSONArray scoreArray = scoreJson.getJSONArray(OSB_EVENT);
        String eventDate =  scoreJson.getString(EVENT_DATE);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(scoreArray.length());

        String[] resultStrs = new String[scoreArray.length()];
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

            eventStatus = dayScoreboard.getString(OSB_EVENT_STATUS);
            awayPointScored = dayScoreboard.getInt(OSB_AWAY_POINT_SCORED);
            homePointScored = dayScoreboard.getInt(OSB_HOME_POINT_SCORED);

            JSONObject awayTeamObject = dayScoreboard.getJSONObject(OSB_AWAY_TEAM);

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

            eventValues.put(ScoreboardContract.EventEntry.COLUMN_EVENT_ID, locationId);
            eventValues.put(ScoreboardContract.EventEntry.COLUMN_EVENT_DATE, eventDate);
            eventValues.put(ScoreboardContract.EventEntry.COLUMN_EVENT_STATUS, eventStatus);
            eventValues.put(ScoreboardContract.EventEntry.COLUMN_START_DATE_TIME, pressure);
            eventValues.put(ScoreboardContract.EventEntry.COLUMN_AWAY_TEAM_ID_KEY, IdTeamAway);
            eventValues.put(ScoreboardContract.EventEntry.COLUMN_HOME_TEAM_ID_KEY, IdTeamHome);
            eventValues.put(ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_FIRTS, high);
            eventValues.put(ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_SECOND, low);
            eventValues.put(ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_THIRD, description);
            eventValues.put(ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_FOURTH, weatherId);
            eventValues.put(ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_FIRTS, high);
            eventValues.put(ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_SECOND, low);
            eventValues.put(ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_THIRD, description);
            eventValues.put(ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_FOURTH, weatherId);


            cVVector.add(eventValues);
            

           // Vector<ContentValues> cVVector = new Vector<ContentValues>(weatherArray.length());

            resultStrs[i] = awayTeam + " " + awayPointScored + " @ " + homeTeam + " " + homePointScored;
        }

        for (String s : resultStrs) {
            Log.v(LOG_TAG, "Score entry: " + s);
        }
        return resultStrs;

    }

    @Override
    protected String[] doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String scoreboardJsonStr = null;
        String sport_type = "nba";
        String authotization_value = "Bearer fecf2b5f-f91a-4f7c-bfc8-abdad5613809";

        try {

            final String SCOREBOARD_BASE_URL = "https://erikberg.com/events.json?";
            final String DAY_PARAM = "date";
            final String CATEGORY_PARAM = "sport";
            final String AUTHORIZATION_PARAM = "Authorization";

            Uri builtUri = Uri.parse(SCOREBOARD_BASE_URL).buildUpon()
                    .appendQueryParameter(DAY_PARAM, params[0])
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
            Log.v(LOG_TAG, " Scoreboard JSON string " + scoreboardJsonStr);

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

        try {
            return getScoreDataFromJson(scoreboardJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String[] result) {
        if (result != null) {
            mScoreboardAdapter.clear();
            for (String dayScoreStr : result) {
                mScoreboardAdapter.add(dayScoreStr);
            }
            // New data is back from the server.  Hooray!
        }
    }
}