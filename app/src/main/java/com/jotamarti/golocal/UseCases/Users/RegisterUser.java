package com.jotamarti.golocal.UseCases.Users;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.Utils.OnResponseCallback;
import com.jotamarti.golocal.Utils.RequestQueueSingleton;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterUser {

    private OnResponseCallback callerObject = null;
    private final String TAG = "REGISTER_USER_USECASE";

    public RegisterUser(OnResponseCallback onResponseCallback){
        callerObject = onResponseCallback;
    }

    public void registerUser(Context context, String uid) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("id", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(Request.Method.POST, String.valueOf(context.getText(R.string.api_base_url)), postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callerObject.onResponse(response, "registerUser");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callerObject.onErrorResponse(error.networkResponse.statusCode, "registerUser");
            }
        });
        RequestQueueSingleton.getInstance(context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
