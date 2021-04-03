package com.jotamarti.golocal.UseCases.Users;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;

import java.util.ArrayList;
import java.util.List;

public interface UserRepositoryFactory {

    // Backend
    LiveData<User> getUserFromBackend(String uid);
    LiveData<ArrayList<Shop>> getShopsNearby(String lat, String lang);

    // Auth
    LiveData<String> loginUserInAuthService(String email, String password);
    LiveData<FirebaseUser> registerUserInAuthService(String email, String password);
    LiveData<AuthErrors> getAuthServiceError();

}
