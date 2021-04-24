package com.jotamarti.golocal.UseCases.Shops;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

public interface ShopRepositoryFactory {

    LiveData<Shop> registerShopInBackend(Shop shop);
    LiveData<String> modifyShopInBackend(Shop shop);
    MutableLiveData<BackendErrors> getBackendError();
}
