package com.jotamarti.golocal.Repositories;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.jotamarti.golocal.Models.Client;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.UseCases.Clients.ClientRepositoryFactory;
import com.jotamarti.golocal.UseCases.Clients.ClientApi;
import com.jotamarti.golocal.UseCases.Clients.ClientCallbacks;
import com.jotamarti.golocal.UseCases.Clients.ClientUsecases;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ClientRepository implements ClientRepositoryFactory {

    private final String TAG = "UserRepository";

    private ClientApi userUsecases;

    // Backend
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<BackendErrors> haveHttpNetworkError = new MutableLiveData<>();

    // Auth
    private MutableLiveData<String> userLoggedUid = new MutableLiveData<>();
    private MutableLiveData<AuthErrors> authError = new MutableLiveData<>();
    private MutableLiveData<String> userRegisteredUid = new MutableLiveData<>();


    public ClientRepository(){
        userUsecases = new ClientUsecases();
    }

    @Override
    public LiveData<User> getUser(String userUid) {
        userUsecases.getClient("1234", new ClientCallbacks.onResponseCallBackGetClient() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    // TODO: Aqui tendremos que ver que tipo de usuario es y pasarle a current user una shop o un client.
                    JSONArray jsonArray = json.getJSONArray("results");
                    JSONObject userObject = jsonArray.getJSONObject(0);
                    JSONObject loginObject = userObject.getJSONObject("login");
                    JSONObject pictureObject = userObject.getJSONObject("picture");
                    String uid = loginObject.getString("uuid");
                    String userName = loginObject.getString("username");
                    String email = userObject.getString("email");
                    String imageUrl = pictureObject.getString("medium");
                    URL url;
                    url = new URL(imageUrl);


                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap = null;
                            InputStream inputStream = null;
                            try {
                                inputStream = new URL(imageUrl).openStream();
                                bitmap = BitmapFactory.decodeStream(inputStream);
                                User user = new Client(url, email, uid, userName);
                                currentUser.postValue(user);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    new Thread(runnable).start();
                } catch (JSONException | MalformedURLException e) {
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
        userUsecases.loginClient(email, password, new ClientCallbacks.onResponseCallBackAuthClient() {
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
        userUsecases.registerClientInAuthService(email, password, new ClientCallbacks.onResponseCallBackAuthClient() {
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
