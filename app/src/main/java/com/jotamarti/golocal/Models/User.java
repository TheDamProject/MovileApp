package com.jotamarti.golocal.Models;

import java.io.Serializable;
import java.net.URL;

public abstract class User implements Serializable {

    protected URL avatar;
    protected String email;
    protected String userUid;

    public User(URL avatar, String email, String userUid) {
        this.avatar = avatar;
        this.email = email;
        this.userUid = userUid;
    }

    public User() {

    }

    public URL getAvatar() {
        return avatar;
    }

    public void setAvatar(URL avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

}
