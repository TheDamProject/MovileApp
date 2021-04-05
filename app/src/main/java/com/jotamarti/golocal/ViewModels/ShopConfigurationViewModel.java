package com.jotamarti.golocal.ViewModels;

import androidx.lifecycle.ViewModel;

import com.jotamarti.golocal.Models.Shop;

import java.util.List;

public class ShopConfigurationViewModel extends ViewModel {

    public String email;
    public String password;
    public List<Shop> nearbyShops;

    public String caller;
    public Shop shop;
    public String imageBase64;

}
