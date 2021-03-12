package com.jotamarti.golocal.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.Models.Client;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.Repositories.ClientRepository;
import com.jotamarti.golocal.Repositories.UserRepository;
import com.jotamarti.golocal.UseCases.Clients.ClientRepositoryFactory;
import com.jotamarti.golocal.UseCases.Users.UserRepositoryFactory;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

public class ClientConfigurationViewModel extends ViewModel {

    // Backend
    private final ClientRepositoryFactory clientRepository;
    private LiveData<User> client = new MutableLiveData<>();
    private LiveData<BackendErrors> backendError;

    // Auth
    private final UserRepositoryFactory userRepository;
    private LiveData<FirebaseUser> authUserRegistered = new MutableLiveData<>();
    private LiveData<AuthErrors> authError;

    public ClientConfigurationViewModel(){
        userRepository = new UserRepository();
        clientRepository = new ClientRepository();
        backendError = clientRepository.getBackendError();
        authError = userRepository.getRegisterUserInAuthServiceError();
    }

    // Backend
    public void registerClientInBackend(String uid){
        client = clientRepository.getUser(uid);
    }

    public LiveData<User> getClient(){
        return client;
    }

    public LiveData<BackendErrors> getBackendError(){
        return this.backendError;
    }

    // Auth
    public void registerClientInAuthService(String email, String password){
       authUserRegistered = userRepository.registerUserInAuthService(email, password);
    }

    public LiveData<FirebaseUser> getAuthUser(){
        return authUserRegistered;
    }

    public LiveData<AuthErrors> getAuthError(){
        return authError;
    }

}
