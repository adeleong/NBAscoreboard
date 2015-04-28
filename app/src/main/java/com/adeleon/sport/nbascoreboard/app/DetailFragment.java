package com.adeleon.sport.nbascoreboard.app;

/**
 * Created by GPEREZ on 4/27/2015.
 */

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adeleon.sport.nbascoreboard.app.data.ScoreboardContract;
import com.adeleon.sport.nbascoreboard.app.data.ScoreboardProvider;
import com.adeleon.sport.nbascoreboard.app.utils.ScoreUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final int SCORED_DETAIL_LOADER = 0;
    private static final String SCORE_SHARE_HASHTAG = " #NBAscoreboardApp";
    private String mScoreboardStr;

    private TextView awayNameTeam;
    private TextView homeNameTeam;
    private TextView awayFirstPeriod;
    private TextView homeFirstPeriod;
    private TextView awaySecondPeriod;
    private TextView homeSecondPeriod;
    private TextView awayThirdPeriod;
    private TextView homeThirdPeriod;
    private TextView awayFourthPeriod;
    private TextView homeFourthPeriod;
    private TextView awayTotalPoints;
    private TextView homeTotalPoints;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    private static final String[] SCORE_DETAIL_COLUMNS = {
            ScoreboardContract.EventEntry.TABLE_NAME + "." + ScoreboardContract.EventEntry._ID,
            ScoreboardProvider.AWAY_TEAM + "." + ScoreboardContract.TeamEntry.COLUMN_LAST_NAME_TEAM,
            ScoreboardProvider.HOME_TEAM + "." + ScoreboardContract.TeamEntry.COLUMN_LAST_NAME_TEAM,
            ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_FIRTS,
            ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_SECOND,
            ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_THIRD,
            ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_FOURTH,
            ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_FIRTS,
            ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_SECOND,
            ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_THIRD,
            ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_FOURTH,
            ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_SCORES,
            ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_SCORES
    };

    public static final int COL_EVENT_ID = 0;
    public static final int COL_AWAY_TEAM_NAME = 1;
    public static final int COL_HOME_TEAM_NAME = 2;

    public static final int COL_AWAY_PERIOD_FIRTS = 3;
    public static final int COL_AWAY_PERIOD_SECOND = 4;
    public static final int COL_AWAY_PERIOD_THIRD =  5;
    public static final int COL_AWAY_PERIOD_FOURTH = 6;

    public static final int COL_HOME_PERIOD_FIRTS = 7;
    public static final int COL_HOME_PERIOD_SECOND = 8;
    public static final int COL_HOME_PERIOD_THIRD = 9;
    public static final int COL_HOME_PERIOD_FOURTH = 10;

    public static final int COL_AWAY_PERIOD_SCORES = 11;
    public static final int COL_HOME_PERIOD_SCORES = 12;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        awayNameTeam = (TextView) rootView.findViewById(R.id.away_name_team);
        homeNameTeam = (TextView) rootView.findViewById(R.id.home_name_team);
        awayFirstPeriod = (TextView) rootView.findViewById(R.id.away_first_period);
        homeFirstPeriod = (TextView) rootView.findViewById(R.id.home_first_period);
        awaySecondPeriod = (TextView) rootView.findViewById(R.id.away_second_period);
        homeSecondPeriod = (TextView) rootView.findViewById(R.id.home_second_period);
        awayThirdPeriod = (TextView) rootView.findViewById(R.id.away_third_period);
        homeThirdPeriod = (TextView) rootView.findViewById(R.id.home_third_period);
        awayFourthPeriod = (TextView) rootView.findViewById(R.id.away_fourth_period);
        homeFourthPeriod = (TextView) rootView.findViewById(R.id.home_fourth_period);
        awayTotalPoints = (TextView) rootView.findViewById(R.id.away_total_points);
        homeTotalPoints = (TextView) rootView.findViewById(R.id.home_total_points);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SCORED_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareScoreIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareScoreIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                awayNameTeam.getText() +" "+awayTotalPoints.getText() +" vs "+homeNameTeam.getText() +" "+homeTotalPoints.getText()  +" "+ SCORE_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        return new CursorLoader(getActivity(), intent.getData(), SCORE_DETAIL_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            awayNameTeam.setText(data.getString(COL_AWAY_TEAM_NAME));
            homeNameTeam.setText(data.getString(COL_HOME_TEAM_NAME));
            awayFirstPeriod.setText(data.getString(COL_AWAY_PERIOD_FIRTS));
            homeFirstPeriod.setText(data.getString(COL_HOME_PERIOD_FIRTS));
            awaySecondPeriod.setText(data.getString(COL_AWAY_PERIOD_SECOND));
            homeSecondPeriod.setText(data.getString(COL_HOME_PERIOD_SECOND));
            awayThirdPeriod.setText(data.getString(COL_AWAY_PERIOD_THIRD));
            homeThirdPeriod.setText(data.getString(COL_HOME_PERIOD_THIRD));
            awayFourthPeriod.setText(data.getString(COL_AWAY_PERIOD_FOURTH));
            homeFourthPeriod.setText(data.getString(COL_HOME_PERIOD_FOURTH));
            awayTotalPoints.setText(data.getString(COL_AWAY_PERIOD_SCORES));
            homeTotalPoints.setText(data.getString(COL_HOME_PERIOD_SCORES));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


    }
}
