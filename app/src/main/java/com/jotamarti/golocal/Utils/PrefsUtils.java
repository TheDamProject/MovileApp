package com.jotamarti.golocal.Utils;

import android.content.SharedPreferences;

public class PrefsUtils {

    public static String getEmailPrefs(SharedPreferences preferences) {
        return preferences.getString("email", "");
    }

    public static String getPasswordPrefs(SharedPreferences preferences) {
        return preferences.getString("password", "");
    }
}
