package com.jotamarti.golocal.UseCases.Users;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

public interface UserRepositoryFactory {

    // Backend calls
    LiveData<User> getUser(String userUid);
    MutableLiveData<BackendErrors> getBackendError();

    // Auth Service
    LiveData<String> loginUser(String email, String password);
    LiveData<AuthErrors> getLoginUserInAuthServiceError();

    LiveData<String> registerUserInAuthService(String email, String password);
    LiveData<AuthErrors> getRegisterUserInAuthServiceError();
}
