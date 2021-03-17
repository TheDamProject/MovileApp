package com.jotamarti.golocal.UseCases.Clients;

import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;
import com.jotamarti.golocal.App;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;
import com.jotamarti.golocal.Utils.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ClientUsecases implements ClientApi {

    private final String TAG = "UserUsecases";


    @Override
    public void getClientFromBackend(String uid, ClientCallbacks.onResponseCallBackGetClient onResponseCallBackGetClient) {
        String baseUrl = String.valueOf(App.getContext().getResources().getText(R.string.api_base_url));
        String uri = baseUrl + "/api/client/" + uid;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onResponseCallBackGetClient.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error == null) {
                    onResponseCallBackGetClient.onErrorResponse(BackendErrors.SERVER_ERROR);
                }
                Log.d(TAG, error.toString());
                BackendErrors httpNetworkError = BackendErrors.getBackendError(error.networkResponse.statusCode);
                onResponseCallBackGetClient.onErrorResponse(httpNetworkError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("X-AUTH-TOKEN", "TC9[L7<D4gd)5{6<!=H!jUYE7mum<H~NS4yJo/a+7(3v>f5n+_49u|_a4|7W");
                return params;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, 1, 5.0f));
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void registerClientInBackend(String uid, String avatar, String nickName, ClientCallbacks.onResponseRegisterClientInBackend onResponseRegisterClientInBackend) {
        String baseUrl = String.valueOf(App.getContext().getResources().getText(R.string.api_base_url));
        String uri = baseUrl + "/api/clients/add";

        // Params
        Map<String, String> mParams = new HashMap<String, String>();
        mParams.put("uid", uid);
        mParams.put("avatar", "data:image/png;base64," + avatar);
        mParams.put("nick", nickName);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, uri, new JSONObject(mParams), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onResponseRegisterClientInBackend.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error == null) {
                    onResponseRegisterClientInBackend.onErrorResponse(BackendErrors.SERVER_ERROR);
                }
                Log.d(TAG, error.toString());
                BackendErrors httpNetworkError = BackendErrors.getBackendError(error.networkResponse.statusCode);
                onResponseRegisterClientInBackend.onErrorResponse(httpNetworkError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("X-AUTH-TOKEN", "TC9[L7<D4gd)5{6<!=H!jUYE7mum<H~NS4yJo/a+7(3v>f5n+_49u|_a4|7W");
                return params;
            }

        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, 1, 5.0f));
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    @Override
    public void modifyClient(String uid, JsonObject newValues) {

    }

    @Override
    public void deleteClient(String uid) {

    }

}
