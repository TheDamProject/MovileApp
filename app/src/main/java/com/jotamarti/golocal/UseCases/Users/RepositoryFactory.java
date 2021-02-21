package com.jotamarti.golocal.UseCases.Users;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jotamarti.golocal.Models.User;

public interface RepositoryFactory {

    LiveData<User> getUser(String userUid);
    MutableLiveData<Integer> getError();
}
