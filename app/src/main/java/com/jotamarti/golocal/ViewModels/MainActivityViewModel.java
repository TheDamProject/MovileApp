package com.jotamarti.golocal.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.User;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private User user;
    private MutableLiveData<List<Post>> posts = new MutableLiveData<>();

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser(){
        return user;
    }

    public void setPosts(List<Post> posts) {
        this.posts.setValue(posts);
    }

    public LiveData<List<Post>> getPosts(){
        return posts;
    }
}
