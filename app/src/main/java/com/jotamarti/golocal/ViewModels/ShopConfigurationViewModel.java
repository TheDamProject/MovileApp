package com.jotamarti.golocal.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Repositories.ShopRepository;
import com.jotamarti.golocal.Repositories.UserRepository;
import com.jotamarti.golocal.UseCases.Shops.ShopRepositoryFactory;
import com.jotamarti.golocal.UseCases.Users.UserRepositoryFactory;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

import java.util.ArrayList;
import java.util.List;

public class ShopConfigurationViewModel extends ViewModel {

    public String email;
    public String password;
    public ArrayList<Shop> nearbyShops;
    public FirebaseUser firebaseUser;

    public String caller;
    public Shop shop;
    public String imageBase64;
    public Boolean imageInserted = false;
    public LatLng userCoordinates;

    private ShopRepositoryFactory shopRepository;

    private LiveData<Shop> backendShop;
    private LiveData<BackendErrors> backendError;

    // Auth
    private final UserRepositoryFactory userRepository;
    private LiveData<FirebaseUser> authUserRegistered;
    private LiveData<String> modifyShopResponse;
    private LiveData<AuthErrors> authError;

    public ShopConfigurationViewModel(){
        shopRepository = new ShopRepository();
        userRepository = new UserRepository();
        authError = userRepository.getAuthServiceError();
        backendError = shopRepository.getBackendError();
    }

    // Backend
    public void registerShopInBackend() {
        backendShop = shopRepository.registerShopInBackend(shop);
    }

    public void modifyShopInBackend(){
        modifyShopResponse = shopRepository.modifyShopInBackend(shop);
    }

    public LiveData<Shop> getShop() {
        return backendShop;
    }

    public LiveData<String> getModifyShopResponse() {
        return modifyShopResponse;
    }

    public LiveData<BackendErrors> getBackendError() {
        return this.backendError;
    }

    // Auth
    public void registerShopInAuthService(String email, String password) {
        authUserRegistered = userRepository.registerUserInAuthService(email, password);
    }

    public LiveData<FirebaseUser> getAuthUser() {
        return authUserRegistered;
    }

    public LiveData<AuthErrors> getAuthError() {
        return authError;
    }

}
