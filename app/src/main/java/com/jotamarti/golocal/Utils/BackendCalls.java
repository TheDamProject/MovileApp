package com.jotamarti.golocal.Utils;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class BackendCalls {

    OnResponseCallback callerObject = null;
    private final String TAG = "BACKED_CALLS";

    public BackendCalls(OnResponseCallback onResponseCallback) {
        callerObject = onResponseCallback;
    }

    public void postRequest(String url, JSONObject postData, Response.Listener<JSONObject> onResponse, Response.ErrorListener onErrorResponse, String tag, OnResponseCallback callerObject) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callerObject.onResponse(response, tag);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callerObject.onErrorResponse("error", tag);
            }
        });
    }

}
