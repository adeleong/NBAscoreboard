package com.adeleon.sport.nbascoreboard.app;

/**
 * Created by GPEREZ on 4/27/2015.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

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
                mScoreboardStr + SCORE_SHARE_HASHTAG);
        return shareIntent;
    }
}
