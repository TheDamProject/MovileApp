package com.jotamarti.golocal.UseCases.Users;

import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

import org.json.JSONObject;

public class UserCallbacks {

    public interface OnResponseCallbackGetUser {
        void onResponse(JSONObject json);
        void onErrorResponse(BackendErrors httpNetworkError);
    }

    public interface onResponseCallbackAuthUser {
        void onResponse(String uid);
        void onErrorResponse(AuthErrors error);
    }


}
