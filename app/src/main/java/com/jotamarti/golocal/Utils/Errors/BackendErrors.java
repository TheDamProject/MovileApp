package com.jotamarti.golocal.Utils.Errors;

public enum BackendErrors {
    SUCCESS,
    REDIRECTION,
    CLIENT_ERROR,
    SERVER_ERROR;

    public static BackendErrors getBackendError(int httpNetworkResponse){
        if(httpNetworkResponse < 300) {
            return SUCCESS;
        } else if (httpNetworkResponse < 400) {
            return REDIRECTION;
        } else if (httpNetworkResponse < 500) {
            return CLIENT_ERROR;
        } else {
            return SERVER_ERROR;
        }
    }
}
