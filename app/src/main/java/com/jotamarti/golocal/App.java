package com.jotamarti.golocal;

import android.app.Application;
import android.content.Context;

import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Models.User;

import java.util.List;

public class App extends Application {

    private static Context mContext;
    private static final Boolean appInDevelopment = false;

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
