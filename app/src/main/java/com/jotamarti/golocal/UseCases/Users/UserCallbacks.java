package com.jotamarti.golocal.UseCases.Users;

import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;

public class UserCallbacks {

    public interface onResponseCallBackLoginUserInAuthService {
        void onResponse(String uid);
        void onErrorResponse(AuthErrors error);
    };

    public interface onResponseCallBackRegisterUserInAuthService {
        void onResponse(FirebaseUser firebaseUser);
        void onErrorResponse(AuthErrors error);
    };

}
