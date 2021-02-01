package com.jotamarti.golocal.Utils;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface OnResponseCallback {
    void onResponse(JSONObject json, String tag);
    void onErrorResponse(int error, String tag);
}
