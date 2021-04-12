package com.jotamarti.golocal.UseCases.Posts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

public interface PostRepositoryFactory {

    LiveData<Post> createPostInBackend(Post post);
    MutableLiveData<BackendErrors> getBackendError();
}
