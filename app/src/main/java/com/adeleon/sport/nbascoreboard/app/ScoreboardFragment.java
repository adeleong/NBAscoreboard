package com.adeleon.sport.nbascoreboard.app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by adeleon on 3/25/2015.
 */

public class ScoreboardFragment extends Fragment {
    private static final String TAG_DATE_DIALOG = "date_dialog";
    private EditText dateScoreEditText;
    private DatePickerFragment dateScoreDatePickDialog;
    private SimpleDateFormat formatter;

    private ArrayAdapter<String> mScoreboardAdapter;

    public ScoreboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.scoreboardfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateScoreboard();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateScoreboard() {
        FetchScoreTask scoreTask = new FetchScoreTask();
        String dayScoreStr, dayScoreResult = null;
        Date date = null;

        if (! dateScoreEditText.getText().toString().equals("")) {
            formatter = new SimpleDateFormat("MMMM d, yyyy");
            dayScoreStr = dateScoreEditText.getText().toString();
            try {
            date = formatter.parse(dayScoreStr);
            formatter = new SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault());
            dayScoreResult  = formatter.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else{
            formatter = new SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            dayScoreResult = formatter.format( cal.getTime());
            formatter  = new SimpleDateFormat("MMMM d, yyyy", java.util.Locale.getDefault());
            dayScoreStr = formatter.format( cal.getTime());
            dateScoreEditText.setText(dayScoreStr);
        }

        scoreTask.execute(dayScoreResult);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateScoreboard();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mScoreboardAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_scoreboard, // The name of the layout ID.
                        R.id.list_item_scoreboard_textView, // The ID of the textview to populate.
                        new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_scoreboard);
        listView.setAdapter(mScoreboardAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String scoreBoard = mScoreboardAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, scoreBoard);
                startActivity(intent);
            }
        });

        dateScoreEditText = (EditText) rootView.findViewById(R.id.date_score_edittext);
        dateScoreEditText.setInputType(InputType.TYPE_NULL);
        dateScoreEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == dateScoreEditText) {
                    dateScoreDatePickDialog.show(getFragmentManager(), TAG_DATE_DIALOG);
                }
            }
        });

        setDateTimeField();

        return rootView;
    }

    private void setDateTimeField() {
        dateScoreDatePickDialog = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        dateScoreDatePickDialog.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        dateScoreDatePickDialog.setCallBack(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                formatter  = new SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
                String dateInString = dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
                Date date = null;
                try {

                    date = formatter .parse(dateInString);
                    formatter  = new SimpleDateFormat("MMMM d, yyyy", java.util.Locale.getDefault());

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                dateScoreEditText.setText(formatter.format(date));
            }
        });
    }

    public class FetchScoreTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchScoreTask.class.getSimpleName();

        private String getReadableDateString(long time) {
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
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
            final String OSB_EVENT = "event";
            final String OSB_AWAY_TEAM = "away_team";
            final String OSB_HOME_TEAM = "home_team";
            final String OSB_EVENT_STATUS = "event_status";
            final String OSB_FULL_NAME = "full_name";
            final String OSB_AWAY_POINT_SCORED = "away_points_scored";
            final String OSB_HOME_POINT_SCORED = "home_points_scored";

            JSONObject scoreJson = new JSONObject(scoreJsonStr);
            JSONArray scoreArray = scoreJson.getJSONArray(OSB_EVENT);

            String[] resultStrs = new String[scoreArray.length()];
            for (int i = 0; i < scoreArray.length(); i++) {

                String awayTeam;
                String homeTeam;
                String eventStatus;
                int awayPointScored;
                int homePointScored;

                JSONObject dayScoreboard = scoreArray.getJSONObject(i);

                eventStatus = dayScoreboard.getString(OSB_EVENT_STATUS);
                awayPointScored = dayScoreboard.getInt(OSB_AWAY_POINT_SCORED);
                homePointScored = dayScoreboard.getInt(OSB_HOME_POINT_SCORED);

                JSONObject awayTeamObject = dayScoreboard.getJSONObject(OSB_AWAY_TEAM);
                awayTeam = awayTeamObject.getString(OSB_FULL_NAME);

                JSONObject homeTeamObject = dayScoreboard.getJSONObject(OSB_HOME_TEAM);
                homeTeam = homeTeamObject.getString(OSB_FULL_NAME);

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

    public static class DatePickerFragment extends DialogFragment {
        DatePickerDialog.OnDateSetListener ondateSet;

        public DatePickerFragment() {
        }

        public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
            ondateSet = ondate;
        }

        private int year, month, day;

        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
            year = args.getInt("year");
            month = args.getInt("month");
            day = args.getInt("day");
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getActivity(), ondateSet, year, month, day);
        }
    }
}