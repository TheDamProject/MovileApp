package com.jotamarti.golocal.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jotamarti.golocal.App;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.Repositories.UserRepository;
import com.jotamarti.golocal.SharedPreferences.DataStorage;
import com.jotamarti.golocal.SharedPreferences.UserPreferences;
import com.jotamarti.golocal.UseCases.Users.UserRepositoryFactory;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;


public class AuthActivityViewModel extends ViewModel {

    private LiveData<User> currentUser;
    private LiveData<Integer> backendError;
    private UserRepositoryFactory repository;

    private String currentInsertedEmail;
    private String currentInsertedPassword;

    // Auth
    private LiveData<String> userLoggedUid;
    private LiveData<String> userRegisteredUid;
    private LiveData<AuthErrors> authError;


    public final int MIN_PASS_LENGTH = 6;

    // SharedPreferences
    private DataStorage dataStorage;
    private String emailPreferences;
    private String passwordPreferences;
    private MutableLiveData<UserPreferences> preferences = new MutableLiveData<>();

    public AuthActivityViewModel(){
        super();
        repository = new UserRepository();
        backendError = repository.getBackendError();
        authError = repository.getLoginUserInAuthServiceError();
        dataStorage = new DataStorage(App.getContext());
    }

    public LiveData<User> getCurrentUser() {
        return this.currentUser;
    }

    public void getNewUser(String uid){
        this.currentUser = repository.getUser(uid);
    }

    public LiveData<Integer> getBackendError(){
        return this.backendError;
    }

    public void loginUser(){
        userLoggedUid = repository.loginUser(currentInsertedEmail, currentInsertedPassword);
    }

    public void registerUser(){
        userRegisteredUid = repository.registerUserInAuthService(currentInsertedEmail, currentInsertedPassword);
    }

    public LiveData<String> getRegisteredUserUid(){
        return userRegisteredUid;
    }

    public LiveData<String> getLoggedUser(){
        return userLoggedUid;
    }

    public LiveData<AuthErrors> getAuthError(){
        return authError;
    }


    public LiveData<UserPreferences> getSharedPreferences(){
        emailPreferences = (String) dataStorage.read("email", DataStorage.STRING);
        passwordPreferences = (String) dataStorage.read("password", DataStorage.STRING);
        UserPreferences userPreferences = new UserPreferences(emailPreferences, passwordPreferences);
        preferences.setValue(userPreferences);
        return preferences;
    }

    public void setPreferences(String email, String password){
        dataStorage.write("email", email);
        dataStorage.write("password", password);
    }

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
