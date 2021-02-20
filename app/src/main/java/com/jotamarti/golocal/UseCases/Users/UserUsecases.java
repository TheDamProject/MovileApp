package com.jotamarti.golocal.UseCases.Users;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;
import com.jotamarti.golocal.App;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.Utils.RequestQueueSingleton;

import org.json.JSONObject;

public class UserUsecases implements UserApi {


    @Override
    public void getUser(String uid, UserCallbacks.OnResponseCallbackGetUser onResponseCallbackGetUser) {
        String baseUrl = String.valueOf(App.getContext().getResources().getText(R.string.api_base_url));
        String uri = baseUrl + "/user?" + uid;
        JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(Request.Method.GET, baseUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //callerObject.onResponse(response, TAG);
                onResponseCallbackGetUser.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onResponseCallbackGetUser.onErrorResponse(error.networkResponse.statusCode);
            }
        });
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void registerUser(String uid) {

    }

    @Override
    public void modifyUser(String uid, JsonObject newValues) {

    }

    @Override
    public void deleteUser(String uid) {

    }
}
