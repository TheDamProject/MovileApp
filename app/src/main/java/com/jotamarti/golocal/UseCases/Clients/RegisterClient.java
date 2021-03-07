package com.jotamarti.golocal.UseCases.Clients;

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

public class RegisterClient {

    private OnResponseCallback callerObject = null;
    private final String TAG = "REGISTER_USER_USECASE";

    public RegisterClient(OnResponseCallback onResponseCallback){
        callerObject = onResponseCallback;
    }

    public void registerClient(Context context, String uid) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("id", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(Request.Method.POST, String.valueOf(context.getText(R.string.api_base_url)), postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callerObject.onResponse(response, "registerClient");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callerObject.onErrorResponse(error.networkResponse.statusCode, "registerClient");
            }
        });
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}
