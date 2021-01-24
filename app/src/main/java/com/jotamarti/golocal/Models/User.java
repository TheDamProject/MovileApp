package com.jotamarti.golocal.Models;

import android.graphics.Bitmap;
import android.media.Image;

import java.io.Serializable;
import java.util.List;

public class User {

    private Bitmap avatar;
    private String email;
    private String userId;
    private List<String> companiesFollowed;

    public User(Bitmap avatar, String email, String userId) {
        this.avatar = avatar;
        this.email = email;
        this.userId = userId;
        //this.companiesFollowed = companiesFollowed;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getCompaniesFollowed() {
        return companiesFollowed;
    }

    public void setCompaniesFollowed(List<String> companiesFollowed) {
        this.companiesFollowed = companiesFollowed;
    }
}
