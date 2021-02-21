package com.jotamarti.golocal.ViewModels;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jotamarti.golocal.App;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.Repositories.UserRepository;
import com.jotamarti.golocal.SharedPreferences.DataStorage;
import com.jotamarti.golocal.SharedPreferences.UserPreferences;
import com.jotamarti.golocal.UseCases.Users.RepositoryFactory;


public class AuthActivityViewModel extends ViewModel {

    private LiveData<User> currentUser;
    private LiveData<Integer> error;
    private RepositoryFactory repository;

    public final int MIN_PASS_LENGTH = 6;

    // SharedPreferences
    private DataStorage dataStorage;
    private String emailPreferences;
    private String passwordPreferences;
    private MutableLiveData<UserPreferences> preferences = new MutableLiveData<>();

    public AuthActivityViewModel(){
        super();
        repository = new UserRepository();
        error = repository.getError();
        dataStorage = new DataStorage(App.getContext());
    }

    public LiveData<User> getCurrentUser() {
        return this.currentUser;
    }

    public void getNewUser(String uid){
        this.currentUser = repository.getUser(uid);
    }

    public LiveData<Integer> getError(){
        return this.error;
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

}
