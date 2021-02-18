package com.jotamarti.golocal;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private static Context mContext;
    private static Boolean appInDevelopment = true;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }

    public static Boolean getAppInDevelopment() {
        return appInDevelopment;
    }
}
