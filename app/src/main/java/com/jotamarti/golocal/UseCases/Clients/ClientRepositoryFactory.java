package com.jotamarti.golocal.UseCases.Clients;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.Utils.Errors.AuthErrors;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

public interface ClientRepositoryFactory {

    // Backend calls
    LiveData<User> registerClientInBackend(String userUid, String avatar, String nickName);
    LiveData<User> getClientFromBackend(String userUid);
    MutableLiveData<BackendErrors> getBackendError();
}
