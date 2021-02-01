package com.jotamarti.golocal.UseCases.Users;

import android.content.Context;
import android.content.res.Resources;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jotamarti.golocal.App;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.Utils.OnResponseCallback;
import com.jotamarti.golocal.Utils.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class GetUser {

    private OnResponseCallback callerObject = null;
    private final String TAG = App.getContext().getResources().getString(R.string.get_user_usecase);

    public GetUser(OnResponseCallback onResponseCallback){
        callerObject = onResponseCallback;
    }

    public void getUser(Context context, String uid) {
        String baseUrl = String.valueOf(context.getText(R.string.api_base_url));
        String uri = baseUrl + "/user?" + uid;
        JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(Request.Method.GET, baseUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callerObject.onResponse(response, TAG);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callerObject.onErrorResponse(error.networkResponse.statusCode, TAG);
            }
        });
        RequestQueueSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
