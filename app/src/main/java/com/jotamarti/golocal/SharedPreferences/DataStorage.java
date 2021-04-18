package com.jotamarti.golocal.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class DataStorage {

    public static final int INTEGER = 1;
    public static final int FLOAT = 2;
    public static final int LONG = 3;
    public static final int STRING = 4;
    public static final int BOOLEAN = 5;
    private Context ctx;
    private SharedPreferences pref;
    private SharedPreferences.Editor writer;

    public DataStorage(Context ctx) {
        this.ctx = ctx;
        pref = ctx.getSharedPreferences("authPreferences", Context.MODE_PRIVATE);
        writer = pref.edit();
    }

    public void write(String key, int value) {
        writer.putInt(key, value);
        writer.apply();
    }

    public void write(String key, float value) {
        writer.putFloat(key, value);
        writer.apply();
    }

    public void write(String key, String value) {
        writer.putString(key, value);
        writer.apply();
    }

    public void write(String key, boolean value) {
        writer.putBoolean(key, value);
        writer.apply();
    }

    public void write(String key, long value) {
        writer.putLong(key, value);
        writer.apply();
    }

    public void removePreferences(){
        writer.clear().apply();
    }

    public Object read(String key, int DataType) {
        Object response = new Object();
        if (DataType == INTEGER)
            response = pref.getInt(key, 0);
        else if (DataType == BOOLEAN)
            response = pref.getBoolean(key, false);
        else if (DataType == LONG)
            response = pref.getLong(key, 0);
        else if (DataType == STRING)
            response = pref.getString(key, "");
        else
            response = pref.getFloat(key, 0.0f);
        return response;
    }
}
