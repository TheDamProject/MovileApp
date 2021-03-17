package com.jotamarti.golocal.UseCases.Clients;

import com.google.gson.JsonObject;

public interface ClientApi {

    // Backend
    void getClientFromBackend(String uid, ClientCallbacks.onResponseCallBackGetClient onResponseCallBackGetClient);
    void registerClientInBackend(String uid, String avatar, String nickName, ClientCallbacks.onResponseRegisterClientInBackend onResponseRegisterClientInBackend);
    void modifyClient(String uid, JsonObject newValues);
    void deleteClient(String uid);

}
