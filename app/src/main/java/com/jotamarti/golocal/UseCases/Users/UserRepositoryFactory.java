package com.jotamarti.golocal.UseCases.Users;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;

public interface UserRepositoryFactory {

    LiveData<String> loginUserInAuthService(String email, String password);
    LiveData<AuthErrors> getLoginUserInAuthServiceError();

    LiveData<FirebaseUser> registerUserInAuthService(String email, String password);
    LiveData<AuthErrors> getRegisterUserInAuthServiceError();

}
