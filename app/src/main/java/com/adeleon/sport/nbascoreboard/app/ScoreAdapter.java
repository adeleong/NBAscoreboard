package com.adeleon.sport.nbascoreboard.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adeleon.sport.nbascoreboard.app.utils.ScoreUtil;

/**
 * Created by theade on 4/26/2015.
 */


public class ScoreAdapter extends CursorAdapter {
    public static final String LOG_TAG = ScoreAdapter.class.getSimpleName();

    public static class ViewHolder {
        private final ImageView icoFirstTeam;
        private final TextView nameFirstTeam;
        private final TextView pointsFirstTeam;
        private final ImageView icoSecondTeam;
        private final TextView nameSecondTeam;
        private final TextView pointsSecondTeam;

        public ViewHolder(View view) {
            icoFirstTeam = (ImageView) view.findViewById(R.id.away_ico_team);
            nameFirstTeam = (TextView) view.findViewById(R.id.name_first_team);
            pointsFirstTeam = (TextView) view.findViewById(R.id.points_first_team);
            icoSecondTeam = (ImageView)view.findViewById(R.id.home_ico_team);
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

        String awayNameTeam = cursor.getString(ScoreboardFragment.COL_AWAY_TEAM_NAME);
        String homeNameTeam = cursor.getString(ScoreboardFragment.COL_HOME_TEAM_NAME);

        viewHolder.icoFirstTeam.setImageDrawable(ScoreUtil.getImagenTeam(awayNameTeam));
        viewHolder.icoSecondTeam.setImageDrawable(ScoreUtil.getImagenTeam(homeNameTeam));

        viewHolder.nameFirstTeam.setText(awayNameTeam);
        viewHolder.nameSecondTeam.setText(homeNameTeam);

        viewHolder.pointsFirstTeam.setText(cursor.getString(ScoreboardFragment.COL_AWAY_PERIOD_SCORES));
        viewHolder.pointsSecondTeam.setText(cursor.getString(ScoreboardFragment.COL_HOME_PERIOD_SCORES));

    }
}