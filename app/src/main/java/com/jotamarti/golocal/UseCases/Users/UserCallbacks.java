package com.jotamarti.golocal.UseCases.Users;

import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

import org.json.JSONObject;

public class UserCallbacks {

    // Backend
    public interface onResponseCallBackGetUserFromBackend {
        void onResponse(JSONObject jsonObject);
        void onErrorResponse(BackendErrors backendError);
    };

    public interface onResponseCallBackGetShopsNearby {
        void onResponse(JSONObject jsonObject);
        void onErrorResponse(BackendErrors backendError);
    };

    // Auth service
    public interface onResponseCallBackLoginUserInAuthService {
        void onResponse(String uid);
        void onErrorResponse(AuthErrors error);
    };

    public interface onResponseCallBackRegisterUserInAuthService {
        void onResponse(FirebaseUser firebaseUser);
        void onErrorResponse(AuthErrors error);
    };



}
