package com.jotamarti.golocal.UseCases.Users;

import com.jotamarti.golocal.Utils.Errors.AuthErrors;

public class UserCallbacks {

    public interface onResponseCallBackLoginUserInAuthService {
        void onResponse(String uid);
        void onErrorResponse(AuthErrors error);
    };

    public interface onResponseCallBackRegisterUserInAuthService {
        void onResponse(String uid);
        void onErrorResponse(AuthErrors error);
    };

}
