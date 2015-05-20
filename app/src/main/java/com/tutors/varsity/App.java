package com.tutors.varsity;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private static Context sContext;
    private static App sInstance;


    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sContext = getApplicationContext();
    }
}
