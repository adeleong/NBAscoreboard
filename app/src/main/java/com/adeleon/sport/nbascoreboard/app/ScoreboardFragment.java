package com.adeleon.sport.nbascoreboard.app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
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
        FetchScoreTask scoreTask = new FetchScoreTask(getActivity(), mScoreboardAdapter);
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