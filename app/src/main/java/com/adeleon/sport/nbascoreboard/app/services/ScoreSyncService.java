package com.adeleon.sport.nbascoreboard.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by theade on 4/26/2015.
 */

/**
 * Created by GPEREZ on 3/29/2015.
 */
public class ScoreSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static ScoreSyncAdapter sScoreSyncAdapter = null;

    @Override
    public void onCreate() {

        synchronized (sSyncAdapterLock) {
            if (sScoreSyncAdapter == null) {
                sScoreSyncAdapter = new ScoreSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sScoreSyncAdapter.getSyncAdapterBinder();
    }

}