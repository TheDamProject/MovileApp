package com.jotamarti.golocal.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.User;
import com.jotamarti.golocal.dummy.PostsDummy;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private User user;
    private MutableLiveData<String> title = new MutableLiveData<>();
    private MutableLiveData<List<Post>> posts = new MutableLiveData<>();

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser(){
        return user;
    }

    public void setPosts() {
        List<Post> posts = PostsDummy.getITems();
        this.posts.setValue(posts);
    }

    public LiveData<List<Post>> getPosts(){
        return posts;
    }

    public MutableLiveData<String> getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title.setValue(title);
    }
}
