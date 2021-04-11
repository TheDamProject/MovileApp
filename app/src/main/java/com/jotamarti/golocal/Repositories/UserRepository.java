package com.jotamarti.golocal.Repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.Models.Client;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.UseCases.Shops.ShopParser;
import com.jotamarti.golocal.UseCases.Users.UserCallbacks;
import com.jotamarti.golocal.UseCases.Users.UserRepositoryFactory;
import com.jotamarti.golocal.UseCases.Users.UserUseCases;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;
import com.jotamarti.golocal.dummy.ShopsDummy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserRepository implements UserRepositoryFactory {

    private final String TAG = "UserRepository";

    private UserUseCases userUsecases;

    // Auth
    private MutableLiveData<String> userLoggedUid = new MutableLiveData<>();
    private MutableLiveData<AuthErrors> authError = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> fireBaseUserRegistered;

    // Backend
    private MutableLiveData<User> backendUser = new MutableLiveData<>();
    private MutableLiveData<BackendErrors> backendErrorData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Shop>> nearbyShops = new MutableLiveData<>();

    public UserRepository() {
        userUsecases = new UserUseCases();
        fireBaseUserRegistered = new MutableLiveData<>();
    }

    // Backend
    @Override
    public LiveData<User> getUserFromBackend(String uid) {
        backendUser = new MutableLiveData<>();
        userUsecases.getUserFromBackend(uid, new UserCallbacks.onResponseCallBackGetUserFromBackend() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                User user = null;
                try {
                    String type = jsonObject.getString("type");
                    String uid = jsonObject.getString("uid");
                    String url = jsonObject.getString("avatar");
                    if (type.equals("client")) {
                        String nickName = jsonObject.getString("nick");
                        user = new Client();
                        user.setAvatar(url);
                        user.setUserUid(uid);
                        ((Client) user).setNickName(nickName);
                    } else {
                        // TODO: Si es tienda
                    }
                    backendUser.setValue(user);
                } catch (JSONException e) {
                    backendErrorData.setValue(BackendErrors.CLIENT_ERROR);
                }
            }

            @Override
            public void onErrorResponse(BackendErrors backendError) {
                backendErrorData.setValue(backendError);
            }
        });
        return backendUser;
    }

    @Override
    public LiveData<ArrayList<Shop>> getShopsNearby(String lat, String lang) {
        nearbyShops = new MutableLiveData<>();
        userUsecases.getShopsNearby(Double.parseDouble(lat), Double.parseDouble(lang), new UserCallbacks.onResponseCallBackGetShopsNearby() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                ArrayList<Shop> shopList = new ArrayList<>();
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonShopObject = jsonArray.getJSONObject(i);
                        Shop shop = ShopParser.parseShopFromJsonObject(jsonShopObject);
                        shopList.add(shop);
                    }
                } catch (JSONException jsonException) {
                    backendErrorData.setValue(BackendErrors.CLIENT_ERROR);
                }
                nearbyShops.setValue(shopList);
            }

            @Override
            public void onErrorResponse(BackendErrors backendError) {
                backendErrorData.setValue(backendError);
            }
        });
        return nearbyShops;
    }

    @Override
    public MutableLiveData<BackendErrors> getBackendError() {
        return backendErrorData;
    }

    // Auth Service
    @Override
    public LiveData<String> loginUserInAuthService(String email, String password) {
        userLoggedUid = new MutableLiveData<>();
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
        fireBaseUserRegistered = new MutableLiveData<>();
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
    public LiveData<AuthErrors> getAuthServiceError() {
        return authError;
    }
}
