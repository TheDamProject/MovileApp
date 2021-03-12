package com.jotamarti.golocal.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.jotamarti.golocal.Models.Client;
import com.jotamarti.golocal.Repositories.ClientRepository;
import com.jotamarti.golocal.Repositories.UserRepository;
import com.jotamarti.golocal.UseCases.Clients.ClientRepositoryFactory;
import com.jotamarti.golocal.UseCases.Users.UserRepositoryFactory;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

public class ClientConfigurationViewModel extends ViewModel {

    private LiveData<Client> client;
    private ClientRepositoryFactory clientRepository;
    private LiveData<BackendErrors> backendError;

    // Auth
    private final UserRepositoryFactory userRepository;
    private LiveData<String> firebaseUserUid;
    private LiveData<String> userRegisteredUid;
    private LiveData<FirebaseUser> fireBaseUserRegistered;
    private LiveData<AuthErrors> authError;

    public ClientConfigurationViewModel(){
        clientRepository = new ClientRepository();
        backendError = clientRepository.getBackendError();
        userRepository = new UserRepository();
        authError = userRepository.getRegisterUserInAuthServiceError();
    }

    public void registerClientInBackend(String uid){
        clientRepository.getUser(uid);
    }



    public LiveData<Client> getClient(){
        return client;
    }

    public LiveData<BackendErrors> getBackendError(){
        return this.backendError;
    }

    // Firebase
    public void registerClientInAuthService(String email, String password){
       fireBaseUserRegistered = userRepository.registerUserInAuthService(email, password);
    }

    public LiveData<AuthErrors> getAuthError(){
        return authError;
    }

    public LiveData<FirebaseUser> getFirebaseUser(){
        return fireBaseUserRegistered;
    }

}
