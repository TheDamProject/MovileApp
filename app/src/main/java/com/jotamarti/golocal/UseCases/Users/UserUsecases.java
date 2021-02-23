package com.jotamarti.golocal.UseCases.Users;

import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.jotamarti.golocal.Activities.AuthActivity;
import com.jotamarti.golocal.App;
import com.jotamarti.golocal.R;
import com.jotamarti.golocal.Utils.CustomToast;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.RequestQueueSingleton;

import org.json.JSONObject;

public class UserUsecases implements UserApi {

    private final String TAG = "UserUsecases";


    @Override
    public void getUser(String uid, UserCallbacks.OnResponseCallbackGetUser onResponseCallbackGetUser) {
        String baseUrl = String.valueOf(App.getContext().getResources().getText(R.string.api_base_url));
        String uri = baseUrl + "/user?" + uid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, baseUrl, null, new Response.Listener<JSONObject>() {
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
    public void registerUserInBackend(String uid) {

    }

    @Override
    public void modifyUser(String uid, JsonObject newValues) {

    }

    @Override
    public void deleteUser(String uid) {

    }

    // Auth
    @Override
    public void loginUser(String email, String password, UserCallbacks.onResponseCallbackAuthUser onResponseCallbackAuthUser) {
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
    public void registerUserInAuthService(String email, String password, UserCallbacks.onResponseCallbackAuthUser onResponseCallbackAuthUser) {
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