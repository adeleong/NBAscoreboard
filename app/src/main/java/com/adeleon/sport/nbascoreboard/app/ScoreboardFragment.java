package com.adeleon.sport.nbascoreboard.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by adeleon on 3/25/2015.
 */

public class ScoreboardFragment extends Fragment {

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
        inflater.inflate(R.menu.scoreboardfragment , menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchScoreTask scoreTask = new FetchScoreTask();
            scoreTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        String[] data = {
                "Boston Celtics @ Dallas Mavericks",
                "Brooklyn Nets @ Memphis Grizzlies",
                "New York Knicks @ Houston Rockets",
                "Philadelphia 76ers @ New Orleans Pelicans",
                "Chicago Bulls @ Denver Nuggets",
                "San Antonio Spurs @ Cleveland Cavaliers",
                "Detroit Pistons @ Oklahoma City Thunder"
        };
        List<String> dayScoreboard = new ArrayList<String>(Arrays.asList(data));

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mScoreboardAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_scoreboard, // The name of the layout ID.
                        R.id.list_item_scoreboard_textView, // The ID of the textview to populate.
                        dayScoreboard);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_scoreboard);
        listView.setAdapter(mScoreboardAdapter);

        return rootView;
    }

    public class FetchScoreTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchScoreTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String scoreboardJsonStr = null;

            try {

                URL url = new URL("https://erikberg.com/events.json?date=20150325&sport=nba");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer a96e320b-7cdd-49ef-87a8-d30a6054cb1e");
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
                Log.v(LOG_TAG," Scoreboard JSON string "+scoreboardJsonStr);

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
            return null;
        }
    }
}