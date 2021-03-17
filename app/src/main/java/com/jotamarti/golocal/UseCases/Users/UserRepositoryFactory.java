package com.jotamarti.golocal.UseCases.Users;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;

public interface UserRepositoryFactory {

    // Backend
    LiveData<User> getUserFromBackend(String uid);

    // Auth
    LiveData<String> loginUserInAuthService(String email, String password);
    LiveData<FirebaseUser> registerUserInAuthService(String email, String password);
    LiveData<AuthErrors> getAuthServiceError();

}
