package com.jotamarti.golocal.Repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.Models.Client;
import com.jotamarti.golocal.Models.Shop;
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
import java.util.ArrayList;
import java.util.List;

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
    private MutableLiveData<ArrayList<Shop>> nearbyShops = new MutableLiveData<>();
    private MutableLiveData<BackendErrors> backendErrorGetNearbyShops = new MutableLiveData<>();

    public UserRepository() {
        userUsecases = new UserUseCases();
    }

    // Backend
    @Override
    public LiveData<User> getUserFromBackend(String uid) {
        userUsecases.getUserFromBackend(uid, new UserCallbacks.onResponseCallBackGetUserFromBackend() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                User user = null;
                try {
                    String type = jsonObject.getString("type");
                    String uid = jsonObject.getString("uid");
                    String url = jsonObject.getString("avatar");
                    if(type.equals("client")) {
                        String nickName = jsonObject.getString("nick");
                        user = new Client();
                        user.setAvatar(url);
                        user.setUserUid(uid);
                        ((Client)user).setNickName(nickName);
                    } else {
                        // TODO: Si es tienda
                    }
                    backendUser.setValue(user);
                } catch (JSONException e) {
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

    @Override
    public LiveData<ArrayList<Shop>> getShopsNearby(String lat, String lang) {
        userUsecases.getShopsNearby(Double.parseDouble(lat), Double.parseDouble(lang), new UserCallbacks.onResponseCallBackGetShopsNearby() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                // TODO: Devolver lista de tiendas
                ArrayList<Shop> shops = new ArrayList<>();
                nearbyShops.setValue(shops);
            }

            @Override
            public void onErrorResponse(BackendErrors backendError) {
                backendErrorGetNearbyShops.setValue(backendError);
            }
        });
        return nearbyShops;
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
