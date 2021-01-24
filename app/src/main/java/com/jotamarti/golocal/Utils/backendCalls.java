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

public class backendCalls {

    OnResponseCallback callerObject = null;
    private final String TAG = "BACKED_CALLS";

    public backendCalls(OnResponseCallback onResponseCallback){
        callerObject = onResponseCallback;
    }

    public static String url = "https://randomuser.me/api/";

    public void registerUser(Context context, String uid) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("id", uid);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    response.put("type", "registerUser");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callerObject.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callerObject.onErrorResponse("error");
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void getUser(Context context, String uid) {
        String uri = url + "user?" + uid;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    response.put("type", "getUser");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callerObject.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callerObject.onErrorResponse("error");
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

}
