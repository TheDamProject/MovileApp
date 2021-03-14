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
    public void getClient(String uid, String avatar, ClientCallbacks.onResponseCallBackGetClient onResponseCallBackGetClient) {
        //String baseUrl = String.valueOf(App.getContext().getResources().getText(R.string.api_base_url));
        String baseUrl = "http://37.133.227.193:30180";
        String uri = baseUrl + "/api/client/add";
        Map<String, String> mParams = new HashMap<String, String>();
        mParams.put("uid", uid);
        mParams.put("avatar", "data:image/png;base64," + avatar);
        mParams.put("nick", "tu culo");

        JSONObject jsonBodyObj = new JSONObject();
        try{
            jsonBodyObj.put("uid", uid);
            jsonBodyObj.put("avatar", avatar);
            jsonBodyObj.put("nick", "tu culo");
        }catch (JSONException e){
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, uri, new JSONObject(mParams), new Response.Listener<JSONObject>() {
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

            /*@Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }*/
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, 1, 10.0f));
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void registerClientInBackend(String uid, String avatar, ClientCallbacks.onResponseCallBackGetClient onResponseCallBackGetClient) {
        //String baseUrl = String.valueOf(App.getContext().getResources().getText(R.string.api_base_url));
        String baseUrl = "http://jotamarti.ddns.net:30180";
        String uri = baseUrl + "/api/client/add";
        Map<String, String> mParams = new HashMap<String, String>();
        //mParams.put("X-AUTH-TOKEN", "TC9[L7<D4gd)5{6<!=H!jUYE7mum<H~NS4yJo/a+7(3v>f5n+_49u|_a4|7W");
        mParams.put("uid", uid);
        mParams.put("avatar", avatar);
        mParams.put("nick", "tu culo");

        JSONObject jsonBodyObj = new JSONObject();
        try{
            jsonBodyObj.put("uid", uid);
            jsonBodyObj.put("avatar", avatar);
            jsonBodyObj.put("nick", "tu culo");
        }catch (JSONException e){
            e.printStackTrace();
        }
        final String requestBody = jsonBodyObj.toString();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, uri, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //callerObject.onResponse(response, TAG);
                onResponseCallBackGetClient.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.getMessage());
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

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }
        };
        RequestQueueSingleton.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    @Override
    public void modifyClient(String uid, JsonObject newValues) {
        // Esto es el get




    }

    @Override
    public void deleteClient(String uid) {

    }

    // Auth
    @Override
    public void loginClient(String email, String password, ClientCallbacks.onResponseCallBackAuthClient onResponseCallbackAuthUser) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(TaskExecutors.MAIN_THREAD, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            onResponseCallbackAuthUser.onResponse(user.getUid());
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                onResponseCallbackAuthUser.onErrorResponse(AuthErrors.WRONG_PASSWORD);
                            } catch (FirebaseAuthInvalidUserException e) {
                                onResponseCallbackAuthUser.onErrorResponse(AuthErrors.EMAIL_NOT_FOUND);
                            } catch (Exception e) {
                                onResponseCallbackAuthUser.onErrorResponse(AuthErrors.GENERIC_LOGIN_ERROR);
                            }
                        }
                    }
                });
    }

    @Override
    public void registerClientInAuthService(String email, String password, ClientCallbacks.onResponseCallBackAuthClient onResponseCallbackAuthUser) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(TaskExecutors.MAIN_THREAD, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            onResponseCallbackAuthUser.onResponse(user.getUid());
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                onResponseCallbackAuthUser.onErrorResponse(AuthErrors.EMAIL_ALREADY_IN_USE);
                            } catch (Exception e) {
                                onResponseCallbackAuthUser.onErrorResponse(AuthErrors.GENERIC_REGISTER_ERROR);
                            }
                        }
                    }
                });

    }

}
