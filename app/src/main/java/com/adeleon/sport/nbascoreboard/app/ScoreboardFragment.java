package com.adeleon.sport.nbascoreboard.app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.adeleon.sport.nbascoreboard.app.data.ScoreboardContract;
import com.adeleon.sport.nbascoreboard.app.data.ScoreboardProvider;
import com.adeleon.sport.nbascoreboard.app.utils.ScoreUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by adeleon on 3/25/2015.
 */

public class ScoreboardFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG_DATE_DIALOG = "date_dialog";
    private EditText dateScoreEditText;
    private DatePickerFragment dateScoreDatePickDialog;
    private SimpleDateFormat formatter;

    private static final int SCORED_LOADER = 0;

    private ScoreAdapter mScoreboardAdapter;

    private static final String[] SCORE_COLUMNS = {
            ScoreboardContract.EventEntry.TABLE_NAME + "." + ScoreboardContract.EventEntry._ID,
            ScoreboardProvider.AWAY_TEAM + "." + ScoreboardContract.TeamEntry.COLUMN_LAST_NAME_TEAM,
            ScoreboardProvider.HOME_TEAM + "." + ScoreboardContract.TeamEntry.COLUMN_LAST_NAME_TEAM,
            ScoreboardContract.EventEntry.TABLE_NAME + "." + ScoreboardContract.EventEntry.COLUMN_EVENT_DATE,
            ScoreboardContract.EventEntry.COLUMN_AWAY_PERIOD_SCORES,
            ScoreboardContract.EventEntry.COLUMN_HOME_PERIOD_SCORES
    };

    public static final int COL_EVENT_ID = 0;
    public static final int COL_AWAY_TEAM_NAME = 1;
    public static final int COL_HOME_TEAM_NAME = 2;
    public static final int COL_EVENT_DATE = 3;
    public static final int COL_AWAY_PERIOD_SCORES = 4;
    public static final int COL_HOME_PERIOD_SCORES = 5;

    public ScoreboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.

        ScoreUtil.initcialize(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SCORED_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
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
//        FetchScoreTask scoreTask = new FetchScoreTask(getActivity(), mScoreboardAdapter);
//        String dayScoreStr, dayScoreResult = null;
//        Date date = null;
//
//        if (! dateScoreEditText.getText().toString().equals("")) {
//            formatter = new SimpleDateFormat("MMMM d, yyyy");
//            dayScoreStr = dateScoreEditText.getText().toString();
//            try {
//            date = formatter.parse(dayScoreStr);
//            formatter = new SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault());
//            dayScoreResult  = formatter.format(date);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//        else{
//            formatter = new SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault());
//            Calendar cal = Calendar.getInstance();
//            dayScoreResult = formatter.format( cal.getTime());
//            formatter  = new SimpleDateFormat("MMMM d, yyyy", java.util.Locale.getDefault());
//            dayScoreStr = formatter.format( cal.getTime());
//            dateScoreEditText.setText(dayScoreStr);
//        }
//
//        scoreTask.execute(dayScoreResult);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateScoreboard();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mScoreboardAdapter = new ScoreAdapter(getActivity(), null, 0);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_scoreboard);
        listView.setAdapter(mScoreboardAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                if (cursor != null) {
                    Uri uri = ScoreboardContract.EventEntry.buildEvetIdAndDateUri(cursor.getLong(COL_EVENT_ID),cursor.getString(COL_EVENT_DATE));

                    Intent intent = new Intent(getActivity(), DetailActivity.class).setData(uri);
                    startActivity(intent);
                }
            }
        });
//
//        dateScoreEditText = (EditText) rootView.findViewById(R.id.date_score_edittext);
//        dateScoreEditText.setInputType(InputType.TYPE_NULL);
//        dateScoreEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v == dateScoreEditText) {
//                    dateScoreDatePickDialog.show(getFragmentManager(), TAG_DATE_DIALOG);
//                }
//            }
//        });

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
                formatter = new SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
                String dateInString = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                Date date = null;
                try {

                    date = formatter.parse(dateInString);
                    formatter = new SimpleDateFormat("MMMM d, yyyy", java.util.Locale.getDefault());

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                dateScoreEditText.setText(formatter.format(date));
            }
        });
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri scoredUri = ScoreboardContract.EventEntry.buildEventDate(ScoreUtil.getCurrentDate());

        return new CursorLoader(getActivity(), scoredUri, SCORE_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        mScoreboardAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mScoreboardAdapter.swapCursor(null);
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