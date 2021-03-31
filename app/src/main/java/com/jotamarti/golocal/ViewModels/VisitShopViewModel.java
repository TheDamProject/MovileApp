package com.jotamarti.golocal.ViewModels;

import androidx.lifecycle.ViewModel;

import com.jotamarti.golocal.Models.Shop;

public class VisitShopViewModel extends ViewModel {

    private Shop shop;

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
