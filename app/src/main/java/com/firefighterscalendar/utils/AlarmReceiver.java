package com.firefighterscalendar.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    private Utility utility;
    private SessionManager sessionManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        utility = new Utility(context);
        sessionManager = new SessionManager(context);

        if (sessionManager.getBooleanDetail("fire")) {
            if (sessionManager.getStringDetail("freeSubscription").equalsIgnoreCase("yes")) {

                if (sessionManager.getIntDetail("dayCount") <= 2)
                    utility.sendNotification(1, "Check out your new photo of the day in the Firefighters Calendar!", true);
                else
                    utility.sendNotification(1, "See today's Firefighter of the day", true);
            }
            else {
                if (sessionManager.getIntDetail("dayCount") <= 2)
                    utility.sendNotification(1, "Check out your new photo of the day in the Firefighters Calendar!", true);
                else
                    utility.sendNotification(1, "See today's Firefighter of the day", true);
            }
        }
        else {
            sessionManager.setBooleanDetail("fire", true);
        }
    }
}