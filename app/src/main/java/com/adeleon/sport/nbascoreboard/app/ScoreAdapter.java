package com.adeleon.sport.nbascoreboard.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by theade on 4/26/2015.
 */


public class ScoreAdapter extends CursorAdapter {
    public static final String LOG_TAG = ScoreAdapter.class.getSimpleName();

    public static class ViewHolder {
        public final TextView nameFirstTeam;
        public final TextView pointsFirstTeam;
        public final TextView nameSecondTeam;
        public final TextView pointsSecondTeam;

        public ViewHolder(View view) {
            nameFirstTeam = (TextView) view.findViewById(R.id.name_first_team);
            pointsFirstTeam = (TextView) view.findViewById(R.id.points_first_team);
            nameSecondTeam = (TextView) view.findViewById(R.id.name_second_team);
            pointsSecondTeam = (TextView) view.findViewById(R.id.points_second_team);
        }
    }

    public ScoreAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_scored, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.nameFirstTeam.setText(cursor.getString(ScoreboardFragment.COL_AWAY_TEAM_NAME));
        viewHolder.nameSecondTeam.setText(cursor.getString(ScoreboardFragment.COL_HOME_TEAM_NAME));
        viewHolder.pointsFirstTeam.setText(cursor.getString(ScoreboardFragment.COL_AWAY_PERIOD_SCORES));
        viewHolder.pointsSecondTeam.setText(cursor.getString(ScoreboardFragment.COL_HOME_PERIOD_SCORES));

//        String url = cursor.getString(MoviesFragment.COL_MOVIE_THUMBNAIL_URL);
//        Uri uri = Uri.parse(url);
//
//        viewHolder.iconView.setImageURI(uri);
//
//        String title = cursor.getString(MoviesFragment.COL_MOVIE_TITLE);
//        viewHolder.titleView.setText(title);
//
//        String date = cursor.getString(MoviesFragment.COL_MOVIE_RELEASE_DATE);
//        viewHolder.releaseDateView.setText(MovieUtils.formatDate(date));
    }
}