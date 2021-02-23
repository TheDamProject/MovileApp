package com.jotamarti.golocal.UseCases.Users;

import com.jotamarti.golocal.Utils.Errors.AuthErrors;

import org.json.JSONObject;

public class UserCallbacks {

    public interface OnResponseCallbackGetUser {
        void onResponse(JSONObject json);
        void onErrorResponse(int error);
    }

    public interface onResponseCallbackAuthUser {
        void onResponse(String uid);
        void onErrorResponse(AuthErrors error);
    }


}
