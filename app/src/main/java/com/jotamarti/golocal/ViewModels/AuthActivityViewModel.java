package com.jotamarti.golocal.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.jotamarti.golocal.App;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.Repositories.ClientRepository;
import com.jotamarti.golocal.Repositories.UserRepository;
import com.jotamarti.golocal.SharedPreferences.DataStorage;
import com.jotamarti.golocal.SharedPreferences.UserPreferences;
import com.jotamarti.golocal.UseCases.Clients.ClientRepositoryFactory;
import com.jotamarti.golocal.UseCases.Users.UserRepositoryFactory;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

import java.util.ArrayList;
import java.util.List;


public class AuthActivityViewModel extends ViewModel {

    private final String TAG = "AuthActivityViewModel";

    public final int MIN_PASS_LENGTH = 6;

    // Backend
    private final ClientRepositoryFactory clientRepository;
    private LiveData<User> currentUser;
    private LiveData<BackendErrors> backendError;
    private LiveData<ArrayList<Shop>> nearbyShops;

    // Activity Views data
    public String currentInsertedEmail;
    public String currentInsertedPassword;
    public LatLng userCoordinates;
    public User user;

    // Auth
    private final UserRepositoryFactory userRepository;
    private LiveData<String> firebaseUserUid;
    private LiveData<AuthErrors> authError;

    // SharedPreferences
    private final DataStorage dataStorage;
    private final MutableLiveData<UserPreferences> preferences = new MutableLiveData<>();

    public AuthActivityViewModel(){
        super();
        clientRepository = new ClientRepository();
        userRepository = new UserRepository();
        backendError = clientRepository.getBackendError();
        authError = userRepository.getAuthServiceError();
        dataStorage = new DataStorage(App.getContext());
    }

    // Manage Backend
    public void getUserFromBackend(String uid){
        this.currentUser = userRepository.getUserFromBackend(uid);
    }

    public LiveData<User> getCurrentUser() {
        if (currentUser == null) {
            currentUser = new MutableLiveData<>();
        }
        return this.currentUser;
    }

    public LiveData<BackendErrors> getBackendError(){
        return this.backendError;
    }

    public void getNearbyShops() {
        this.nearbyShops = userRepository.getShopsNearby(String.valueOf(userCoordinates.latitude), String.valueOf(userCoordinates.longitude));
    }

    public LiveData<ArrayList<Shop>> getNearbyShopsList(){
        if (nearbyShops == null) {
            nearbyShops = new MutableLiveData<>();
        }
        return this.nearbyShops;
    }

    // Manage Auth
    public void loginUserInAuthService(String mode){
        if(mode.equals("register")) {
            firebaseUserUid = userRepository.loginUserInAuthService(currentInsertedEmail, "a");
        } else {
            firebaseUserUid = userRepository.loginUserInAuthService(currentInsertedEmail, currentInsertedPassword);
        }

    }

    public LiveData<String> getFirebaseUserUid(){
        return firebaseUserUid;
    }

    public LiveData<AuthErrors> getAuthError(){
        return authError;
    }

    // Manage SharedPreferences
    public LiveData<UserPreferences> getSharedPreferences(){
        String emailPreferences = (String) dataStorage.read("email", DataStorage.STRING);
        String passwordPreferences = (String) dataStorage.read("password", DataStorage.STRING);
        UserPreferences userPreferences = new UserPreferences(emailPreferences, passwordPreferences);
        preferences.setValue(userPreferences);
        return preferences;
    }

    public void setPreferences(){
        dataStorage.write("email", currentInsertedEmail);
        dataStorage.write("password", currentInsertedPassword);
    }

    // Activity View Data
    public void setCurrentInsertedEmail(String email){
        currentInsertedEmail = email;
    }

    public void setCurrentInsertedPassword(String password){
        currentInsertedPassword = password;
    }

    public String getCurrentInsertedEmail(){
        return currentInsertedEmail;
    }

    public String getCurrentInsertedPassword(){
        return currentInsertedPassword;
    }

}
