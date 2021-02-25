package com.jotamarti.golocal.Repositories;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.UseCases.Users.UserRepositoryFactory;
import com.jotamarti.golocal.UseCases.Users.UserApi;
import com.jotamarti.golocal.UseCases.Users.UserCallbacks;
import com.jotamarti.golocal.UseCases.Users.UserUsecases;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class UserRepository implements UserRepositoryFactory {

    private final String TAG = "UserRepository";

    private UserApi userUsecases;

    // Backend
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<BackendErrors> haveHttpNetworkError = new MutableLiveData<>();

    // Auth
    private MutableLiveData<String> userLoggedUid = new MutableLiveData<>();
    private MutableLiveData<AuthErrors> authError = new MutableLiveData<>();
    private MutableLiveData<String> userRegisteredUid = new MutableLiveData<>();


    public UserRepository(){
        userUsecases = new UserUsecases();
    }

    @Override
    public LiveData<User> getUser(String userUid) {
        userUsecases.getUser("1234", new UserCallbacks.OnResponseCallbackGetUser() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    JSONArray jsonArray = json.getJSONArray("results");
                    JSONObject userObject = jsonArray.getJSONObject(0);
                    JSONObject loginObject = userObject.getJSONObject("login");
                    JSONObject pictureObject = userObject.getJSONObject("picture");
                    String uid = loginObject.getString("uuid");
                    String email = userObject.getString("email");
                    String imageUrl = pictureObject.getString("medium");


                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap = null;
                            InputStream inputStream = null;
                            try {
                                inputStream = new URL(imageUrl).openStream();
                                bitmap = BitmapFactory.decodeStream(inputStream);
                                User user = new User(bitmap, email, uid);
                                currentUser.postValue(user);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    new Thread(runnable).start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(BackendErrors httpNetworkError) {
                haveHttpNetworkError.setValue(httpNetworkError);
            }
        });
        return currentUser;
    }

    @Override
    public MutableLiveData<BackendErrors> getBackendError(){
        return haveHttpNetworkError;
    }

    @Override
    public LiveData<String> loginUser(String email, String password) {
        userUsecases.loginUser(email, password, new UserCallbacks.onResponseCallbackAuthUser() {
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

    @Override
    public LiveData<String> registerUserInAuthService(String email, String password) {
        userUsecases.registerUserInAuthService(email, password, new UserCallbacks.onResponseCallbackAuthUser() {
            @Override
            public void onResponse(String uid) {
                userRegisteredUid.setValue(uid);
            }

            @Override
            public void onErrorResponse(AuthErrors error) {
                authError.setValue(error);
            }
        });
        return userRegisteredUid;
    }

    @Override
    public LiveData<AuthErrors> getRegisterUserInAuthServiceError() {
        return null;
    }

}
