package com.jotamarti.golocal.UseCases.Shops;

import com.google.gson.JsonObject;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.UseCases.Clients.ClientCallbacks;

public interface ShopApi {

    void getShop(String uid);
    void registerShopInBackend(Shop shop, ShopCallbacks.onResponseRegisterShopInBackend onResponseRegisterShopInBackend);
    void modifyShopInBackend(Shop shop, ShopCallbacks.onResponseModifyShopInBackend onResponseModifyShopInBackend);

}
