package com.jotamarti.golocal.UseCases.Users;

public interface UserApi {

    // Backend
    void getUserFromBackend(String uid, UserCallbacks.onResponseCallBackGetUserFromBackend onResponseCallBackGetUserFromBackend);

    // Auth Service
    void loginUserInAuthService(String email, String password, UserCallbacks.onResponseCallBackLoginUserInAuthService onResponseCallBackLoginUserInAuthService);
    void registerUserInAuthService(String email, String password, UserCallbacks.onResponseCallBackRegisterUserInAuthService onResponseCallBackRegisterUserInAuthService);

}
