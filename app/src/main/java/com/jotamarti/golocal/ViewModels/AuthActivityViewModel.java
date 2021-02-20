package com.jotamarti.golocal.ViewModels;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.Repositories.UserRepository;


public class AuthActivityViewModel extends ViewModel {

    private LiveData<User> currentUser;
    private LiveData<Integer> error;
    private UserRepository repository;

    public AuthActivityViewModel(){
        super();
        repository = new UserRepository();
        //this.currentUser = repository.getUser("1234");
        this.error = repository.getError();

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

}
