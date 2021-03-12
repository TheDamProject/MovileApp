package com.jotamarti.golocal.Repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.UseCases.Users.UserCallbacks;
import com.jotamarti.golocal.UseCases.Users.UserRepositoryFactory;
import com.jotamarti.golocal.UseCases.Users.UserUseCases;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;

public class UserRepository implements UserRepositoryFactory {

    UserUseCases userUsecases;
    // Auth
    private MutableLiveData<String> userLoggedUid = new MutableLiveData<>();
    private MutableLiveData<AuthErrors> authError = new MutableLiveData<>();
    private MutableLiveData<String> userRegisteredUid = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> fireBaseUserRegistered = new MutableLiveData<>();

    public UserRepository() {
        userUsecases = new UserUseCases();
    }


    // Login
    @Override
    public LiveData<String> loginUserInAuthService(String email, String password) {
        userUsecases.loginUserInAuthService(email, password, new UserCallbacks.onResponseCallBackLoginUserInAuthService() {
            @Override
            public void onResponse(String uid) {
                userLoggedUid.setValue(uid);
            }

            @Override
            public void onErrorResponse(AuthErrors error) {
                authError.setValue(error);
            }
        });
        return userLoggedUid;
    }

    @Override
    public LiveData<AuthErrors> getLoginUserInAuthServiceError() {
        return authError;
    }

    // Register
    @Override
    public LiveData<FirebaseUser> registerUserInAuthService(String email, String password) {
        userUsecases.registerUserInAuthService(email, password, new UserCallbacks.onResponseCallBackRegisterUserInAuthService() {
            @Override
            public void onResponse(FirebaseUser firebaseUser) {
                fireBaseUserRegistered.setValue(firebaseUser);
            }

            @Override
            public void onErrorResponse(AuthErrors error) {
                authError.setValue(error);
            }
        });
        return fireBaseUserRegistered;
    }

    @Override
    public LiveData<AuthErrors> getRegisterUserInAuthServiceError() {
        return authError;
    }
}
