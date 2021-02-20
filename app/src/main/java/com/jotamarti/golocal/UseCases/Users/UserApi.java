package com.jotamarti.golocal.UseCases.Users;

import com.google.gson.JsonObject;

public interface UserApi {

    void getUser(String uid, UserCallbacks.OnResponseCallbackGetUser onResponseCallbackGetUser);
    void registerUser(String uid);
    void modifyUser(String uid, JsonObject newValues);
    void deleteUser(String uid);

}
