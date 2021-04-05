package com.jotamarti.golocal.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Repositories.ShopRepository;
import com.jotamarti.golocal.Repositories.UserRepository;
import com.jotamarti.golocal.UseCases.Shops.ShopRepositoryFactory;
import com.jotamarti.golocal.UseCases.Users.UserRepositoryFactory;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

import java.util.List;

public class ShopConfigurationViewModel extends ViewModel {

    public String email;
    public String password;
    public List<Shop> nearbyShops;

    public String caller;
    public Shop shop;
    public String imageBase64;

    private ShopRepositoryFactory shopRepository;

    private LiveData<Shop> backendShop;
    private LiveData<BackendErrors> backendError;

    // Auth
    private final UserRepositoryFactory userRepository;
    private LiveData<FirebaseUser> authUserRegistered;
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

    public LiveData<Shop> getShop() {
        return backendShop;
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
