package com.jotamarti.golocal.UseCases.Users;

import com.google.gson.JsonObject;

public interface UserApi {

    // Backend
    void getUser(String uid, UserCallbacks.OnResponseCallbackGetUser onResponseCallbackGetUser);
    void registerUserInBackend(String uid);
    void modifyUser(String uid, JsonObject newValues);
    void deleteUser(String uid);

    // Auth
    void loginUser(String email, String password, UserCallbacks.onResponseCallbackAuthUser onResponseCallbackAuthUser);
    void registerUserInAuthService(String email, String password, UserCallbacks.onResponseCallbackAuthUser onResponseCallbackAuthUser);

}
