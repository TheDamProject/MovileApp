package com.jotamarti.golocal.ViewModels;

import androidx.lifecycle.ViewModel;

import com.jotamarti.golocal.Models.Post;
import com.jotamarti.golocal.Models.Shop;

public class PostDetailActivityViewModel extends ViewModel {

    private Post post;
    private Shop shop;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
