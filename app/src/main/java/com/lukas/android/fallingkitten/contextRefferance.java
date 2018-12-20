package com.lukas.android.fallingkitten;

import android.app.Application;
import android.content.Context;


//used to get context in static enviroment
public class contextRefferance extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        contextRefferance.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return contextRefferance.context;
    }

}