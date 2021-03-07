package com.jotamarti.golocal.UseCases.Users;

import androidx.lifecycle.LiveData;

import com.jotamarti.golocal.Utils.Errors.AuthErrors;

public interface UserRepositoryFactory {

    LiveData<String> loginUserInAuthService(String email, String password);
    LiveData<AuthErrors> getLoginUserInAuthServiceError();

    LiveData<String> registerUserInAuthService(String email, String password);
    LiveData<AuthErrors> getRegisterUserInAuthServiceError();

}
