package com.jotamarti.golocal.UseCases.Users;

import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jotamarti.golocal.App;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.Utils.CustomJsonArrayRequest;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;
import com.jotamarti.golocal.Utils.RequestQueueSingleton;
import com.jotamarti.golocal.dummy.ShopsDummy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserUseCases implements UserApi {

    private final String TAG = "UserUseCases";

    private FirebaseAuth firebaseAuth;

    public UserUseCases() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void getUserFromBackend(String uid, UserCallbacks.onResponseCallBackGetUserFromBackend onResponseCallBackGetUserFromBackend) {
        String baseUrl = String.valueOf(App.getContext().getResources().getText(R.string.api_base_url));
        String uri = baseUrl + "/api/login";

        Map<String, String> mParams = new HashMap<String, String>();
        mParams.put("uid", uid);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, uri, new JSONObject(mParams), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onResponseCallBackGetUserFromBackend.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error == null || error.networkResponse == null) {
                    onResponseCallBackGetUserFromBackend.onErrorResponse(BackendErrors.SERVER_ERROR);
                }
                Log.d(TAG, error.toString());
                BackendErrors httpNetworkError = BackendErrors.getBackendError(error.networkResponse.statusCode);
                onResponseCallBackGetUserFromBackend.onErrorResponse(httpNetworkError);
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
    public void getShopsNearby(Double lat, Double lang, UserCallbacks.onResponseCallBackGetShopsNearby onResponseCallBackGetShopsNearby) {
        String baseUrl = String.valueOf(App.getContext().getResources().getText(R.string.api_base_url));
        String uri = baseUrl + "/api/shops";

        JSONObject mParams = new JSONObject();

        try {
            mParams.put("lat", lat);
            mParams.put("lat", lang);
            mParams.put("range", 5000);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //TODO: Cambiar al post
        CustomJsonArrayRequest getNearbyShopsRequest = new CustomJsonArrayRequest(Request.Method.GET, uri, mParams, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                onResponseCallBackGetShopsNearby.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error == null) {
                    onResponseCallBackGetShopsNearby.onErrorResponse(BackendErrors.SERVER_ERROR);
                }
                Log.d(TAG, error.toString());
                BackendErrors httpNetworkError = BackendErrors.getBackendError(error.networkResponse.statusCode);
                onResponseCallBackGetShopsNearby.onErrorResponse(httpNetworkError);
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
        getNearbyShopsRequest.setRetryPolicy(new DefaultRetryPolicy(0, 1, 5.0f));
        RequestQueueSingleton.getInstance().addToRequestQueue(getNearbyShopsRequest);
    }

    @Override
    public void loginUserInAuthService(String email, String password, UserCallbacks.onResponseCallBackLoginUserInAuthService onResponseCallBackLoginUserInAuthService) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(TaskExecutors.MAIN_THREAD, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = task.getResult().getUser();
                            Log.d(TAG, "Me ha llegado el firebase user con el siguiente UID: " + user.getUid());
                            onResponseCallBackLoginUserInAuthService.onResponse(user.getUid());
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                onResponseCallBackLoginUserInAuthService.onErrorResponse(AuthErrors.WRONG_PASSWORD);
                            } catch (FirebaseAuthInvalidUserException e) {
                                onResponseCallBackLoginUserInAuthService.onErrorResponse(AuthErrors.EMAIL_NOT_FOUND);
                            } catch (Exception e) {
                                onResponseCallBackLoginUserInAuthService.onErrorResponse(AuthErrors.GENERIC_LOGIN_ERROR);
                            }
                        }
                    }
                });
    }

    @Override
    public void registerUserInAuthService(String email, String password, UserCallbacks.onResponseCallBackRegisterUserInAuthService onResponseCallBackRegisterUserInAuthService) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(TaskExecutors.MAIN_THREAD, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = task.getResult().getUser();
                            Log.d(TAG, "Me ha llegado el firebase user con el siguiente UID: " + user.getUid());
                            onResponseCallBackRegisterUserInAuthService.onResponse(user);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                onResponseCallBackRegisterUserInAuthService.onErrorResponse(AuthErrors.EMAIL_ALREADY_IN_USE);
                            } catch (Exception e) {
                                onResponseCallBackRegisterUserInAuthService.onErrorResponse(AuthErrors.GENERIC_REGISTER_ERROR);
                            }
                        }
                    }
                });
    }
}
