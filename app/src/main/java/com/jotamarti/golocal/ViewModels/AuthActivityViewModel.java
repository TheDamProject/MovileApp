package com.jotamarti.golocal.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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


public class AuthActivityViewModel extends ViewModel {

    private final String TAG = "AuthActivityViewModel";

    public final int MIN_PASS_LENGTH = 6;

    // Backend
    private final ClientRepositoryFactory clientRepository;
    private LiveData<User> currentUser = new MutableLiveData<>();
    private LiveData<BackendErrors> backendError;

    // Activity Views data
    private String currentInsertedEmail;
    private String currentInsertedPassword;

    // Auth
    private final UserRepositoryFactory userRepository;
    private LiveData<String> firebaseUserUid = new MutableLiveData<>();
    private LiveData<AuthErrors> authError;

    // SharedPreferences
    private final DataStorage dataStorage;
    private final MutableLiveData<UserPreferences> preferences = new MutableLiveData<>();

    public AuthActivityViewModel(){
        super();
        clientRepository = new ClientRepository();
        userRepository = new UserRepository();
        backendError = clientRepository.getBackendError();
        authError = userRepository.getLoginUserInAuthServiceError();
        dataStorage = new DataStorage(App.getContext());
    }

    // Manage Backend
    public LiveData<User> getCurrentUser() {
        return this.currentUser;
    }

    public void getNewClient(String uid){
        this.currentUser = clientRepository.getUser(uid);
    }

    public void getNewShop(String uid){
        // TODO: Cambiar por el backend de la tienda
        this.currentUser = clientRepository.getUser(uid);
    }

    public LiveData<BackendErrors> getBackendError(){
        return this.backendError;
    }

    // Manage Auth
    public void loginUserInAuthService(){
        firebaseUserUid = userRepository.loginUserInAuthService(currentInsertedEmail, currentInsertedPassword);
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
