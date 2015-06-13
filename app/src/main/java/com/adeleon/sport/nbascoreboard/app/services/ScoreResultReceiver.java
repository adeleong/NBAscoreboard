package com.adeleon.sport.nbascoreboard.app.services;

/**
 * Created by Adeleon on 6/12/2015.
 */

    import android.os.Bundle;
    import android.os.Handler;
    import android.os.ResultReceiver;

    public class ScoreResultReceiver extends ResultReceiver {

    private Receiver mReceiver;

    public ScoreResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}