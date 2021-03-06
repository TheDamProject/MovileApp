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

    public ClientRepository(){
        userUsecases = new ClientUsecases();
    }

    @Override
    public LiveData<User> registerClientInBackend(String userUid, String avatar, String nickName) {
        currentUser = new MutableLiveData<>();
        userUsecases.registerClientInBackend(userUid, avatar, nickName, new ClientCallbacks.onResponseRegisterClientInBackend() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    String uid = json.getString("uid");
                    String userName = json.getString("nick");
                    String imageUrl = json.getString("avatar");

                    User user = new Client(imageUrl, uid, userName);
                    currentUser.postValue(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                    haveHttpNetworkError.setValue(BackendErrors.CLIENT_ERROR);
                }
            }
            @Override
            public void onErrorResponse(BackendErrors httpNetworkError) {
                haveHttpNetworkError.setValue(httpNetworkError);
            }
        });
        return currentUser;
    }

    //TODO: Borrar esto por que tener el getUserFromBackend
    /*@Override
    public LiveData<User> getClientFromBackend(String userUid) {
        userUsecases.getClientFromBackend(userUid, new ClientCallbacks.onResponseCallBackGetClient() {
            @Override
            public void onResponse(JSONObject json) {
                try {
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
    }*/

    @Override
    public MutableLiveData<BackendErrors> getBackendError(){
        return haveHttpNetworkError;
    }

}
