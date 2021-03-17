package com.jotamarti.golocal.UseCases.Clients;

import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

import org.json.JSONObject;

public class ClientCallbacks {

    public interface onResponseCallBackGetClient {
        void onResponse(JSONObject json);
        void onErrorResponse(BackendErrors httpNetworkError);
    }

    public interface onResponseRegisterClientInBackend {
        void onResponse(JSONObject json);
        void onErrorResponse(BackendErrors httpNetworkError);
    }

}
