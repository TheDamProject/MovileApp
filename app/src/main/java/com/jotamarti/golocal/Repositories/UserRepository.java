package com.jotamarti.golocal.Repositories;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.UseCases.Users.RepositoryFactory;
import com.jotamarti.golocal.UseCases.Users.UserCallbacks;
import com.jotamarti.golocal.UseCases.Users.UserUsecases;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class UserRepository implements RepositoryFactory {

    private final String TAG = "UserRepository";

    private UserUsecases userUsecases;

    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<Integer> haveError = new MutableLiveData<>();

    public UserRepository(){
        userUsecases = new UserUsecases();
        //haveError.setValue(false);
    }


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
                                haveError.postValue(23);
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
            public void onErrorResponse(int error) {
                haveError.setValue(23);
            }
        });
        return currentUser;
    }

    public MutableLiveData<Integer> getError(){
        return haveError;
    }

}
