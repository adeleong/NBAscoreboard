package com.adeleon.sport.nbascoreboard.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by theade on 4/26/2015.
 */

public class ScoreAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private ScoreAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new ScoreAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}