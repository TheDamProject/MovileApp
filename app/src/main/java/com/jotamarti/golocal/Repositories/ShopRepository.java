package com.jotamarti.golocal.Repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jotamarti.golocal.Models.Client;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.UseCases.Shops.ShopApi;
import com.jotamarti.golocal.UseCases.Shops.ShopCallbacks;
import com.jotamarti.golocal.UseCases.Shops.ShopRepositoryFactory;
import com.jotamarti.golocal.UseCases.Shops.ShopUsescases;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

import org.json.JSONException;
import org.json.JSONObject;

public class ShopRepository implements ShopRepositoryFactory {

    private final String TAG = "ShopRepository";

    private ShopApi ShopUsecases;

    public ShopRepository(){
        ShopUsecases = new ShopUsescases();
    }

    // Backend
    private MutableLiveData<Shop> currentShop = new MutableLiveData<>();
    private MutableLiveData<BackendErrors> haveHttpNetworkError = new MutableLiveData<>();

    @Override
    public LiveData<Shop> registerShopInBackend(Shop shop) {
        currentShop = new MutableLiveData<>();
        ShopUsecases.registerShopInBackend(shop, new ShopCallbacks.onResponseRegisterShopInBackend() {
            @Override
            public void onResponse(JSONObject json) {
                try {
                    String uid = json.getString("uid");
                    String userName = json.getString("nick");
                    String imageUrl = json.getString("avatar");

                    User user = new Client(imageUrl, uid, userName);
                    currentShop.postValue(new Shop());
                } catch (JSONException e) {
                    e.printStackTrace();
                    haveHttpNetworkError.setValue(BackendErrors.CLIENT_ERROR);
                }
            }
            @Override
            public void onErrorResponse(BackendErrors httpNetworkError) {
                haveHttpNetworkError.setValue(httpNetworkError);
            }
        });
        return currentShop;
    }

    @Override
    public MutableLiveData<BackendErrors> getBackendError() {
        return haveHttpNetworkError;
    }
}
