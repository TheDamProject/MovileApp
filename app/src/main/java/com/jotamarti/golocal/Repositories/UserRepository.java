package com.jotamarti.golocal.Repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.Models.Client;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.UseCases.Users.UserCallbacks;
import com.jotamarti.golocal.UseCases.Users.UserRepositoryFactory;
import com.jotamarti.golocal.UseCases.Users.UserUseCases;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class UserRepository implements UserRepositoryFactory {

    private final String TAG = "UserRepository";

    private UserUseCases userUsecases;

    // Auth
    private MutableLiveData<String> userLoggedUid = new MutableLiveData<>();
    private MutableLiveData<AuthErrors> authError = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> fireBaseUserRegistered = new MutableLiveData<>();

    // Backend
    private MutableLiveData<User> backendUser = new MutableLiveData<>();
    private MutableLiveData<BackendErrors> backendErrorGetUser = new MutableLiveData<>();

    public UserRepository() {
        userUsecases = new UserUseCases();
    }

    // Backend
    @Override
    public LiveData<User> getUserFromBackend(String uid) {
        userUsecases.getUserFromBackend(uid, new UserCallbacks.onResponseCallBackGetUserFromBackend() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                User user = new Client();

                try {
                    String uid = jsonObject.getString("uid");
                    String url = jsonObject.getString("avatar");
                    String nickName = jsonObject.getString("nick");
                    user.setAvatar(new URL(url));
                    user.setUserUid(uid);
                    ((Client)user).setUserName(nickName);
                    backendUser.setValue(user);
                } catch (JSONException | MalformedURLException e) {
                    backendErrorGetUser.setValue(BackendErrors.CLIENT_ERROR);
                }
            }

            @Override
            public void onErrorResponse(BackendErrors backendError) {
                backendErrorGetUser.setValue(backendError);
            }
        });
        return backendUser;
    }

    // Auth Service
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
    public LiveData<FirebaseUser> registerUserInAuthService(String email, String password) {
        userUsecases.registerUserInAuthService(email, password, new UserCallbacks.onResponseCallBackRegisterUserInAuthService() {
            @Override
            public void onResponse(FirebaseUser firebaseUser) {
                Log.d(TAG, "He llegado el repositorio");
                Log.d(TAG, firebaseUser.getEmail());
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
    public LiveData<AuthErrors> getAuthServiceError() {
        return authError;
    }
}
