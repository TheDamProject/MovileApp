package com.jotamarti.golocal.UseCases.Users;

import org.json.JSONObject;

public class UserCallbacks {

    public interface OnResponseCallbackGetUser {
        void onResponse(JSONObject json);
        void onErrorResponse(int error);
    }

}
