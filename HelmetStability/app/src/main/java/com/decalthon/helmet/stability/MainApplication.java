package com.decalthon.helmet.stability;

import android.app.Application;
import android.content.Context;

import com.decalthon.helmet.stability.Activities.MainActivity;

public class MainApplication extends Application {
    private static Context appContext;
    static MainActivity mActivity;

    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        appContext = this;
    }

    public static Context getAppContext() {
        return appContext;
    }
}
