package com.jotamarti.golocal.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.Shop;
import com.jotamarti.golocal.Repositories.PostRepository;
import com.jotamarti.golocal.UseCases.Posts.PostRepositoryFactory;
import com.jotamarti.golocal.Utils.Errors.BackendErrors;

import java.util.ArrayList;

public class NewPostActivityViewModel extends ViewModel {

    public String imageBase64;
    public Post post;
    public Boolean imageInserted;
    public Shop thisShop;
    public ArrayList<Shop> shopList;

    private PostRepositoryFactory postRepository;

    private LiveData<Post> backendPost;
    private LiveData<BackendErrors> backendError;

    public NewPostActivityViewModel(){
        postRepository = new PostRepository();
        backendError = postRepository.getBackendError();
        imageInserted = false;
    }

    public void createPostInBackend(){
        backendPost = postRepository.createPostInBackend(post);
    }

    public LiveData<Post> getBackendPost(){
        return backendPost;
    }

    public LiveData<BackendErrors> getBackendError() {
        return this.backendError;
    }


}
