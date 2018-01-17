package com.firefighterscalendar;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseApp;

public class App extends Application {
    @Override
    public void onCreate() {
        FacebookSdk.sdkInitialize(this);
        FirebaseApp.initializeApp(this);
    }
}
