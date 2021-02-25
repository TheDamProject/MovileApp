package com.jotamarti.golocal.UseCases.Shops;

import com.google.gson.JsonObject;

public interface ShopApi {

    void getShop(String uid);
    void registerShopInBackend(String uid);
    void modifyShop(String uid, JsonObject newValues);

}
