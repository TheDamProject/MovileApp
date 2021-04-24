package com.jotamarti.golocal.UseCases.Users;

public interface UserApi {

    // Backend
    void getUserFromBackend(String uid, UserCallbacks.onResponseCallBackGetUserFromBackend onResponseCallBackGetUserFromBackend);
    void getShopsNearby(Double lat, Double lang, UserCallbacks.onResponseCallBackGetShopsNearby onResponseCallBackGetShopsNearby);
    void deleteUserFromBackend(String uid, UserCallbacks.onResponseCallDeleteUserFromBackend onResponseCallDeleteUserFromBackend);

    // Auth Service
    void loginUserInAuthService(String email, String password, UserCallbacks.onResponseCallBackLoginUserInAuthService onResponseCallBackLoginUserInAuthService);
    void registerUserInAuthService(String email, String password, UserCallbacks.onResponseCallBackRegisterUserInAuthService onResponseCallBackRegisterUserInAuthService);

}
