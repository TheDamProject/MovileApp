package com.jotamarti.golocal.UseCases.Shops;

import com.jotamarti.golocal.Utils.Errors.BackendErrors;

import org.json.JSONObject;

public class ShopCallbacks {

    public interface onResponseRegisterShopInBackend {
        void onResponse(JSONObject json);
        void onErrorResponse(BackendErrors httpNetworkError);
    }

    public interface onResponseModifyShopInBackend {
        void onResponse(JSONObject json);
        void onErrorResponse(BackendErrors httpNetworkError);
    }
}
