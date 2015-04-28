package com.adeleon.sport.nbascoreboard.app.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.adeleon.sport.nbascoreboard.app.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by theade on 4/26/2015.
 */
public class ScoreUtil {
    private static Context mContext;

    public static void initcialize(Context context) {
        mContext = context;
    }

    private String getReadableDateString(long time) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return formatter.format(cal.getTime());
    }

    public static Drawable getImagenTeam(String nameTeam) {

        for (ImageTeam imageTeam : ImageTeam.values()) {
            if (imageTeam.name.toUpperCase().equals(nameTeam.toUpperCase())) {
                return imageTeam.drawable;
            }
        }

        return mContext.getResources().getDrawable(R.drawable.ic_launcher);
    }

    private enum ImageTeam {
        CAVALIERS("Cavaliers", mContext.getResources().getDrawable(R.drawable.cavaliers)),
        CELTICS("Celtics", mContext.getResources().getDrawable(R.drawable.celtics)),
        CLIPPERS("Clippers", mContext.getResources().getDrawable(R.drawable.clippers)),
        SPURS("Spurs", mContext.getResources().getDrawable(R.drawable.spurs)),
        RAPTORS("Raptors", mContext.getResources().getDrawable(R.drawable.raptors)),
        WIZARDS("Wizards", mContext.getResources().getDrawable(R.drawable.wizards)),
        ROCKETS("Rockets", mContext.getResources().getDrawable(R.drawable.rockets)),
        MAVERICKS("Mavericks", mContext.getResources().getDrawable(R.drawable.mavericks)),
        TAIL_BLAZERS("Trail Blazers",mContext.getResources().getDrawable(R.drawable.blazers)),
        BULLS("Bulls",mContext.getResources().getDrawable(R.drawable.bulls)),
        GRIZZLIES("Grizzlies",mContext.getResources().getDrawable(R.drawable.grizzlies)),
        HAWKS("Hawks",mContext.getResources().getDrawable(R.drawable.hawks)),
        NETS("Nets",mContext.getResources().getDrawable(R.drawable.nets)),
        WARRIORS("Warriors",mContext.getResources().getDrawable(R.drawable.warriors)),
        PELICANS("Pelicans",mContext.getResources().getDrawable(R.drawable.pelicans)),
        BUCKS("Bucks",mContext.getResources().getDrawable(R.drawable.bucks));

        private String name;
        private Drawable drawable;

        ImageTeam(String name, Drawable drawable) {
            this.name = name;
            this.drawable = drawable;
        }
    }
}
