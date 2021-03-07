package com.jotamarti.golocal.UseCases.Users;

public interface UserApi {

    void loginUserInAuthService(String email, String password, UserCallbacks.onResponseCallBackLoginUserInAuthService onResponseCallBackLoginUserInAuthService);
    void registerUserInAuthService(String email, String password, UserCallbacks.onResponseCallBackRegisterUserInAuthService onResponseCallBackRegisterUserInAuthService);

}
