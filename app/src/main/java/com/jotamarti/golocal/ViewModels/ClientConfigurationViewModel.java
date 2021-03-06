package com.jotamarti.golocal.ViewModels;

import android.text.TextWatcher;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.Repositories.ClientRepository;
import com.jotamarti.golocal.Repositories.UserRepository;
import com.jotamarti.golocal.UseCases.Clients.ClientRepositoryFactory;
import com.jotamarti.golocal.UseCases.Users.UserRepositoryFactory;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

import java.util.ArrayList;

public class ClientConfigurationViewModel extends ViewModel {

    // Activity Data
    public FirebaseUser firebaseUser;
    public boolean avatarInserted = false;
    public String email;
    public String password;
    public String nickName;
    public String imageBase64;
    public LatLng userCoordinates;
    public ArrayList<Shop> nearbyShops;

    // Backend
    private final ClientRepositoryFactory clientRepository;
    private LiveData<User> client;
    private LiveData<BackendErrors> backendError;

    // Auth
    private final UserRepositoryFactory userRepository;
    private LiveData<FirebaseUser> authUserRegistered;
    private LiveData<AuthErrors> authError;

    public ClientConfigurationViewModel() {
        userRepository = new UserRepository();
        clientRepository = new ClientRepository();
        backendError = clientRepository.getBackendError();
        authError = userRepository.getAuthServiceError();
    }

    // Backend
    public void registerClientInBackend(String uid, String avatar, String nickName) {
        client = clientRepository.registerClientInBackend(uid, avatar, nickName);
    }

    public LiveData<User> getClient() {
        return client;
    }

    public LiveData<BackendErrors> getBackendError() {
        return this.backendError;
    }

    // Auth
    public void registerClientInAuthService(String email, String password) {
        authUserRegistered = userRepository.registerUserInAuthService(email, password);
    }

    public LiveData<FirebaseUser> getAuthUser() {
        return authUserRegistered;
    }

    public LiveData<AuthErrors> getAuthError() {
        return authError;
    }

}
