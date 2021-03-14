package com.jotamarti.golocal.UseCases.Clients;

import com.google.gson.JsonObject;

public interface ClientApi {

    // Backend
    void getClient(String uid, String avatar, ClientCallbacks.onResponseCallBackGetClient onResponseCallBackGetClient);
    void registerClientInBackend(String uid, String avatar, ClientCallbacks.onResponseCallBackGetClient onResponseCallBackGetClient);
    void modifyClient(String uid, JsonObject newValues);
    void deleteClient(String uid);

    // Auth
    void loginClient(String email, String password, ClientCallbacks.onResponseCallBackAuthClient onResponseCallbackAuthUser);
    void registerClientInAuthService(String email, String password, ClientCallbacks.onResponseCallBackAuthClient onResponseCallbackAuthUser);

}
