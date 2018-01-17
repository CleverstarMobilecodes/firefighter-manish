package com.firefighterscalendar.utils;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    private Utility utility;

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        super.onMessageReceived(from, bundle);
        utility = new Utility(this);
        try {
            String msg = bundle.toString();
            Log.i("msg", msg);
            utility.sendNotification(1, bundle.getString("txMessage"), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
