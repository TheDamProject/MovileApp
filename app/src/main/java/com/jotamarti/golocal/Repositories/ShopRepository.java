package com.jotamarti.golocal.Repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jotamarti.golocal.Models.Client;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.UseCases.Shops.ShopApi;
import com.jotamarti.golocal.UseCases.Shops.ShopCallbacks;
import com.jotamarti.golocal.UseCases.Shops.ShopParser;
import com.jotamarti.golocal.UseCases.Shops.ShopRepositoryFactory;
import com.jotamarti.golocal.UseCases.Shops.ShopUsescases;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

import org.json.JSONException;
import org.json.JSONObject;

public class ShopRepository implements ShopRepositoryFactory {

    private final String TAG = "ShopRepository";

    private ShopApi ShopUsecases;

    public ShopRepository() {
        ShopUsecases = new ShopUsescases();
    }

    // Backend
    private MutableLiveData<Shop> currentShop = new MutableLiveData<>();
    private MutableLiveData<String> modifyShopResponse = new MutableLiveData<>();
    private MutableLiveData<BackendErrors> haveHttpNetworkError = new MutableLiveData<>();

    @Override
    public LiveData<Shop> registerShopInBackend(Shop shop) {
        currentShop = new MutableLiveData<>();
        ShopUsecases.registerShopInBackend(shop, new ShopCallbacks.onResponseRegisterShopInBackend() {
            @Override
            public void onResponse(JSONObject json) {
                Shop shop = ShopParser.parseShopFromJsonObject(json);
                currentShop.postValue(shop);
            }

            @Override
            public void onErrorResponse(BackendErrors httpNetworkError) {
                haveHttpNetworkError.setValue(httpNetworkError);
            }
        });
        return currentShop;
    }

    @Override
    public LiveData<String> modifyShopInBackend(Shop shop) {
        modifyShopResponse = new MutableLiveData<>();
        ShopUsecases.modifyShopInBackend(shop, new ShopCallbacks.onResponseModifyShopInBackend() {
            @Override
            public void onResponse(JSONObject json) {
                String logo = "";
                try {
                    logo = json.getString("logo");
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                modifyShopResponse.postValue(logo);
            }

            @Override
            public void onErrorResponse(BackendErrors httpNetworkError) {
                haveHttpNetworkError.setValue(httpNetworkError);
            }
        });
        return modifyShopResponse;
    }

    @Override
    public MutableLiveData<BackendErrors> getBackendError() {
        return haveHttpNetworkError;
    }
}
